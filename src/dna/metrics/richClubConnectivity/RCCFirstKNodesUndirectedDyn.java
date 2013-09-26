package dna.metrics.richClubConnectivity;

import java.util.LinkedList;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.EdgeUpdate;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class RCCFirstKNodesUndirectedDyn extends RCCFirstKNodesUndirected {

	public RCCFirstKNodesUndirectedDyn() {
		super("RCCFirstKNodesDyn", ApplicationType.AfterUpdate);
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

		if (this.richClub.contains(n1)) {
			inRCCAddition(n1, n2);

		} else if (n1.getDegree() > this.richClub.getLast().getDegree()) {
			rccChangeAddition(n1, n2);
		} else {
			notInRCCAddition(n1);
		}

		if (this.richClub.contains(n2)) {
			inRCCAddition(n2, n1);

		} else if (n2.getDegree() > this.richClub.getLast().getDegree()) {
			rccChangeAddition(n2, n1);
		} else {
			notInRCCAddition(n2);
		}
		this.caculateRCC();
		return true;

	}

	private void notInRCCAddition(UndirectedNode n) {
		this.nodesSortedByDegree.get(n.getDegree() - 1).remove(n);
		if (this.nodesSortedByDegree.get(n.getDegree() - 1).isEmpty()) {
			this.nodesSortedByDegree.remove(n.getDegree() - 1);
		}
		if (this.nodesSortedByDegree.containsKey(n.getDegree())) {
			this.nodesSortedByDegree.get(n.getDegree()).addLast(n);
		} else {
			LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
			temp.add(n);
			this.nodesSortedByDegree.put(n.getDegree(), temp);
		}
	}

	private void rccChangeAddition(UndirectedNode n1, UndirectedNode n2) {
		this.nodesSortedByDegree.get(n1.getDegree() - 1).remove(n1);
		if (this.nodesSortedByDegree.get(n1.getDegree() - 1).isEmpty()) {
			this.nodesSortedByDegree.remove(n1.getDegree() - 1);
		}

		UndirectedNode lastNode = this.richClub.removeLast();

		for (UndirectedEdge edge : n1.getEdges()) {
			UndirectedNode n = edge.getDifferingNode(n1);
			if (this.richClub.contains(n)) {
				this.edgesBetweenRichClub++;
			}
		}

		for (UndirectedEdge edge : lastNode.getEdges()) {
			UndirectedNode n = edge.getDifferingNode(n1);
			if (this.richClub.contains(n)) {
				this.edgesBetweenRichClub--;
			}
		}
		int i = this.richClubSize - 1;
		while (i > 0 && this.richClub.get(i - 1).getDegree() < n1.getDegree()) {
			i--;
		}

		if (this.nodesSortedByDegree.containsKey(lastNode.getDegree())) {
			this.nodesSortedByDegree.get(lastNode.getDegree())
					.addLast(lastNode);
		} else {
			LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
			temp.add(lastNode);
			this.nodesSortedByDegree.put(lastNode.getDegree(), temp);
		}

		this.richClub.add(i, n1);
	}

	private void inRCCAddition(UndirectedNode n1, UndirectedNode n2) {
		int i = this.richClub.indexOf(n1);
		this.richClub.remove(n1);

		while (i > 0 && this.richClub.get(i - 1).getDegree() < n1.getDegree()) {
			i--;
		}
		this.richClub.add(i, n1);

		if (this.richClub.contains(n1)) {
			this.edgesBetweenRichClub++;
		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		// DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		// if (this.richClub.contains(node)) {
		// for (DirectedEdge edge : node.getIncomingEdges()) {
		// if (this.richClub.contains(edge.getSrc())) {
		// this.edgesBetweenRichClub--;
		// }
		// }
		// for (DirectedEdge edge : node.getOutgoingEdges()) {
		// if (this.richClub.contains(edge.getDst())) {
		// this.edgesBetweenRichClub--;
		// }
		// }
		// for (DirectedEdge edge : this.rest.getFirst().getIncomingEdges()) {
		// if (this.richClub.contains(edge.getSrc())) {
		// this.edgesBetweenRichClub++;
		// }
		// }
		// for (DirectedEdge edge : this.rest.getFirst().getOutgoingEdges()) {
		// if (this.richClub.contains(edge.getDst())) {
		// this.edgesBetweenRichClub++;
		// }
		// }
		// this.richClub.remove(node);
		// node = this.rest.removeFirst();
		// this.richClub.addLast(node);
		// }

		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
		if (this.nodesSortedByDegree.containsKey(node.getDegree())) {
			this.nodesSortedByDegree.get(node.getDegree()).addLast(node);
		} else {
			LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
			temp.add(node);
			this.nodesSortedByDegree.put(node.getDegree(), temp);
		}
		return true;
	}

	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		if (this.richClub.contains(n1)) {
			inRCCRemoval(n1, n2);
		} else {
			notInRCCRemoval(n1);
		}
		if (this.richClub.contains(n2)) {
			inRCCRemoval(n2, n1);
		} else {
			notInRCCRemoval(n2);
		}

		return true;
	}

	private void notInRCCRemoval(UndirectedNode n1) {
		int n1Degree = n1.getDegree();
		this.nodesSortedByDegree.get(n1Degree + 1).remove(n1);
		if (this.nodesSortedByDegree.get(n1Degree + 1).isEmpty()) {
			this.nodesSortedByDegree.remove(n1Degree + 1);
		}
		if (this.nodesSortedByDegree.containsKey(n1Degree)) {
			this.nodesSortedByDegree.get(n1Degree).addLast(n1);
		} else {
			LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
			temp.add(n1);
			this.nodesSortedByDegree.put(n1Degree, temp);
		}
	}

	private void inRCCRemoval(UndirectedNode n1, UndirectedNode n2) {
		int n1Degree = n1.getDegree();

		if (!this.nodesSortedByDegree.containsKey(n1Degree + 1)) {
			int i = this.richClub.indexOf(n1);
			this.richClub.remove(i);

			while (i < this.richClubSize - 1
					&& this.richClub.get(i).getDegree() > n1Degree) {
				i++;
			}

			this.richClub.add(i, n1);
			if (this.richClub.contains(n2)) {
				this.edgesBetweenRichClub--;
			}

		} else {
			this.richClub.remove(n1);
			UndirectedNode newNode = this.nodesSortedByDegree.get(n1Degree + 1)
					.removeFirst();
			if (this.nodesSortedByDegree.get(n1Degree + 1).isEmpty()) {
				this.nodesSortedByDegree.remove(n1Degree + 1);
			}

			for (UndirectedEdge edge : n1.getEdges()) {
				UndirectedNode node = edge.getDifferingNode(n1);
				if (this.richClub.contains(node)) {
					this.edgesBetweenRichClub -= 2;
				}
			}
			for (UndirectedEdge edge : newNode.getEdges()) {
				UndirectedNode node = edge.getDifferingNode(newNode);
				if (this.richClub.contains(node)) {
					this.edgesBetweenRichClub += 2;
				}
			}

			int i = this.richClubSize - 1;
			while (i > 0
					&& this.richClub.get(i - 1).getDegree() < newNode
							.getDegree()) {
				i--;
			}

			if (this.nodesSortedByDegree.containsKey(n1Degree)) {
				this.nodesSortedByDegree.get(n1Degree).addLast(n1);
			} else {
				LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
				temp.add(n1);
				this.nodesSortedByDegree.put(n1Degree, temp);
			}

			this.richClub.add(i, newNode);

		}
	}

}
