package dna.metrics.richClubConnectivity.richClubConnectivitySizeN;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * Calculate the rich club connectivity value for a richClub with size @param
 * RichClubSize
 * 
 */
public class RichClubConnectivitySizeNU extends RichClubConnectivitySizeN {

	public RichClubConnectivitySizeNU(int richClubSize) {
		super("RichClubConnectivitySizeNU", ApplicationType.AfterUpdate,
				richClubSize);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition) {
			return applyAfterNodeAddition(u);
		} else if (u instanceof NodeRemoval) {
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				return applyAfterDirectedNodeRemoval(u);
			} else {
				return applyAfterUndirectedNodeRemoval(u);
			}
		} else if (u instanceof EdgeAddition) {
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				return applyAfterDirectedEdgeAddition(u);
			} else {
				return applyAfterUndirectedEdgeAddition(u);
			}

		} else if (u instanceof EdgeRemoval) {
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				return applyAfterDirectedEdgeRemoval(u);
			} else {
				return applyAfterUndirectedEdgeRemoval(u);

			}
		}
		return false;
	}

	private boolean applyAfterDirectedEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();

		if (richClub.containsKey(srcDegree - 1)
				&& richClub.get(srcDegree - 1).contains(src)) {

			if (richClub.containsKey(dstDegree)
					&& richClub.get(dstDegree).contains(dst)) {
				this.edgesBetweenRichClub++;
			}
			removeFromRichclub(src);
			addToRichClub(src);
		} else if (this.richClub.containsKey(srcDegree - 1)
				&& this.nodesSortedByDegree.containsKey(srcDegree - 1)
				&& this.nodesSortedByDegree.get(srcDegree - 1).contains(src)) {

			// changes for lastNode of Richclub
			DirectedNode lastNode = (DirectedNode) this.richClub.get(
					srcDegree - 1).removeLast();
			if (this.richClub.get(lastNode.getOutDegree()).isEmpty()) {
				this.richClub.remove(lastNode.getOutDegree());
			}
			addNodeToRest(lastNode);

			// Changes for src node for richclub
			this.nodesSortedByDegree.get(srcDegree - 1).remove(src);
			if (this.nodesSortedByDegree.get(src.getOutDegree() - 1).isEmpty()) {
				this.nodesSortedByDegree.remove(src.getOutDegree() - 1);
			}
			addToRichClub(src);

			// calculate changes for richclub connectivity
			for (IElement ie : src.getEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
				DirectedNode node = edge.getDifferingNode(src);
				if (this.richClub.containsKey(node.getOutDegree())
						&& this.richClub.get(node.getOutDegree())
								.contains(node)) {
					this.edgesBetweenRichClub++;
				}
			}

			for (IElement ie : lastNode.getEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
				DirectedNode node = edge.getDifferingNode(lastNode);
				if (this.richClub.containsKey(node.getOutDegree())
						&& this.richClub.get(node.getOutDegree())
								.contains(node)) {
					this.edgesBetweenRichClub--;
				}
			}

		} else {
			this.nodesSortedByDegree.get(src.getOutDegree() - 1).remove(src);
			if (this.nodesSortedByDegree.get(src.getOutDegree() - 1).isEmpty()) {
				this.nodesSortedByDegree.remove(src.getOutDegree() - 1);
			}
			addNodeToRest(src);
		}
		return true;
	}

	private boolean applyAfterDirectedNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		int min = Integer.MAX_VALUE;
		int max = 0;
		HashMap<Integer, LinkedList<DirectedNode>> order = new HashMap<>();
		for (IElement ie : node.getIncomingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			DirectedNode n = e.getSrc();

			if (richClub.containsKey(n.getOutDegree() + 1)
					&& richClub.get(n.getOutDegree() + 1).contains(n)) {
				this.edgesBetweenRichClub--;
				min = Math.min(min, n.getOutDegree());
				max = Math.max(max, n.getOutDegree());
				richClub.get(n.getOutDegree() + 1).remove(n);
				if (richClub.get(n.getOutDegree() + 1).isEmpty()) {
					richClub.remove(n.getOutDegree() + 1);
				}
				if (richClub.containsKey(n.getOutDegree())) {
					richClub.get(n.getOutDegree()).add(n);
				} else {
					richClub.put(n.getOutDegree(), new LinkedList<Node>());
					richClub.get(n.getOutDegree()).add(n);
				}
				if (order.containsKey(n.getOutDegree())) {
					order.get(n.getOutDegree()).add(n);
				} else {
					order.put(n.getOutDegree(), new LinkedList<DirectedNode>());
					order.get(n.getOutDegree()).add(n);
				}
			} else if (nodesSortedByDegree.containsKey(n.getOutDegree() + 1)
					&& nodesSortedByDegree.get(n.getOutDegree() + 1)
							.contains(n)) {
				nodesSortedByDegree.get(n.getOutDegree() + 1).remove(n);
				if (nodesSortedByDegree.get(n.getOutDegree() + 1).isEmpty()) {
					nodesSortedByDegree.remove(n.getOutDegree() + 1);
				}
				if (nodesSortedByDegree.containsKey(n.getOutDegree())) {
					nodesSortedByDegree.get(n.getOutDegree()).add(n);
				} else {
					nodesSortedByDegree.put(n.getOutDegree(),
							new LinkedList<Node>());
					nodesSortedByDegree.get(n.getOutDegree()).add(n);
				}
			} else {
				System.out.println("fuck");
			}
		}

		for (int i = min; i <= max; i++) {
			boolean f = false;
			if (f) {
				break;
			}
			if (!order.containsKey(i)) {
				continue;
			}
			for (DirectedNode n1 : order.get(i)) {
				int big = getBiggest();
				if (i >= big) {
					f = true;
					break;
				}

				// changes for firstNode of Rest
				DirectedNode firstNode = (DirectedNode) this.nodesSortedByDegree
						.get(big).removeFirst();
				if (this.nodesSortedByDegree.get(firstNode.getOutDegree())
						.isEmpty()) {
					this.nodesSortedByDegree.remove(firstNode.getOutDegree());
				}
				addToRichClub(firstNode);

				// Changes for src node for richclub
				this.richClub.get(i).remove(n1);
				if (this.richClub.get(i).isEmpty()) {
					this.richClub.remove(i);
				}
				addNodeToRest(n1);

				// calculate changes for richclub connectivity
				for (IElement ie : n1.getEdges()) {
					DirectedEdge edge = (DirectedEdge) ie;
					DirectedNode n2 = edge.getDifferingNode(n1);
					if (this.richClub.containsKey(n2.getOutDegree())
							&& this.richClub.get(n2.getOutDegree())
									.contains(n2)) {
						this.edgesBetweenRichClub--;
					}
				}

				for (IElement ie : firstNode.getEdges()) {
					DirectedEdge edge = (DirectedEdge) ie;
					DirectedNode n2 = edge.getDifferingNode(firstNode);
					if (this.richClub.containsKey(n2.getOutDegree())
							&& this.richClub.get(n2.getOutDegree())
									.contains(n2)) {
						this.edgesBetweenRichClub++;
					}
				}
			}
		}
		if (this.richClub.containsKey(node.getOutDegree())
				&& this.richClub.get(node.getOutDegree()).contains(node)) {
			int degree = ((DirectedNode) node).getOutDegree();

			// changes for firstNode of Rest
			DirectedNode firstNode = (DirectedNode) this.nodesSortedByDegree
					.get(getBiggest()).removeFirst();
			if (this.nodesSortedByDegree.get(firstNode.getOutDegree())
					.isEmpty()) {
				this.nodesSortedByDegree.remove(firstNode.getOutDegree());
			}
			addToRichClub(firstNode);

			// Changes for src node for richclub
			this.richClub.get(degree).remove(node);
			if (this.richClub.get(degree).isEmpty()) {
				this.richClub.remove(degree);
			}

			// calculate changes for richclub connectivity
			for (IElement ie : node.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
				DirectedNode n2 = edge.getDifferingNode(node);
				if (this.richClub.containsKey(n2.getOutDegree())
						&& this.richClub.get(n2.getOutDegree()).contains(n2)) {
					this.edgesBetweenRichClub--;
				}
			}

			for (IElement ie : firstNode.getEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
				DirectedNode n2 = edge.getDifferingNode(firstNode);
				if (this.richClub.containsKey(n2.getOutDegree())
						&& this.richClub.get(n2.getOutDegree()).contains(n2)) {
					this.edgesBetweenRichClub++;
				}
			}

		} else {
			int degree = ((DirectedNode) node).getOutDegree();

			this.nodesSortedByDegree.get(degree).remove(node);
			if (this.nodesSortedByDegree.get(degree).isEmpty()) {
				this.nodesSortedByDegree.remove(degree);
			}
		}

		return true;

	}

	private boolean applyAfterUndirectedNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		int min = Integer.MAX_VALUE;
		int max = 0;
		HashMap<Integer, LinkedList<UndirectedNode>> order = new HashMap<>();
		for (IElement ie : node.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			UndirectedNode n = e.getDifferingNode(node);

			if (richClub.containsKey(n.getDegree() + 1)
					&& richClub.get(n.getDegree() + 1).contains(n)) {
				this.edgesBetweenRichClub -= 2;
				min = Math.min(min, n.getDegree());
				max = Math.max(max, n.getDegree());
				richClub.get(n.getDegree() + 1).remove(n);
				if (richClub.get(n.getDegree() + 1).isEmpty()) {
					richClub.remove(n.getDegree() + 1);
				}
				if (richClub.containsKey(n.getDegree())) {
					richClub.get(n.getDegree()).add(n);
				} else {
					richClub.put(n.getDegree(), new LinkedList<Node>());
					richClub.get(n.getDegree()).add(n);
				}
				if (order.containsKey(n.getDegree())) {
					order.get(n.getDegree()).add(n);
				} else {
					order.put(n.getDegree(), new LinkedList<UndirectedNode>());
					order.get(n.getDegree()).add(n);
				}
			} else if (nodesSortedByDegree.containsKey(n.getDegree() + 1)
					&& nodesSortedByDegree.get(n.getDegree() + 1).contains(n)) {
				nodesSortedByDegree.get(n.getDegree() + 1).remove(n);
				if (nodesSortedByDegree.get(n.getDegree() + 1).isEmpty()) {
					nodesSortedByDegree.remove(n.getDegree() + 1);
				}
				if (nodesSortedByDegree.containsKey(n.getDegree())) {
					nodesSortedByDegree.get(n.getDegree()).add(n);
				} else {
					nodesSortedByDegree.put(n.getDegree(),
							new LinkedList<Node>());
					nodesSortedByDegree.get(n.getDegree()).add(n);
				}
			} else {
				System.out.println("fuck");
			}
		}

		for (int i = min; i <= max; i++) {
			boolean f = false;
			if (f) {
				break;
			}
			if (!order.containsKey(i)) {
				continue;
			}
			for (UndirectedNode n1 : order.get(i)) {
				int big = getBiggest();
				if (i >= big) {
					f = true;
					break;
				}

				// changes for firstNode of Rest
				UndirectedNode firstNode = (UndirectedNode) this.nodesSortedByDegree
						.get(big).removeFirst();
				if (this.nodesSortedByDegree.get(firstNode.getDegree())
						.isEmpty()) {
					this.nodesSortedByDegree.remove(firstNode.getDegree());
				}
				addToRichClub(firstNode);

				// Changes for src node for richclub
				this.richClub.get(i).remove(n1);
				if (this.richClub.get(i).isEmpty()) {
					this.richClub.remove(i);
				}
				addNodeToRest(n1);

				// calculate changes for richclub connectivity
				for (IElement ie : n1.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) ie;
					UndirectedNode n2 = edge.getDifferingNode(n1);
					if (this.richClub.containsKey(n2.getDegree())
							&& this.richClub.get(n2.getDegree()).contains(n2)) {
						this.edgesBetweenRichClub--;
					}
				}

				for (IElement ie : firstNode.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) ie;
					UndirectedNode n2 = edge.getDifferingNode(firstNode);
					if (this.richClub.containsKey(n2.getDegree())
							&& this.richClub.get(n2.getDegree()).contains(n2)) {
						this.edgesBetweenRichClub++;
					}
				}
			}
		}
		if (this.richClub.containsKey(node.getDegree())
				&& this.richClub.get(node.getDegree()).contains(node)) {
			int degree = node.getDegree();

			// changes for firstNode of Rest
			UndirectedNode firstNode = (UndirectedNode) this.nodesSortedByDegree
					.get(getBiggest()).removeFirst();
			if (this.nodesSortedByDegree.get(firstNode.getDegree()).isEmpty()) {
				this.nodesSortedByDegree.remove(firstNode.getDegree());
			}
			addToRichClub(firstNode);

			// Changes for src node for richclub
			this.richClub.get(degree).remove(node);
			if (this.richClub.get(degree).isEmpty()) {
				this.richClub.remove(degree);
			}

			for (IElement ie : firstNode.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode n2 = edge.getDifferingNode(firstNode);
				if (this.richClub.containsKey(n2.getDegree())
						&& this.richClub.get(n2.getDegree()).contains(n2)) {
					this.edgesBetweenRichClub++;
				}
			}

		} else {
			int degree = node.getDegree();

			this.nodesSortedByDegree.get(degree).remove(node);
			if (this.nodesSortedByDegree.get(degree).isEmpty()) {
				this.nodesSortedByDegree.remove(degree);
			}
		}

		return true;

	}

	private int getBiggest() {
		int biggest = 0;
		for (int i : nodesSortedByDegree.keySet()) {
			biggest = Math.max(biggest, i);
		}
		return biggest;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		Node node = (Node) ((NodeRemoval) u).getNode();
		HashSet<Edge> edges = new HashSet<Edge>();
		g.addNode(node);
		for (IElement ie : node.getEdges()) {
			Edge e = (Edge) ie;
			edges.add(e);
			e.connectToNodes();
		}
		for (Edge e : edges) {
			e.disconnectFromNodes();
			g.removeEdge(e);
			applyAfterUpdate(new EdgeRemoval(e));
		}
		g.removeNode(node);
		int degree;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			degree = ((DirectedNode) node).getOutDegree();
		} else {
			degree = ((UndirectedNode) node).getDegree();
		}
		this.nodesSortedByDegree.get(degree).remove(node);
		if (this.nodesSortedByDegree.get(degree).isEmpty()) {
			this.nodesSortedByDegree.remove(degree);
		}
		return true;

	}

	private boolean applyAfterNodeAddition(Update u) {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();
			addNodeToRest(node);
		} else {
			UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
			addUndirectedNodeToRest(node);
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeRemoval(Update u) {

		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

		DirectedNode dst = e.getDst();
		DirectedNode src = e.getSrc();

		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();

		if (richClub.containsKey(srcDegree + 1)
				&& !nodesSortedByDegree.containsKey(srcDegree + 1)
				&& richClub.get(srcDegree + 1).contains(src)) {

			if (richClub.containsKey(dstDegree)
					&& richClub.get(dstDegree).contains(dst)) {
				this.edgesBetweenRichClub--;
			}
			this.richClub.get(srcDegree + 1).remove(src);
			if (this.richClub.get(srcDegree + 1).isEmpty()) {
				this.richClub.remove(srcDegree + 1);
			}
			addToRichClub(src);
		} else if (richClub.containsKey(srcDegree + 1)
				&& nodesSortedByDegree.containsKey(srcDegree + 1)
				&& richClub.get(srcDegree + 1).contains(src)) {

			// changes for firstNode of Rest
			DirectedNode firstNode = (DirectedNode) this.nodesSortedByDegree
					.get(srcDegree + 1).removeFirst();
			if (this.nodesSortedByDegree.get(firstNode.getOutDegree())
					.isEmpty()) {
				this.nodesSortedByDegree.remove(firstNode.getOutDegree());
			}
			addToRichClub(firstNode);

			// Changes for src node for richclub
			this.richClub.get(srcDegree + 1).remove(src);
			if (this.richClub.get(src.getOutDegree() + 1).isEmpty()) {
				this.richClub.remove(src.getOutDegree() + 1);
			}
			addNodeToRest(src);

			// calculate changes for richclub connectivity
			for (IElement ie : src.getEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
				DirectedNode node = edge.getDifferingNode(src);
				if (this.richClub.containsKey(node.getOutDegree())
						&& this.richClub.get(node.getOutDegree())
								.contains(node)) {
					this.edgesBetweenRichClub--;
				}
			}

			for (IElement ie : firstNode.getEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
				DirectedNode node = edge.getDifferingNode(firstNode);
				if (this.richClub.containsKey(node.getOutDegree())
						&& this.richClub.get(node.getOutDegree())
								.contains(node)) {
					this.edgesBetweenRichClub++;
				}
			}
		} else {
			this.nodesSortedByDegree.get(src.getOutDegree() + 1).remove(src);
			if (this.nodesSortedByDegree.get(src.getOutDegree() + 1).isEmpty()) {
				this.nodesSortedByDegree.remove(src.getOutDegree() + 1);
			}
			addNodeToRest(src);
		}
		return true;
	}

	private boolean applyAfterUndirectedEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (richClub.containsKey(n1.getDegree() - 1)
				&& richClub.get(n1.getDegree() - 1).contains(n1)
				&& richClub.containsKey(n2.getDegree() - 1)
				&& richClub.get(n2.getDegree() - 1).contains(n2)) {
			this.edgesBetweenRichClub++;
		}

		checkAdd(n1, n2);
		checkAdd(n2, n1);
		return true;
	}

	private void checkAdd(UndirectedNode n1, UndirectedNode n2) {
		if (richClub.containsKey(n1.getDegree() - 1)
				&& richClub.get(n1.getDegree() - 1).contains(n1)) {

			this.richClub.get(n1.getDegree() - 1).remove(n1);
			if (this.richClub.get(n1.getDegree() - 1).isEmpty()) {
				this.richClub.remove(n1.getDegree() - 1);
			}

			addToRichClub(n1);
		} else if (this.richClub.containsKey(n1.getDegree() - 1)
				&& this.nodesSortedByDegree.containsKey(n1.getDegree() - 1)
				&& this.nodesSortedByDegree.get(n1.getDegree() - 1)
						.contains(n1)) {

			// changes for lastNode of Richclub
			UndirectedNode lastNode;

			if (this.richClub.get(n1.getDegree() - 1).size() == 1
					&& this.richClub.get(n1.getDegree() - 1).getLast()
							.equals(n2)) {
				this.nodesSortedByDegree.get(n1.getDegree() - 1).remove(n1);
				if (this.nodesSortedByDegree.get(n1.getDegree() - 1).isEmpty()) {
					this.nodesSortedByDegree.remove(n1.getDegree() - 1);
				}
				addUndirectedNodeToRest(n1);
				return;
			}

			if (this.richClub.get(n1.getDegree() - 1).getLast().equals(n2)) {
				lastNode = (UndirectedNode) this.richClub.get(
						n1.getDegree() - 1).removeFirst();
			} else {
				lastNode = (UndirectedNode) this.richClub.get(
						n1.getDegree() - 1).removeLast();
			}
			if (this.richClub.get(lastNode.getDegree()).isEmpty()) {
				this.richClub.remove(lastNode.getDegree());
			}
			addUndirectedNodeToRest(lastNode);

			// Changes for n1 node for richclub
			this.nodesSortedByDegree.get(n1.getDegree() - 1).remove(n1);
			if (this.nodesSortedByDegree.get(n1.getDegree() - 1).isEmpty()) {
				this.nodesSortedByDegree.remove(n1.getDegree() - 1);
			}
			addToRichClub(n1);

			// calculate changes for richclub connectivity
			for (IElement ie : n1.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode node = edge.getDifferingNode(n1);
				if (this.richClub.containsKey(node.getDegree())
						&& this.richClub.get(node.getDegree()).contains(node)) {
					this.edgesBetweenRichClub++;
				}
			}

			for (IElement ie : lastNode.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode node = edge.getDifferingNode(lastNode);
				if (this.richClub.containsKey(node.getDegree())
						&& this.richClub.get(node.getDegree()).contains(node)) {
					this.edgesBetweenRichClub--;
				}
			}

		} else {

			this.nodesSortedByDegree.get(n1.getDegree() - 1).remove(n1);
			if (this.nodesSortedByDegree.get(n1.getDegree() - 1).isEmpty()) {
				this.nodesSortedByDegree.remove(n1.getDegree() - 1);
			}
			addUndirectedNodeToRest(n1);
		}
	}

	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (richClub.containsKey(n1.getDegree() + 1)
				&& richClub.get(n1.getDegree() + 1).contains(n1)
				&& richClub.containsKey(n2.getDegree() + 1)
				&& richClub.get(n2.getDegree() + 1).contains(n2)) {
			this.edgesBetweenRichClub--;
		}

		checkRemoval(n1, n2);
		checkRemoval(n2, n1);
		return true;
	}

	private void checkRemoval(UndirectedNode n, UndirectedNode partner) {
		int degree = n.getDegree();
		if (richClub.containsKey(degree + 1)
				&& !nodesSortedByDegree.containsKey(degree + 1)
				&& richClub.get(degree + 1).contains(n)) {

			this.richClub.get(degree + 1).remove(n);
			if (this.richClub.get(degree + 1).isEmpty()) {
				this.richClub.remove(degree + 1);
			}
			addToRichClub(n);

		} else if (richClub.containsKey(degree + 1)
				&& nodesSortedByDegree.containsKey(degree + 1)
				&& richClub.get(degree + 1).contains(n)) {

			// changes for firstNode of Rest
			UndirectedNode firstNode;

			if (this.nodesSortedByDegree.get(degree + 1).size() == 1
					&& this.nodesSortedByDegree.get(degree + 1).getFirst()
							.equals(partner)) {

				this.richClub.get(degree + 1).remove(n);
				if (this.richClub.get(degree + 1).isEmpty()) {
					this.richClub.remove(degree + 1);
				}
				addToRichClub(n);
				return;
			}
			if (this.nodesSortedByDegree.get(degree + 1).getFirst()
					.equals(partner)) {
				firstNode = (UndirectedNode) this.nodesSortedByDegree.get(
						degree + 1).removeLast();
			} else {
				firstNode = (UndirectedNode) this.nodesSortedByDegree.get(
						degree + 1).removeFirst();
			}
			if (this.nodesSortedByDegree.get(firstNode.getDegree()).isEmpty()) {
				this.nodesSortedByDegree.remove(firstNode.getDegree());
			}
			addToRichClub(firstNode);

			// Changes for n1 node for richclub
			this.richClub.get(degree + 1).remove(n);
			if (this.richClub.get(degree + 1).isEmpty()) {
				this.richClub.remove(degree + 1);
			}
			addUndirectedNodeToRest(n);

			// calculate changes for richclub connectivity
			for (IElement ie : n.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode node = edge.getDifferingNode(n);
				if (this.richClub.containsKey(node.getDegree())
						&& this.richClub.get(node.getDegree()).contains(node)) {
					this.edgesBetweenRichClub--;
				}
			}

			for (IElement ie : firstNode.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode node = edge.getDifferingNode(firstNode);
				if (this.richClub.containsKey(node.getDegree())
						&& this.richClub.get(node.getDegree()).contains(node)) {
					this.edgesBetweenRichClub++;
				}
			}
		} else {
			this.nodesSortedByDegree.get(degree + 1).remove(n);
			if (this.nodesSortedByDegree.get(degree + 1).isEmpty()) {
				this.nodesSortedByDegree.remove(degree + 1);
			}
			addUndirectedNodeToRest(n);
		}
	}

	private void check() {
		HashSet<Node> seen = new HashSet<>();
		for (int i : nodesSortedByDegree.keySet()) {
			if (nodesSortedByDegree.get(i).isEmpty()) {
				System.out.println(i);
			}
			for (Node n : nodesSortedByDegree.get(i)) {
				if (((UndirectedNode) n).getDegree() != i) {
					System.out.println(n);
				}
				if (seen.contains(n)) {
					System.out.println(n);
				} else {
					seen.add(n);
				}
			}

		}
		int size = 0;
		for (int i : richClub.keySet()) {
			if (richClub.get(i).isEmpty()) {
				System.out.println(i);
			}
			for (Node n : richClub.get(i)) {
				size++;
				if (((UndirectedNode) n).getDegree() != i) {
					System.out.println(n);
				}
				if (seen.contains(n)) {
					System.out.println(n);
				} else {
					seen.add(n);
				}
			}
		}
		if (seen.size() != g.getNodeCount() || size != richClubSize) {
			System.out.println("fuck");
		}

	}

	private void addUndirectedNodeToRest(UndirectedNode node) {
		if (this.nodesSortedByDegree.containsKey(node.getDegree())) {
			this.nodesSortedByDegree.get(node.getDegree()).add(node);
		} else {
			LinkedList<Node> temp = new LinkedList<Node>();
			temp.add(node);
			this.nodesSortedByDegree.put(node.getDegree(), temp);
		}
	}

	private void addToRichClub(UndirectedNode node) {
		int degree = node.getDegree();
		if (this.richClub.containsKey(degree)) {
			this.richClub.get(degree).add(node);
		} else {
			LinkedList<Node> temp = new LinkedList<Node>();
			temp.add(node);
			this.richClub.put(degree, temp);
		}
	}

	private void addToRichClub(DirectedNode node) {
		int degree = node.getOutDegree();
		if (this.richClub.containsKey(degree)) {
			this.richClub.get(degree).add(node);
		} else {
			LinkedList<Node> temp = new LinkedList<Node>();
			temp.add(node);
			this.richClub.put(degree, temp);
		}
	}

	private void removeFromRichclub(DirectedNode node) {
		int degree = node.getOutDegree() - 1;
		this.richClub.get(degree).remove(node);
		if (this.richClub.get(degree).isEmpty()) {
			this.richClub.remove(degree);
		}
	}

	private void addNodeToRest(DirectedNode node) {
		if (this.nodesSortedByDegree.containsKey(node.getOutDegree())) {
			this.nodesSortedByDegree.get(node.getOutDegree()).add(node);
		} else {
			LinkedList<Node> temp = new LinkedList<Node>();
			temp.add(node);
			this.nodesSortedByDegree.put(node.getOutDegree(), temp);
		}
	}

	private void addNodeToRest(UndirectedNode node) {
		if (this.nodesSortedByDegree.containsKey(node.getDegree())) {
			this.nodesSortedByDegree.get(node.getDegree()).add(node);
		} else {
			LinkedList<Node> temp = new LinkedList<Node>();
			temp.add(node);
			this.nodesSortedByDegree.put(node.getDegree(), temp);
		}
	}

}
