package dna.depr.metrics.similarityMeasures.matching;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * The class implements the changes of {@link UndirectedNode}s and unweighted
 * {@link UndirectedEdge}s by recompute the matching similarity measure.
 * 
 * @see MatchingUndirected
 */
public class MatchingUndirectedR extends MatchingUndirected {

	/**
	 * Initializes {@link MatchingUndirectedR}.
	 */
	public MatchingUndirectedR() {
		super("MatchingUndirectedR", ApplicationType.Recomputation);
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
