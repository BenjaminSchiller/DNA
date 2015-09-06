package dna.metrics.workload.operations;

import dna.graph.IGraph;
import dna.metrics.algorithms.IRecomputation;
import dna.metrics.workload.Operation;
import dna.util.parameters.StringParameter;

public class MetricComputation extends Operation {

	private IRecomputation metric;

	public MetricComputation(int times, IRecomputation metric) {
		super("MetricConputation", ListType.V, times, new StringParameter(
				"Metric", metric.getNamePlain()));
		this.metric = metric;
	}

	@Override
	protected void createWorkloadV(IGraph g) {
		this.metric.setGraph(g);
		this.metric.reset();
		this.metric.recompute();
	}

}
