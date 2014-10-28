package dna.metrics.similarityMeasures.overlap;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;

/**
 * The class implements the changes of {@link UndirectedNode}s and unweighted
 * {@link UndirectedEdge}s by recompute the overlap similarity measure.
 * 
 * @see OverlapUndirected
 */
public class OverlapUndirectedR extends OverlapUndirected implements
		IRecomputation {

	/**
	 * Initializes {@link OverlapUndirectedR}.
	 */
	public OverlapUndirectedR() {
		super("OverlapUndirectedR");
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
