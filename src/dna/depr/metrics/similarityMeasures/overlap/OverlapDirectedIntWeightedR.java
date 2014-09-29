package dna.depr.metrics.similarityMeasures.overlap;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the overlap similarity measure.
 * 
 * @see OverlapDirectedIntWeighted
 */
public class OverlapDirectedIntWeightedR extends OverlapDirectedIntWeighted {

	/**
	 * Initializes {@link OverlapDirectedIntWeightedR}.
	 */
	public OverlapDirectedIntWeightedR() {
		super("OverlapDirectedIntWeightedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link OverlapDirectedIntWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public OverlapDirectedIntWeightedR(Parameter directedDegreeType) {
		super("OverlapDirectedIntWeightedR", ApplicationType.Recomputation,
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
