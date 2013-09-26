package dna.metrics.richClubConnectivity;

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
public class RCCOneDegreeUndirectedDyn extends RCCOneDegreeUndirected {

	public RCCOneDegreeUndirectedDyn() {
		super("RCCOneDegreeDyn", ApplicationType.AfterUpdate);
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
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		if (this.richClub.contains(node1) && this.richClub.contains(node2)
				&& node1.getDegree() >= this.k && node2.getDegree() >= this.k) {
			this.richClubEdges -= 2;
		} else {
			if (node1.getDegree() < this.k && richClub.contains(node1)) {
				this.richClub.remove(node1);
				for (UndirectedEdge ed : node1.getEdges()) {
					UndirectedNode n = ed.getDifferingNode(node1);
					if (this.richClub.contains(n)) {
						this.richClubEdges -= 2;
					}
				}
			}

			if (node2.getDegree() < this.k && richClub.contains(node2)) {
				this.richClub.remove(node2);
				for (UndirectedEdge ed : node2.getEdges()) {
					UndirectedNode n = ed.getDifferingNode(node2);
					if (this.richClub.contains(n) && n != node1) {
						this.richClubEdges -= 2;
					}
				}
			}
		}
		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));

		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		if (this.richClub.contains(node1) && this.richClub.contains(node2)) {
			this.richClubEdges += 2;
		} else {
			if (node1.getDegree() >= this.k && !richClub.contains(node1)) {

				this.richClub.add(node1);
				for (UndirectedEdge ed : node1.getEdges()) {
					UndirectedNode n = ed.getDifferingNode(node1);
					if (this.richClub.contains(n)) {
						this.richClubEdges += 2;
					} else if (n == node2 && node2.getDegree() >= this.k) {
						this.richClubEdges++;
					}

				}

			}
			if (node2.getDegree() >= this.k && !richClub.contains(node2)) {

				this.richClub.add(node2);
				for (UndirectedEdge ed : node2.getEdges()) {
					UndirectedNode n = ed.getDifferingNode(node2);
					if (this.richClub.contains(n)) {
						if (node1 == n) {
							this.richClubEdges++;
						} else {
							this.richClubEdges += 2;
						}
					}
				}

			}

		}

		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
		return true;

	}

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();
		if (this.richClub.contains(n)) {
			richClub.remove(n);
			for (UndirectedEdge ed : n.getEdges()) {
				UndirectedNode d = ed.getDifferingNode(n);
				if (richClub.contains(d)) {
					this.richClubEdges -= 2;
				}
			}

		}

		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeAddition) u).getNode();
		if (n.getDegree() >= this.k) {
			richClub.add(n);
			for (UndirectedEdge ed : n.getEdges()) {
				UndirectedNode d = ed.getDifferingNode(n);
				if (richClub.contains(d)) {
					this.richClubEdges += 2;
				}
			}

		}

		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
		return true;
	}

}
