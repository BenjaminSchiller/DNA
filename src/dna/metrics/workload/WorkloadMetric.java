package dna.metrics.workload;

import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

/**
 * 
 * this metric executes a list of workloads (each is a list of operations) in
 * round robin fashion and record the runtime of this execution. a workload is
 * executed for workload.times batches before the next workload is used. after
 * the last workload is executed for workload.times batches, the process starts
 * at the first workload again.
 * 
 * @author benni
 *
 */
public class WorkloadMetric extends Metric implements IRecomputation {

	private Workload[] workloads;

	private int round;

	private int currentIndex;

	private long duration;

	public WorkloadMetric(Workload... workloads) {
		super("WorkloadMetric", MetricType.exact);
		this.workloads = workloads;
		this.round = 0;
		this.currentIndex = 0;
	}

	@Override
	public boolean reset() {
		this.round = 0;
		this.currentIndex = 0;
		return true;
	}

	@Override
	public boolean recompute() {
		this.duration = this.workloads[this.currentIndex].createWorkload(g);

		this.round++;
		if ((this.round % this.workloads[this.currentIndex].getRounds()) == 0) {
			this.currentIndex = (this.currentIndex + 1) % this.workloads.length;
			this.round = 0;
		}

		return true;
	}

	@Override
	public Value[] getValues() {
		Value v = new Value("Duration", this.duration);
		return new Value[] { v };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[0];
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[0];
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof WorkloadMetric;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof WorkloadMetric)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

}
