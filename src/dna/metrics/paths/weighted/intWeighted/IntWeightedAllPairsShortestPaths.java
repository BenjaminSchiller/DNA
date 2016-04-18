package dna.metrics.paths.weighted.intWeighted;

import dna.graph.Graph;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.intW.IntWeight;
import dna.metrics.IMetric;
import dna.metrics.paths.AllPairsShortestPaths;
import dna.updates.batch.Batch;

public abstract class IntWeightedAllPairsShortestPaths extends
		AllPairsShortestPaths {

	public IntWeightedAllPairsShortestPaths(String name, MetricType metricType) {
		super(name, metricType);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof IntWeightedAllPairsShortestPaths;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& g.getGraphDatastructures().isEdgeWeightType(IntWeight.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isEdgeType(IWeightedEdge.class)
				&& b.getGraphDatastructures().isEdgeWeightType(IntWeight.class);
	}

	@Override
	public double getCharacteristicPathLength() {
		return this.apsp.computeAverage();
	}

}
