package dna.metrics.similarityMeasures.overlap;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the overlap similarity measure.
 * 
 * @see OverlapDirectedDoubleWeighted
 */
public class OverlapDirectedDoubleWeightedR extends
		OverlapDirectedDoubleWeighted implements IRecomputation {

	/**
	 * Initializes {@link OverlapDirectedDoubleWeightedR}. Implicitly sets
	 * degree type for directed graphs to outdegree.
	 */
	public OverlapDirectedDoubleWeightedR() {
		super("OverlapDirectedDoubleWeightedR");
	}

	/**
	 * Initializes {@link OverlapDirectedDoubleWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public OverlapDirectedDoubleWeightedR(Parameter directedDegreeType) {
		super("OverlapDirectedDoubleWeightedR", directedDegreeType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
