package dna.metrics.similarityMeasures.dice;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;


/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by recompute the dice similarity measure.
 * 
 * @see DiceDirectedDoubleWeighted
 */
public class DiceDirectedDoubleWeightedR extends DiceDirectedDoubleWeighted {

	/**
	 * Initializes {@link DiceDirectedDoubleWeightedR}.
	 */
	public DiceDirectedDoubleWeightedR() {
		super("DiceDirectedDoubleWeightedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link DiceDirectedDoubleWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public DiceDirectedDoubleWeightedR(Parameter directedDegreeType) {
		super("DiceDirectedDoubleWeightedR", ApplicationType.Recomputation,
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
