package dna.parallel.collation;

import java.io.IOException;

import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IRecomputation;
import dna.parallel.partition.Partition;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.data.BatchData;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;

public abstract class Collation<M extends Metric, T extends Partition> extends
		Metric implements IRecomputation {

	public Metric m;
	public String dir;
	public int partitionCount;
	public int run;

	public Collation(String name, MetricType metricType, Parameter[] p,
			Metric m, String dir, int partitionCount, int run) {
		super(name, metricType, p);
		this.m = m;
		this.dir = dir;
		this.partitionCount = partitionCount;
		this.run = run;
	}

	public Collation(String name, MetricType metricType, Metric m, String dir,
			int partitionCount, int run) {
		this(name, metricType, new Parameter[0], m, dir, partitionCount, run);
	}

	@Override
	public boolean recompute() {
		try {
			return this.collate(this.readWorkerData());
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	protected BatchData[] readWorkerData() throws IOException {
		BatchData[] bd = new BatchData[this.partitionCount];
		for (int i = 0; i < bd.length; i++) {
			bd[i] = BatchData.readIntelligent(dir + "worker" + i + "/run."
					+ run + "/batch." + this.g.getTimestamp() + "/",
					this.g.getTimestamp(), BatchReadMode.readAllValues);
		}
		return bd;
	}

	public abstract boolean collate(BatchData[] bd);

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
