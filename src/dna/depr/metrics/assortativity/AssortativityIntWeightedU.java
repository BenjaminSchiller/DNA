package dna.depr.metrics.assortativity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IntWeight;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;
import dna.util.Log;
import dna.util.parameters.Parameter;

/**
 * {@link ApplicationType#BeforeUpdate} of {@link AssortativityDoubleWeighted}.
 */
public class AssortativityIntWeightedU extends AssortativityIntWeighted {

	/** Stores the weighted degree of each node. */
	private HashMap<Node, Long> weightedDegrees;

	/**
	 * Initializes {@link AssortativityWeighetdU}. Implicitly sets degree type
	 * for directed graphs to outdegree.
	 */
	public AssortativityIntWeightedU() {
		super("AssortativityIntWeightedU", ApplicationType.BeforeUpdate);
	}

	/**
	 * Initializes {@link AssortativityWeighetdU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 */
	public AssortativityIntWeightedU(Parameter directedDegreeType) {
		super("AssortativityWeightedU", ApplicationType.BeforeUpdate,
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
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {
			if (u instanceof NodeRemoval) {
				return this
						.directedIntUpdateForNodeRemoval((DirectedNode) ((NodeRemoval) u)
								.getNode());
			} else if (u instanceof EdgeAddition) {
				return this
						.updateForEdgeAddition((DirectedWeightedEdge) ((EdgeAddition) u)
								.getEdge());
			} else if (u instanceof EdgeRemoval) {
				return this
						.updateForEdgeRemoval((DirectedWeightedEdge) ((EdgeRemoval) u)
								.getEdge());
			} else if (u instanceof EdgeWeight) {
				final EdgeWeight edgeWeightUpdate = (EdgeWeight) u;
				return this.updateForEdgeWeight(
						(DirectedWeightedEdge) edgeWeightUpdate.getEdge(),
						((IntWeight) edgeWeightUpdate.getWeight()).getWeight());
			}
		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {
			if (u instanceof NodeRemoval) {
				return this
						.undirectedIntUpdateForNodeRemoval((UndirectedNode) ((NodeRemoval) u)
								.getNode());
			} else if (u instanceof EdgeAddition) {
				return this
						.updateForEdgeAddition((UndirectedWeightedEdge) ((EdgeAddition) u)
								.getEdge());
			} else if (u instanceof EdgeRemoval) {
				return this
						.updateForEdgeRemoval((UndirectedWeightedEdge) ((EdgeRemoval) u)
								.getEdge());
			} else if (u instanceof EdgeWeight) {
				final EdgeWeight edgeWeightUpdate = (EdgeWeight) u;
				return this.updateForEdgeWeight(
						(UndirectedWeightedEdge) edgeWeightUpdate.getEdge(),
						((IntWeight) edgeWeightUpdate.getWeight()).getWeight());
			}
		}

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

	/**
	 * Decreases stored weighted degree of given node by given weight.
	 * 
	 * @param node
	 *            The node which weighted degree should be decreased.
	 * @param weight
	 *            The weight by which the degree should be decreased.
	 * 
	 * @see AssortativityIntWeightedU#weightedDegrees
	 */
	private void decreaseWeightedDegree(Node node, long weight) {
		if (this.weightedDegrees.containsKey(node))
			this.weightedDegrees.put(node, this.weightedDegrees.get(node)
					- weight);
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for the
	 * outdated {@link DirectedNode} and {@link DirectedIntWeightedEdge}s.
	 * 
	 * @param node
	 *            The outdated node.
	 * @return true
	 */
	private boolean directedIntUpdateForNodeRemoval(DirectedNode node) {

		if (this.directedDegreeType.equals("out")) {

			final long nodeOutDegree = this.directedIntWeightedDegree(node);
			final Set<Node> nodeNeighbors = new HashSet<Node>();

			DirectedWeightedEdge edge, edgeAtOtherNode2;
			long edgeWeight, edgeAtOtherNode2Weight;
			DirectedNode otherNode1, otherNode2;
			long otherNode1OutDegree, otherNode2OutDegree;
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = ((IntWeight) edge.getWeight()).getWeight();

				this.totalEdgeWeight -= edgeWeight;

				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				otherNode1OutDegree = this
						.directedIntWeightedDegree(otherNode1);

				this.sum1 -= edgeWeight * (nodeOutDegree * otherNode1OutDegree);

				this.sum2 -= edgeWeight * (nodeOutDegree + otherNode1OutDegree);

				this.sum3 -= edgeWeight
						* (nodeOutDegree * nodeOutDegree + otherNode1OutDegree
								* otherNode1OutDegree);

				if (edge.getDst().equals(node)) {
					// only for otherNode1s at incoming edges of node do the
					// following

					nodeNeighbors.add(otherNode1);

					for (IElement iElement2 : otherNode1.getEdges()) {
						edgeAtOtherNode2 = ((DirectedWeightedEdge) iElement2);
						otherNode2 = (DirectedNode) edgeAtOtherNode2
								.getDifferingNode(otherNode1);

						if (!otherNode2.equals(node)) {
							edgeAtOtherNode2Weight = ((IntWeight) edgeAtOtherNode2
									.getWeight()).getWeight();
							otherNode2OutDegree = this
									.directedIntWeightedDegree(otherNode2);

							this.sum1 -= edgeWeight * edgeAtOtherNode2Weight
									* otherNode2OutDegree;

							this.sum2 -= edgeWeight * edgeAtOtherNode2Weight;

							this.sum3 -= edgeAtOtherNode2Weight
									* (2 * edgeWeight * otherNode1OutDegree - edgeWeight
											* edgeWeight);
						}
					}
					// too much is subtracted from sum1 if any of the neighbors
					// of otherNode1 are connected among themselves; add it
					// again
					this.sum1 += this
							.intWeightOfDirectedEdgesWithinNeighborsOfNode(
									node, otherNode1, nodeNeighbors);
				}
			}

			// update weighted degree
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = ((IntWeight) edge.getWeight()).getWeight();
				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				if (edge.getDst().equals(node))
					this.decreaseWeightedDegree(otherNode1, edgeWeight);
			}

		} else if (this.directedDegreeType.equals("in")) {

			final long nodeInDegree = this.directedIntWeightedDegree(node);
			final Set<Node> nodeNeighbors = new HashSet<Node>();

			DirectedWeightedEdge edge, edgeAtOtherNode2;
			long edgeWeight, edgeAtOtherNode2Weight;
			DirectedNode otherNode1, otherNode2;
			long otherNode1InDegree, otherNode2InDegree;
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = ((IntWeight) edge.getWeight()).getWeight();

				this.totalEdgeWeight -= edgeWeight;

				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				otherNode1InDegree = this.directedIntWeightedDegree(otherNode1);

				this.sum1 -= edgeWeight * (nodeInDegree * otherNode1InDegree);

				this.sum2 -= edgeWeight * (nodeInDegree + otherNode1InDegree);

				this.sum3 -= edgeWeight
						* (nodeInDegree * nodeInDegree + otherNode1InDegree
								* otherNode1InDegree);

				if (edge.getSrc().equals(node)) {
					// only for otherNode1s at outgoing edges of node do the
					// following

					nodeNeighbors.add(otherNode1);

					for (IElement iElement2 : otherNode1.getEdges()) {
						edgeAtOtherNode2 = ((DirectedWeightedEdge) iElement2);
						otherNode2 = (DirectedNode) edgeAtOtherNode2
								.getDifferingNode(otherNode1);

						if (!otherNode2.equals(node)) {
							edgeAtOtherNode2Weight = ((IntWeight) edgeAtOtherNode2
									.getWeight()).getWeight();
							otherNode2InDegree = this
									.directedIntWeightedDegree(otherNode2);

							this.sum1 -= edgeWeight * edgeAtOtherNode2Weight
									* otherNode2InDegree;

							this.sum2 -= edgeWeight * edgeAtOtherNode2Weight;

							this.sum3 -= edgeAtOtherNode2Weight
									* (2 * edgeWeight * otherNode1InDegree - edgeWeight
											* edgeWeight);
						}
					}
					// too much is subtracted from sum1 if any of the neighbors
					// of otherNode1 are connected among themselves; add it
					// again
					this.sum1 += this
							.intWeightOfDirectedEdgesWithinNeighborsOfNode(
									node, otherNode1, nodeNeighbors);
				}
			}

			// update weighted degree
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = ((IntWeight) edge.getWeight()).getWeight();
				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				if (edge.getSrc().equals(node))
					this.decreaseWeightedDegree(otherNode1, edgeWeight);
			}

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		this.setR();

		return true;
	}

	/**
	 * This method reads the weighted degree from
	 * {@link AssortativityIntWeightedU#weightedDegrees}. The value is only
	 * computed if it is not stored. It is updated by
	 * {@link AssortativityIntWeightedU#decreaseWeightedDegree(Node, long)} and
	 * {@link AssortativityIntWeightedU#increaseWeightedDegree(Node, long)}.
	 */
	@Override
	long directedIntWeightedDegree(DirectedNode node) {
		if (this.weightedDegrees.containsKey(node))
			return this.weightedDegrees.get(node);
		else {
			final long weightedDegree = super.directedIntWeightedDegree(node);
			this.weightedDegrees.put(node, weightedDegree);
			return weightedDegree;
		}
	}

	/**
	 * Increases stored weighted degree of given node by given weight.
	 * 
	 * @param node
	 *            The node which weighted degree should be increased.
	 * @param weight
	 *            The weight by which the degree should be increased.
	 * 
	 * @see AssortativityIntWeightedU#weightedDegrees
	 */
	private void increaseWeightedDegree(Node node, long weight) {
		if (this.weightedDegrees.containsKey(node))
			this.weightedDegrees.put(node, this.weightedDegrees.get(node)
					+ weight);
	}

	@Override
	public void init_() {
		this.totalEdgeWeight = 0;

		this.sum1 = 0;
		this.sum2 = 0;
		this.sum3 = 0;

		this.r = 0.0;

		this.weightedDegrees = new HashMap<Node, Long>();
	}

	/**
	 * This method reads the weighted degree from
	 * {@link AssortativityIntWeightedU#weightedDegrees}. The value is only
	 * computed if it is not stored. It is updated by
	 * {@link AssortativityIntWeightedU#decreaseWeightedDegree(Node, long)} and
	 * {@link AssortativityIntWeightedU#increaseWeightedDegree(Node, long)}.
	 */
	@Override
	long intWeightedDegree(UndirectedNode node) {
		if (this.weightedDegrees.containsKey(node))
			return this.weightedDegrees.get(node);
		else {
			final long weightedDegree = super.intWeightedDegree(node);
			this.weightedDegrees.put(node, weightedDegree);
			return weightedDegree;
		}
	}

	/**
	 * @return The total weight of all undirected edges between the given
	 *         neighbor of given node and every other neighbor node in the given
	 *         set of nodes.
	 */
	private long intWeightOfDirectedEdgesWithinNeighborsOfNode(Node node,
			Node nodeNeighbor, Set<Node> nodeNeighbors) {
		final GraphDataStructure gds = this.g.getGraphDatastructures();

		long addAgain = 0;

		if (this.directedDegreeType.equals("out")) {

			DirectedWeightedEdge edge_NodeNeighbor_OtherNodeNeighbor, edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor;
			for (Node otherNodeNeighbor : nodeNeighbors) {
				edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) gds
						.newEdgeInstance(nodeNeighbor, otherNodeNeighbor);
				if (this.g.containsEdge(edge_NodeNeighbor_OtherNodeNeighbor)) {
					edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) this.g
							.getEdge(nodeNeighbor, otherNodeNeighbor);
					edge_Node_NodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(nodeNeighbor, node));
					edge_Node_OtherNodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(otherNodeNeighbor, node));

					addAgain += ((IntWeight) edge_NodeNeighbor_OtherNodeNeighbor
							.getWeight()).getWeight()
							* ((IntWeight) edge_Node_NodeNeighbor.getWeight())
									.getWeight()
							* ((IntWeight) edge_Node_OtherNodeNeighbor
									.getWeight()).getWeight();
				}

				edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) gds
						.newEdgeInstance(otherNodeNeighbor, nodeNeighbor);
				if (this.g.containsEdge(edge_NodeNeighbor_OtherNodeNeighbor)) {
					edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) this.g
							.getEdge(otherNodeNeighbor, nodeNeighbor);
					edge_Node_NodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(nodeNeighbor, node));
					edge_Node_OtherNodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(otherNodeNeighbor, node));

					addAgain += ((IntWeight) edge_NodeNeighbor_OtherNodeNeighbor
							.getWeight()).getWeight()
							* ((IntWeight) edge_Node_NodeNeighbor.getWeight())
									.getWeight()
							* ((IntWeight) edge_Node_OtherNodeNeighbor
									.getWeight()).getWeight();
				}

			}

		} else if (this.directedDegreeType.equals("in")) {

			DirectedWeightedEdge edge_NodeNeighbor_OtherNodeNeighbor, edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor;
			for (Node otherNodeNeighbor : nodeNeighbors) {
				edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) gds
						.newEdgeInstance(nodeNeighbor, otherNodeNeighbor);
				if (this.g.containsEdge(edge_NodeNeighbor_OtherNodeNeighbor)) {
					edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) this.g
							.getEdge(nodeNeighbor, otherNodeNeighbor);
					edge_Node_NodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(node, nodeNeighbor));
					edge_Node_OtherNodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(node, otherNodeNeighbor));

					addAgain += ((IntWeight) edge_NodeNeighbor_OtherNodeNeighbor
							.getWeight()).getWeight()
							* ((IntWeight) edge_Node_NodeNeighbor.getWeight())
									.getWeight()
							* ((IntWeight) edge_Node_OtherNodeNeighbor
									.getWeight()).getWeight();
				}

				edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) gds
						.newEdgeInstance(otherNodeNeighbor, nodeNeighbor);
				if (this.g.containsEdge(edge_NodeNeighbor_OtherNodeNeighbor)) {
					edge_NodeNeighbor_OtherNodeNeighbor = (DirectedWeightedEdge) this.g
							.getEdge(otherNodeNeighbor, nodeNeighbor);
					edge_Node_NodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(node, nodeNeighbor));
					edge_Node_OtherNodeNeighbor = (DirectedWeightedEdge) (this.g
							.getEdge(node, otherNodeNeighbor));

					addAgain += ((IntWeight) edge_NodeNeighbor_OtherNodeNeighbor
							.getWeight()).getWeight()
							* ((IntWeight) edge_Node_NodeNeighbor.getWeight())
									.getWeight()
							* ((IntWeight) edge_Node_OtherNodeNeighbor
									.getWeight()).getWeight();
				}

			}

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		return addAgain;
	}

	/**
	 * @return The total weight of all undirected edges between the given
	 *         neighbor of given node and every other neighbor node in the given
	 *         set of nodes.
	 */
	private long intWeightOfUndirectedEdgesWithinNeighborsOfNode(Node node,
			Node nodeNeighbor, Set<Node> nodeNeighbors) {
		final GraphDataStructure gds = this.g.getGraphDatastructures();

		long addAgain = 0;

		UndirectedWeightedEdge edge_NodeNeighbor_OtherNodeNeighbor, edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor;
		for (Node otherNodeNeighbor : nodeNeighbors) {
			edge_NodeNeighbor_OtherNodeNeighbor = (UndirectedWeightedEdge) gds
					.newEdgeInstance(nodeNeighbor, otherNodeNeighbor);
			if (this.g.containsEdge(edge_NodeNeighbor_OtherNodeNeighbor)) {
				edge_NodeNeighbor_OtherNodeNeighbor = (UndirectedWeightedEdge) this.g
						.getEdge(nodeNeighbor, otherNodeNeighbor);
				edge_Node_NodeNeighbor = (UndirectedWeightedEdge) (this.g
						.getEdge(node, nodeNeighbor));
				edge_Node_OtherNodeNeighbor = (UndirectedWeightedEdge) (this.g
						.getEdge(node, otherNodeNeighbor));

				addAgain += ((IntWeight) edge_NodeNeighbor_OtherNodeNeighbor
						.getWeight()).getWeight()
						* ((IntWeight) edge_Node_NodeNeighbor.getWeight())
								.getWeight()
						* ((IntWeight) edge_Node_OtherNodeNeighbor.getWeight())
								.getWeight();
			}
		}

		return addAgain;
	}

	@Override
	public void reset_() {
		this.totalEdgeWeight = 0;

		this.sum1 = 0;
		this.sum2 = 0;
		this.sum3 = 0;

		this.r = 0.0;

		this.weightedDegrees = new HashMap<Node, Long>();
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for the
	 * outdated {@link UndirectedNode} and {@link UndirectedIntWeightedEdge}s.
	 * 
	 * @param node
	 *            The outdated node.
	 * @return true
	 */
	private boolean undirectedIntUpdateForNodeRemoval(UndirectedNode node) {
		final long nodeDegree = this.intWeightedDegree(node);
		final Set<Node> nodeNeighbors = new HashSet<Node>();

		UndirectedWeightedEdge edge, edgeAtOtherNode1;
		long edgeWeight, edgeAtOtherNode1Weight;
		UndirectedNode otherNode1, otherNode2;
		long otherNode1Degree, otherNode2Degree;
		for (IElement iElement1 : node.getEdges()) {
			edge = (UndirectedWeightedEdge) iElement1;
			edgeWeight = ((IntWeight) edge.getWeight()).getWeight();

			this.totalEdgeWeight -= edgeWeight;

			otherNode1 = (UndirectedNode) edge.getDifferingNode(node);
			nodeNeighbors.add(otherNode1);
			otherNode1Degree = this.intWeightedDegree(otherNode1);

			this.sum1 -= edgeWeight * (nodeDegree * otherNode1Degree);

			this.sum2 -= edgeWeight * (nodeDegree + otherNode1Degree);

			this.sum3 -= edgeWeight
					* (nodeDegree * nodeDegree + otherNode1Degree
							* otherNode1Degree);

			for (IElement iElement2 : otherNode1.getEdges()) {
				edgeAtOtherNode1 = ((UndirectedWeightedEdge) iElement2);
				otherNode2 = (UndirectedNode) edgeAtOtherNode1
						.getDifferingNode(otherNode1);

				if (!otherNode2.equals(node)) {
					edgeAtOtherNode1Weight = ((IntWeight) edgeAtOtherNode1
							.getWeight()).getWeight();
					otherNode2Degree = this.intWeightedDegree(otherNode2);

					this.sum1 -= edgeWeight * edgeAtOtherNode1Weight
							* otherNode2Degree;

					this.sum2 -= edgeWeight * edgeAtOtherNode1Weight;

					this.sum3 -= edgeAtOtherNode1Weight
							* (2 * edgeWeight * otherNode1Degree - edgeWeight
									* edgeWeight);
				}
			}
			// too much is subtracted from sum1 if any of the neighbors of
			// otherNode1 are connected among themselves; add it again
			this.sum1 += this.intWeightOfUndirectedEdgesWithinNeighborsOfNode(
					node, otherNode1, nodeNeighbors);
		}

		// update weighted degree
		for (IElement iElement1 : node.getEdges()) {
			edge = (UndirectedWeightedEdge) iElement1;
			edgeWeight = ((IntWeight) edge.getWeight()).getWeight();
			otherNode1 = (UndirectedNode) edge.getDifferingNode(node);
			this.decreaseWeightedDegree(otherNode1, edgeWeight);
		}

		this.setR();

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for a new
	 * {@link DirectedIntWeightedEdge}.
	 * 
	 * @param edge
	 *            The new edge.
	 * @return true
	 */
	private boolean updateForEdgeAddition(DirectedWeightedEdge edge) {

		final long edgeWeight = ((IntWeight) edge.getWeight()).getWeight();

		this.totalEdgeWeight += edgeWeight;

		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		if (this.directedDegreeType.equals("out")) {

			final long srcNodeOutDegree = this
					.directedIntWeightedDegree(srcNode)
					+ ((IntWeight) edge.getWeight()).getWeight();
			final long dstNodeOutDegree = this
					.directedIntWeightedDegree(dstNode);

			this.sum1 += edgeWeight * (srcNodeOutDegree * dstNodeOutDegree);

			this.sum2 += edgeWeight * (srcNodeOutDegree + dstNodeOutDegree);

			this.sum3 += edgeWeight
					* (srcNodeOutDegree * srcNodeOutDegree + dstNodeOutDegree
							* dstNodeOutDegree);

			DirectedWeightedEdge edgeAtNode;
			long edgeAtNodeWeight;
			long otherNodeOutDegree;

			for (IElement iElement : srcNode.getEdges()) {
				edgeAtNode = (DirectedWeightedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
							.getWeight();
					otherNodeOutDegree = this
							.directedIntWeightedDegree((DirectedNode) edgeAtNode
									.getDifferingNode(srcNode));

					this.sum1 += edgeWeight * edgeAtNodeWeight
							* otherNodeOutDegree;

					this.sum2 += edgeWeight * edgeAtNodeWeight;

					this.sum3 += edgeAtNodeWeight
							* (2 * edgeWeight * srcNodeOutDegree - edgeWeight
									* edgeWeight);
				}
			}

			// update weighted degree
			this.increaseWeightedDegree(srcNode, edgeWeight);

		} else if (this.directedDegreeType.equals("in")) {

			final long srcNodeInDegree = this
					.directedIntWeightedDegree(srcNode);
			final long dstNodeInDegree = this
					.directedIntWeightedDegree(dstNode)
					+ ((IntWeight) edge.getWeight()).getWeight();

			this.sum1 += edgeWeight * (srcNodeInDegree * dstNodeInDegree);

			this.sum2 += edgeWeight * (srcNodeInDegree + dstNodeInDegree);

			this.sum3 += edgeWeight
					* (srcNodeInDegree * srcNodeInDegree + dstNodeInDegree
							* dstNodeInDegree);

			DirectedWeightedEdge edgeAtNode;
			long edgeAtNodeWeight;
			long otherNodeInDegree;

			for (IElement iElement : dstNode.getEdges()) {
				edgeAtNode = (DirectedWeightedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
							.getWeight();
					otherNodeInDegree = this
							.directedIntWeightedDegree((DirectedNode) edgeAtNode
									.getDifferingNode(dstNode));

					this.sum1 += edgeWeight * edgeAtNodeWeight
							* otherNodeInDegree;

					this.sum2 += edgeWeight * edgeAtNodeWeight;

					this.sum3 += edgeAtNodeWeight
							* (2 * edgeWeight * dstNodeInDegree - edgeWeight
									* edgeWeight);
				}
			}

			// update weighted degree
			this.increaseWeightedDegree(dstNode, edgeWeight);

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		this.setR();

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for a new
	 * {@link UndirectedDoubleWeightedEdge}.
	 * 
	 * @param edge
	 *            The new edge.
	 * @return true
	 */
	private boolean updateForEdgeAddition(UndirectedWeightedEdge edge) {
		final long edgeWeight = ((IntWeight) edge.getWeight()).getWeight();

		this.totalEdgeWeight += edgeWeight;

		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final long node1Degree = this.intWeightedDegree(node1)
				+ ((IntWeight) edge.getWeight()).getWeight();
		final long node2Degree = this.intWeightedDegree(node2)
				+ ((IntWeight) edge.getWeight()).getWeight();

		this.sum1 += edgeWeight * (node1Degree * node2Degree);

		this.sum2 += edgeWeight * (node1Degree + node2Degree);

		this.sum3 += edgeWeight
				* (node1Degree * node1Degree + node2Degree * node2Degree);

		UndirectedWeightedEdge edgeAtNode;
		long edgeAtNodeWeight;
		long otherNodeDegree;

		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
						.getWeight();
				otherNodeDegree = this
						.intWeightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node1));

				this.sum1 += edgeWeight * edgeAtNodeWeight * otherNodeDegree;

				this.sum2 += edgeWeight * edgeAtNodeWeight;

				this.sum3 += edgeAtNodeWeight
						* (2 * edgeWeight * node1Degree - edgeWeight
								* edgeWeight);
			}
		}

		for (IElement iElement : node2.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
						.getWeight();
				otherNodeDegree = this
						.intWeightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node2));

				this.sum1 += edgeWeight * edgeAtNodeWeight * otherNodeDegree;

				this.sum2 += edgeWeight * edgeAtNodeWeight;

				this.sum3 += edgeAtNodeWeight
						* (2 * edgeWeight * node2Degree - edgeWeight
								* edgeWeight);
			}
		}

		// update weighted degree
		this.increaseWeightedDegree(node1, edgeWeight);
		this.increaseWeightedDegree(node2, edgeWeight);

		this.setR();

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for the
	 * outdated {@link DirectedIntWeightedEdge}.
	 * 
	 * @param edge
	 *            The outdated edge.
	 * @return true
	 */
	private boolean updateForEdgeRemoval(DirectedWeightedEdge edge) {
		final long edgeWeight = ((IntWeight) edge.getWeight()).getWeight();

		this.totalEdgeWeight -= edgeWeight;

		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		if (this.directedDegreeType.equals("out")) {

			final long srcNodeOutDegree = this
					.directedIntWeightedDegree(srcNode);
			final long dstNodeOutDegree = this
					.directedIntWeightedDegree(dstNode);

			this.sum1 -= edgeWeight * (srcNodeOutDegree * dstNodeOutDegree);

			this.sum2 -= edgeWeight * (srcNodeOutDegree + dstNodeOutDegree);

			this.sum3 -= edgeWeight
					* (srcNodeOutDegree * srcNodeOutDegree + dstNodeOutDegree
							* dstNodeOutDegree);

			DirectedWeightedEdge edgeAtNode;
			long edgeAtNodeWeight;
			long otherNodeOutDegree;

			for (IElement iElement : srcNode.getEdges()) {
				edgeAtNode = (DirectedWeightedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
							.getWeight();
					otherNodeOutDegree = this
							.directedIntWeightedDegree((DirectedNode) edgeAtNode
									.getDifferingNode(srcNode));

					this.sum1 -= edgeWeight * edgeAtNodeWeight
							* otherNodeOutDegree;

					this.sum2 -= edgeWeight * edgeAtNodeWeight;

					this.sum3 -= edgeAtNodeWeight
							* (2 * edgeWeight * srcNodeOutDegree - edgeWeight
									* edgeWeight);
				}
			}

			// update weighted degree
			this.decreaseWeightedDegree(srcNode, edgeWeight);

		} else if (this.directedDegreeType.equals("in")) {

			final long srcNodeInDegree = this
					.directedIntWeightedDegree(srcNode);
			final long dstNodeInDegree = this
					.directedIntWeightedDegree(dstNode);

			this.sum1 -= edgeWeight * (srcNodeInDegree * dstNodeInDegree);

			this.sum2 -= edgeWeight * (srcNodeInDegree + dstNodeInDegree);

			this.sum3 -= edgeWeight
					* (srcNodeInDegree * srcNodeInDegree + dstNodeInDegree
							* dstNodeInDegree);

			DirectedWeightedEdge edgeAtNode;
			long edgeAtNodeWeight;
			long otherNodeInDegree;

			for (IElement iElement : dstNode.getEdges()) {
				edgeAtNode = (DirectedWeightedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
							.getWeight();
					otherNodeInDegree = this
							.directedIntWeightedDegree((DirectedNode) edgeAtNode
									.getDifferingNode(dstNode));

					this.sum1 -= edgeWeight * edgeAtNodeWeight
							* otherNodeInDegree;

					this.sum2 -= edgeWeight * edgeAtNodeWeight;

					this.sum3 -= edgeAtNodeWeight
							* (2 * edgeWeight * dstNodeInDegree - edgeWeight
									* edgeWeight);
				}
			}

			// update weighted degree
			this.decreaseWeightedDegree(dstNode, edgeWeight);

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		this.setR();

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for the
	 * outdated {@link UndirectedIntWeightedEdge}.
	 * 
	 * @param edge
	 *            The outdated edge.
	 * @return true
	 */
	private boolean updateForEdgeRemoval(UndirectedWeightedEdge edge) {
		final long edgeWeight = ((IntWeight) edge.getWeight()).getWeight();

		this.totalEdgeWeight -= edgeWeight;

		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final long node1Degree = this.intWeightedDegree(node1);
		final long node2Degree = this.intWeightedDegree(node2);

		this.sum1 -= edgeWeight * (node1Degree * node2Degree);

		this.sum2 -= edgeWeight * (node1Degree + node2Degree);

		this.sum3 -= edgeWeight
				* (node1Degree * node1Degree + node2Degree * node2Degree);

		UndirectedWeightedEdge edgeAtNode;
		long edgeAtNodeWeight;
		long otherNodeDegree;

		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
						.getWeight();
				otherNodeDegree = this
						.intWeightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node1));

				this.sum1 -= edgeWeight * edgeAtNodeWeight * otherNodeDegree;

				this.sum2 -= edgeWeight * edgeAtNodeWeight;

				this.sum3 -= edgeAtNodeWeight
						* (2 * edgeWeight * node1Degree - edgeWeight
								* edgeWeight);
			}
		}

		for (IElement iElement : node2.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
						.getWeight();
				otherNodeDegree = this
						.intWeightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node2));

				this.sum1 -= edgeWeight * edgeAtNodeWeight * otherNodeDegree;

				this.sum2 -= edgeWeight * edgeAtNodeWeight;

				this.sum3 -= edgeAtNodeWeight
						* (2 * edgeWeight * node2Degree - edgeWeight
								* edgeWeight);
			}
		}

		// update weighted degree
		this.decreaseWeightedDegree(node1, edgeWeight);
		this.decreaseWeightedDegree(node2, edgeWeight);

		this.setR();

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for the
	 * newly weighted {@link DirectedIntWeightedEdge}.
	 * 
	 * @param edge
	 *            The newly weighted edge.
	 * @return true
	 */
	private boolean updateForEdgeWeight(DirectedWeightedEdge edge,
			long newWeight) {
		final long oldEdgeWeight = ((IntWeight) edge.getWeight()).getWeight();

		this.totalEdgeWeight -= oldEdgeWeight;
		this.totalEdgeWeight += newWeight;

		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		if (this.directedDegreeType.equals("out")) {

			final long oldSrcNodeOutDegree = this
					.directedIntWeightedDegree(srcNode);
			final long oldDstNodeOutDegree = this
					.directedIntWeightedDegree(dstNode);
			final long newSrcNodeOutDegree = oldSrcNodeOutDegree
					- oldEdgeWeight + newWeight;
			final long newDstNodeOutDegree = oldDstNodeOutDegree;

			this.sum1 -= oldEdgeWeight
					* (oldSrcNodeOutDegree * oldDstNodeOutDegree);
			this.sum1 += newWeight
					* (newSrcNodeOutDegree * newDstNodeOutDegree);

			this.sum2 -= oldEdgeWeight
					* (oldSrcNodeOutDegree + oldDstNodeOutDegree);
			this.sum2 += newWeight
					* (newSrcNodeOutDegree + newDstNodeOutDegree);

			this.sum3 -= oldEdgeWeight
					* (oldSrcNodeOutDegree * oldSrcNodeOutDegree + oldDstNodeOutDegree
							* oldDstNodeOutDegree);
			this.sum3 += newWeight
					* (newSrcNodeOutDegree * newSrcNodeOutDegree + newDstNodeOutDegree
							* newDstNodeOutDegree);

			DirectedWeightedEdge edgeAtNode;
			long edgeAtNodeWeight;
			long otherNodeOutDegree;

			for (IElement iElement : srcNode.getEdges()) {
				edgeAtNode = (DirectedWeightedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
							.getWeight();
					otherNodeOutDegree = this
							.directedIntWeightedDegree((DirectedNode) edgeAtNode
									.getDifferingNode(srcNode));

					this.sum1 -= oldEdgeWeight * edgeAtNodeWeight
							* otherNodeOutDegree;
					this.sum1 += newWeight * edgeAtNodeWeight
							* otherNodeOutDegree;

					this.sum2 -= oldEdgeWeight * edgeAtNodeWeight;
					this.sum2 += newWeight * edgeAtNodeWeight;

					this.sum3 -= edgeAtNodeWeight
							* (2 * oldEdgeWeight * oldSrcNodeOutDegree - oldEdgeWeight
									* oldEdgeWeight);
					this.sum3 += edgeAtNodeWeight
							* (2 * newWeight * newSrcNodeOutDegree - newWeight
									* newWeight);
				}
			}

			// update weighted degree
			this.decreaseWeightedDegree(srcNode, oldEdgeWeight);
			this.increaseWeightedDegree(srcNode, newWeight);

		} else if (this.directedDegreeType.equals("in")) {

			final long oldSrcNodeInDegree = this
					.directedIntWeightedDegree(srcNode);
			final long oldDstNodeInDegree = this
					.directedIntWeightedDegree(dstNode);
			final long newSrcNodeInDegree = oldSrcNodeInDegree;
			final long newDstNodeInDegree = oldDstNodeInDegree - oldEdgeWeight
					+ newWeight;

			this.sum1 -= oldEdgeWeight
					* (oldSrcNodeInDegree * oldDstNodeInDegree);
			this.sum1 += newWeight * (newSrcNodeInDegree * newDstNodeInDegree);

			this.sum2 -= oldEdgeWeight
					* (oldSrcNodeInDegree + oldDstNodeInDegree);
			this.sum2 += newWeight * (newSrcNodeInDegree + newDstNodeInDegree);

			this.sum3 -= oldEdgeWeight
					* (oldSrcNodeInDegree * oldSrcNodeInDegree + oldDstNodeInDegree
							* oldDstNodeInDegree);
			this.sum3 += newWeight
					* (newSrcNodeInDegree * newSrcNodeInDegree + newDstNodeInDegree
							* newDstNodeInDegree);

			DirectedWeightedEdge edgeAtNode;
			long edgeAtNodeWeight;
			long otherNodeInDegree;

			for (IElement iElement : dstNode.getEdges()) {
				edgeAtNode = (DirectedWeightedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
							.getWeight();
					otherNodeInDegree = this
							.directedIntWeightedDegree((DirectedNode) edgeAtNode
									.getDifferingNode(dstNode));

					this.sum1 -= oldEdgeWeight * edgeAtNodeWeight
							* otherNodeInDegree;
					this.sum1 += newWeight * edgeAtNodeWeight
							* otherNodeInDegree;

					this.sum2 -= oldEdgeWeight * edgeAtNodeWeight;
					this.sum2 += newWeight * edgeAtNodeWeight;

					this.sum3 -= edgeAtNodeWeight
							* (2 * oldEdgeWeight * oldDstNodeInDegree - oldEdgeWeight
									* oldEdgeWeight);
					this.sum3 += edgeAtNodeWeight
							* (2 * newWeight * newDstNodeInDegree - newWeight
									* newWeight);
				}
			}

			// update weighted degree
			this.decreaseWeightedDegree(dstNode, oldEdgeWeight);
			this.increaseWeightedDegree(dstNode, newWeight);

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		this.setR();

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR()} for the
	 * newly weighted {@link UndirectedIntWeightedEdge}.
	 * 
	 * @param edge
	 *            The newly weighted edge.
	 * @return true
	 */
	private boolean updateForEdgeWeight(UndirectedWeightedEdge edge,
			long newWeight) {
		final long oldEdgeWeight = ((IntWeight) edge.getWeight()).getWeight();

		this.totalEdgeWeight -= oldEdgeWeight;
		this.totalEdgeWeight += newWeight;

		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final long oldNode1Degree = this.intWeightedDegree(node1);
		final long oldNode2Degree = this.intWeightedDegree(node2);
		final long newNode1Degree = oldNode1Degree - oldEdgeWeight + newWeight;
		final long newNode2Degree = oldNode2Degree - oldEdgeWeight + newWeight;

		this.sum1 -= oldEdgeWeight * (oldNode1Degree * oldNode2Degree);
		this.sum1 += newWeight * (newNode1Degree * newNode2Degree);

		this.sum2 -= oldEdgeWeight * (oldNode1Degree + oldNode2Degree);
		this.sum2 += newWeight * (newNode1Degree + newNode2Degree);

		this.sum3 -= oldEdgeWeight
				* (oldNode1Degree * oldNode1Degree + oldNode2Degree
						* oldNode2Degree);
		this.sum3 += newWeight
				* (newNode1Degree * newNode1Degree + newNode2Degree
						* newNode2Degree);

		UndirectedWeightedEdge edgeAtNode;
		long edgeAtNodeWeight;
		long otherNodeDegree;

		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
						.getWeight();
				otherNodeDegree = this
						.intWeightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node1));

				this.sum1 -= oldEdgeWeight * edgeAtNodeWeight * otherNodeDegree;
				this.sum1 += newWeight * edgeAtNodeWeight * otherNodeDegree;

				this.sum2 -= oldEdgeWeight * edgeAtNodeWeight;
				this.sum2 += newWeight * edgeAtNodeWeight;

				this.sum3 -= edgeAtNodeWeight
						* (2 * oldEdgeWeight * oldNode1Degree - oldEdgeWeight
								* oldEdgeWeight);
				this.sum3 += edgeAtNodeWeight
						* (2 * newWeight * newNode1Degree - newWeight
								* newWeight);
			}
		}

		for (IElement iElement : node2.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = ((IntWeight) edgeAtNode.getWeight())
						.getWeight();
				otherNodeDegree = this
						.intWeightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node2));

				this.sum1 -= oldEdgeWeight * edgeAtNodeWeight * otherNodeDegree;
				this.sum1 += newWeight * edgeAtNodeWeight * otherNodeDegree;

				this.sum2 -= oldEdgeWeight * edgeAtNodeWeight;
				this.sum2 += newWeight * edgeAtNodeWeight;

				this.sum3 -= edgeAtNodeWeight
						* (2 * oldEdgeWeight * oldNode2Degree - oldEdgeWeight
								* oldEdgeWeight);
				this.sum3 += edgeAtNodeWeight
						* (2 * newWeight * newNode2Degree - newWeight
								* newWeight);
			}
		}

		// update weighted degree
		this.decreaseWeightedDegree(node1, oldEdgeWeight);
		this.increaseWeightedDegree(node1, newWeight);
		this.decreaseWeightedDegree(node2, oldEdgeWeight);
		this.increaseWeightedDegree(node2, newWeight);

		this.setR();

		return true;
	}

}
