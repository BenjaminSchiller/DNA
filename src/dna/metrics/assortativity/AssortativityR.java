package dna.metrics.assortativity;

import dna.metrics.algorithms.IRecomputation;

/**
 * {@link IRecomputation} of {@link Assortativity}.
 */
public class AssortativityR extends Assortativity implements IRecomputation {

	/**
	 * Initializes {@link AssortativityR}. Implicitly sets degree type for
	 * directed graphs to outdegree and ignores wedge weights (if any).
	 */
	public AssortativityR() {
		super("AssortativityR");
	}

	public AssortativityR(String[] nodeTypes) {
		super("AssortativityR", nodeTypes);
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
	public AssortativityR(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super("AssortativityR", directedDegreeType, edgeWeightType);
	}

	public AssortativityR(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType, String[] nodeTypes) {
		super("AssortativityR", directedDegreeType, edgeWeightType, nodeTypes);
	}

	@Override
	public boolean recompute() {
		this.totalEdgeWeight = 0.0;

		this.sum1 = 0.0;
		this.sum2 = 0.0;
		this.sum3 = 0.0;

		this.r = 0.0;

		return this.compute();
	}

}
