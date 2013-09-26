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
public class RCCKNodeIntervalUndirectedDyn extends RCCKNodeIntervalUndirected {

	public RCCKNodeIntervalUndirectedDyn() {
		super("RCCKNodeIntervalDyn", ApplicationType.AfterUpdate);
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

	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		checkRemovalForNodeN(n1, n2);
		checkRemovalForNodeN(n2, n1);

		int edgesbetweenRCC = 0;
		for (int i = 0; i < richClubs.size(); i++) {
			edgesbetweenRCC += this.richClubEdges.get(i);
			richClubCoefficienten.put(i, (double) edgesbetweenRCC
					/ (double) ((i + 1) * this.richClubIntervall * ((i + 1)
							* this.richClubIntervall - 1)));
		}
		return true;
	}

	private void checkRemovalForNodeN(UndirectedNode n1, UndirectedNode n2) {
		LinkedList<UndirectedNode> n1RC = this.richClubs.get(this.nodesRichClub
				.get(n1.getIndex()));
		int n1Degree = n1.getDegree();
		if (n1RC.getLast().getDegree() >= n1Degree
				|| this.nodesRichClub.get(n1.getIndex()) == this.richClubs
						.size() - 1) {
			int i = n1RC.indexOf(n1);
			n1RC.remove(n1);

			while (i < n1RC.size() - 1
					&& n1RC.get(i + 1).getDegree() < n1Degree) {
				i++;
			}
			n1RC.add(i, n1);

			if (n1RC.contains(n2)) {
				this.richClubEdges.put(n1Degree,
						this.richClubEdges.get(n1Degree) - 1);
			}
		} else {

			LinkedList<UndirectedNode> newRC = this.richClubs
					.get(this.nodesRichClub.get(n1.getIndex()) + 1);

			UndirectedNode firstNode = newRC.removeFirst();
			n1RC.remove(n1);
			n1RC.addLast(firstNode);

			int srcEdges = 0;
			int lastNodeEdges = 0;
			for (UndirectedEdge edge : n1.getEdges()) {
				UndirectedNode node = edge.getDifferingNode(n1);
				if (this.nodesRichClub.get(node.getIndex()) >= this.nodesRichClub
						.get(n1.getIndex())) {
					srcEdges += 2;
				}
			}

			for (UndirectedEdge edge : firstNode.getEdges()) {
				UndirectedNode node = edge.getDifferingNode(firstNode);
				if (this.nodesRichClub.get(node.getIndex()) > this.nodesRichClub
						.get(firstNode.getIndex())) {
					lastNodeEdges += 2;
				}
			}

			int j = newRC.size() - 1;

			while (j < newRC.size() - 1
					&& newRC.get(j + 1).getDegree() > n1Degree) {
				j++;
			}

			this.richClubEdges
					.put(this.nodesRichClub.get(n1.getIndex()),
							this.richClubEdges.get(this.nodesRichClub.get(n1
									.getIndex())) - srcEdges + lastNodeEdges);
			this.richClubEdges
					.put(this.nodesRichClub.get(n1.getIndex() + 1),
							this.richClubEdges.get(this.nodesRichClub.get(n1
									.getIndex()) + 1)
									+ srcEdges
									- lastNodeEdges);

			newRC.add(j, n1);
			newRC.remove(firstNode);

			this.nodesRichClub.put(n1.getIndex(),
					this.nodesRichClub.get(n1.getIndex()) + 1);
			this.nodesRichClub.put(firstNode.getIndex(),
					this.nodesRichClub.get(firstNode.getIndex()) - 1);

		}
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		checkEdgeAddForNodeN(n1, n2);
		checkEdgeAddForNodeN(n2, n1);

		int edgesbetweenRCC = 0;
		for (int i = 0; i < richClubs.size(); i++) {
			edgesbetweenRCC += this.richClubEdges.get(i);
			richClubCoefficienten.put(i, (double) edgesbetweenRCC
					/ (double) ((i + 1) * this.richClubIntervall * ((i + 1)
							* this.richClubIntervall - 1)));
		}

		return true;
	}

	private void checkEdgeAddForNodeN(UndirectedNode n1, UndirectedNode n2) {
		LinkedList<UndirectedNode> n1RC = this.richClubs.get(this.nodesRichClub
				.get(n1.getIndex()));

		int n1Degree = n1.getDegree();
		if (n1RC.getFirst().getDegree() >= n1Degree
				|| this.nodesRichClub.get(n1.getIndex()) == 0) {
			int i = n1RC.indexOf(n1);
			n1RC.remove(n1);

			while (i > 0 && n1RC.get(i - 1).getDegree() < n1Degree) {
				i--;
			}
			n1RC.add(i, n1);

			if (n1RC.contains(n2)) {
				this.richClubEdges.get(n1.getDegree());
			}
		} else {

			LinkedList<UndirectedNode> newRC = this.richClubs
					.get(this.nodesRichClub.get(n1.getIndex()) - 1);

			UndirectedNode lastNode = newRC.removeLast();
			n1RC.remove(n1);
			n1RC.addFirst(lastNode);

			int srcEdges = 0;
			int lastNodeEdges = 0;
			for (UndirectedEdge edge : n1.getEdges()) {
				UndirectedNode node = edge.getDifferingNode(n1);
				if (this.nodesRichClub.get(node.getIndex()) > this.nodesRichClub
						.get(n1.getIndex())) {
					srcEdges += 2;
				}
			}

			for (UndirectedEdge edge : lastNode.getEdges()) {
				UndirectedNode node = edge.getDifferingNode(lastNode);
				if (this.nodesRichClub.get(node.getIndex()) >= this.nodesRichClub
						.get(lastNode.getIndex())) {
					lastNodeEdges += 2;
				}
			}

			int j = newRC.size() - 1;

			newRC.remove(lastNode);
			while (j > 0 && newRC.get(j - 1).getDegree() < n1Degree) {
				j--;
			}

			this.richClubEdges
					.put(this.nodesRichClub.get(n1.getIndex()),
							this.richClubEdges.get(this.nodesRichClub.get(n1
									.getIndex())) - srcEdges + lastNodeEdges);
			this.richClubEdges
					.put(this.nodesRichClub.get(n1.getIndex() - 1),
							this.richClubEdges.get(this.nodesRichClub.get(n1
									.getIndex()) - 1)
									+ srcEdges
									- lastNodeEdges);

			newRC.add(j, n1);

			this.nodesRichClub.put(n1.getIndex(),
					this.nodesRichClub.get(n1.getIndex()) - 1);
			this.nodesRichClub.put(lastNode.getIndex(),
					this.nodesRichClub.get(lastNode.getIndex()) + 1);

		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		return false;
	}

}
