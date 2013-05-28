package dna.metrics.clusterCoefficient;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class ClosedTriangleClusteringCoefficientUpdate extends
		ClosedTriangleClusteringCoefficient {

	public ClosedTriangleClusteringCoefficientUpdate() {
		super("closedTriangleClusteringCoefficientUpdate",
				ApplicationType.BeforeAndAfterUpdate);
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			return this.applyBeforeUpdateDirected(u);
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return this.applyBeforeUpdateUndirected(u);
		}
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			return this.applyAfterUpdateDirected(u);
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return this.applyAfterUpdateUndirected(u);
		}
		return false;
	}

	private boolean applyBeforeUpdateDirected(Update u) {
		if (u instanceof NodeAddition) {
			// TODO implement closed triangle CC update (node addition D)
		} else if (u instanceof NodeRemoval) {
			// TODO implement closed triangle CC update (node removal D)
		} else if (u instanceof EdgeAddition) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (a.hasEdge(new DirectedEdge(b, a))) {
				// new triangles
				for (DirectedNode c : a.getNeighbors()) {
					if (b.hasNeighbor(c)) {
						this.addTriangle(a);
						this.addTriangle(b);
						this.addTriangle(c);
					}
				}
				// new potentials
				this.addPotentials(a, a.getNeighborCount());
				this.addPotentials(b, b.getNeighborCount());
			}
		}
		return true;
	}

	private boolean applyAfterUpdateDirected(Update u) {
		if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (a.hasEdge(new DirectedEdge(b, a))) {
				// remove triangles
				for (DirectedNode c : a.getNeighbors()) {
					if (b.hasNeighbor(c)) {
						this.removeTriangle(a);
						this.removeTriangle(b);
						this.removeTriangle(c);
					}
				}
				// remove potentials
				this.removePotentials(a, a.getNeighborCount());
				this.removePotentials(b, b.getNeighborCount());
			}
		}
		return true;
	}

	private boolean applyBeforeUpdateUndirected(Update u) {
		if (u instanceof NodeAddition) {
			// TODO implement closed triangle CC update (node addition U)
		} else if (u instanceof NodeRemoval) {
			// TODO implement closed triangle CC update (node removal U)
		} else if (u instanceof EdgeAddition) {
			UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
			UndirectedNode a = e.getNode1();
			UndirectedNode b = e.getNode2();
			// new triangles
			for (UndirectedEdge c_ : a.getEdges()) {
				UndirectedNode c = c_.getDifferingNode(a);
				if (c.hasEdge(new UndirectedEdge(c, b))) {
					this.addTriangle(a);
					this.addTriangle(b);
					this.addTriangle(c);
				}
			}
			// new potentials
			this.addPotentials(a, a.getDegree());
			this.addPotentials(b, b.getDegree());
		}
		return true;
	}

	private boolean applyAfterUpdateUndirected(Update u) {
		if (u instanceof EdgeRemoval) {
			UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
			UndirectedNode a = e.getNode1();
			UndirectedNode b = e.getNode2();
			for (UndirectedEdge a_ : a.getEdges()) {
				UndirectedNode c = a_.getDifferingNode(a);
				if (c.hasEdge(new UndirectedEdge(c, b))) {
					// remove triangles
					this.removeTriangle(a);
					this.removeTriangle(b);
					this.removeTriangle(c);
				}
			}
			// remove potentials
			this.removePotentials(a, a.getDegree());
			this.removePotentials(b, b.getDegree());
		}
		return true;
	}

}
