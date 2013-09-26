package dna.metrics.clusterCoefficient;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.series.data.NodeValueList;
import dna.updates.Batch;
import dna.updates.Update;
import dna.util.ArrayUtils;

@SuppressWarnings("rawtypes")
public class OpenTriangleClusteringCoefficientRecomp extends
		OpenTriangleClusteringCoefficient {

	public OpenTriangleClusteringCoefficientRecomp() {
		super("openTriangleClusteringCoefficientRecomp",
				ApplicationType.Recomputation, MetricType.exact);
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
		return false;
	}

	@Override
	public boolean compute() {
		this.triangleCount = 0;
		this.potentialCount = 0;
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
					if (u.hasEdge(new DirectedEdge(u, v))) {
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
