package dna.metrics.similarityMeasures.overlap;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by recompute the overlap similarity measure.
 * 
 * @see OverlapDirected
 */
public class OverlapDirectedR extends OverlapDirected {

	/**
	 * Initializes {@link OverlapDirectedR}.
	 */
	public OverlapDirectedR() {
		super("OverlapDirectedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link OverlapDirectedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public OverlapDirectedR(Parameter directedDegreeType) {
		super("OverlapDirectedR", ApplicationType.Recomputation,
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
