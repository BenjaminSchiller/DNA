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

		if (node1.getDegree() > node2.getDegree()) {
			this.richClubEdges.put(node2.getDegree() + 1,
					this.richClubEdges.get(node2.getDegree() + 1) - 2);
		} else {
			this.richClubEdges.put(node1.getDegree() + 1,
					this.richClubEdges.get(node1.getDegree() + 1) - 2);
		}

		checkChangesDel(node1);
		checkChangesDel(node2);

		calculateRCC();
		return true;
	}

	private void checkChangesDel(UndirectedNode node) {
		int degree = node.getDegree();
		int edges = 0;
		for (UndirectedEdge ed : node.getEdges()) {
			UndirectedNode n = ed.getDifferingNode(node);

			if (n.getDegree() > degree) {
				edges += 2;
			}

		}
		this.richClubs.put(degree + 1, this.richClubs.get(degree + 1) - 1);
		this.richClubEdges.put(degree + 1, this.richClubEdges.get(degree + 1)
				- edges);
		if (this.richClubs.get(degree + 1) == 0) {
			removeRCC(degree + 1);
		}

		if (this.richClubs.containsKey(degree)) {
			this.richClubs.put(degree, this.richClubs.get(degree) + 1);
			this.richClubEdges.put(degree, this.richClubEdges.get(degree)
					+ edges);

		} else {
			this.richClubs.put(degree, 1);
			this.richClubEdges.put(degree, edges);
		}

	}

	private void removeRCC(int degree) {
		this.richClubs.remove(degree);
		this.richClubEdges.remove(degree);
		this.richClubCoefficienten.remove(degree);
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		checkChangesAdd(node1);
		checkChangesAdd(node2);

		calculateRCC();
		return true;
	}

	private void checkChangesAdd(UndirectedNode node) {
		int degree = node.getDegree();
		int edges = 0;
		for (UndirectedEdge ed : node.getEdges()) {
			UndirectedNode n = ed.getDifferingNode(node);

			if (n.getDegree() >= degree) {
				edges += 2;
			}

		}
		this.richClubs.put(degree - 1, this.richClubs.get(degree - 1) - 1);
		this.richClubEdges.put(degree - 1, this.richClubEdges.get(degree - 1)
				- edges);
		if (this.richClubs.get(degree - 1) == 0) {
			removeRCC(degree - 1);
		}

		if (this.richClubs.containsKey(degree)) {
			this.richClubs.put(degree, this.richClubs.get(degree) + 1);
			this.richClubEdges.put(degree, this.richClubEdges.get(degree)
					+ edges);

		} else {
			this.richClubs.put(degree, 1);
			this.richClubEdges.put(degree, edges);
			this.highestDegree = Math.max(degree, this.highestDegree);
		}

	}

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		this.richClubs.put(node.getDegree(),
				this.richClubs.get(node.getDegree()) - 1);
		int updateEdges = 0;

		for (UndirectedEdge ed : node.getEdges()) {
			UndirectedNode n = ed.getDifferingNode(node);

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
