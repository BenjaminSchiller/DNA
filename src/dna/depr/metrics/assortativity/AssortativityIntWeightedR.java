package dna.depr.metrics.assortativity;

import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.weights.IntWeight;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * {@link ApplicationType#Recomputation} of {@link AssortativityDoubleWeighted}.
 */
public class AssortativityIntWeightedR extends AssortativityIntWeighted {

	/**
	 * Initializes {@link AssortativityIntWeightedR}. Implicitly sets degree
	 * type for directed graphs to outdegree.
	 */
	public AssortativityIntWeightedR() {
		super("AssortativityIntWeightedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link AssortativityIntWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 */
	public AssortativityIntWeightedR(Parameter directedDegreeType) {
		super("AssortativityWeightedR", ApplicationType.Recomputation,
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

	@Override
	boolean computeForDirectedIntWeightedGraph() {
		DirectedWeightedEdge edge;
		long edgeWeight;
		long srcWeightedDegree, dstWeightedDegree;
		for (IElement iElement : this.g.getEdges()) {
			edge = (DirectedWeightedEdge) iElement;

			edgeWeight = ((IntWeight) edge.getWeight()).getWeight();
			this.totalEdgeWeight += edgeWeight;

			srcWeightedDegree = this.directedIntWeightedDegree(edge.getSrc());
			dstWeightedDegree = this.directedIntWeightedDegree(edge.getDst());

			this.sum1 += edgeWeight * (srcWeightedDegree * dstWeightedDegree);
			this.sum2 += edgeWeight * (srcWeightedDegree + dstWeightedDegree);
			this.sum3 += edgeWeight
					* (srcWeightedDegree * srcWeightedDegree + dstWeightedDegree
							* dstWeightedDegree);
		}

		this.setR();

		return true;
	}

	@Override
	boolean computeForUndirectedIntWeightedGraph() {
		UndirectedWeightedEdge edge;
		long edgeWeight;
		long node1WeightedDegree, node2WeightedDegree;
		for (IElement iElement : this.g.getEdges()) {
			edge = (UndirectedWeightedEdge) iElement;

			edgeWeight = ((IntWeight) edge.getWeight()).getWeight();
			this.totalEdgeWeight += edgeWeight;

			node1WeightedDegree = this.intWeightedDegree(edge.getNode1());
			node2WeightedDegree = this.intWeightedDegree(edge.getNode2());

			this.sum1 += edgeWeight
					* (node1WeightedDegree * node2WeightedDegree);
			this.sum2 += edgeWeight
					* (node1WeightedDegree + node2WeightedDegree);
			this.sum3 += edgeWeight
					* (node1WeightedDegree * node1WeightedDegree + node2WeightedDegree
							* node2WeightedDegree);
		}

		this.setR();

		return true;
	}

	@Override
	public void init_() {
		this.totalEdgeWeight = 0;

		this.sum1 = 0;
		this.sum2 = 0;
		this.sum3 = 0;

		this.r = 0.0;
	}

	@Override
	public void reset_() {
		this.totalEdgeWeight = 0;

		this.sum1 = 0;
		this.sum2 = 0;
		this.sum3 = 0;

		this.r = 0.0;
	}

}
