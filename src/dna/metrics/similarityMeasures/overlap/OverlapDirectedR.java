package dna.metrics.similarityMeasures.overlap;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IRecomputation;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by recompute the overlap similarity measure.
 * 
 * @see OverlapDirected
 */
public class OverlapDirectedR extends OverlapDirected implements IRecomputation {

	/**
	 * Initializes {@link OverlapDirectedR}.
	 */
	public OverlapDirectedR() {
		super("OverlapDirectedR");
	}

	/**
	 * Initializes {@link OverlapDirectedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public OverlapDirectedR(Parameter directedDegreeType) {
		super("OverlapDirectedR", directedDegreeType);
	}

	@Override
	public boolean recompute() {
		reset_();
		return compute();
	}
}
