package dna.metrics.richClubConnectivity;

import java.util.LinkedList;
import java.util.ListIterator;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.EdgeUpdate;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class RCCFirstKNodesDirectedDyn extends RCCFirstKNodesDirected {

	public RCCFirstKNodesDirectedDyn() {
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
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (this.positonInRcc.get(src) != Integer.MAX_VALUE) {
			int i = this.positonInRcc.get(src);
			int newPosition = 0;
			for (ListIterator it = this.richClub.listIterator(i); it
					.hasPrevious();) {
				newPosition = it.previousIndex() + 1;
				DirectedNode n = (DirectedNode) it.previous();
				if (n.getOutDegree() >= src.getOutDegree()) {
					break;
				}
				this.positonInRcc.put(n, newPosition);
				if (!it.hasPrevious()) {
					newPosition = 0;
				}
			}

			this.richClub.remove(i);
			this.richClub.add(newPosition, src);
			this.positonInRcc.put(src, newPosition);
			if (this.positonInRcc.get(dst) != Integer.MAX_VALUE) {
				this.edgesBetweenRichClub++;
			}
		} else if (src.getOutDegree() > this.richClub.getLast().getOutDegree()) {
			this.nodesSortedByDegree.get(src.getOutDegree() - 1).remove(src);
			if (this.nodesSortedByDegree.get(src.getOutDegree() - 1).isEmpty()) {
				this.nodesSortedByDegree.remove(src.getOutDegree() - 1);
			}

			DirectedNode lastNode = this.richClub.removeLast();
			this.positonInRcc.put(lastNode, Integer.MAX_VALUE);

			for (DirectedEdge edge : src.getOutgoingEdges()) {
				if (this.positonInRcc.get(edge.getDst()) != Integer.MAX_VALUE) {
					this.edgesBetweenRichClub++;
				}
			}
			for (DirectedEdge edge : src.getIncomingEdges()) {
				if (this.positonInRcc.get(edge.getSrc()) != Integer.MAX_VALUE) {
					this.edgesBetweenRichClub++;
				}
			}
			for (DirectedEdge edge : lastNode.getIncomingEdges()) {
				if (this.positonInRcc.get(edge.getSrc()) != Integer.MAX_VALUE) {
					this.edgesBetweenRichClub--;
				}
			}
			for (DirectedEdge edge : lastNode.getOutgoingEdges()) {
				if (this.positonInRcc.get(edge.getDst()) != Integer.MAX_VALUE) {
					this.edgesBetweenRichClub--;
				}
			}
			int i = this.richClubSize - 1;
			int newPosition = 0;
			for (ListIterator it = this.richClub.listIterator(i); it
					.hasPrevious();) {
				newPosition = it.previousIndex() + 1;
				DirectedNode n = (DirectedNode) it.previous();
				if (n.getOutDegree() >= src.getOutDegree()) {
					break;
				}
				this.positonInRcc.put(n, newPosition);
				if (!it.hasPrevious()) {
					newPosition = 0;
				}
			}

			if (this.nodesSortedByDegree.containsKey(lastNode.getOutDegree())) {
				this.nodesSortedByDegree.get(lastNode.getOutDegree()).addLast(
						lastNode);
			} else {
				LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
				temp.add(lastNode);
				this.nodesSortedByDegree.put(lastNode.getOutDegree(), temp);
			}

			this.richClub.add(newPosition, src);
			this.positonInRcc.put(src, newPosition);
		} else {
			this.nodesSortedByDegree.get(src.getOutDegree() - 1).remove(src);
			if (this.nodesSortedByDegree.get(src.getOutDegree() - 1).isEmpty()) {
				this.nodesSortedByDegree.remove(src.getOutDegree() - 1);
			}
			if (this.nodesSortedByDegree.containsKey(src.getOutDegree())) {
				this.nodesSortedByDegree.get(src.getOutDegree()).addLast(src);
			} else {
				LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
				temp.add(src);
				this.nodesSortedByDegree.put(src.getOutDegree(), temp);
			}
		}

		this.caculateRCC();
		return true;

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
		return true;
	}

	private boolean applyAfterEdgeRemoval(Update u) {

		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

		DirectedNode dst = e.getDst();
		DirectedNode src = e.getSrc();

		int srcDegree = src.getOutDegree();

		if (this.positonInRcc.get(src) != Integer.MAX_VALUE) {

			if (!this.nodesSortedByDegree.containsKey(srcDegree + 1)) {
				int i = this.richClub.indexOf(src);
				int newPosition = this.richClubSize - 1;
				this.richClub.remove(i);

				for (ListIterator it = this.richClub.listIterator(i); it
						.hasNext();) {
					newPosition = it.nextIndex();
					DirectedNode n = (DirectedNode) it.next();

					if (n.getOutDegree() <= src.getOutDegree()) {
						break;
					}
					this.positonInRcc.put(n, newPosition);
					if (!it.hasNext()) {
						newPosition = this.richClubSize - 1;
					}
				}

				this.richClub.add(newPosition, src);
				if (this.richClub.contains(dst)) {
					this.edgesBetweenRichClub--;
				}

			} else {
				this.richClub.remove(src);
				this.positonInRcc.put(src, Integer.MAX_VALUE);
				DirectedNode newNode = this.nodesSortedByDegree.get(
						srcDegree + 1).removeFirst();
				if (this.nodesSortedByDegree.get(srcDegree + 1).isEmpty()) {
					this.nodesSortedByDegree.remove(srcDegree + 1);
				}

				for (DirectedEdge edge : src.getOutgoingEdges()) {
					if (this.positonInRcc.get(edge.getDst()) != Integer.MAX_VALUE) {
						this.edgesBetweenRichClub--;
					}
				}
				for (DirectedEdge edge : src.getIncomingEdges()) {
					if (this.positonInRcc.get(edge.getSrc()) != Integer.MAX_VALUE) {
						this.edgesBetweenRichClub--;
					}
				}
				for (DirectedEdge edge : newNode.getIncomingEdges()) {
					if (this.positonInRcc.get(edge.getSrc()) != Integer.MAX_VALUE) {
						this.edgesBetweenRichClub++;
					}
				}
				for (DirectedEdge edge : newNode.getOutgoingEdges()) {
					if (this.positonInRcc.get(edge.getDst()) != Integer.MAX_VALUE) {
						this.edgesBetweenRichClub--;
					}
				}

				int i = this.richClubSize - 1;
				int newPosition = 0;
				for (ListIterator it = this.richClub.listIterator(i); it
						.hasPrevious();) {
					newPosition = it.previousIndex() + 1;
					DirectedNode n = (DirectedNode) it.previous();
					if (n.getOutDegree() >= src.getOutDegree()) {
						break;
					}
					this.positonInRcc.put(n, newPosition);
					if (!it.hasPrevious()) {
						newPosition = 0;
					}
				}

				if (this.nodesSortedByDegree.containsKey(srcDegree)) {
					this.nodesSortedByDegree.get(srcDegree).addLast(src);
				} else {
					LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
					temp.add(src);
					this.nodesSortedByDegree.put(srcDegree, temp);
				}

				this.richClub.add(newPosition, newNode);
				this.positonInRcc.put(newNode, newPosition);
			}

		} else {
			this.nodesSortedByDegree.get(srcDegree + 1).remove(src);
			if (this.nodesSortedByDegree.get(srcDegree + 1).isEmpty()) {
				this.nodesSortedByDegree.remove(srcDegree + 1);
			}
			if (this.nodesSortedByDegree.containsKey(srcDegree)) {
				this.nodesSortedByDegree.get(srcDegree).addLast(src);
			} else {
				LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
				temp.add(src);
				this.nodesSortedByDegree.put(srcDegree, temp);
			}
		}

		this.caculateRCC();
		return true;
	}
}
