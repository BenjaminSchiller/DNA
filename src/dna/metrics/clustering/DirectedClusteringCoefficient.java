package dna.metrics.clustering;

import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.nodes.DirectedNode;
import dna.series.data.lists.LongList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public abstract class DirectedClusteringCoefficient extends
		ClusteringCoefficient {

	public DirectedClusteringCoefficient(String name, Parameter... p) {
		super(name, p);
	}

	public DirectedClusteringCoefficient(String name, String[] nodeTypes,
			Parameter... p) {
		super(name, nodeTypes, p);
	}

	@Override
	public boolean isApplicable(IGraph g) {
		return g.getGraphDatastructures().isNodeType(DirectedNode.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(DirectedNode.class);
	}

	protected boolean compute() {
		this.globalCC = 0;
		this.averageCC = 0;
		this.localCC = new NodeValueList("localCC",
				this.g.getMaxNodeIndex() + 1);
		this.triangleCount = 0;
		this.potentialCount = 0;
		// this.nodeTriangleCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
		// Long.MIN_VALUE);
		// this.nodePotentialCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
		// Long.MIN_VALUE);
		this.nodePotentialCount = new LongList(g.getMaxNodeIndex() + 1);
		this.nodeTriangleCount = new LongList(g.getMaxNodeIndex() + 1);

		for (IElement nUncasted : g.getNodes()) {
			DirectedNode n = (DirectedNode) nUncasted;

			for (IElement uUncasted : n.getNeighbors()) {
				DirectedNode u = (DirectedNode) uUncasted;
				for (IElement vUncasted : n.getNeighbors()) {
					DirectedNode v = (DirectedNode) vUncasted;
					if (u.equals(v)) {
						continue;
					}
					this.nodePotentialCount.incr(n.getIndex());
					if (u.hasEdge(u, v)) {
						this.nodeTriangleCount.incr(n.getIndex());
					}
				}
			}
			this.triangleCount += this.nodeTriangleCount.getValue(n.getIndex());
			this.potentialCount += this.nodePotentialCount.getValue(n
					.getIndex());
			if (this.nodePotentialCount.getValue(n.getIndex()) == 0) {
				this.localCC.setValue(n.getIndex(), 0);
			} else {
				this.localCC.setValue(
						n.getIndex(),
						(double) this.nodeTriangleCount.getValue(n.getIndex())
								/ (double) this.nodePotentialCount.getValue(n
										.getIndex()));
			}
		}

		if (this.potentialCount == 0) {
			this.globalCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
		}
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

}
