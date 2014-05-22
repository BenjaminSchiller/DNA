package dna.metrics.assortativity;

import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.weights.DoubleWeight;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * {@link ApplicationType#Recomputation} of {@link AssortativityDoubleWeighted}.
 */
public class AssortativityDoubleWeightedR extends AssortativityDoubleWeighted {

	/**
	 * Initializes {@link AssortativityDoubleWeightedR}. Implicitly sets degree
	 * type for directed graphs to outdegree.
	 */
	public AssortativityDoubleWeightedR() {
		super("AssortativityDoubleWeightedR", ApplicationType.Recomputation);
	}

	/**
	 * Initializes {@link AssortativityDoubleWeightedR}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 */
	public AssortativityDoubleWeightedR(Parameter directedDegreeType) {
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
	boolean computeForDirectedDoubleWeightedGraph() {
		DirectedWeightedEdge edge;
		double edgeWeight;
		double srcWeightedDegree, dstWeightedDegree;
		for (IElement iElement : this.g.getEdges()) {
			edge = (DirectedWeightedEdge) iElement;

			edgeWeight = ((DoubleWeight) edge.getWeight()).getWeight();
			this.totalEdgeWeight += edgeWeight;

			srcWeightedDegree = this
					.directedDoubleWeightedDegree(edge.getSrc());
			dstWeightedDegree = this
					.directedDoubleWeightedDegree(edge.getDst());

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
	boolean computeForUndirectedDoubleWeightedGraph() {
		UndirectedWeightedEdge edge;
		double edgeWeight;
		double node1WeightedDegree, node2WeightedDegree;
		for (IElement iElement : this.g.getEdges()) {
			edge = (UndirectedWeightedEdge) iElement;

			edgeWeight = ((DoubleWeight) edge.getWeight()).getWeight();
			this.totalEdgeWeight += edgeWeight;

			node1WeightedDegree = this.doubleWeightedDegree(edge.getNode1());
			node2WeightedDegree = this.doubleWeightedDegree(edge.getNode2());

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
		this.totalEdgeWeight = 0.0;

		this.sum1 = 0.0;
		this.sum2 = 0.0;
		this.sum3 = 0.0;

		this.r = 0.0;
	}

	@Override
	public void reset_() {
		this.totalEdgeWeight = 0.0;

		this.sum1 = 0.0;
		this.sum2 = 0.0;
		this.sum3 = 0.0;

		this.r = 0.0;
	}

}
