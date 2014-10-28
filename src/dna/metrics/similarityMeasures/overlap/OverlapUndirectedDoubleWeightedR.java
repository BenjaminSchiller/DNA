package dna.metrics.similarityMeasures.overlap;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by recompute the overlap similarity measure.
 * 
 * @see OverlapUndirectedDoubleWeighted
 */
public class OverlapUndirectedDoubleWeightedR extends
		OverlapUndirectedDoubleWeighted implements IRecomputation {

	/**
	 * Initializes {@link OverlapUndirectedDoubleWeightedR}
	 */
	public OverlapUndirectedDoubleWeightedR() {
		super("OverlapUndirectedDoubleWeightedR");
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}
}
