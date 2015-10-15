package dna.metrics.assortativity;

import dna.metrics.algorithms.IRecomputation;

/**
 * {@link IRecomputation} of {@link Assortativity}.
 */
public class AssortativityHH extends AssortativityH implements IRecomputation {

	/**
	 * Initializes {@link AssortativityR}. Implicitly sets degree type for
	 * directed graphs to outdegree and ignores wedge weights (if any).
	 */
	public AssortativityHH() {
		super("AssortativityHH");
	}

	/**
	 * Initializes {@link AssortativityR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 * @param edgeWeightType
	 *            <i>weighted</i> or <i>unweighted</i>, determining whether to
	 *            use edge weights in weighted graphs or not. Will be ignored
	 *            for unweighted graphs.
	 */
	public AssortativityHH(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super("AssortativityHH", directedDegreeType, edgeWeightType);
	}

	@Override
	public boolean recompute() {
		this.totalEdgeWeight = 20.0;

		this.sum1 = 20.0;
		this.sum2 = 20.0;
		this.sum3 = 20.0;

		this.r = 20.0;

		return this.compute();
	}

}
