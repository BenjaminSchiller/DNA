package dna.metrics.similarityMeasures.matching;

import dna.graph.generators.zalando.data.EventColumn;
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
	 * @param computeDistributionWithoutMatrixDiagonal
	 *            <i>with_diagonal</i> or <i>without_diagonal</i>, determining
	 *            whether to use the matrix diagonal to compute the
	 *            distributions or not.
	 */
	public MatchingR(
			DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType,
			ComputeDistributionWithoutMatrixDiagonal computeDistributionWithoutMatrixDiagonal) {
		super("MatchingR", directedDegreeType, edgeWeightType,
				computeDistributionWithoutMatrixDiagonal);
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
	 * @param type
	 *            The node types ({@link EventColumn}) for which the matching
	 *            should be calculated.
	 * @param computeDistributionWithoutMatrixDiagonal
	 *            <i>with_diagonal</i> or <i>without_diagonal</i>, determining
	 *            whether to use the matrix diagonal to compute the
	 *            distributions or not.
	 */
	public MatchingR(
			DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType,
			EventColumn[] type,
			ComputeDistributionWithoutMatrixDiagonal computeDistributionWithoutMatrixDiagonal) {
		super("MatchingR", directedDegreeType, edgeWeightType, type,
				computeDistributionWithoutMatrixDiagonal);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}
}
