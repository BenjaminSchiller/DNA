package dna.metrics.richClubConnectivity.richClubConnectivityPerDegree;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class RichClubConnectivityPerDegreeU extends
		RichClubConnectivityPerDegree {

	public RichClubConnectivityPerDegreeU() {
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterNodeAddition(u);
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
				return applyAfterNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterUndirectedNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterUndirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterUndirectedEdgeRemoval(u);
			}
		}

		return false;
	}

	private boolean applyAfterDirectedEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();

		// Current removal the deleted edge is still in the set from the source
		// Node
		int edges = 0;

		for (IElement iE : src.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getDst().getOutDegree() > srcDegree) {
				edges++;
			}
		}
		for (IElement iE : src.getIncomingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getSrc().getOutDegree() > srcDegree) {
				edges++;
			}
		}

		// update edges for delete edge
		if (dstDegree > srcDegree + 1) {
			this.richClubEdges.put(srcDegree + 1,
					this.richClubEdges.get(srcDegree + 1) - 1);
		} else {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) - 1);
		}

		this.richClubEdges.put(srcDegree + 1,
				this.richClubEdges.get(srcDegree + 1) - edges);

		// update Old richclub size
		this.richClubs
				.put(srcDegree + 1, this.richClubs.get(srcDegree + 1) - 1);
		if (this.richClubs.get(srcDegree + 1) == 0) {
			this.richClubs.remove(srcDegree + 1);
			this.richClubEdges.remove(srcDegree + 1);
		}

		// update new RichClub size and edges
		if (this.richClubs.containsKey(srcDegree)) {
			this.richClubs.put(srcDegree, this.richClubs.get(srcDegree) + 1);
			this.richClubEdges.put(srcDegree, this.richClubEdges.get(srcDegree)
					+ edges);

		} else {
			this.richClubs.put(srcDegree, 1);
			this.richClubEdges.put(srcDegree, edges);
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcDegree = src.getOutDegree();
		int dstDegree = dst.getOutDegree();
		int edges = 0;

		for (IElement iE : src.getOutgoingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getDst().getOutDegree() >= srcDegree) {
				edges++;
			}

		}
		for (IElement iE : src.getIncomingEdges()) {
			DirectedEdge edge = (DirectedEdge) iE;
			if (edge.getSrc().getOutDegree() >= srcDegree) {
				edges++;
			}

		}

		if (dstDegree < srcDegree) {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) + 1);
			this.richClubEdges.put(srcDegree - 1,
					this.richClubEdges.get(srcDegree - 1) - edges);
		} else {
			this.richClubEdges.put(srcDegree - 1,
					this.richClubEdges.get(srcDegree - 1) - (edges - 1));
		}

		this.richClubs
				.put(srcDegree - 1, this.richClubs.get(srcDegree - 1) - 1);
		if (this.richClubs.get(srcDegree - 1) == 0) {
			this.richClubs.remove(srcDegree - 1);
			this.richClubEdges.remove(srcDegree - 1);
		}

		if (this.richClubs.containsKey(srcDegree)) {
			this.richClubs.put(srcDegree, this.richClubs.get(srcDegree) + 1);
			this.richClubEdges.put(srcDegree, this.richClubEdges.get(srcDegree)
					+ edges);

		} else {
			this.richClubs.put(srcDegree, 1);
			this.richClubEdges.put(srcDegree, edges);
			this.highestDegree = Math.max(highestDegree, srcDegree);
		}

		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		int degree = -1;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();
			degree = node.getOutDegree();
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
			degree = node.getDegree();
		}
		if (this.richClubs.containsKey(degree)) {
			this.richClubs.put(degree, this.richClubs.get(degree) + 1);
		} else {
			this.richClubs.put(degree, 1);
			this.richClubEdges.put(degree, 0);
		}
		return true;
	}

	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
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

	private boolean applyAfterUndirectedEdgeAddition(Update u) {
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
		this.richClubs.put(node.getOutDegree(),
				this.richClubs.get(node.getOutDegree()) - 1);
		return true;
	}

	private boolean applyAfterUndirectedNodeRemoval(Update u) {
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
		this.richClubs.put(node.getDegree(),
				this.richClubs.get(node.getDegree()) - 1);
		return true;
	}

}
