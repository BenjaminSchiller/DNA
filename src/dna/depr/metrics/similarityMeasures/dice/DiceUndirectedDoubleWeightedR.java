package dna.depr.metrics.similarityMeasures.dice;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by recompute the dice similarity measure.
 * 
 * @see DiceUndirectedDoubleWeighted
 */
public class DiceUndirectedDoubleWeightedR extends DiceUndirectedDoubleWeighted {

	/**
	 * Initializes {@link DiceUndirectedDoubleWeightedR}
	 */
	public DiceUndirectedDoubleWeightedR() {
		super("DiceUndirectedDoubleWeightedR", ApplicationType.Recomputation);
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}
}
