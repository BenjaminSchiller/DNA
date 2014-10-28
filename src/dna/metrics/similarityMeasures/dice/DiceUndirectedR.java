package dna.metrics.similarityMeasures.dice;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IRecomputation;

/**
 * The class implements the changes of {@link UndirectedNode}s and unweighted
 * {@link UndirectedEdge}s by recompute the dice similarity measure.
 * 
 * @see DiceUndirected
 */
public class DiceUndirectedR extends DiceUndirected implements IRecomputation {
	/**
	 * Initializes {@link DiceUndirectedR}.
	 */
	public DiceUndirectedR() {
		super("DiceUndirectedR");
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

}
