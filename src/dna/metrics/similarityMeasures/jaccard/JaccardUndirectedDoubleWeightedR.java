package dna.metrics.similarityMeasures.jaccard;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardUndirectedDoubleWeighted
 */
public class JaccardUndirectedDoubleWeightedR extends
		JaccardUndirectedDoubleWeighted implements IRecomputation {

	/**
	 * Initializes {@link JaccardUndirectedDoubleWeightedR}
	 */
	public JaccardUndirectedDoubleWeightedR() {
		super("JaccardUndirectedDoubleWeightedR");
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
