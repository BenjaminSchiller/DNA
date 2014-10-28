package dna.metrics.similarityMeasures.dice;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the dice similarity measure.
 * 
 * @see DiceDirectedIntWeighted
 */
public class DiceDirectedIntWeightedR extends DiceDirectedIntWeighted implements
		IRecomputation {

	/**
	 * Initializes {@link DiceDirectedIntWeightedR}.
	 */
	public DiceDirectedIntWeightedR() {
		super("DiceDirectedIntWeightedR");
	}

	/**
	 * Initializes {@link DiceDirectedIntWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public DiceDirectedIntWeightedR(Parameter directedDegreeType) {
		super("DiceDirectedIntWeightedR", directedDegreeType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}
}
