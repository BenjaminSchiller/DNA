package dna.depr.metrics.clusterCoefficient;

import dna.depr.metrics.MetricOld;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

/**
 * 
 * Abstract super class for all implementations that compute the directed
 * clustering coefficient, i.e., potential triangles are all triplets a<->b<->c.
 * For each potential triangle, two triangles can exist, i.e., if a->c or a<-c
 * exist(s). This metric can only be applied to DIRECTED graphs.
 * 
 * @author benni
 * 
 */
public abstract class DirectedClusteringCoefficient extends
		ClusteringCoefficient {
	
	GraphDataStructure gds;

	public DirectedClusteringCoefficient(String name, ApplicationType type,
			Metric.MetricType mType) {
		super(name, type, mType);
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof DirectedClusteringCoefficient;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean compute() {
		gds = g.getGraphDatastructures();
		
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
