package dna.depr.metrics.clusterCoefficient;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;
import dna.util.ArrayUtils;

/**
 * 
 * Update version of the undirected clustering coefficient.
 * 
 * @author benni
 * 
 */
public class UndirectedClusteringCoefficientU extends
		UndirectedClusteringCoefficient {

	public UndirectedClusteringCoefficientU() {
		super("UndirectedClusteringCoefficientU",
				ApplicationType.BeforeAndAfterUpdate, IMetric.MetricType.exact);
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
			Node n = (Node) ((NodeAddition) u).getNode();
			this.localCC.setValue(n.getIndex(), 0);
			this.nodePotentialCount = ArrayUtils.set(this.nodePotentialCount,
					n.getIndex(), 0, Long.MIN_VALUE);
			this.nodeTriangleCount = ArrayUtils.set(this.nodeTriangleCount,
					n.getIndex(), 0, Long.MIN_VALUE);
			this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());
		} else if (u instanceof NodeRemoval) {

			DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();

			DirectedNode[] neighbors = new DirectedNode[n.getNeighborCount()];
			int index = 0;
			for (IElement neighbor : n.getNeighbors()) {
				neighbors[index++] = (DirectedNode) neighbor;
			}

			for (int i = 0; i < neighbors.length; i++) {
				for (int j = i + 1; j < neighbors.length; j++) {
					if (neighbors[i].hasEdge(neighbors[i], neighbors[j])
							&& neighbors[i].hasEdge(neighbors[j], neighbors[i])) {
						this.removeTriangle(n);
						this.removeTriangle(neighbors[i]);
						this.removeTriangle(neighbors[j]);
					}
				}
				this.removePotentials(neighbors[i],
						neighbors[i].getNeighborCount() - 1);
			}

			this.removePotentials(n,
					n.getNeighborCount() * (n.getNeighborCount() - 1) / 2);

			this.localCC.setValue(n.getIndex(), NodeValueList.emptyValue);
			this.nodePotentialCount[n.getIndex()] = Long.MIN_VALUE;
			this.nodeTriangleCount[n.getIndex()] = Long.MIN_VALUE;
			this.localCC.truncate();
			this.nodePotentialCount = ArrayUtils.truncate(
					this.nodePotentialCount, Long.MIN_VALUE);
			this.nodeTriangleCount = ArrayUtils.truncate(
					this.nodeTriangleCount, Long.MIN_VALUE);

			this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		} else if (u instanceof EdgeAddition) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (a.hasEdge(b, a)) {
				// new triangles
				for (IElement cUncasted : a.getNeighbors()) {
					DirectedNode c = (DirectedNode) cUncasted;
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
			if (a.hasEdge(b, a)) {
				// remove triangles
				for (IElement cUncasted : a.getNeighbors()) {
					DirectedNode c = (DirectedNode) cUncasted;
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
			Node n = (Node) ((NodeAddition) u).getNode();
			this.localCC.setValue(n.getIndex(), 0);
			this.nodePotentialCount = ArrayUtils.set(this.nodePotentialCount,
					n.getIndex(), 0, Long.MIN_VALUE);
			this.nodeTriangleCount = ArrayUtils.set(this.nodeTriangleCount,
					n.getIndex(), 0, Long.MIN_VALUE);
			this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());
		} else if (u instanceof NodeRemoval) {
			UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();

			UndirectedNode[] neighbors = new UndirectedNode[n.getDegree()];
			int index = 0;
			for (IElement eUncasted : n.getEdges()) {
				UndirectedEdge e = (UndirectedEdge) eUncasted;
				neighbors[index++] = (UndirectedNode) e.getDifferingNode(n);
			}

			for (int i = 0; i < neighbors.length; i++) {
				for (int j = i + 1; j < neighbors.length; j++) {
					if (neighbors[i].hasEdge(neighbors[i], neighbors[j])) {
						this.removeTriangle(n);
						this.removeTriangle(neighbors[i]);
						this.removeTriangle(neighbors[j]);
					}
				}
				this.removePotentials(neighbors[i],
						neighbors[i].getDegree() - 1);
			}

			this.removePotentials(n, n.getDegree() * (n.getDegree() - 1) / 2);

			this.localCC.setValue(n.getIndex(), NodeValueList.emptyValue);
			this.nodePotentialCount[n.getIndex()] = Long.MIN_VALUE;
			this.nodeTriangleCount[n.getIndex()] = Long.MIN_VALUE;
			this.localCC.truncate();
			this.nodePotentialCount = ArrayUtils.truncate(
					this.nodePotentialCount, Long.MIN_VALUE);
			this.nodeTriangleCount = ArrayUtils.truncate(
					this.nodeTriangleCount, Long.MIN_VALUE);
		} else if (u instanceof EdgeAddition) {
			UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
			UndirectedNode a = e.getNode1();
			UndirectedNode b = e.getNode2();
			// new triangles
			for (IElement c_Uncasted : a.getEdges()) {
				UndirectedEdge c_ = (UndirectedEdge) c_Uncasted;
				UndirectedNode c = (UndirectedNode) c_.getDifferingNode(a);
				if (c.hasEdge(c, b)) {
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
			for (IElement a_Uncasted : a.getEdges()) {
				UndirectedEdge a_ = (UndirectedEdge) a_Uncasted;
				UndirectedNode c = (UndirectedNode) a_.getDifferingNode(a);
				if (c.hasEdge(c, b)) {
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
