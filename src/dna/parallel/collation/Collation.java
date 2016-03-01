package dna.parallel.collation;

import java.io.File;
import java.util.ArrayList;

import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IRecomputation;
import dna.parallel.auxData.AuxData;
import dna.parallel.auxData.AuxData.AuxWriteType;
import dna.parallel.partition.Partition;
import dna.parallel.partition.Partition.PartitionType;
import dna.parallel.util.Sleeper;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.BatchData;
import dna.series.data.MetricData;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.Config;
import dna.util.parameters.Parameter;

public abstract class Collation<M extends Metric, T extends Partition> extends
		Metric implements IRecomputation {

	public static final String partitionKeyword = "PARTITION";

	public PartitionType partitionType;
	public Metric m;
	public String auxDir;
	public String inputDir;
	public int partitionCount;
	public int run;

	protected Sleeper sleeper;

	protected String[] sourceMetrics;

	public Collation(String name, MetricType metricType, Parameter[] p,
			PartitionType partitionType, Metric m, String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper,
			String[] sourceMetrics) {
		super(name, metricType, p);
		this.partitionType = partitionType;
		this.m = m;
		this.auxDir = auxDir;
		this.inputDir = inputDir;
		this.partitionCount = partitionCount;
		this.run = run;
		this.sleeper = sleeper;
		this.sourceMetrics = sourceMetrics;
	}

	public Collation(String name, MetricType metricType,
			PartitionType partitionType, Metric m, String auxDir,
			String inputDir, int partitionCount, int run, Sleeper sleeper,
			String[] sourceMetrics) {
		this(name, metricType, new Parameter[0], partitionType, m, auxDir,
				inputDir, partitionCount, run, sleeper, sourceMetrics);
	}

	@Override
	public boolean recompute() {
		return this.collate(this.readCollationData());
	}

	@SuppressWarnings("rawtypes")
	protected AuxData aux = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected CollationData readCollationData() {
		System.out.println("\n\nREADING WORKER DATA....");
		this.sleeper.reset();
		BatchData[] bd = new BatchData[this.partitionCount];
		AuxData aux = null;
		while (!this.sleeper.isTimedOut()) {
			boolean missing = false;
			for (int i = 0; i < bd.length; i++) {
				if (bd[i] != null) {
					continue;
				}
				try {
					String batchDir = inputDir
							.replace(partitionKeyword, "" + i)
							+ "run."
							+ run
							+ "/batch." + this.g.getTimestamp() + "/";
					String batchZip = inputDir
							.replace(partitionKeyword, "" + i)
							+ "run."
							+ run
							+ "/batch." + this.g.getTimestamp() + ".zip";

					if (Config.get("GENERATION_AS_ZIP").equals("batches")
							&& (new File(batchZip)).exists()) {
						bd[i] = BatchData.readIntelligent(batchDir,
								this.g.getTimestamp(),
								BatchReadMode.readAllValues);
						System.out.println("read " + this.g.getTimestamp()
								+ " for worker " + i + " as zip");
					} else if (Config.get("GENERATION_AS_ZIP").equals("none")
							&& (new File(batchDir)).exists()) {
						bd[i] = BatchData.read(batchDir, this.g.getTimestamp(),
								BatchReadMode.readAllValues);
						// bd[i] = BatchData.readIntelligent(
						// inputDir.replace(partitionKeyword, "" + i)
						// + "run." + run + "/batch."
						// + this.g.getTimestamp() + "/",
						// this.g.getTimestamp(),
						// BatchReadMode.readAllValues);
						System.out.println("read " + this.g.getTimestamp()
								+ " for worker " + i + " as dir");
					} else {
						missing = true;
						bd[i] = null;
						System.out.println("not existing @ "
								+ Config.get("GENERATION_AS_ZIP") + " zip: "
								+ (new File(batchZip)).exists() + " dir: "
								+ (new File(batchDir)).exists());
						System.out.println(batchZip + " ----- " + batchDir);
					}
				} catch (Exception e) {
					missing = true;
					bd[i] = null;
					System.out.println("exception...");
					e.printStackTrace();
				}
			}
			if (aux == null) {
				try {
					if (this.aux == null) {
						this.aux = AuxData.read(
								g.getGraphDatastructures(),
								this.partitionCount,
								auxDir,
								g.getTimestamp()
										+ AuxData.getSuffix(partitionType,
												AuxWriteType.Init));
						aux = this.aux;
						System.out.println("read aux from "
								+ auxDir
								+ g.getTimestamp()
								+ AuxData.getSuffix(partitionType,
										AuxWriteType.Init));
					} else {
						AuxData auxAdd = AuxData.read(
								g.getGraphDatastructures(),
								this.partitionCount,
								auxDir,
								g.getTimestamp()
										+ AuxData.getSuffix(partitionType,
												AuxWriteType.Add));
						AuxData auxRemove = AuxData.read(
								g.getGraphDatastructures(),
								this.partitionCount,
								auxDir,
								g.getTimestamp()
										+ AuxData.getSuffix(partitionType,
												AuxWriteType.Remove));
						this.aux.add(auxAdd);
						this.aux.remove(auxRemove);
						aux = this.aux;
						System.out.println("read aux add/remove from " + auxDir
								+ g.getTimestamp());
					}
				} catch (Exception e) {
					missing = true;
					aux = null;
					System.out.println("aux reading exception:");
					// e.printStackTrace();
				}
			}
			if (!missing) {
				return new CollationData(bd, aux);
			}
			this.sleeper.sleep();
		}
		throw new IllegalStateException(
				"could not read (all) worker data from " + inputDir);
	}

	protected Iterable<MetricData> getSources(CollationData cd) {
		ArrayList<MetricData> mds = new ArrayList<MetricData>(cd.bd.length);
		for (BatchData bd : cd.bd) {
			for (String name : this.sourceMetrics) {
				if (bd.getMetrics().getNames().contains(name)) {
					mds.add(bd.getMetrics().get(name));
					break;
				}
			}
		}
		return mds;
	}

	public abstract boolean collate(CollationData cd);

	@Override
	public Value[] getValues() {
		return this.m.getValues();
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return this.m.getDistributions();
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return this.m.getNodeValueLists();
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return this.m.getNodeNodeValueLists();
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return this.m.isComparableTo(m);
	}

	@Override
	public boolean equals(IMetric m) {
		return this.m.equals(m);
	}

	@Override
	public boolean isApplicable(Graph g) {
		return this.m.isApplicable(g);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return this.m.isApplicable(b);
	}

}
