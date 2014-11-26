package dna.metrics.similarityMeasures.overlap;

import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;

/**
 * {@link IRecomputation} of {@link Overlap}.
 */
public class OverlapR extends Overlap implements IRecomputation {

	/**
	 * Initializes {@link OverlapR}. Implicitly sets degree type for directed
	 * graphs to outdegree and ignores wedge weights (if any).
	 */
	public OverlapR() {
		super("OverlapR");
	}

	/**
	 * Initializes {@link OverlapR}.
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
	public OverlapR(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super("OverlapR", directedDegreeType, edgeWeightType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}

	@Override
	protected void update(Node node1, Node node2) {
		// only implemented in update-class
	}
}
