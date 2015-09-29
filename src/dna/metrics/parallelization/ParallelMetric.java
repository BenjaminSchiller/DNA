package dna.metrics.parallelization;

import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IAfterBatch;
import dna.metrics.algorithms.IAfterUpdates;
import dna.metrics.algorithms.IBeforeBatch;
import dna.metrics.algorithms.IRecomputation;
import dna.metrics.parallelization.collation.Collation;
import dna.metrics.parallelization.partitioning.Partition;
import dna.metrics.parallelization.partitioning.nodeAssignment.NodeAssignment;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme;
import dna.series.data.Value;
import dna.series.data.distributions.Distribution;
import dna.series.data.distributions.DistributionInt;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.ArrayUtils;

public class ParallelMetric extends Metric implements IBeforeBatch,
		IAfterBatch, IAfterUpdates {
	protected PartitioningScheme partitioningScheme;
	protected NodeAssignment nodeAssignment;
	protected Metric metric;
	protected Collation collation;

	protected Metric[] metrics;

	protected long[] partitionRuntimes;

	public ParallelMetric(String name, PartitioningScheme partitioningScheme,
			NodeAssignment nodeAssignment, Metric metric, Collation collation) {
		super(name);
		this.partitioningScheme = partitioningScheme;
		this.nodeAssignment = nodeAssignment;
		this.metric = metric;
		this.collation = collation;
		this.collation.setMetric(metric);
	}

	@Override
	public Value[] getValues() {
		Value runtimeEstimated = new Value(
				"runtimeEstimated",
				(double) (ArrayUtils.max(this.partitionRuntimes) + this.collation
						.getTimer().getDutation()));
		Value runtimeMax = new Value("runtimeMax",
				(double) ArrayUtils.max(this.partitionRuntimes));
		Value runtimeCollation = new Value("runtimeCollation",
				(double) this.collation.getTimer().getDutation());
		Value runtimeTotal = new Value(
				"runtimeTotal",
				(double) (ArrayUtils.sum(this.partitionRuntimes) + this.collation
						.getTimer().getDutation()));
		Value[] runtimes = new Value[] { runtimeEstimated, runtimeMax,
				runtimeCollation, runtimeTotal };

		Value partitions = new Value("partitions",
				this.partitioningScheme.partitions.length);
		Value[] etc = new Value[] { partitions };

		Value[][] values = new Value[this.partitioningScheme.partitions.length][];
		for (int i = 0; i < values.length; i++) {
			values[i] = this.partitioningScheme.partitions[i].getValues();
		}
		Value[] stats = new Value[values[0].length];
		for (int i = 0; i < values[0].length; i++) {
			String name = values[0][i].getName();
			double[] v = new double[values.length];
			for (int j = 0; j < values.length; j++) {
				v[j] = values[j][i].getValue();
			}
			stats[i] = new Value(name + "Sum", ArrayUtils.sum(v));
		}

		Value[] results = this.collation.getValues();

		Value[] v = new Value[runtimes.length + etc.length + stats.length
				+ results.length];
		System.arraycopy(runtimes, 0, v, 0, runtimes.length);
		System.arraycopy(etc, 0, v, runtimes.length, etc.length);
		System.arraycopy(stats, 0, v, runtimes.length + etc.length,
				stats.length);
		System.arraycopy(results, 0, v, runtimes.length + etc.length
				+ stats.length, results.length);

		return v;
	}

	@Override
	public Distribution[] getDistributions() {
		Value[][] values = new Value[this.partitioningScheme.partitions.length][];
		for (int i = 0; i < values.length; i++) {
			values[i] = this.partitioningScheme.partitions[i].getValues();
		}

		DistributionInt[] stats = new DistributionInt[values[0].length];
		for (int i = 0; i < values[0].length; i++) {
			String name = values[0][i].getName();
			int[] v = new int[values.length];
			for (int j = 0; j < values.length; j++) {
				v[j] = (int) values[j][i].getValue();
			}
			stats[i] = new DistributionInt(name, v, 1);
		}

		Distribution[] results = this.collation.getDistributions();

		Distribution[] all = new Distribution[stats.length + results.length];
		System.arraycopy(stats, 0, all, 0, stats.length);
		System.arraycopy(results, 0, all, stats.length, results.length);

		return all;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return this.collation.getNodeValueLists();
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return this.collation.getNodeNodeValueLists();
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		if (m == null) {
			return false;
		} else if (m instanceof ParallelMetric) {
			return this.metric.isComparableTo(((ParallelMetric) m).metric);
		} else {
			return this.metric.isComparableTo(m);
		}
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null) {
			return false;
		} else if (m instanceof ParallelMetric) {
			return this.metric.equals(((ParallelMetric) m).metric);
		} else {
			return this.metric.equals(m);
		}
	}

	@Override
	public boolean isApplicable(Graph g) {
		return this.metric.isApplicable(g);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return this.metric.isApplicable(b);
	}

	@Override
	public void setGraph(Graph g) {
		super.setGraph(g);
		this.partitioningScheme.init(g, this.metric);
		this.partitionRuntimes = new long[this.partitioningScheme.partitions.length];
	}

	@Override
	public boolean init() {
		boolean success = true;

		for (Partition p : this.partitioningScheme.partitions) {
			p.getTimer().restart();
			if (metric instanceof IRecomputation) {
				success &= p.recompute();
			} else {
				success &= p.init();
			}
			p.getTimer().end();
		}

		this.collation.getTimer().restart();
		success &= this.collation.collate(this.g,
				this.partitioningScheme.partitions);
		this.collation.getTimer().end();

		return success;
	}

	protected Batch currentBatch = null;

	@Override
	public boolean applyBeforeBatch(Batch b) {
		this.currentBatch = b;

		for (Partition p : this.partitioningScheme.partitions) {
			p.getTimer().reset();
		}
		this.collation.getTimer().reset();

		return true;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		boolean success = true;

		if (metric instanceof IRecomputation) {
			for (Partition p : this.partitioningScheme.partitions) {
				p.getTimer().restart();
				p.recompute();
				p.getTimer().end();
			}
		}

		this.collation.getTimer().reset();
		success &= this.collation.collate(this.g,
				this.partitioningScheme.partitions);
		this.collation.getTimer().end();

		for (int i = 0; i < this.partitioningScheme.partitions.length; i++) {
			this.partitionRuntimes[i] = this.partitioningScheme.partitions[i]
					.getTimer().getDutation();
		}

		return success;
	}

	/*
	 * NA
	 */

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		Partition p = this.nodeAssignment.assignNode(this.partitioningScheme,
				na, this.currentBatch);
		return p.propagate(na);
	}

	/*
	 * NR
	 */

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		boolean success = true;
		for (Partition p : this.partitioningScheme.partitions) {
			if (p.shouldPropagate(nr)) {
				p.getTimer().restart();
				success &= p.propagate(nr);
				p.getTimer().end();
			}
		}
		return success;
	}

	/*
	 * EA
	 */

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		boolean success = true;
		for (Partition p : this.partitioningScheme.partitions) {
			if (p.shouldPropagate(ea)) {
				p.getTimer().restart();
				success &= p.propagate(ea);
				p.getTimer().end();
			}
		}
		return success;
	}

	/*
	 * ER
	 */

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		boolean success = true;
		for (Partition p : this.partitioningScheme.partitions) {
			if (p.shouldPropagate(er)) {
				p.getTimer().restart();
				success &= p.propagate(er);
				p.getTimer().end();
			}
		}
		return success;
	}
}
