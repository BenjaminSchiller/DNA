package dna.metrics.similarityMeasures.jaccard;

import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardUndirectedIntWeighted
 */
public class JaccardUndirectedIntWeightedR extends JaccardUndirectedIntWeighted {

	/**
	 * Initializes {@link JaccardUndirectedIntWeightedR}
	 */
	public JaccardUndirectedIntWeightedR() {
		super("JaccardUndirectedIntWeightedR", ApplicationType.Recomputation);
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
