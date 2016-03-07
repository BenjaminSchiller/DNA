package dna.metrics.clustering;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeNA;
import dna.metrics.algorithms.IBeforeNR;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.ArrayUtils;

public class UndirectedClusteringCoefficientU extends
		UndirectedClusteringCoefficient implements IBeforeNA, IBeforeNR,
		IBeforeEA, IAfterER, Cloneable {

	public UndirectedClusteringCoefficientU() {
		super("UndirectedClusteringCoefficientU");
	}

	public UndirectedClusteringCoefficientU(String[] nodeTypes) {
		super("UndirectedClusteringCoefficientU", nodeTypes);
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		Node n = (Node) na.getNode();
		this.localCC.setValue(n.getIndex(), 0);
		// this.nodePotentialCount = ArrayUtils.set(this.nodePotentialCount,
		// n.getIndex(), 0, Long.MIN_VALUE);
		// this.nodeTriangleCount = ArrayUtils.set(this.nodeTriangleCount,
		// n.getIndex(), 0, Long.MIN_VALUE);
		this.nodePotentialCount.setValue(n.getIndex(), 0);
		this.nodeTriangleCount.setValue(n.getIndex(), 0);
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		if (this.g.isDirected()) {
			return this.applyBeforeUpdateDirected(nr);
		} else {
			return this.applyBeforeUpdateUndirected(nr);
		}
	}

	public boolean applyBeforeUpdateDirected(NodeRemoval nr) {
		DirectedNode n = (DirectedNode) nr.getNode();

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

		this.removePotentials(n, n.getNeighborCount()
				* (n.getNeighborCount() - 1) / 2);

		this.localCC.setValue(n.getIndex(), NodeValueList.emptyValue);
		// this.nodePotentialCount[n.getIndex()] = Long.MIN_VALUE;
		// this.nodeTriangleCount[n.getIndex()] = Long.MIN_VALUE;
		this.localCC.truncate();
		// this.nodePotentialCount =
		// ArrayUtils.truncate(this.nodePotentialCount,
		// Long.MIN_VALUE);
		// this.nodeTriangleCount = ArrayUtils.truncate(this.nodeTriangleCount,
		// Long.MIN_VALUE);

		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

	private boolean applyBeforeUpdateUndirected(NodeRemoval nr) {
		UndirectedNode n = (UndirectedNode) nr.getNode();

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
			this.removePotentials(neighbors[i], neighbors[i].getDegree() - 1);
		}

		this.removePotentials(n, n.getDegree() * (n.getDegree() - 1) / 2);

		this.localCC.setValue(n.getIndex(), NodeValueList.emptyValue);
		// this.nodePotentialCount[n.getIndex()] = Long.MIN_VALUE;
		// this.nodeTriangleCount[n.getIndex()] = Long.MIN_VALUE;
		this.localCC.truncate();
		// this.nodePotentialCount =
		// ArrayUtils.truncate(this.nodePotentialCount,
		// Long.MIN_VALUE);
		// this.nodeTriangleCount = ArrayUtils.truncate(this.nodeTriangleCount,
		// Long.MIN_VALUE);

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		if (this.g.isDirected()) {
			return this.applyBeforeUpdateDirected(ea);
		} else {
			return this.applyBeforeUpdateUndirected(ea);
		}
	}

	private boolean applyBeforeUpdateDirected(EdgeAddition ea) {
		DirectedEdge e = (DirectedEdge) ea.getEdge();
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

		return true;
	}

	private boolean applyBeforeUpdateUndirected(EdgeAddition ea) {
		UndirectedEdge e = (UndirectedEdge) ea.getEdge();
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

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		if (this.g.isDirected()) {
			return this.applyAfterUpdateDirected(er);
		} else {
			return this.applyAfterUpdateUndirected(er);
		}
	}

	private boolean applyAfterUpdateDirected(EdgeRemoval er) {
		DirectedEdge e = (DirectedEdge) er.getEdge();
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

		return true;
	}

	private boolean applyAfterUpdateUndirected(EdgeRemoval er) {
		UndirectedEdge e = (UndirectedEdge) er.getEdge();
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

		return true;
	}

	@Override
	public UndirectedClusteringCoefficientU clone() {
		return new UndirectedClusteringCoefficientU();
	}

}
