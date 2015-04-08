package dna.metrics.paths;

import dna.graph.Graph;
import dna.graph.weights.Double3dWeight;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.IMetric;
import dna.updates.batch.Batch;
/**
 * 
 * Adaption of IntWeightedAllPairsShortestPath
 * 
 * using NodeWeights and DoubleValues
 * 
 * @author barracuda317 (Maurice Wendt)
 * @date 25.10.2014
 */
public abstract class DoubleWeightedAllPairsShortestPaths extends
		AllPairsShortestPathsDouble {

	public DoubleWeightedAllPairsShortestPaths(String name,double binsize) {
		super(name,binsize);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof DoubleWeightedAllPairsShortestPaths;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& g.getGraphDatastructures().isNodeWeightType(Double3dWeight.class); //TODO fragt den Z-Wert von Double3dWeight ab
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(IWeightedNode.class)
				&& b.getGraphDatastructures().isNodeWeightType(Double3dWeight.class);  //TODO fragt den Z-Wert von Double3dWeight ab
	}

}
