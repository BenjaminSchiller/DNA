package dna.metrics.parallelization;

import java.util.HashMap;

import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IAfterBatch;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IAfterNA;
import dna.metrics.algorithms.IAfterUpdates;
import dna.metrics.algorithms.IBeforeBatch;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeER;
import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.metrics.algorithms.IDynamicAlgorithm;
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
import dna.util.Timer;

public class ParallelMetric extends Metric implements IRecomputation,
		IBeforeUpdates, IAfterUpdates, IBeforeBatch, IAfterBatch {
	protected PartitioningScheme partitioningScheme;
	protected NodeAssignment nodeAssignment;
	protected Metric metric;
	protected Collation collation;

	protected Metric[] metrics;

	protected long[] partitionRuntimes;
	protected long maxRuntime;
	protected long collationRuntime;
	protected long totalRuntime;
	protected long estimatedRuntime;

	public ParallelMetric(String name, PartitioningScheme partitioningScheme,
			NodeAssignment nodeAssignment, Metric metric, Collation collation) {
		super(name);
		this.partitioningScheme = partitioningScheme;
		this.nodeAssignment = nodeAssignment;
		this.metric = metric;
		this.collation = collation;
		this.collation.setMetric(metric);
		this.eas = new HashMap<Partition, EdgeAddition>();
		this.ers = new HashMap<Partition, EdgeRemoval>();
	}

	@Override
	public Value[] getValues() {
		Value runtimeEstimated = new Value("runtimeEstimated",
				(double) this.estimatedRuntime);
		Value runtimeMax = new Value("runtimeMax", (double) this.maxRuntime);
		Value runtimeCollation = new Value("runtimeCollation",
				(double) this.collationRuntime);
		Value runtimeTotal = new Value("runtimeTotal",
				(double) this.totalRuntime);
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
	}

	@Override
	public boolean recompute() {
		if (!(this.metric instanceof IRecomputation)) {
			// System.out.println("not recomputing " + this.metric);
			return true;
		}
		// System.out.println("RECOMPUTE " + this.toString() + " for "
		// + this.partitioningScheme.getPartitions().length
		// + " partitions");
		this.partitionRuntimes = new long[this.partitioningScheme
				.getPartitions().length];
		boolean success = true;
		int index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			p.getTimer().reset();
			p.getTimer().restart();
			success &= ((IRecomputation) p.getMetric()).recompute();
			p.getTimer().end();
			this.partitionRuntimes[index++] = p.getTimer().getDutation();
		}
		Timer t = new Timer();
		this.collation.getTimer().restart();
		this.collation.collate(this.g, this.partitioningScheme.getPartitions());
		this.collation.getTimer().end();
		t.end();
		this.collationRuntime = t.getDutation();
		this.maxRuntime = ArrayUtils.max(this.partitionRuntimes);
		this.estimatedRuntime = this.collationRuntime
				+ ArrayUtils.max(this.partitionRuntimes);
		this.totalRuntime = this.collationRuntime
				+ ArrayUtils.sum(this.partitionRuntimes);
		return success;
	}

	@Override
	public boolean init() {
		if (!(this.metric instanceof IDynamicAlgorithm)) {
			return this.recompute();
		}

		this.partitionRuntimes = new long[this.partitioningScheme
				.getPartitions().length];
		boolean success = true;
		int index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			p.getTimer().reset();
			success &= ((IDynamicAlgorithm) p.getMetric()).init();
			p.getTimer().end();
			this.partitionRuntimes[index++] = p.getTimer().getDutation();
		}
		this.collation.getTimer().reset();
		this.collation.collate(this.g, this.partitioningScheme.getPartitions());
		this.collation.getTimer().end();

		this.maxRuntime = ArrayUtils.max(this.partitionRuntimes);
		this.estimatedRuntime = this.collationRuntime
				+ ArrayUtils.max(this.partitionRuntimes);
		this.totalRuntime = this.collationRuntime
				+ ArrayUtils.sum(this.partitionRuntimes);
		return success;
	}

	protected Batch currentBatch;

	protected Partition nodeAddedTo = null;

	/*
	 * BATCH
	 */

	@Override
	public boolean applyBeforeBatch(Batch b) {
		this.currentBatch = b;

		// reset collation & partition timers
		this.collation.getTimer().reset();
		for (Partition p : this.partitioningScheme.partitions) {
			p.getTimer().reset();
			// System.out.println(">>> " + p);
		}

		if (!(this.metric instanceof IBeforeBatch)) {
			return true;
		}

		// apply partition metrics before batch
		boolean success = true;
		for (Partition p : this.partitioningScheme.partitions) {
			throw new IllegalStateException(
					"parallelization of batch-based application (BEFORE) not implemented yet");
			// p.getTimer().restart();
			// success &= ((IBeforeBatch) p.getMetric()).applyBeforeBatch(b);
			// p.getTimer().end();
		}
		return success;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		boolean success = true;

		// apply partition metrics after batch
		if ((this.metric instanceof IAfterBatch)) {
			for (Partition p : this.partitioningScheme.partitions) {
				throw new IllegalStateException(
						"parallelization of batch-based application (AFTER) not implemented yet");
				// p.getTimer().restart();
				// success &= ((IAfterBatch) p.getMetric()).applyAfterBatch(b);
				// p.getTimer().end();
			}
		}

		// collate results
		if (!(this.metric instanceof IRecomputation)) {
			this.collation.getTimer().restart();
			this.collation.collate(this.g,
					this.partitioningScheme.getPartitions());
			this.collation.getTimer().end();
		}

		return success;
	}

	/*
	 * NA
	 */

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		// determining partition to add node to
		this.nodeAddedTo = this.nodeAssignment.assignNode(
				this.partitioningScheme, na, this.currentBatch);

		if (!(this.metric instanceof IBeforeNA)) {
			return true;
		}

		// NA application BEFORE
		this.nodeAddedTo.getTimer().restart();
		boolean success = ((IBeforeNA) this.nodeAddedTo.getMetric())
				.applyBeforeUpdate(na);
		this.nodeAddedTo.getTimer().end();

		return success;
	}

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		// NA propagation
		boolean success = this.nodeAddedTo.propagate(na);

		if (!(this.metric instanceof IAfterNA)) {
			return success;
		}

		// NA applicatin AFTER
		this.nodeAddedTo.getTimer().restart();
		success &= ((IAfterNA) this.nodeAddedTo.getMetric())
				.applyAfterUpdate(na);
		this.nodeAddedTo.getTimer().end();

		return success;
	}

	/*
	 * NR
	 */

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		throw new IllegalStateException("NR not implemented yet");
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		throw new IllegalStateException("NR not implemented yet");
	}

	/*
	 * EA
	 */

	protected HashMap<Partition, EdgeAddition> eas;

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		if (!(this.metric instanceof IBeforeEA)) {
			return true;
		}

		// EA application BEFORE
		boolean success = true;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldApply(ea)) {
				p.getTimer().restart();
				success &= ((IBeforeEA) p.getMetric()).applyBeforeUpdate(p
						.getLocalEA(ea));
				p.getTimer().end();
			}
		}
		return success;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		boolean success = true;

		// EA propagation
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(ea)) {
				p.getTimer().restart();
				success &= p.propagate(ea);
				p.getTimer().end();
			}
		}

		// EA application AFTER
		if (this.metric instanceof IAfterEA) {
			for (Partition p : this.partitioningScheme.getPartitions()) {
				if (p.shouldApply(ea)) {
					p.getTimer().restart();
					success &= ((IAfterEA) p.getMetric()).applyAfterUpdate(p
							.getLocalEA(ea));
					p.getTimer().end();
				}
			}
		}

		// clear local EA
		for (Partition p : this.partitioningScheme.partitions) {
			p.clearLocalEA();
		}

		return success;
	}

	/*
	 * ER
	 */

	protected HashMap<Partition, EdgeRemoval> ers;

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		if (!(this.metric instanceof IBeforeER)) {
			return true;
		}

		// ER application BEFORE
		boolean success = true;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldApply(er)) {
				p.getTimer().restart();
				success &= ((IBeforeER) p.getMetric()).applyBeforeUpdate(p
						.getLocalER(er));
				p.getTimer().end();
			}
		}
		return success;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		boolean success = true;

		// ER propagation
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(er)) {
				p.getTimer().restart();
				success &= p.propagate(er);
				p.getTimer().end();
			}
		}

		// ER application AFTER
		if (this.metric instanceof IAfterER) {
			for (Partition p : this.partitioningScheme.getPartitions()) {
				if (p.shouldApply(er)) {
					p.getTimer().restart();
					success &= ((IAfterER) p.getMetric()).applyAfterUpdate(p
							.getLocalER(er));
					p.getTimer().end();
				}
			}
		}

		// clear local ER
		for (Partition p : this.partitioningScheme.partitions) {
			p.clearLocalER();
		}

		return success;
	}
}
