package dna.metrics.similarityMeasures.matching;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by recompute the matching similarity measure.
 * 
 * @see MatchingDirected
 */
public class MatchingDirectedR extends MatchingDirected {

	/**
	 * Initializes {@link MatchingDirectedR}. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 */
	public MatchingDirectedR() {
		super("MatchingDirectedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link MatchingDirectedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public MatchingDirectedR(Parameter directedDegreeType) {
		super("MatchingDirectedR", ApplicationType.Recomputation,
				directedDegreeType);
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
