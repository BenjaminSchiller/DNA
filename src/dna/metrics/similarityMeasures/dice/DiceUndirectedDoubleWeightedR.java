package dna.metrics.similarityMeasures.dice;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by recompute the dice similarity measure.
 * 
 * @see DiceUndirectedDoubleWeighted
 */
public class DiceUndirectedDoubleWeightedR extends DiceUndirectedDoubleWeighted
		implements IRecomputation {

	/**
	 * Initializes {@link DiceUndirectedDoubleWeightedR}
	 */
	public DiceUndirectedDoubleWeightedR() {
		super("DiceUndirectedDoubleWeightedR");
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
