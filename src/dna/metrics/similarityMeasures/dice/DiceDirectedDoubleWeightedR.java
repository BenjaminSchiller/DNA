package dna.metrics.similarityMeasures.dice;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the dice similarity measure.
 * 
 * @see DiceDirectedDoubleWeighted
 */
public class DiceDirectedDoubleWeightedR extends DiceDirectedDoubleWeighted implements IRecomputation {

	/**
	 * Initializes {@link DiceDirectedDoubleWeightedR}.
	 */
	public DiceDirectedDoubleWeightedR() {
		super("DiceDirectedDoubleWeightedR");
	}

	/**
	 * Initializes {@link DiceDirectedDoubleWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public DiceDirectedDoubleWeightedR(Parameter directedDegreeType) {
		super("DiceDirectedDoubleWeightedR", directedDegreeType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
