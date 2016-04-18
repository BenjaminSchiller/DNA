package dna.metrics.paths.weighted.doubleWeighted;

import dna.graph.Graph;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.metrics.IMetric;
import dna.metrics.paths.AllPairsShortestPaths;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;

public abstract class DoubleWeightedAllPairsShortestPaths extends
		AllPairsShortestPaths {

	public DoubleWeightedAllPairsShortestPaths(String name,
			MetricType metricType, Parameter... p) {
		super(name, metricType, p);
	}

	protected double characteristicPathLength;

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof DoubleWeightedAllPairsShortestPaths;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& g.getGraphDatastructures().isEdgeWeightType(
						DoubleWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& b.getGraphDatastructures().isEdgeWeightType(
						DoubleWeight.class);
	}

	@Override
	public double getCharacteristicPathLength() {
		return this.characteristicPathLength;
	}

}
