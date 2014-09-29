package dna.metrics.clustering;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.IMetricNew;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterNA;
import dna.metrics.algorithms.IAfterNR;
import dna.metrics.algorithms.IBeforeER;
import dna.series.data.NodeValueList;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.ArrayUtils;

public class DirectedClusteringCoefficientU extends
		DirectedClusteringCoefficient implements IAfterNA, IAfterNR, IAfterEA,
		IBeforeER {

	public DirectedClusteringCoefficientU() {
		super("DirectedClusteringCoefficientU", IMetricNew.MetricType.exact);
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		DirectedEdge e = (DirectedEdge) er.getEdge();
		DirectedNode a = e.getSrc();
		DirectedNode b = e.getDst();

		// t1
		for (IElement cUncasted : a.getNeighbors()) {
			DirectedNode c = (DirectedNode) cUncasted;
			if (b.hasNeighbor(c)) {
				this.removeTriangle(c);
			}
		}

		// t2 / t3
		if (a.hasNeighbor(b)) {
			// t2
			for (IElement cUncasted : a.getNeighbors()) {
				DirectedNode c = (DirectedNode) cUncasted;
				if (!a.hasNeighbor(b)) {
					continue;
				}
				if (c.hasEdge(c, b)) {
					this.removeTriangle(a);
				}
				if (c.hasEdge(b, c)) {
					this.removeTriangle(a);
				}
			}

			// t3
			for (IElement cUncasted : b.getNeighbors()) {
				DirectedNode c = (DirectedNode) cUncasted;
				if (c.hasEdge(c, a)) {
					this.removeTriangle(b);
				}
				if (c.hasEdge(a, c)) {
					this.removeTriangle(b);
				}
			}

		}

		// p
		if (a.hasNeighbor(b)) {
			this.removePotentials(a, 2 * (a.getNeighborCount() - 1));
			this.removePotentials(b, 2 * (b.getNeighborCount() - 1));
		}

		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		Node n = (Node) na.getNode();
		this.localCC.setValue(n.getIndex(), 0);
		this.nodePotentialCount = ArrayUtils.set(this.nodePotentialCount,
				n.getIndex(), 0, Long.MIN_VALUE);
		this.nodeTriangleCount = ArrayUtils.set(this.nodeTriangleCount,
				n.getIndex(), 0, Long.MIN_VALUE);
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		DirectedNode a = (DirectedNode) nr.getNode();

		// t1
		for (IElement bUncasted : a.getNeighbors()) {
			DirectedNode b = (DirectedNode) bUncasted;
			for (IElement cUncasted : a.getNeighbors()) {
				DirectedNode c = (DirectedNode) cUncasted;
				if (b.equals(c)) {
					continue;
				}
				if (b.hasEdge(b, c)) {
					this.removeTriangle(a);
				}
			}
		}

		// t2
		for (IElement bUncasted : a.getNeighbors()) {
			DirectedNode b = (DirectedNode) bUncasted;
			for (IElement cUncasted : b.getNeighbors()) {
				DirectedNode c = (DirectedNode) cUncasted;
				if (a.hasEdge(a, c)) {
					this.removeTriangle(b);
				}
				if (a.hasEdge(c, a)) {
					this.removeTriangle(b);
				}
			}
		}

		// p1
		this.removePotentials(a, a.getNeighborCount()
				* (a.getNeighborCount() - 1));

		// p2
		for (IElement bUncasted : a.getNeighbors()) {
			DirectedNode b = (DirectedNode) bUncasted;
			this.removePotentials(b, b.getNeighborCount() * 2);
		}

		this.localCC.setValue(a.getIndex(), NodeValueList.emptyValue);
		this.nodePotentialCount[a.getIndex()] = Long.MIN_VALUE;
		this.nodeTriangleCount[a.getIndex()] = Long.MIN_VALUE;
		this.localCC.truncate();
		this.nodePotentialCount = ArrayUtils.truncate(this.nodePotentialCount,
				Long.MIN_VALUE);
		this.nodeTriangleCount = ArrayUtils.truncate(this.nodeTriangleCount,
				Long.MIN_VALUE);

		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		DirectedEdge e = (DirectedEdge) ea.getEdge();
		DirectedNode a = e.getSrc();
		DirectedNode b = e.getDst();

		// t1
		for (IElement cUncasted : a.getNeighbors()) {
			DirectedNode c = (DirectedNode) cUncasted;
			if (b.hasNeighbor(c)) {
				this.addTriangle(c);
			}
		}

		// t2 / t3
		if (a.hasNeighbor(b)) {

			// t2
			for (IElement cUncasted : a.getNeighbors()) {
				DirectedNode c = (DirectedNode) cUncasted;
				if (!a.hasNeighbor(b)) {
					continue;
				}
				if (c.hasEdge(c, b)) {
					this.addTriangle(a);
				}
				if (c.hasEdge(b, c)) {
					this.addTriangle(a);
				}
			}

			// t3
			for (IElement cUncasted : b.getNeighbors()) {
				DirectedNode c = (DirectedNode) cUncasted;
				if (c.hasEdge(c, a)) {
					this.addTriangle(b);
				}
				if (c.hasEdge(a, c)) {
					this.addTriangle(b);
				}
			}

		}

		// p
		if (a.hasNeighbor(b)) {
			this.addPotentials(a, 2 * (a.getNeighborCount() - 1));
			this.addPotentials(b, 2 * (b.getNeighborCount() - 1));
		}

		return true;
	}

}
