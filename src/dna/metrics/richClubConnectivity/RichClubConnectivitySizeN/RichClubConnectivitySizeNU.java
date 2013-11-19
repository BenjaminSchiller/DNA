package dna.metrics.richClubConnectivity.RichClubConnectivitySizeN;

import java.util.HashSet;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
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

public class RichClubConnectivitySizeNU extends RichClubConnectivitySizeN {

	public RichClubConnectivitySizeNU(int richClubSize) {
		super("RCCSizeN", ApplicationType.AfterUpdate, richClubSize);
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterDirectedNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterDirectedNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterDirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterDirectedEdgeRemoval(u);
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterUndirectedNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyUndirectedAfterNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterUndirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
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
		HashSet<DirectedEdge> edges = new HashSet<DirectedEdge>();
		g.addNode(node);
		for (IElement ie : node.getEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			edges.add(e);
			e.connectToNodes();
		}
		for (DirectedEdge e : edges) {
			e.disconnectFromNodes();
			g.removeEdge(e);
			applyAfterDirectedEdgeRemoval(new EdgeRemoval(e));
		}
		g.removeNode(node);
		this.nodesSortedByDegree.get(node.getOutDegree()).remove(node);
		if (this.nodesSortedByDegree.get(node.getOutDegree()).isEmpty()) {
			this.nodesSortedByDegree.remove(node.getOutDegree());
		}
		return true;

	}

	private boolean applyAfterDirectedNodeAddition(Update u) {
		DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();
		addNodeToRest(node);
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

		checkAdd(n1);
		checkAdd(n2);

		return true;
	}

	private void checkAdd(UndirectedNode n1) {
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
			UndirectedNode lastNode = (UndirectedNode) this.richClub.get(
					n1.getDegree() - 1).removeLast();
			if (this.richClub.get(lastNode.getDegree()).isEmpty()) {
				this.richClub.remove(lastNode.getDegree());
			}
			addNodeToRest(lastNode);

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
			addNodeToRest(n1);
		}
	}

	private boolean applyUndirectedAfterNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		HashSet<UndirectedEdge> edges = new HashSet<UndirectedEdge>();
		g.addNode(node);
		for (IElement ie : node.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			edges.add(e);
			e.connectToNodes();
		}
		for (UndirectedEdge e : edges) {
			e.disconnectFromNodes();
			g.removeEdge(e);
			applyAfterUndirectedEdgeRemoval(new EdgeRemoval(e));
		}
		g.removeNode(node);
		this.nodesSortedByDegree.get(node.getDegree()).remove(node);
		if (this.nodesSortedByDegree.get(node.getDegree()).isEmpty()) {
			this.nodesSortedByDegree.remove(node.getDegree());
		}
		return true;
	}

	private boolean applyAfterUndirectedNodeAddition(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
		addNodeToRest(node);
		return true;
	}

	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (richClub.containsKey(n1.getDegree() + 1)
				&& richClub.get(n1.getDegree() + 1).contains(n1)
				&& richClub.containsKey(n2.getDegree() + 1)
				&& richClub.get(n2.getDegree() + 1).contains(n2)) {
			this.edgesBetweenRichClub++;
		}

		checkRemoval(n1);
		checkRemoval(n2);

		return true;
	}

	private void checkRemoval(UndirectedNode n1) {
		if (richClub.containsKey(n1.getDegree() + 1)
				&& !nodesSortedByDegree.containsKey(n1.getDegree() + 1)
				&& richClub.get(n1.getDegree() + 1).contains(n1)) {

			this.richClub.get(n1.getDegree() + 1).remove(n1);
			if (this.richClub.get(n1.getDegree() + 1).isEmpty()) {
				this.richClub.remove(n1.getDegree() + 1);
			}
			addToRichClub(n1);

		} else if (richClub.containsKey(n1.getDegree() + 1)
				&& nodesSortedByDegree.containsKey(n1.getDegree() + 1)
				&& richClub.get(n1.getDegree() + 1).contains(n1)) {

			// changes for firstNode of Rest
			UndirectedNode firstNode = (UndirectedNode) this.nodesSortedByDegree
					.get(n1.getDegree() + 1).removeFirst();
			if (this.nodesSortedByDegree.get(firstNode.getDegree()).isEmpty()) {
				this.nodesSortedByDegree.remove(firstNode.getDegree());
			}
			addToRichClub(firstNode);

			// Changes for n1 node for richclub
			this.richClub.get(n1.getDegree() + 1).remove(n1);
			if (this.richClub.get(n1.getDegree() + 1).isEmpty()) {
				this.richClub.remove(n1.getDegree() + 1);
			}
			addNodeToRest(n1);

			// calculate changes for richclub connectivity
			for (IElement ie : n1.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode node = edge.getDifferingNode(n1);
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
			this.nodesSortedByDegree.get(n1.getDegree() + 1).remove(n1);
			if (this.nodesSortedByDegree.get(n1.getDegree() + 1).isEmpty()) {
				this.nodesSortedByDegree.remove(n1.getDegree() + 1);
			}
			addNodeToRest(n1);
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

}
