package dna.metrics.richClubConnectivity.directedRichClubConnectivitySizeN;

import java.util.HashSet;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedRichClubConnectivitySizeNU extends
		DirectedRichClubConnectivitySizeN {

	public DirectedRichClubConnectivitySizeNU(int richClubSize) {
		super("RCCSizeNDyn", ApplicationType.AfterUpdate, richClubSize);
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
			return applyAfterDirectedNodeAddition(u);
		} else if (u instanceof NodeRemoval) {
			return applyAfterDirectedNodeRemoval(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterDirectedEdgeAddition(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterDirectedEdgeRemoval(u);
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
			DirectedNode lastNode = this.richClub.get(srcDegree - 1)
					.removeLast();
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

	private void addNodeToRest(DirectedNode node) {
		if (this.nodesSortedByDegree.containsKey(node.getOutDegree())) {
			this.nodesSortedByDegree.get(node.getOutDegree()).add(node);
		} else {
			LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
			temp.add(node);
			this.nodesSortedByDegree.put(node.getOutDegree(), temp);
		}
	}

	private void addToRichClub(DirectedNode node) {
		int degree = node.getOutDegree();
		if (this.richClub.containsKey(degree)) {
			this.richClub.get(degree).add(node);
		} else {
			LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
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
			DirectedNode firstNode = this.nodesSortedByDegree
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
}
