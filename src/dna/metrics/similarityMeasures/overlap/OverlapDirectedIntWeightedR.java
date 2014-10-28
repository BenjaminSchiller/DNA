package dna.metrics.similarityMeasures.overlap;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the overlap similarity measure.
 * 
 * @see OverlapDirectedIntWeighted
 */
public class OverlapDirectedIntWeightedR extends OverlapDirectedIntWeighted
		implements IRecomputation {

	/**
	 * Initializes {@link OverlapDirectedIntWeightedR}.
	 */
	public OverlapDirectedIntWeightedR() {
		super("OverlapDirectedIntWeightedR");
	}

	/**
	 * Initializes {@link OverlapDirectedIntWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public OverlapDirectedIntWeightedR(Parameter directedDegreeType) {
		super("OverlapDirectedIntWeightedR", directedDegreeType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
