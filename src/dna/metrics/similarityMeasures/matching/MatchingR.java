package dna.metrics.similarityMeasures.matching;

import dna.metrics.algorithms.IRecomputation;

/**
 * {@link IRecomputation} of {@link Matching}.
 */
public class MatchingR extends Matching implements IRecomputation {

	/**
	 * Initializes {@link MatchingR}. Implicitly sets degree type for directed
	 * graphs to outdegree and ignores wedge weights (if any).
	 */
	public MatchingR() {
		super("MatchingR");
	}

	/**
	 * Initializes {@link MatchingR}.
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
	public MatchingR(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super("MatchingR", directedDegreeType, edgeWeightType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}
}
