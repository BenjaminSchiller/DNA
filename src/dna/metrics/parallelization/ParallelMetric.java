package dna.metrics.parallelization;

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
import dna.metrics.parallelization.partitioning.NonOverlappingPartition;
import dna.metrics.parallelization.partitioning.OverlappingPartition;
import dna.metrics.parallelization.partitioning.Partition;
import dna.metrics.parallelization.partitioning.nodeAssignment.NodeAssignment;
import dna.metrics.parallelization.partitioning.schemes.PartitioningScheme;
import dna.series.data.Value;
import dna.series.data.distributions.Distribution;
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
	}

	@Override
	public Value[] getValues() {
		int totalNodes = 0;
		int totalEdges = 0;
		int auxiliaryNodes = 0;
		int auxiliaryEdges = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			totalNodes += p.getGraph().getNodeCount();
			totalEdges += p.getGraph().getEdgeCount();
			if (p instanceof OverlappingPartition) {
				OverlappingPartition op = (OverlappingPartition) p;
				auxiliaryNodes += op.getGraph().getNodeCount()
						- op.getNodes().size();
			} else if (p instanceof NonOverlappingPartition) {
				NonOverlappingPartition nop = (NonOverlappingPartition) p;
				auxiliaryEdges += nop.getConnections().size();
			}
		}

		Value[] v1 = this.collation.getValues();
		Value[] v2 = new Value[] {
				new Value("RUNTIME_ESTIMATED", (double) this.estimatedRuntime),
				new Value("RUNTIME_MAX", (double) this.maxRuntime),
				new Value("RUNTIME_COLLATION", (double) this.collationRuntime),
				new Value("RUNTIME_TOTAL", (double) this.totalRuntime),
				new Value("TOTAL_NODES", totalNodes),
				new Value("TOTAL_EDGES", totalEdges),
				new Value("AUXILARY_NODES", auxiliaryNodes),
				new Value("AUXILIARY_EDGES", auxiliaryEdges),
				new Value("PARTITIONS",
						this.partitioningScheme.getPartitions().length) };
		Value[] v = new Value[v1.length + v2.length];
		System.arraycopy(v1, 0, v, 0, v1.length);
		System.arraycopy(v2, 0, v, v1.length, v2.length);
		return v;
	}

	@Override
	public Distribution[] getDistributions() {
		return this.collation.getDistributions();
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
			// System.out.println(">>> " + p.getMetric().getName());
			Timer t = new Timer();
			success &= ((IRecomputation) p.getMetric()).recompute();
			t.end();
			this.partitionRuntimes[index++] = t.getDutation();
			// System.out.println("<<< " + p.getMetric().getName() + " @ " +
			// t.getRuntime());
		}
		Timer t = new Timer();
		this.collation.collate(this.g, this.partitioningScheme.getPartitions());
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
			Timer t = new Timer();
			success &= ((IDynamicAlgorithm) p.getMetric()).init();
			t.end();
			this.partitionRuntimes[index++] = t.getDutation();
		}
		Timer t = new Timer();
		this.collation.collate(this.g, this.partitioningScheme.getPartitions());
		t.end();
		this.collationRuntime = t.getDutation();
		this.maxRuntime = ArrayUtils.max(this.partitionRuntimes);
		this.estimatedRuntime = this.collationRuntime
				+ ArrayUtils.max(this.partitionRuntimes);
		this.totalRuntime = this.collationRuntime
				+ ArrayUtils.sum(this.partitionRuntimes);
		return success;
	}

	private Partition nodeAddedTo = null;

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		if (!(this.metric instanceof IBeforeNA)) {
			return true;
		}
		this.nodeAddedTo = this.nodeAssignment.assignNode(
				this.partitioningScheme, na, this.currentBatch);
		boolean success = ((IBeforeNA) this.nodeAddedTo.getMetric())
				.applyBeforeUpdate(na);
		if (!(this.metric instanceof IAfterNA)) {
			this.nodeAddedTo = null;
		}
		return success;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		throw new IllegalStateException("NR not implemented yet");
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		if (!(this.metric instanceof IBeforeEA)) {
			return true;
		}
		boolean success = true;
		int index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(ea)) {
				Timer t = new Timer();
				success &= ((IBeforeEA) p.getMetric()).applyBeforeUpdate(ea);
				t.end();
				this.partitionRuntimes[index++] += t.getDutation();
			}
		}
		return success;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		if (!(this.metric instanceof IBeforeER)) {
			return true;
		}
		boolean success = true;
		int index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(er)) {
				Timer t = new Timer();
				success &= ((IBeforeER) p.getMetric()).applyBeforeUpdate(er);
				t.end();
				this.partitionRuntimes[index++] += t.getDutation();
			}
		}
		return success;
	}

	protected Batch currentBatch;

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		if (!(this.metric instanceof IBeforeNA)) {
			this.nodeAddedTo = this.nodeAssignment.assignNode(
					this.partitioningScheme, na, this.currentBatch);
		}
		boolean success = true;
		success &= this.nodeAddedTo.propagate(na);
		if (!(this.metric instanceof IAfterNA)) {
			this.nodeAddedTo = null;
			return success;
		}
		success &= ((IAfterNA) this.nodeAddedTo.getMetric())
				.applyAfterUpdate(na);
		this.nodeAddedTo = null;
		return success;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		throw new IllegalStateException("NR not implemented yet");
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		boolean success = true;
		int index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(ea)) {
				Timer t = new Timer();
				success &= p.propagate(ea);
				t.end();
				this.partitionRuntimes[index++] += t.getDutation();
			}
		}
		if (!(this.metric instanceof IAfterEA)) {
			return success;
		}
		index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(ea)) {
				Timer t = new Timer();
				success &= ((IAfterEA) p.getMetric()).applyAfterUpdate(ea);
				t.end();
				this.partitionRuntimes[index++] += t.getDutation();
			}
		}
		System.out.println(success);
		return success;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		boolean success = true;
		int index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(er)) {
				Timer t = new Timer();
				success &= p.propagate(er);
				t.end();
				this.partitionRuntimes[index++] += t.getDutation();
			}
		}
		if (!(this.metric instanceof IAfterER)) {
			return success;
		}
		index = 0;
		for (Partition p : this.partitioningScheme.getPartitions()) {
			if (p.shouldPropagate(er)) {
				Timer t = new Timer();
				success &= ((IAfterER) p.getMetric()).applyAfterUpdate(er);
				t.end();
				this.partitionRuntimes[index++] += t.getDutation();
			}
		}
		return success;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		this.currentBatch = b;
		if ((this.metric instanceof IRecomputation)) {
			return true;
		}
		this.partitionRuntimes = new long[this.partitioningScheme
				.getPartitions().length];
		return true;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		if ((this.metric instanceof IRecomputation)) {
			return true;
		}
		Timer t = new Timer();
		this.collation.collate(this.g, this.partitioningScheme.getPartitions());
		t.end();
		this.collationRuntime = t.getDutation();
		this.maxRuntime = ArrayUtils.max(this.partitionRuntimes);
		this.estimatedRuntime = this.collationRuntime
				+ ArrayUtils.max(this.partitionRuntimes);
		this.totalRuntime = this.collationRuntime
				+ ArrayUtils.sum(this.partitionRuntimes);
		return true;
	}
}
