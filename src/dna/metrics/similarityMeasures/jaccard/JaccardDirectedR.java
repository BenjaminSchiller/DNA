package dna.metrics.similarityMeasures.jaccard;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardDirected
 */
public class JaccardDirectedR extends JaccardDirected implements IRecomputation {

	/**
	 * Initializes {@link JaccardDirectedR}.
	 */
	public JaccardDirectedR() {
		super("JacardDirectedR");
	}

	/**
	 * Initializes {@link JaccardDirectedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public JaccardDirectedR(Parameter directedDegreeType) {
		super("JacardDirectedR", directedDegreeType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
