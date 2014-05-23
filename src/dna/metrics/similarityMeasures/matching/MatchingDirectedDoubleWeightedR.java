package dna.metrics.similarityMeasures.matching;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the matching similarity measure.
 * 
 * @see MatchingDirectedDoubleWeighted
 */
public class MatchingDirectedDoubleWeightedR extends
		MatchingDirectedDoubleWeighted {

	/**
	 * Initializes {@link MatchingDirectedDoubleWeightedR}. Implicitly sets
	 * degree type for directed graphs to outdegree.
	 */
	public MatchingDirectedDoubleWeightedR() {
		super("MatchingDirectedDoubleWeightedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link MatchingDirectedDoubleWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public MatchingDirectedDoubleWeightedR(Parameter directedDegreeType) {
		super("MatchingDirectedDoubleWeightedR", ApplicationType.Recomputation,
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
