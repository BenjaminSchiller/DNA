package dna.metrics.richClubConnectivity.undirectedRichClubConnectivitySizeN;

import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedRichClubConnectivitySizeNU extends
		UndirectedRichClubConnectivitySizeN {

	public UndirectedRichClubConnectivitySizeNU(int richClubSize) {
		super("RCCFirstKNodesDyn", ApplicationType.AfterUpdate, richClubSize);
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
			return applyAfterNodeRemoval(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterEdgeAddition(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterEdgeRemoval(u);
		}
		return false;
	}

	private boolean applyAfterEdgeAddition(Update u) {
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
			UndirectedNode lastNode = this.richClub.get(n1.getDegree() - 1)
					.removeLast();
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

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();

		for (IElement ie : node.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			UndirectedNode n2 = e.getDifferingNode(node);

			if (richClub.containsKey(node.getDegree())
					&& richClub.get(node.getDegree()).contains(node)
					&& richClub.containsKey(n2.getDegree() + 1)
					&& richClub.get(n2.getDegree() + 1).contains(n2)) {
				this.edgesBetweenRichClub--;
			}
			checkRemoval(n2);
		}

		if (richClub.containsKey(node.getDegree())
				&& richClub.get(node.getDegree()).contains(node)) {
			this.richClub.get(node.getDegree()).remove(node);
			if (this.richClub.get(node.getDegree()).isEmpty()) {
				this.richClub.remove(node.getDegree());
			}

			int max = 0;
			for (int i : this.nodesSortedByDegree.keySet()) {
				max = Math.max(max, i);
			}
			// changes for firstNode of Rest
			UndirectedNode firstNode = this.nodesSortedByDegree.get(max)
					.removeFirst();
			if (this.nodesSortedByDegree.get(firstNode.getDegree()).isEmpty()) {
				this.nodesSortedByDegree.remove(firstNode.getDegree());
			}
			addToRichClub(firstNode);
			for (IElement ie : firstNode.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode n = edge.getDifferingNode(firstNode);
				if (this.richClub.containsKey(n.getDegree())
						&& this.richClub.get(n.getDegree()).contains(n)) {
					this.edgesBetweenRichClub++;
				}
			}
		} else {
			this.nodesSortedByDegree.get(node.getDegree()).remove(node);
			if (this.nodesSortedByDegree.get(node.getDegree()).isEmpty()) {
				this.nodesSortedByDegree.remove(node.getDegree());
			}
		}

		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
		addNodeToRest(node);
		return true;
	}

	private boolean applyAfterEdgeRemoval(Update u) {
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
			UndirectedNode firstNode = this.nodesSortedByDegree.get(
					n1.getDegree() + 1).removeFirst();
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
			LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
			temp.add(node);
			this.nodesSortedByDegree.put(node.getDegree(), temp);
		}
	}

	private void addToRichClub(UndirectedNode node) {
		int degree = node.getDegree();
		if (this.richClub.containsKey(degree)) {
			this.richClub.get(degree).add(node);
		} else {
			LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
			temp.add(node);
			this.richClub.put(degree, temp);
		}
	}

}
