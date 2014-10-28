package dna.metrics.similarityMeasures.dice;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by recompute the dice similarity measure.
 * 
 * @see DiceDirected
 */
public class DiceDirectedR extends DiceDirected implements IRecomputation {

	/**
	 * Initializes {@link DiceDirectedR}.
	 */
	public DiceDirectedR() {
		super("DiceDirectedR");
	}

	/**
	 * Initializes {@link DiceDirectedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public DiceDirectedR(Parameter directedDegreeType) {
		super("DiceDirectedR", directedDegreeType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
