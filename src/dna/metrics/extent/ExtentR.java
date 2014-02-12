package dna.metrics.extent;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.updates.walkingAlgorithms.WalkingAlgorithm;
import dna.util.parameters.Parameter;

/**
 * @author Benedict
 * 
 */
public class ExtentR extends Metric {

	private WalkingAlgorithm algorithm;

	/**
	 * @param name
	 * @param type
	 * @param metricType
	 * @param p
	 */
	public ExtentR(String name, WalkingAlgorithm algorithm) {
		super(name, ApplicationType.Recomputation, MetricType.exact);
		this.algorithm = algorithm;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		return false;
	}

	@Override
	public void init_() {

	}

	@Override
	public void reset_() {

	}

	@Override
	public Value[] getValues() {
		return null;
	}

	@Override
	public Distribution[] getDistributions() {
		return null;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return null;
	}

	@Override
	public boolean equals(Metric m) {
		return false;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		
		return false;
	}

}
