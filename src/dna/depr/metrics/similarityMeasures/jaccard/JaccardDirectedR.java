package dna.depr.metrics.similarityMeasures.jaccard;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by recompute the jaccard similarity measure.
 * 
 * @see JaccardDirected
 */
public class JaccardDirectedR extends JaccardDirected {

	/**
	 * Initializes {@link JaccardDirectedR}.
	 */
	public JaccardDirectedR() {
		super("JacardDirectedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link JaccardDirectedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public JaccardDirectedR(Parameter directedDegreeType) {
		super("JacardDirectedR", ApplicationType.Recomputation,
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
