package dna.metrics.assortativity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IBeforeUpdatesWeighted;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.util.Log;
import dna.util.parameters.Parameter;

/**
 * {@link IDynamicAlgorithm} of {@link AssortativityWeighted}.
 */
public class AssortativityU extends Assortativity implements
		IBeforeUpdatesWeighted {

	/** Stores the weighted degree of each node. */
	private HashMap<Node, Double> weightedDegrees;

	/**
	 * Initializes {@link AssortativityWeighetdU}. Implicitly sets degree type
	 * for directed graphs to outdegree and ignores wedge weights (if any).
	 */
	public AssortativityU() {
		super("AssortativityU");
	}

	/**
	 * Initializes {@link AssortativityWeighetdU}.
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
	public AssortativityU(Parameter directedDegreeType, Parameter edgeWeightType) {
		super("AssortativityU", directedDegreeType, edgeWeightType);
	}

	private double _addAgain(double addAgain,
			DirectedWeightedEdge edge_NodeNeighbor_OtherNodeNeighbor,
			DirectedWeightedEdge edge_Node_NodeNeighbor,
			DirectedWeightedEdge edge_Node_OtherNodeNeighbor) {
		addAgain += this
				.weight(edge_NodeNeighbor_OtherNodeNeighbor.getWeight())
				* this.weight(edge_Node_NodeNeighbor.getWeight())
				* this.weight(edge_Node_OtherNodeNeighbor.getWeight());
		return addAgain;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(USE_EDGE_WEIGHTS))
				return this.updateForEdgeAddition((DirectedWeightedEdge) ea
						.getEdge());
			else if (this.edgeWeightType.equals(IGNORE_EDGE_WEIGHTS))
				return this.updateForEdgeAddition((DirectedEdge) ea.getEdge());
			else
				Log.error("Graph is weighted but edge weight type set is neither '"
						+ IGNORE_EDGE_WEIGHTS
						+ "' (default) nor '"
						+ USE_EDGE_WEIGHTS + "'.");

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(USE_EDGE_WEIGHTS))
				return this.updateForEdgeAddition((UndirectedWeightedEdge) ea
						.getEdge());
			else if (this.edgeWeightType.equals(IGNORE_EDGE_WEIGHTS))
				return this
						.updateForEdgeAddition((UndirectedEdge) ea.getEdge());
			else
				Log.error("Graph is weighted but edge weight type set is neither '"
						+ IGNORE_EDGE_WEIGHTS
						+ "' (default) nor '"
						+ USE_EDGE_WEIGHTS + "'.");

		} else if (DirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			return this.updateForEdgeAddition((DirectedEdge) ea.getEdge());

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			return this.updateForEdgeAddition((UndirectedEdge) ea.getEdge());

		}

		return false;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(USE_EDGE_WEIGHTS))
				return this.updateForEdgeRemoval((DirectedWeightedEdge) er
						.getEdge());
			else if (this.edgeWeightType.equals(IGNORE_EDGE_WEIGHTS))
				return this.updateForEdgeRemoval((DirectedEdge) er.getEdge());
			else
				Log.error("Graph is weighted but edge weight type set is neither '"
						+ IGNORE_EDGE_WEIGHTS
						+ "' (default) nor '"
						+ USE_EDGE_WEIGHTS + "'.");

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(USE_EDGE_WEIGHTS))
				return this.updateForEdgeRemoval((UndirectedWeightedEdge) er
						.getEdge());
			else if (this.edgeWeightType.equals(IGNORE_EDGE_WEIGHTS))
				return this.updateForEdgeRemoval((UndirectedEdge) er.getEdge());
			else
				Log.error("Graph is weighted but edge weight type set is neither '"
						+ IGNORE_EDGE_WEIGHTS
						+ "' (default) nor '"
						+ USE_EDGE_WEIGHTS + "'.");

		} else if (DirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			return this.updateForEdgeRemoval((DirectedEdge) er.getEdge());

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			return this.updateForEdgeRemoval((UndirectedEdge) er.getEdge());

		}

		return false;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeWeight ew) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {
			return this.updateForEdgeWeight(
					(DirectedWeightedEdge) ew.getEdge(),
					this.weight(ew.getWeight()));
		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {
			return this.updateForEdgeWeight(
					(UndirectedWeightedEdge) ew.getEdge(),
					this.weight(ew.getWeight()));
		}

		return false;
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		// nothing to do
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(USE_EDGE_WEIGHTS))
				return this
						.directedWeightedUpdateForNodeRemoval((DirectedNode) nr
								.getNode());
			else if (this.edgeWeightType.equals(IGNORE_EDGE_WEIGHTS))
				return this.updateForNodeRemoval((DirectedNode) nr.getNode());
			else
				Log.error("Graph is weighted but edge weight type set is neither '"
						+ IGNORE_EDGE_WEIGHTS
						+ "' (default) nor '"
						+ USE_EDGE_WEIGHTS + "'.");

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(USE_EDGE_WEIGHTS))
				return this
						.undirectedWeightedUpdateForNodeRemoval((UndirectedNode) nr
								.getNode());
			else if (this.edgeWeightType.equals(IGNORE_EDGE_WEIGHTS))
				return this.updateForNodeRemoval((UndirectedNode) nr.getNode());
			else
				Log.error("Graph is weighted but edge weight type set is neither '"
						+ IGNORE_EDGE_WEIGHTS
						+ "' (default) nor '"
						+ USE_EDGE_WEIGHTS + "'.");

		} else if (DirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			return this.updateForNodeRemoval((DirectedNode) nr.getNode());

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			return this.updateForNodeRemoval((UndirectedNode) nr.getNode());

		}

		return false;
	}

	@Override
	public boolean applyBeforeUpdate(NodeWeight nw) {
		// nothing to do
		return true;
	}

	private void decreaseSum123AtNode(double nodeADegree, double nodeBDegree) {
		this.sum1 -= nodeADegree;
		this.sum2 -= 1;
		this.sum3 -= 2 * (nodeBDegree - 1) + 1;
	}

	private void decreaseSum123AtNode(double edgeWeight,
			double edgeAtOtherNode1Weight, double otherNode1Degree,
			double otherNode2Degree) {
		this.sum1 -= edgeWeight * edgeAtOtherNode1Weight * otherNode2Degree;

		this.sum2 -= edgeWeight * edgeAtOtherNode1Weight;

		this.sum3 -= edgeAtOtherNode1Weight
				* (2 * edgeWeight * otherNode1Degree - edgeWeight * edgeWeight);
	}

	private void decreaseSum123AtNodeForAllEdges(DirectedWeightedEdge edge,
			final double edgeWeight, final DirectedNode srcNode,
			double srcNodeDegree) {
		DirectedWeightedEdge edgeAtNode;
		double edgeAtNodeWeight;
		double otherNodeDegree;
		for (IElement iElement : srcNode.getEdges()) {
			edgeAtNode = (DirectedWeightedEdge) iElement;
			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = this.weight(edgeAtNode.getWeight());
				otherNodeDegree = this.weightedDegree((DirectedNode) edgeAtNode
						.getDifferingNode(srcNode));

				decreaseSum123AtNode(edgeWeight, edgeAtNodeWeight,
						srcNodeDegree, otherNodeDegree);
			}
		}

		// update weighted degree
		this.decreaseWeightedDegree(srcNode, edgeWeight);
	}

	private void decreaseSum123AtNodeForAllEdges(UndirectedEdge edge,
			final UndirectedNode node1, final int node1Degree) {
		UndirectedEdge edgeAtNode;
		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedEdge) iElement;
			if (!edgeAtNode.equals(edge)) {
				this.decreaseSum123AtNode(edgeAtNode.getDifferingNode(node1)
						.getDegree(), node1Degree);
			}
		}
	}

	private void decreaseSum123AtNodeForAllEdges(UndirectedWeightedEdge edge,
			final double edgeWeight, final UndirectedNode node1,
			final double node1Degree) {
		UndirectedWeightedEdge edgeAtNode;
		double edgeAtNodeWeight;
		double otherNodeDegree;
		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = this.weight(edgeAtNode.getWeight());
				otherNodeDegree = this
						.weightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node1));

				decreaseSum123AtNode(edgeWeight, edgeAtNodeWeight, node1Degree,
						otherNodeDegree);
			}
		}
	}

	/**
	 * Decreases stored weighted degree of given node by given weight.
	 * 
	 * @param node
	 *            The node which weighted degree should be decreased.
	 * @param weight
	 *            The weight by which the degree should be decreased.
	 * 
	 * @see AssortativityU#weightedDegrees
	 */
	private void decreaseWeightedDegree(Node node, double weight) {
		if (this.weightedDegrees.containsKey(node))
			this.weightedDegrees.put(node, this.weightedDegrees.get(node)
					- weight);
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for the outdated {@link DirectedNode} and
	 * {@link DirectedDoubleWeightedEdge}s.
	 * 
	 * @param node
	 *            The outdated node.
	 * @return true
	 */
	private boolean directedWeightedUpdateForNodeRemoval(DirectedNode node) {

		if (this.directedDegreeType.equals("out")) {

			final double nodeOutDegree = this.weightedDegree(node);
			final Set<Node> nodeNeighbors = new HashSet<Node>();

			DirectedWeightedEdge edge, edgeAtOtherNode2;
			double edgeWeight, edgeAtOtherNode2Weight;
			DirectedNode otherNode1, otherNode2;
			double otherNode1OutDegree, otherNode2OutDegree;
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = this.weight(edge.getWeight());

				this.totalEdgeWeight -= edgeWeight;

				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				otherNode1OutDegree = this.weightedDegree(otherNode1);

				this.decreaseSum123(nodeOutDegree, otherNode1OutDegree,
						edgeWeight);

				if (edge.getDst().equals(node)) {
					// only for otherNode1s at incoming edges of node do the
					// following

					nodeNeighbors.add(otherNode1);

					for (IElement iElement2 : otherNode1.getEdges()) {
						edgeAtOtherNode2 = ((DirectedWeightedEdge) iElement2);
						otherNode2 = (DirectedNode) edgeAtOtherNode2
								.getDifferingNode(otherNode1);

						if (!otherNode2.equals(node)) {
							edgeAtOtherNode2Weight = this
									.weight(edgeAtOtherNode2.getWeight());
							otherNode2OutDegree = this
									.weightedDegree(otherNode2);

							decreaseSum123AtNode(edgeWeight,
									edgeAtOtherNode2Weight,
									otherNode1OutDegree, otherNode2OutDegree);
						}
					}
					// too much is subtracted from sum1 if any of the neighbors
					// of otherNode1 are connected among themselves; add it
					// again
					this.sum1 += this
							.weightOfDirectedEdgesWithinNeighborsOfNode(node,
									otherNode1, nodeNeighbors);
				}
			}

			// update weighted degree
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = this.weight(edge.getWeight());
				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				if (edge.getDst().equals(node))
					this.decreaseWeightedDegree(otherNode1, edgeWeight);
			}

		} else if (this.directedDegreeType.equals("in")) {

			final double nodeInDegree = this.weightedDegree(node);
			final Set<Node> nodeNeighbors = new HashSet<Node>();

			DirectedWeightedEdge edge, edgeAtOtherNode2;
			double edgeWeight, edgeAtOtherNode2Weight;
			DirectedNode otherNode1, otherNode2;
			double otherNode1InDegree, otherNode2InDegree;
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = this.weight(edge.getWeight());

				this.totalEdgeWeight -= edgeWeight;

				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				otherNode1InDegree = this.weightedDegree(otherNode1);

				this.decreaseSum123(nodeInDegree, otherNode1InDegree,
						edgeWeight);

				if (edge.getSrc().equals(node)) {
					// only for otherNode1s at outgoing edges of node do the
					// following

					nodeNeighbors.add(otherNode1);

					for (IElement iElement2 : otherNode1.getEdges()) {
						edgeAtOtherNode2 = ((DirectedWeightedEdge) iElement2);
						otherNode2 = (DirectedNode) edgeAtOtherNode2
								.getDifferingNode(otherNode1);

						if (!otherNode2.equals(node)) {
							edgeAtOtherNode2Weight = this
									.weight(edgeAtOtherNode2.getWeight());
							otherNode2InDegree = this
									.weightedDegree(otherNode2);

							decreaseSum123AtNode(edgeWeight,
									edgeAtOtherNode2Weight, otherNode1InDegree,
									otherNode2InDegree);
						}
					}
					// too much is subtracted from sum1 if any of the neighbors
					// of otherNode1 are connected among themselves; add it
					// again
					this.sum1 += this
							.weightOfDirectedEdgesWithinNeighborsOfNode(node,
									otherNode1, nodeNeighbors);
				}
			}

			// update weighted degree
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedWeightedEdge) iElement1;
				edgeWeight = this.weight(edge.getWeight());
				otherNode1 = (DirectedNode) edge.getDifferingNode(node);
				if (edge.getSrc().equals(node))
					this.decreaseWeightedDegree(otherNode1, edgeWeight);
			}

		} else
			Log.error(COMPUTE_FOR_DEGREE_ERROR);

		this.setR();

		return true;
	}

	private void increaseSum123AtNode(double nodeADegree, double nodeBDegree) {
		this.sum1 += nodeADegree;
		this.sum2 += 1;
		this.sum3 += 2 * (nodeBDegree - 1) + 1;
	}

	private void increaseSum123AtNodeForAllEdges(DirectedWeightedEdge edge,
			final double edgeWeight, final DirectedNode srcNode,
			double srcNodeDegree) {
		DirectedWeightedEdge edgeAtNode;
		double edgeAtNodeWeight;
		double otherNodeDegree;
		for (IElement iElement : srcNode.getEdges()) {
			edgeAtNode = (DirectedWeightedEdge) iElement;
			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = this.weight(edgeAtNode.getWeight());
				otherNodeDegree = this.weightedDegree((DirectedNode) edgeAtNode
						.getDifferingNode(srcNode));

				this.sum1 += edgeWeight * edgeAtNodeWeight * otherNodeDegree;

				this.sum2 += edgeWeight * edgeAtNodeWeight;

				this.sum3 += edgeAtNodeWeight
						* (2 * edgeWeight * srcNodeDegree - edgeWeight
								* edgeWeight);
			}
		}

		// update weighted degree
		this.increaseWeightedDegree(srcNode, edgeWeight);
	}

	private void increaseSum123AtNodeForAllEdges(UndirectedEdge edge,
			final UndirectedNode node1, final int node1Degree) {
		UndirectedEdge edgeAtNode;
		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedEdge) iElement;
			if (!edgeAtNode.equals(edge)) {
				this.increaseSum123AtNode(edgeAtNode.getDifferingNode(node1)
						.getDegree(), node1Degree);
			}
		}
	}

	private void increaseSum123AtNodeForAllEdges(UndirectedWeightedEdge edge,
			final double edgeWeight, final UndirectedNode node1,
			final double node1Degree) {
		UndirectedWeightedEdge edgeAtNode;
		double edgeAtNodeWeight;
		double otherNodeDegree;
		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = this.weight(edgeAtNode.getWeight());
				otherNodeDegree = this
						.weightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node1));

				this.sum1 += edgeWeight * edgeAtNodeWeight * otherNodeDegree;
				this.sum2 += edgeWeight * edgeAtNodeWeight;
				this.sum3 += edgeAtNodeWeight
						* (2 * edgeWeight * node1Degree - edgeWeight
								* edgeWeight);
			}
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
	 * @see AssortativityU#weightedDegrees
	 */
	private void increaseWeightedDegree(Node node, double weight) {
		if (this.weightedDegrees.containsKey(node))
			this.weightedDegrees.put(node, this.weightedDegrees.get(node)
					+ weight);
	}

	@Override
	public boolean init() {
		this.totalEdgeWeight = 0.0;

		this.sum1 = 0.0;
		this.sum2 = 0.0;
		this.sum3 = 0.0;

		this.r = 0.0;

		this.weightedDegrees = new HashMap<Node, Double>();

		return this.compute();
	}

	/**
	 * @return The total number of edges between the given node and every node
	 *         in the given set of nodes.
	 */
	private int numberOfEdgesBetweenNodeAndNodes(Node node, Set<Node> nodes) {
		final GraphDataStructure gds = this.g.getGraphDatastructures();

		int connectedNodes = 0;

		if (DirectedEdge.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getEdgeType())) {
			// directed graph

			for (Node otherNode : nodes) {
				if (this.g.containsEdge(gds.newEdgeInstance(node, otherNode)))
					connectedNodes++;
				if (this.g.containsEdge(gds.newEdgeInstance(otherNode, node)))
					connectedNodes++;
			}

		} else {
			// undirected graph

			for (Node otherNode : nodes)
				if (this.g.containsEdge(gds.newEdgeInstance(node, otherNode)))
					connectedNodes++;
		}

		return connectedNodes;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for the outdated {@link UndirectedNode} and
	 * {@link UndirectedDoubleWeightedEdge} s.
	 * 
	 * @param node
	 *            The outdated node.
	 * @return true
	 */
	private boolean undirectedWeightedUpdateForNodeRemoval(UndirectedNode node) {
		final double nodeDegree = this.weightedDegree(node);
		final Set<Node> nodeNeighbors = new HashSet<Node>();

		UndirectedWeightedEdge edge, edgeAtOtherNode1;
		double edgeWeight, edgeAtOtherNode1Weight;
		UndirectedNode otherNode1, otherNode2;
		double otherNode1Degree, otherNode2Degree;
		for (IElement iElement1 : node.getEdges()) {
			edge = (UndirectedWeightedEdge) iElement1;
			edgeWeight = this.weight(edge.getWeight());

			this.totalEdgeWeight -= edgeWeight;

			otherNode1 = (UndirectedNode) edge.getDifferingNode(node);
			nodeNeighbors.add(otherNode1);
			otherNode1Degree = this.weightedDegree(otherNode1);

			this.decreaseSum123(nodeDegree, otherNode1Degree, edgeWeight);

			for (IElement iElement2 : otherNode1.getEdges()) {
				edgeAtOtherNode1 = ((UndirectedWeightedEdge) iElement2);
				otherNode2 = (UndirectedNode) edgeAtOtherNode1
						.getDifferingNode(otherNode1);

				if (!otherNode2.equals(node)) {
					edgeAtOtherNode1Weight = this.weight(edgeAtOtherNode1
							.getWeight());
					otherNode2Degree = this.weightedDegree(otherNode2);

					decreaseSum123AtNode(edgeWeight, edgeAtOtherNode1Weight,
							otherNode1Degree, otherNode2Degree);
				}
			}
			// too much is subtracted from sum1 if any of the neighbors of
			// otherNode1 are connected among themselves; add it again
			this.sum1 += this.weightOfUndirectedEdgesWithinNeighborsOfNode(
					node, otherNode1, nodeNeighbors);
		}

		// update weighted degree
		for (IElement iElement1 : node.getEdges()) {
			edge = (UndirectedWeightedEdge) iElement1;
			edgeWeight = this.weight(edge.getWeight());
			otherNode1 = (UndirectedNode) edge.getDifferingNode(node);
			this.decreaseWeightedDegree(otherNode1, edgeWeight);
		}

		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR(int)}
	 * for a new {@link DirectedEdge}.
	 * 
	 * @param edge
	 *            The new edge.
	 * @return true
	 */
	private boolean updateForEdgeAddition(DirectedEdge edge) {
		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		int srcNodeDegree;
		int dstNodeDegree;

		if (this.directedDegreeType.equals("out")) {
			srcNodeDegree = srcNode.getOutDegree() + 1;
			dstNodeDegree = dstNode.getOutDegree();
		} else if (this.directedDegreeType.equals("in")) {
			srcNodeDegree = srcNode.getInDegree();
			dstNodeDegree = dstNode.getInDegree() + 1;
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		this.increaseSum123(srcNodeDegree, dstNodeDegree);

		DirectedEdge edgeAtNode;
		int otherNodeDegree;

		if (this.directedDegreeType.equals("out")) {
			for (IElement iElement : srcNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;
				if (!edgeAtNode.equals(edge)) {
					otherNodeDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(srcNode)).getOutDegree();
					this.increaseSum123AtNode(otherNodeDegree, srcNodeDegree);
				}
			}
		} else if (this.directedDegreeType.equals("in")) {
			for (IElement iElement : dstNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;
				if (!edgeAtNode.equals(edge)) {
					otherNodeDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(dstNode)).getInDegree();
					this.increaseSum123AtNode(otherNodeDegree, dstNodeDegree);
				}
			}
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		this.totalEdgeWeight = this.g.getEdgeCount() + 1;
		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for a new {@link DirectedDoubleWeightedEdge}.
	 * 
	 * @param edge
	 *            The new edge.
	 * @return true
	 */
	private boolean updateForEdgeAddition(DirectedWeightedEdge edge) {
		final double edgeWeight = this.weight(edge.getWeight());

		this.totalEdgeWeight += edgeWeight;

		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		double srcNodeDegree;
		double dstNodeDegree;

		if (this.directedDegreeType.equals("out")) {
			srcNodeDegree = this.weightedDegree(srcNode)
					+ this.weight(edge.getWeight());
			dstNodeDegree = this.weightedDegree(dstNode);
		} else if (this.directedDegreeType.equals("in")) {
			srcNodeDegree = this.weightedDegree(srcNode);
			dstNodeDegree = this.weightedDegree(dstNode)
					+ this.weight(edge.getWeight());
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		this.increaseSum123(srcNodeDegree, dstNodeDegree, edgeWeight);

		if (this.directedDegreeType.equals("out")) {
			increaseSum123AtNodeForAllEdges(edge, edgeWeight, srcNode,
					srcNodeDegree);
		} else if (this.directedDegreeType.equals("in")) {
			increaseSum123AtNodeForAllEdges(edge, edgeWeight, dstNode,
					dstNodeDegree);
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR(int)}
	 * for a new {@link UndirectedEdge}.
	 * 
	 * @param edge
	 *            The new edge.
	 * @return true
	 */
	private boolean updateForEdgeAddition(UndirectedEdge edge) {
		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final int node1Degree = node1.getDegree() + 1;
		final int node2Degree = node2.getDegree() + 1;

		this.increaseSum123(node1Degree, node2Degree);
		this.increaseSum123AtNodeForAllEdges(edge, node1, node1Degree);
		this.increaseSum123AtNodeForAllEdges(edge, node2, node2Degree);

		this.totalEdgeWeight = this.g.getEdgeCount() + 1;
		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for a new {@link UndirectedDoubleWeightedEdge}.
	 * 
	 * @param edge
	 *            The new edge.
	 * @return true
	 */
	private boolean updateForEdgeAddition(UndirectedWeightedEdge edge) {
		final double edgeWeight = this.weight(edge.getWeight());

		this.totalEdgeWeight += edgeWeight;

		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final double node1Degree = this.weightedDegree(node1)
				+ this.weight(edge.getWeight());
		final double node2Degree = this.weightedDegree(node2)
				+ this.weight(edge.getWeight());

		this.increaseSum123(node1Degree, node2Degree, edgeWeight);
		this.increaseSum123AtNodeForAllEdges(edge, edgeWeight, node1,
				node1Degree);
		this.increaseSum123AtNodeForAllEdges(edge, edgeWeight, node2,
				node2Degree);

		// update weighted degree
		this.increaseWeightedDegree(node1, edgeWeight);
		this.increaseWeightedDegree(node2, edgeWeight);

		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR(int)}
	 * for the outdated {@link DirectedEdge}.
	 * 
	 * @param edge
	 *            The outdated edge.
	 * @return true
	 */
	private boolean updateForEdgeRemoval(DirectedEdge edge) {
		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		int srcNodeDegree;
		int dstNodeDegree;

		if (this.directedDegreeType.equals("out")) {
			srcNodeDegree = srcNode.getOutDegree();
			dstNodeDegree = dstNode.getOutDegree();
		} else if (this.directedDegreeType.equals("in")) {
			srcNodeDegree = srcNode.getInDegree();
			dstNodeDegree = dstNode.getInDegree();
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		this.decreaseSum123(srcNodeDegree, dstNodeDegree);

		DirectedEdge edgeAtNode;
		int otherNodeDegree;

		if (this.directedDegreeType.equals("out")) {
			for (IElement iElement : srcNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;
				if (!edgeAtNode.equals(edge)) {
					otherNodeDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(srcNode)).getOutDegree();
					this.decreaseSum123AtNode(otherNodeDegree, srcNodeDegree);
				}
			}
		} else if (this.directedDegreeType.equals("in")) {
			for (IElement iElement : dstNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;
				if (!edgeAtNode.equals(edge)) {
					otherNodeDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(dstNode)).getInDegree();
					this.decreaseSum123AtNode(otherNodeDegree, dstNodeDegree);
				}
			}
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		this.totalEdgeWeight = this.g.getEdgeCount() - 1;
		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for the outdated {@link DirectedDoubleWeightedEdge}.
	 * 
	 * @param edge
	 *            The outdated edge.
	 * @return true
	 */
	private boolean updateForEdgeRemoval(DirectedWeightedEdge edge) {
		final double edgeWeight = this.weight(edge.getWeight());

		this.totalEdgeWeight -= edgeWeight;

		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		double srcNodeDegree = this.weightedDegree(srcNode);
		double dstNodeDegree = this.weightedDegree(dstNode);

		this.decreaseSum123(srcNodeDegree, dstNodeDegree, edgeWeight);

		if (this.directedDegreeType.equals("out")) {
			decreaseSum123AtNodeForAllEdges(edge, edgeWeight, srcNode,
					srcNodeDegree);
		} else if (this.directedDegreeType.equals("in")) {
			decreaseSum123AtNodeForAllEdges(edge, edgeWeight, dstNode,
					dstNodeDegree);
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR(int)}
	 * for the outdated {@link UndirectedEdge}.
	 * 
	 * @param edge
	 *            The outdated edge.
	 * @return true
	 */
	private boolean updateForEdgeRemoval(UndirectedEdge edge) {
		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final int node1Degree = node1.getDegree();
		final int node2Degree = node2.getDegree();

		this.decreaseSum123(node1Degree, node2Degree);
		this.decreaseSum123AtNodeForAllEdges(edge, node1, node1Degree);
		this.decreaseSum123AtNodeForAllEdges(edge, node2, node2Degree);

		this.totalEdgeWeight = this.g.getEdgeCount() - 1;
		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for the outdated {@link UndirectedDoubleWeightedEdge}.
	 * 
	 * @param edge
	 *            The outdated edge.
	 * @return true
	 */
	private boolean updateForEdgeRemoval(UndirectedWeightedEdge edge) {
		final double edgeWeight = this.weight(edge.getWeight());

		this.totalEdgeWeight -= edgeWeight;

		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final double node1Degree = this.weightedDegree(node1);
		final double node2Degree = this.weightedDegree(node2);

		this.decreaseSum123(node1Degree, node2Degree, edgeWeight);
		this.decreaseSum123AtNodeForAllEdges(edge, edgeWeight, node1,
				node1Degree);
		this.decreaseSum123AtNodeForAllEdges(edge, edgeWeight, node2,
				node2Degree);

		// update weighted degree
		this.decreaseWeightedDegree(node1, edgeWeight);
		this.decreaseWeightedDegree(node2, edgeWeight);

		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for the newly weighted {@link DirectedDoubleWeightedEdge}.
	 * 
	 * @param edge
	 *            The newly weighted edge.
	 * @return true
	 */
	private boolean updateForEdgeWeight(DirectedWeightedEdge edge,
			double newWeight) {
		final double oldEdgeWeight = this.weight(edge.getWeight());

		this.totalEdgeWeight -= oldEdgeWeight;
		this.totalEdgeWeight += newWeight;

		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		if (this.directedDegreeType.equals("out")) {

			final double oldSrcNodeOutDegree = this.weightedDegree(srcNode);
			final double oldDstNodeOutDegree = this.weightedDegree(dstNode);
			final double newSrcNodeOutDegree = oldSrcNodeOutDegree
					- oldEdgeWeight + newWeight;
			final double newDstNodeOutDegree = oldDstNodeOutDegree;

			updateSum123(newWeight, oldEdgeWeight, oldSrcNodeOutDegree,
					oldDstNodeOutDegree, newSrcNodeOutDegree,
					newDstNodeOutDegree);

			updateSum123AtNodeForAllEdges(edge, newWeight, oldEdgeWeight,
					srcNode, oldSrcNodeOutDegree, newSrcNodeOutDegree);

			// update weighted degree
			this.decreaseWeightedDegree(srcNode, oldEdgeWeight);
			this.increaseWeightedDegree(srcNode, newWeight);

		} else if (this.directedDegreeType.equals("in")) {

			final double oldSrcNodeInDegree = this.weightedDegree(srcNode);
			final double oldDstNodeInDegree = this.weightedDegree(dstNode);
			final double newSrcNodeInDegree = oldSrcNodeInDegree;
			final double newDstNodeInDegree = oldDstNodeInDegree
					- oldEdgeWeight + newWeight;

			updateSum123(newWeight, oldEdgeWeight, oldSrcNodeInDegree,
					oldDstNodeInDegree, newSrcNodeInDegree, newDstNodeInDegree);

			updateSum123AtNodeForAllEdges(edge, newWeight, oldEdgeWeight,
					dstNode, oldDstNodeInDegree, newDstNodeInDegree);

			// update weighted degree
			this.decreaseWeightedDegree(dstNode, oldEdgeWeight);
			this.increaseWeightedDegree(dstNode, newWeight);

		} else
			Log.error(COMPUTE_FOR_DEGREE_ERROR);

		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR()}
	 * for the newly weighted {@link UndirectedDoubleWeightedEdge}.
	 * 
	 * @param edge
	 *            The newly weighted edge.
	 * @return true
	 */
	private boolean updateForEdgeWeight(UndirectedWeightedEdge edge,
			double newWeight) {
		final double oldEdgeWeight = this.weight(edge.getWeight());

		this.totalEdgeWeight -= oldEdgeWeight;
		this.totalEdgeWeight += newWeight;

		final UndirectedNode node1 = edge.getNode1();
		final UndirectedNode node2 = edge.getNode2();
		final double oldNode1Degree = this.weightedDegree(node1);
		final double oldNode2Degree = this.weightedDegree(node2);
		final double newNode1Degree = oldNode1Degree - oldEdgeWeight
				+ newWeight;
		final double newNode2Degree = oldNode2Degree - oldEdgeWeight
				+ newWeight;

		updateSum123(newWeight, oldEdgeWeight, oldNode1Degree, oldNode2Degree,
				newNode1Degree, newNode2Degree);

		updateSum123AtNodeForAllEdges(edge, newWeight, oldEdgeWeight, node1,
				oldNode1Degree, newNode1Degree);

		updateSum123AtNodeForAllEdges(edge, newWeight, oldEdgeWeight, node2,
				oldNode2Degree, newNode2Degree);

		// update weighted degree
		this.decreaseWeightedDegree(node1, oldEdgeWeight);
		this.increaseWeightedDegree(node1, newWeight);
		this.decreaseWeightedDegree(node2, oldEdgeWeight);
		this.increaseWeightedDegree(node2, newWeight);

		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR(int)}
	 * for the outdated {@link DirectedNode}.
	 * 
	 * @param node
	 *            The outdated node.
	 * @return true
	 */
	private boolean updateForNodeRemoval(DirectedNode node) {
		int nodeDegree;
		Set<Node> nodeNeighbors = new HashSet<Node>();

		if (this.directedDegreeType.equals("out")) {
			nodeDegree = node.getOutDegree();
		} else if (this.directedDegreeType.equals("in")) {
			nodeDegree = node.getInDegree();
		} else {
			Log.error(COMPUTE_FOR_DEGREE_ERROR);
			return false;
		}

		DirectedEdge edge;
		DirectedNode otherNode1, otherNode2;
		int otherNode1Degree;

		for (IElement iElement1 : node.getEdges()) {
			edge = (DirectedEdge) iElement1;
			otherNode1 = (DirectedNode) edge.getDifferingNode(node);

			if (this.directedDegreeType.equals("out")) {
				otherNode1Degree = otherNode1.getOutDegree();
			} else if (this.directedDegreeType.equals("in")) {
				otherNode1Degree = otherNode1.getInDegree();
			} else {
				Log.error(COMPUTE_FOR_DEGREE_ERROR);
				return false;
			}

			this.decreaseSum123(nodeDegree, otherNode1Degree);

			if ((this.directedDegreeType.equals("out") && edge.getDst().equals(
					node))
					|| (this.directedDegreeType.equals("in") && edge.getSrc()
							.equals(node))) {
				// only for otherNode1s at incoming (for "out") and outgoing
				// (for "in") edges respectively of node do the following

				nodeNeighbors.add(otherNode1);

				for (IElement iElement2 : otherNode1.getEdges()) {
					otherNode2 = (DirectedNode) ((DirectedEdge) iElement2)
							.getDifferingNode(otherNode1);

					if (!otherNode2.equals(node)) {
						if (this.directedDegreeType.equals("out")) {
							this.sum1 -= otherNode2.getOutDegree();
						} else if (this.directedDegreeType.equals("in")) {
							this.sum1 -= otherNode2.getInDegree();
						} else {
							Log.error(COMPUTE_FOR_DEGREE_ERROR);
							return false;
						}
						this.sum2 -= 1;
						this.sum3 -= 2 * otherNode1Degree - 1;
					}
				}

				this.sum1 += this.numberOfEdgesBetweenNodeAndNodes(otherNode1,
						nodeNeighbors);
			} else if ((!this.directedDegreeType.equals("out"))
					&& (!this.directedDegreeType.equals("in"))) {
				Log.error(COMPUTE_FOR_DEGREE_ERROR);
				return false;
			}
		}

		this.totalEdgeWeight = this.g.getEdgeCount() - node.getDegree();
		this.setR();

		return true;
	}

	/**
	 * Updates {@link AssortativityUnweighted#sum1},
	 * {@link AssortativityUnweighted#sum2},
	 * {@link AssortativityUnweighted#sum3} and finally calls {@link #setR(int)}
	 * for the outdated {@link UndirectedNode}.
	 * 
	 * @param node
	 *            The outdated node.
	 * @return true
	 */
	private boolean updateForNodeRemoval(UndirectedNode node) {
		final int nodeDegree = node.getDegree();
		final Set<Node> nodeNeighbors = new HashSet<Node>();

		UndirectedNode otherNode1, otherNode2;
		int otherNode1Degree;
		for (IElement iElement1 : node.getEdges()) {
			otherNode1 = (UndirectedNode) ((UndirectedEdge) iElement1)
					.getDifferingNode(node);
			nodeNeighbors.add(otherNode1);

			otherNode1Degree = otherNode1.getDegree();

			this.decreaseSum123(nodeDegree, otherNode1Degree);

			for (IElement iElement2 : otherNode1.getEdges()) {
				otherNode2 = (UndirectedNode) ((UndirectedEdge) iElement2)
						.getDifferingNode(otherNode1);

				if (!otherNode2.equals(node)) {
					this.sum1 -= otherNode2.getDegree();
					this.sum2 -= 1;
					this.sum3 -= 2 * otherNode1Degree - 1;
				}
			}

			// too much is subtracted from sum1 if any of the neighbors of
			// otherNode1 are connected among themselves; add it again
			this.sum1 += this.numberOfEdgesBetweenNodeAndNodes(otherNode1,
					nodeNeighbors);
		}

		this.totalEdgeWeight = this.g.getEdgeCount() - nodeDegree;
		this.setR();

		return true;
	}

	private void updateSum123(double newWeight, final double oldEdgeWeight,
			final double oldNode1Degree, final double oldNode2Degree,
			final double newNode1Degree, final double newNode2Degree) {
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
	}

	private void updateSum123AtNode(double newWeight,
			final double oldEdgeWeight, final double oldNode2Degree,
			final double newNode2Degree, double edgeAtNodeWeight,
			double otherNodeDegree) {
		this.sum1 -= oldEdgeWeight * edgeAtNodeWeight * otherNodeDegree;
		this.sum1 += newWeight * edgeAtNodeWeight * otherNodeDegree;

		this.sum2 -= oldEdgeWeight * edgeAtNodeWeight;
		this.sum2 += newWeight * edgeAtNodeWeight;

		this.sum3 -= edgeAtNodeWeight
				* (2 * oldEdgeWeight * oldNode2Degree - oldEdgeWeight
						* oldEdgeWeight);
		this.sum3 += edgeAtNodeWeight
				* (2 * newWeight * newNode2Degree - newWeight * newWeight);
	}

	private void updateSum123AtNodeForAllEdges(DirectedWeightedEdge edge,
			double newWeight, final double oldEdgeWeight,
			final DirectedNode srcNode, final double oldSrcNodeOutDegree,
			final double newSrcNodeOutDegree) {
		DirectedWeightedEdge edgeAtNode;
		double edgeAtNodeWeight;
		double otherNodeOutDegree;
		for (IElement iElement : srcNode.getEdges()) {
			edgeAtNode = (DirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = this.weight(edgeAtNode.getWeight());
				otherNodeOutDegree = this
						.weightedDegree((DirectedNode) edgeAtNode
								.getDifferingNode(srcNode));

				updateSum123AtNode(newWeight, oldEdgeWeight,
						oldSrcNodeOutDegree, newSrcNodeOutDegree,
						edgeAtNodeWeight, otherNodeOutDegree);
			}
		}
	}

	private void updateSum123AtNodeForAllEdges(UndirectedWeightedEdge edge,
			double newWeight, final double oldEdgeWeight,
			final UndirectedNode node1, final double oldNode1Degree,
			final double newNode1Degree) {
		UndirectedWeightedEdge edgeAtNode;
		double edgeAtNodeWeight;
		double otherNodeDegree;
		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedWeightedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				edgeAtNodeWeight = this.weight(edgeAtNode.getWeight());
				otherNodeDegree = this
						.weightedDegree((UndirectedNode) edgeAtNode
								.getDifferingNode(node1));

				updateSum123AtNode(newWeight, oldEdgeWeight, oldNode1Degree,
						newNode1Degree, edgeAtNodeWeight, otherNodeDegree);
			}
		}
	}

	/**
	 * This method reads the weighted degree from
	 * {@link AssortativityU#weightedDegrees}. The value is only computed if it
	 * is not stored. It is updated by
	 * {@link AssortativityU#decreaseWeightedDegree(Node, long)} and
	 * {@link AssortativityU#increaseWeightedDegree(Node, long)}.
	 */
	@Override
	double weightedDegree(Node node) {
		if (this.weightedDegrees.containsKey(node))
			return this.weightedDegrees.get(node);
		else {
			final double weightedDegree = super.weightedDegree(node);
			this.weightedDegrees.put(node, weightedDegree);
			return weightedDegree;
		}
	}

	/**
	 * @return The total weight of all undirected edges between the given
	 *         neighbor of given node and every other neighbor node in the given
	 *         set of nodes.
	 */
	private double weightOfDirectedEdgesWithinNeighborsOfNode(Node node,
			Node nodeNeighbor, Set<Node> nodeNeighbors) {
		final GraphDataStructure gds = this.g.getGraphDatastructures();

		double addAgain = 0;

		DirectedWeightedEdge edge_NodeNeighbor_OtherNodeNeighbor, edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor;

		if (this.directedDegreeType.equals("out")) {

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

					addAgain = _addAgain(addAgain,
							edge_NodeNeighbor_OtherNodeNeighbor,
							edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor);
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

					addAgain = _addAgain(addAgain,
							edge_NodeNeighbor_OtherNodeNeighbor,
							edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor);
				}
			}

		} else if (this.directedDegreeType.equals("in")) {

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

					addAgain = _addAgain(addAgain,
							edge_NodeNeighbor_OtherNodeNeighbor,
							edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor);
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

					addAgain = _addAgain(addAgain,
							edge_NodeNeighbor_OtherNodeNeighbor,
							edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor);
				}

			}

		} else
			Log.error(COMPUTE_FOR_DEGREE_ERROR);

		return addAgain;
	}

	/**
	 * @return The total weight of all undirected edges between the given
	 *         neighbor of given node and every other neighbor node in the given
	 *         set of nodes.
	 */
	private double weightOfUndirectedEdgesWithinNeighborsOfNode(Node node,
			Node nodeNeighbor, Set<Node> nodeNeighbors) {
		double addAgain = 0;

		UndirectedWeightedEdge edge_NodeNeighbor_OtherNodeNeighbor, edge_Node_NodeNeighbor, edge_Node_OtherNodeNeighbor;

		for (Node otherNodeNeighbor : nodeNeighbors) {
			if (this.g.containsEdge(nodeNeighbor, otherNodeNeighbor)) {
				edge_NodeNeighbor_OtherNodeNeighbor = (UndirectedWeightedEdge) this.g
						.getEdge(nodeNeighbor, otherNodeNeighbor);
				edge_Node_NodeNeighbor = (UndirectedWeightedEdge) (this.g
						.getEdge(node, nodeNeighbor));
				edge_Node_OtherNodeNeighbor = (UndirectedWeightedEdge) (this.g
						.getEdge(node, otherNodeNeighbor));

				addAgain += this.weight(edge_NodeNeighbor_OtherNodeNeighbor
						.getWeight())
						* this.weight(edge_Node_NodeNeighbor.getWeight())
						* this.weight(edge_Node_OtherNodeNeighbor.getWeight());
			}
		}

		return addAgain;
	}

}
