package dna.metrics.assortativity;

import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * {@link ApplicationType#Recomputation} of {@link Assortativity}.
 */
public class AssortativityR extends Assortativity {

	/**
	 * Initializes {@link AssortativityR}. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 */
	public AssortativityR() {
		super("AssortativityR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link AssortativityR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 */
	public AssortativityR(Parameter directedDegreeType) {
		super("AssortativityR", ApplicationType.Recomputation,
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
