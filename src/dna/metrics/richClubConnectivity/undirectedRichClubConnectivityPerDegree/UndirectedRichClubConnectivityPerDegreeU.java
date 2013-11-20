package dna.metrics.richClubConnectivity.undirectedRichClubConnectivityPerDegree;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedRichClubConnectivityPerDegreeU extends
		UndirectedRichClubConnectivityPerDegree {

	public UndirectedRichClubConnectivityPerDegreeU() {
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
		if (this.richClubs.containsKey(node.getDegree())) {
			this.richClubs.put(node.getDegree(),
					this.richClubs.get(node.getDegree()) + 1);
		} else {
			this.richClubs.put(node.getDegree(), 1);
			this.richClubEdges.put(node.getDegree(), 0);
		}
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

		return true;
	}

	private void checkChangesDel(UndirectedNode node) {
		int degree = node.getDegree();
		int edges = 0;
		for (IElement iEdge : node.getEdges()) {
			UndirectedEdge ed = (UndirectedEdge) iEdge;
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
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();
		checkChangesAdd(node1, node2);
		checkChangesAdd(node2, node1);

		return true;
	}

	private void checkChangesAdd(UndirectedNode node, UndirectedNode node2) {
		int degree = node.getDegree();
		int edges = 0;
		int node2Degree = node2.getDegree();
		for (IElement iEdge : node.getEdges()) {
			UndirectedEdge ed = (UndirectedEdge) iEdge;
			UndirectedNode n = ed.getDifferingNode(node);
			if (n == node2 && n.getDegree() == degree) {
				edges++;
				continue;
			}

			if (n.getDegree() >= degree) {
				edges += 2;
			}
		}
		if (node2Degree < degree) {
			this.richClubEdges.put(degree - 1,
					this.richClubEdges.get(degree - 1) - edges);
		} else {
			if (node2Degree == degree) {
				this.richClubEdges.put(degree - 1,
						this.richClubEdges.get(degree - 1) - (edges - 1));
			} else {
				this.richClubEdges.put(degree - 1,
						this.richClubEdges.get(degree - 1) - (edges - 2));
			}
		}

		this.richClubs.put(degree - 1, this.richClubs.get(degree - 1) - 1);
		if (this.richClubs.get(degree - 1) == 0) {
			this.richClubs.remove(degree - 1);
			this.richClubEdges.remove(degree - 1);
		}

		if (this.richClubs.containsKey(degree)) {
			this.richClubs.put(degree, this.richClubs.get(degree) + 1);
			this.richClubEdges.put(degree, this.richClubEdges.get(degree)
					+ edges);

		} else {
			this.richClubs.put(degree, 1);
			this.richClubEdges.put(degree, edges);
			this.highestDegree = Math.max(highestDegree, degree);
		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		richClubs.put(node.getDegree(), richClubs.get(node.getDegree()) - 1);
		if (richClubs.get(node.getDegree()) == 0) {
			removeRCC(node.getDegree());
		}
		for (IElement ie : node.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			UndirectedNode n = e.getDifferingNode(node);
			if (node.getDegree() > n.getDegree()) {
				this.richClubEdges.put(n.getDegree() + 1,
						this.richClubEdges.get(n.getDegree() + 1) - 2);
			} else {
				this.richClubEdges.put(node.getDegree(),
						this.richClubEdges.get(node.getDegree()) - 2);
			}
			checkChangesDel(n);
		}
		return true;
	}

}
