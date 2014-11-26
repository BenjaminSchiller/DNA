package dna.metrics.similarityMeasures.jaccard;

import dna.graph.nodes.Node;
import dna.metrics.algorithms.IRecomputation;

/**
 * {@link IRecomputation} of {@link Jaccard}.
 */
public class JaccardR extends Jaccard implements IRecomputation {

	/**
	 * Initializes {@link JaccardR}. Implicitly sets degree type for directed
	 * graphs to outdegree and ignores edge weights (if any).
	 */
	public JaccardR() {
		super("JaccardR");
	}

	/**
	 * Initializes {@link JaccardR}.
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
	public JaccardR(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super("JaccardR", directedDegreeType, edgeWeightType);
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
