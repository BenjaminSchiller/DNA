package dna.metrics.richClubConnectivity;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class RCCPerDegreeUndirectedDyn extends RCCPerDegreeUndirected {

	public RCCPerDegreeUndirectedDyn() {
		super("RCCPerDegreeDyn", ApplicationType.AfterUpdate);
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

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
		this.richClubs.put(node.getDegree(),
				this.richClubs.get(node.getDegree()) + 1);
		calculateRCC();
		return true;
	}

	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();
		int n1Degree = node1.getDegree();
		int n2Degree = node2.getDegree();

		// Current removal the deleted edge is still in the set from the source
		// Node
		int n1edges = 0;
		int n2edges = 0;

		for (UndirectedEdge ed : node1.getEdges()) {
			UndirectedNode n = ed.getNode1();
			if (n == node1) {
				n = ed.getNode2();
			}

			if (n.getDegree() > n1Degree) {
				n1edges += 2;
			}

		}
		for (UndirectedEdge ed : node2.getEdges()) {
			UndirectedNode n = ed.getNode1();
			if (n == node2) {
				n = ed.getNode2();
			}

			if (n.getDegree() > n2Degree) {
				n2edges += 2;
			}

		}

		this.richClubs.put(n1Degree + 1, this.richClubs.get(n1Degree + 1) - 1);
		this.richClubs.put(n2Degree + 1, this.richClubs.get(n2Degree + 1) - 1);
		this.richClubEdges.put(n1Degree + 1,
				this.richClubEdges.get(n1Degree + 1) - n1edges);
		this.richClubEdges.put(n2Degree + 1,
				this.richClubEdges.get(n2Degree + 1) - n2edges);

		if (this.richClubs.get(n1Degree + 1) == 0) {
			this.richClubs.remove(n1Degree + 1);
			this.richClubEdges.remove(n1Degree + 1);
			this.richClubCoefficienten.remove(n1Degree + 1);
		}
		if (this.richClubs.get(n2Degree + 1) == 0) {
			this.richClubs.remove(n2Degree + 1);
			this.richClubEdges.remove(n2Degree + 1);
			this.richClubCoefficienten.remove(n2Degree + 1);
		}

		if (this.richClubs.containsKey(n2Degree)) {
			this.richClubs.put(n2Degree, this.richClubs.get(n2Degree) + 1);
			this.richClubEdges.put(n2Degree, this.richClubEdges.get(n2Degree)
					+ n2edges);

		} else {
			this.richClubs.put(n2Degree, 1);
			this.richClubEdges.put(n2Degree, n2edges);
		}

		if (this.richClubs.containsKey(n1Degree)) {
			this.richClubs.put(n1Degree, this.richClubs.get(n1Degree) + 1);
			this.richClubEdges.put(n1Degree, this.richClubEdges.get(n1Degree)
					+ n1edges);

		} else {
			this.richClubs.put(n1Degree, 1);
			this.richClubEdges.put(n1Degree, n1edges);
		}

		calculateRCC();
		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();
		int n1Degree = node1.getDegree();
		int n2Degree = node2.getDegree();
		int n1edges = 0;
		int n2edges = 0;

		for (UndirectedEdge ed : node1.getEdges()) {
			UndirectedNode n = ed.getNode1();
			if (n == node1) {
				n = ed.getNode2();
			}

			if (n.getDegree() >= n1Degree) {
				n1edges += 2;
			}

		}
		for (UndirectedEdge ed : node2.getEdges()) {
			UndirectedNode n = ed.getNode1();
			if (n == node2) {
				n = ed.getNode2();
			}

			if (n.getDegree() >= n2Degree) {
				n2edges += 2;
			}

		}

		this.richClubs.put(n1Degree - 1, this.richClubs.get(n1Degree - 1) - 1);
		this.richClubs.put(n2Degree - 1, this.richClubs.get(n2Degree - 1) - 1);
		this.richClubEdges.put(n1Degree - 1,
				this.richClubEdges.get(n1Degree - 1) - n1edges);
		this.richClubEdges.put(n2Degree - 1,
				this.richClubEdges.get(n2Degree - 1) - n2edges);

		if (this.richClubs.get(n1Degree - 1) == 0) {
			this.richClubs.remove(n1Degree - 1);
			this.richClubEdges.remove(n1Degree - 1);
			this.richClubCoefficienten.remove(n1Degree - 1);
		}
		if (this.richClubs.get(n2Degree - 1) == 0) {
			this.richClubs.remove(n2Degree - 1);
			this.richClubEdges.remove(n2Degree - 1);
			this.richClubCoefficienten.remove(n2Degree - 1);
		}

		if (this.richClubs.containsKey(n2Degree)) {
			this.richClubs.put(n2Degree, this.richClubs.get(n2Degree) + 1);
			this.richClubEdges.put(n2Degree, this.richClubEdges.get(n2Degree)
					+ n2edges);

		} else {
			this.richClubs.put(n2Degree, 1);
			this.richClubEdges.put(n2Degree, n2edges);
		}

		if (this.richClubs.containsKey(n1Degree)) {
			this.richClubs.put(n1Degree, this.richClubs.get(n1Degree) + 1);
			this.richClubEdges.put(n1Degree, this.richClubEdges.get(n1Degree)
					+ n1edges);

		} else {
			this.richClubs.put(n1Degree, 1);
			this.richClubEdges.put(n1Degree, n1edges);
		}

		calculateRCC();
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		this.richClubs.put(node.getDegree(),
				this.richClubs.get(node.getDegree()) - 1);
		int updateEdges = 0;

		for (UndirectedEdge ed : node.getEdges()) {
			UndirectedNode n = ed.getNode1();
			if (n == node) {
				n = ed.getNode2();
			}

			if (n.getDegree() >= node.getDegree()) {
				updateEdges += 2;
			} else {
				int temp = richClubEdges.get(n.getDegree());
				richClubEdges.put(n.getDegree(), temp - 2);
			}
		}

		int temp = richClubEdges.get(node.getDegree());
		richClubEdges.put(node.getDegree(), temp - updateEdges);
		calculateRCC();
		return true;
	}

}
