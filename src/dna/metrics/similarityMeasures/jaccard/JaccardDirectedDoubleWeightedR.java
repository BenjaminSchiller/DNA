package dna.metrics.similarityMeasures.jaccard;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardUndirectedDoubleWeighted
 */
public class JaccardDirectedDoubleWeightedR extends
		JaccardDirectedDoubleWeighted implements IRecomputation {

	/**
	 * Initializes {@link JaccardDirectedDoubleWeightedR}.
	 */
	public JaccardDirectedDoubleWeightedR() {
		super("JaccardDirectedDoubleWeightedR");
	}

	/**
	 * Initializes {@link JaccardDirectedDoubleWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public JaccardDirectedDoubleWeightedR(Parameter p) {
		super("JaccardDirectedDoubleWeightedR", p);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
