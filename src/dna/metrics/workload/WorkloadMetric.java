package dna.metrics.workload;

import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.Timer;

/**
 * 
 * this metric executes a list of workloads (each is a list of operations) in
 * round robin fashion and record the runtime of this execution. a workload is
 * executed for workload.times batches before the next workload is used. after
 * the last workload is executed for workload.times batches, the process starts
 * at the first workload again. for each workload, the execution time of the
 * created workload is recorded as well as the time needed for initialization
 * (e.g., drawing random elements).
 * 
 * @author benni
 * 
 */
public class WorkloadMetric extends Metric implements IRecomputation {

	public Workload[] workloads;

	private int round;

	private int currentIndex;

	private long workloadDuration;
	private long initDuration;

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
		System.out.println("EXECUTING WORKLOAD: "
				+ this.workloads[this.currentIndex] + " on "
				+ this.g.getNodes().getClass().getSimpleName() + " / "
				+ this.g.getEdges().getClass().getSimpleName());
		Timer initTimer = new Timer();
		this.workloads[this.currentIndex].init(g);
		initTimer.end();
		Timer workloadTimer = new Timer();
		this.workloads[this.currentIndex].createWorkload(g);
		workloadTimer.end();

		this.initDuration = initTimer.getDutation();
		this.workloadDuration = workloadTimer.getDutation();

		this.round++;
		if ((this.round % this.workloads[this.currentIndex].getRounds()) == 0) {
			this.currentIndex = (this.currentIndex + 1) % this.workloads.length;
			this.round = 0;
		}

		Iterable workloadV = this.g.getNodes();
		Iterable workloadE = this.g.getEdges();

		return true;
	}

	@Override
	public Value[] getValues() {
		Value workloadDuration = new Value("WorkloadDuration",
				this.workloadDuration / 1000000000.0);
		Value initDuration = new Value("InitDuration",
				this.initDuration / 1000000000.0);
		return new Value[] { workloadDuration, initDuration };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[0];
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
