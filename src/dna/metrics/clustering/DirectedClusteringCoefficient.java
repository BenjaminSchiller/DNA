package dna.metrics.clustering;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.metrics.IMetric;
import dna.series.data.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public class DirectedClusteringCoefficient extends ClusteringCoefficient {

	public DirectedClusteringCoefficient(String name,
			IMetric.MetricType metricType, Parameter... p) {
		super(name, metricType, p);
	}

	@Override
	public boolean isApplicable(Graph g) {
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
		this.nodeTriangleCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
				Long.MIN_VALUE);
		this.nodePotentialCount = ArrayUtils.init(g.getMaxNodeIndex() + 1,
				Long.MIN_VALUE);

		for (IElement nUncasted : g.getNodes()) {
			DirectedNode n = (DirectedNode) nUncasted;
			this.nodeTriangleCount[n.getIndex()] = 0;
			this.nodePotentialCount[n.getIndex()] = 0;
			for (IElement uUncasted : n.getNeighbors()) {
				DirectedNode u = (DirectedNode) uUncasted;
				for (IElement vUncasted : n.getNeighbors()) {
					DirectedNode v = (DirectedNode) vUncasted;
					if (u.equals(v)) {
						continue;
					}
					this.nodePotentialCount[n.getIndex()]++;
					if (u.hasEdge(u, v)) {
						this.nodeTriangleCount[n.getIndex()]++;
					}
				}
			}
			this.triangleCount += this.nodeTriangleCount[n.getIndex()];
			this.potentialCount += this.nodePotentialCount[n.getIndex()];
			if (this.nodePotentialCount[n.getIndex()] == 0) {
				this.localCC.setValue(n.getIndex(), 0);
			} else {
				this.localCC
						.setValue(
								n.getIndex(),
								(double) this.nodeTriangleCount[n.getIndex()]
										/ (double) this.nodePotentialCount[n
												.getIndex()]);
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
