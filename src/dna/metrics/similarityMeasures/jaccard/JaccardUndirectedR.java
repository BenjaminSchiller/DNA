package dna.metrics.similarityMeasures.jaccard;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;

/**
 * The class implements the changes of {@link UndirectedNode}s and unweighted
 * {@link UndirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardUndirected
 */
public class JaccardUndirectedR extends JaccardUndirected implements IRecomputation {

	/**
	 * Initializes {@link JaccardUndirectedR}.
	 */
	public JaccardUndirectedR() {
		super("JaccardUndirectedR");
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
