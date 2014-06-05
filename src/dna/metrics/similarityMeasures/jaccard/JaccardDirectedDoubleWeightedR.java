package dna.metrics.similarityMeasures.jaccard;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardUndirectedDoubleWeighted
 */
public class JaccardDirectedDoubleWeightedR extends
		JaccardDirectedDoubleWeighted {

	/**
	 * Initializes {@link JaccardDirectedDoubleWeightedR}.
	 */
	public JaccardDirectedDoubleWeightedR() {
		super("JaccardDirectedDoubleWeightedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link JaccardDirectedDoubleWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public JaccardDirectedDoubleWeightedR(Parameter p) {
		super("JaccardDirectedDoubleWeightedR", ApplicationType.Recomputation,
				p);
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
