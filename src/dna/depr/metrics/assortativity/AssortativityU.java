package dna.depr.metrics.assortativity;

import java.util.HashSet;
import java.util.Set;

import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;
import dna.util.Log;
import dna.util.parameters.Parameter;

/**
 * {@link ApplicationType#BeforeUpdate} of {@link Assortativity}.
 */
public class AssortativityU extends Assortativity {

	/**
	 * Initializes {@link AssortativityU}. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 */
	public AssortativityU() {
		super("AssortativityU", ApplicationType.BeforeUpdate);
	}

	/**
	 * Initializes {@link AssortativityU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 */
	public AssortativityU(Parameter directedDegreeType) {
		super("AssortativityU", ApplicationType.BeforeUpdate,
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			if (u instanceof NodeRemoval) {
				return this
						.updateForNodeRemoval((DirectedNode) ((NodeRemoval) u)
								.getNode());
			} else if (u instanceof EdgeAddition) {
				return this
						.updateForEdgeAddition((DirectedEdge) ((EdgeAddition) u)
								.getEdge());
			} else if (u instanceof EdgeRemoval) {
				return this
						.updateForEdgeRemoval((DirectedEdge) ((EdgeRemoval) u)
								.getEdge());
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			if (u instanceof NodeRemoval) {
				return this
						.updateForNodeRemoval((UndirectedNode) ((NodeRemoval) u)
								.getNode());
			} else if (u instanceof EdgeAddition) {
				return this
						.updateForEdgeAddition((UndirectedEdge) ((EdgeAddition) u)
								.getEdge());
			} else if (u instanceof EdgeRemoval) {
				return this
						.updateForEdgeRemoval((UndirectedEdge) ((EdgeRemoval) u)
								.getEdge());
			}
		}

		return false;
	}

	/**
	 * @return The total number of directed edges between the given node and
	 *         every node in the given set of nodes.
	 */
	private int numberOfDirectedEdgesFromNodeToNodes(Node node, Set<Node> nodes) {
		final GraphDataStructure gds = this.g.getGraphDatastructures();

		int connectedNodes = 0;

		for (Node otherNode : nodes) {
			if (this.g.containsEdge(gds.newEdgeInstance(node, otherNode)))
				connectedNodes++;
			if (this.g.containsEdge(gds.newEdgeInstance(otherNode, node)))
				connectedNodes++;
		}

		return connectedNodes;
	}

	/**
	 * @return The total number of undirected edges between the given node and
	 *         every node in the given set of nodes.
	 */
	private int numberOfUndirectedEdgesFromNodeToNodes(Node node,
			Set<Node> nodes) {
		final GraphDataStructure gds = this.g.getGraphDatastructures();

		int connectedNodes = 0;

		for (Node otherNode : nodes)
			if (this.g.containsEdge(gds.newEdgeInstance(node, otherNode)))
				connectedNodes++;

		return connectedNodes;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR(int)} for a new
	 * {@link DirectedEdge}.
	 * 
	 * @param edge
	 *            The new edge.
	 * @return true
	 */
	private boolean updateForEdgeAddition(DirectedEdge edge) {
		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		if (this.directedDegreeType.equals("out")) {

			final int srcNodeOutDegree = srcNode.getOutDegree() + 1;
			final int dstNodeOutDegree = dstNode.getOutDegree();

			this.sum1 += srcNodeOutDegree * dstNodeOutDegree;

			this.sum2 += srcNodeOutDegree + dstNodeOutDegree;

			this.sum3 += srcNodeOutDegree * srcNodeOutDegree + dstNodeOutDegree
					* dstNodeOutDegree;

			DirectedEdge edgeAtNode;
			int otherNodeOutDegree;

			for (IElement iElement : srcNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					otherNodeOutDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(srcNode)).getOutDegree();

					this.sum1 += otherNodeOutDegree;

					this.sum2 += 1;

					this.sum3 += 2 * (srcNodeOutDegree - 1) + 1;
				}
			}

		} else if (this.directedDegreeType.equals("in")) {

			final int srcNodeInDegree = srcNode.getInDegree();
			final int dstNodeInDegree = dstNode.getInDegree() + 1;

			this.sum1 += srcNodeInDegree * dstNodeInDegree;

			this.sum2 += srcNodeInDegree + dstNodeInDegree;

			this.sum3 += srcNodeInDegree * srcNodeInDegree + dstNodeInDegree
					* dstNodeInDegree;

			DirectedEdge edgeAtNode;
			int otherNodeInDegree;

			for (IElement iElement : dstNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					otherNodeInDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(dstNode)).getInDegree();

					this.sum1 += otherNodeInDegree;

					this.sum2 += 1;

					this.sum3 += 2 * (dstNodeInDegree - 1) + 1;
				}
			}

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		this.setR(this.g.getEdgeCount() + 1);

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR(int)} for a new
	 * {@link UndirectedEdge}.
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

		this.sum1 += node1Degree * node2Degree;

		this.sum2 += node1Degree + node2Degree;

		this.sum3 += node1Degree * node1Degree + node2Degree * node2Degree;

		UndirectedEdge edgeAtNode;
		int otherNodeDegree;

		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				otherNodeDegree = edgeAtNode.getDifferingNode(node1)
						.getDegree();

				this.sum1 += otherNodeDegree;

				this.sum2 += 1;

				this.sum3 += 2 * (node1Degree - 1) + 1;
			}
		}

		for (IElement iElement : node2.getEdges()) {
			edgeAtNode = (UndirectedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				otherNodeDegree = edgeAtNode.getDifferingNode(node2)
						.getDegree();

				this.sum1 += otherNodeDegree;

				this.sum2 += 1;

				this.sum3 += 2 * (node2Degree - 1) + 1;
			}
		}

		this.setR(this.g.getEdgeCount() + 1);

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR(int)} for the
	 * outdated {@link DirectedEdge}.
	 * 
	 * @param edge
	 *            The outdated edge.
	 * @return true
	 */
	private boolean updateForEdgeRemoval(DirectedEdge edge) {
		final DirectedNode srcNode = edge.getSrc();
		final DirectedNode dstNode = edge.getDst();

		if (this.directedDegreeType.equals("out")) {

			final int srcNodeOutDegree = srcNode.getOutDegree();
			final int dstNodeOutDegree = dstNode.getOutDegree();

			this.sum1 -= srcNodeOutDegree * dstNodeOutDegree;

			this.sum2 -= srcNodeOutDegree + dstNodeOutDegree;

			this.sum3 -= srcNodeOutDegree * srcNodeOutDegree + dstNodeOutDegree
					* dstNodeOutDegree;

			DirectedEdge edgeAtNode;
			int otherNodeOutDegree;

			for (IElement iElement : srcNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					otherNodeOutDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(srcNode)).getOutDegree();

					this.sum1 -= otherNodeOutDegree;

					this.sum2 -= 1;

					this.sum3 -= 2 * srcNodeOutDegree - 1;
				}
			}

		} else if (this.directedDegreeType.equals("in")) {

			final int srcNodeInDegree = srcNode.getInDegree();
			final int dstNodeInDegree = dstNode.getInDegree();

			this.sum1 -= srcNodeInDegree * dstNodeInDegree;

			this.sum2 -= srcNodeInDegree + dstNodeInDegree;

			this.sum3 -= srcNodeInDegree * srcNodeInDegree + dstNodeInDegree
					* dstNodeInDegree;

			DirectedEdge edgeAtNode;
			int otherNodeInDegree;

			for (IElement iElement : dstNode.getEdges()) {
				edgeAtNode = (DirectedEdge) iElement;

				if (!edgeAtNode.equals(edge)) {
					otherNodeInDegree = ((DirectedNode) edgeAtNode
							.getDifferingNode(dstNode)).getInDegree();

					this.sum1 -= otherNodeInDegree;

					this.sum2 -= 1;

					this.sum3 -= 2 * dstNodeInDegree - 1;
				}
			}

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		this.setR(this.g.getEdgeCount() - 1);

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR(int)} for the
	 * outdated {@link UndirectedEdge}.
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

		this.sum1 -= node1Degree * node2Degree;

		this.sum2 -= node1Degree + node2Degree;

		this.sum3 -= node1Degree * node1Degree + node2Degree * node2Degree;

		UndirectedEdge edgeAtNode;
		int otherNodeDegree;

		for (IElement iElement : node1.getEdges()) {
			edgeAtNode = (UndirectedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				otherNodeDegree = edgeAtNode.getDifferingNode(node1)
						.getDegree();

				this.sum1 -= otherNodeDegree;

				this.sum2 -= 1;

				this.sum3 -= 2 * node1Degree - 1;
			}
		}

		for (IElement iElement : node2.getEdges()) {
			edgeAtNode = (UndirectedEdge) iElement;

			if (!edgeAtNode.equals(edge)) {
				otherNodeDegree = edgeAtNode.getDifferingNode(node2)
						.getDegree();

				this.sum1 -= otherNodeDegree;

				this.sum2 -= 1;

				this.sum3 -= 2 * node2Degree - 1;
			}
		}

		this.setR(this.g.getEdgeCount() - 1);

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR(int)} for the
	 * outdated {@link DirectedNode}.
	 * 
	 * @param node
	 *            The outdated node.
	 * @return true
	 */
	private boolean updateForNodeRemoval(DirectedNode node) {
		if (this.directedDegreeType.equals("out")) {

			final int nodeOutDegree = node.getOutDegree();
			final Set<Node> nodeNeighbors = new HashSet<Node>();

			DirectedEdge edge;
			DirectedNode otherNode1, otherNode2;
			int otherNode1OutDegree;
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedEdge) iElement1;
				otherNode1 = (DirectedNode) edge.getDifferingNode(node);

				otherNode1OutDegree = otherNode1.getOutDegree();

				this.sum1 -= nodeOutDegree * otherNode1OutDegree;

				this.sum2 -= nodeOutDegree + otherNode1OutDegree;

				this.sum3 -= nodeOutDegree * nodeOutDegree
						+ otherNode1OutDegree * otherNode1OutDegree;

				if (edge.getDst().equals(node)) {
					// only for otherNode1s at incoming edges of node do the
					// following

					nodeNeighbors.add(otherNode1);

					for (IElement iElement2 : otherNode1.getEdges()) {
						otherNode2 = (DirectedNode) ((DirectedEdge) iElement2)
								.getDifferingNode(otherNode1);

						if (!otherNode2.equals(node)) {
							this.sum1 -= otherNode2.getOutDegree();

							this.sum2 -= 1;

							this.sum3 -= 2 * otherNode1OutDegree - 1;
						}
					}

					this.sum1 += this.numberOfDirectedEdgesFromNodeToNodes(
							otherNode1, nodeNeighbors);

				}
			}

		} else if (this.directedDegreeType.equals("in")) {

			final int nodeInDegree = node.getInDegree();
			final Set<Node> nodeNeighbors = new HashSet<Node>();

			DirectedEdge edge;
			DirectedNode otherNode1, otherNode2;
			int otherNode1InDegree;
			for (IElement iElement1 : node.getEdges()) {
				edge = (DirectedEdge) iElement1;
				otherNode1 = (DirectedNode) edge.getDifferingNode(node);

				otherNode1InDegree = otherNode1.getInDegree();

				this.sum1 -= nodeInDegree * otherNode1InDegree;

				this.sum2 -= nodeInDegree + otherNode1InDegree;

				this.sum3 -= nodeInDegree * nodeInDegree + otherNode1InDegree
						* otherNode1InDegree;

				if (edge.getSrc().equals(node)) {
					// only for otherNode1s at outgoing edges of node do the
					// following

					nodeNeighbors.add(otherNode1);

					for (IElement iElement2 : otherNode1.getEdges()) {
						otherNode2 = (DirectedNode) ((DirectedEdge) iElement2)
								.getDifferingNode(otherNode1);

						if (!otherNode2.equals(node)) {
							this.sum1 -= otherNode2.getInDegree();

							this.sum2 -= 1;

							this.sum3 -= 2 * otherNode1InDegree - 1;
						}
					}

					this.sum1 += this.numberOfDirectedEdgesFromNodeToNodes(
							otherNode1, nodeNeighbors);

				}
			}

		} else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		this.setR(this.g.getEdgeCount() - node.getDegree());

		return true;
	}

	/**
	 * Updates {@link Assortativity#sum1}, {@link Assortativity#sum2},
	 * {@link Assortativity#sum3} and finally calls {@link #setR(int)} for the
	 * outdated {@link UndirectedNode}.
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

			this.sum1 -= nodeDegree * otherNode1Degree;

			this.sum2 -= nodeDegree + otherNode1Degree;

			this.sum3 -= nodeDegree * nodeDegree + otherNode1Degree
					* otherNode1Degree;

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
			this.sum1 += this.numberOfUndirectedEdgesFromNodeToNodes(
					otherNode1, nodeNeighbors);
		}

		this.setR(this.g.getEdgeCount() - nodeDegree);

		return true;
	}

}
