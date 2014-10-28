package dna.metrics.similarityMeasures.jaccard;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardUndirectedIntWeighted
 */
public class JaccardUndirectedIntWeightedR extends JaccardUndirectedIntWeighted
		implements IRecomputation {

	/**
	 * Initializes {@link JaccardUndirectedIntWeightedR}
	 */
	public JaccardUndirectedIntWeightedR() {
		super("JaccardUndirectedIntWeightedR");
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}
}
