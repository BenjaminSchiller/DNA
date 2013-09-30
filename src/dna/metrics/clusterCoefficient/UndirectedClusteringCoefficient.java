package dna.metrics.clusterCoefficient;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class UndirectedClusteringCoefficient extends
		ClusteringCoefficient {

	public UndirectedClusteringCoefficient(String name, ApplicationType type,
			MetricType mType) {
		super(name, type, mType);
	}

	@Override
	public boolean compute() {
		this.triangleCount = 0;
		this.potentialCount = 0;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			return this.computeDirected();
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return this.computeUndirected();
		}
		return false;
	}

	public boolean computeUndirected() {

		for (IElement aUncasted : g.getNodes()) {
			UndirectedNode a = (UndirectedNode) aUncasted;
			this.nodeTriangleCount[a.getIndex()] = 0;
			this.nodePotentialCount[a.getIndex()] = 0;

			for (IElement e1Uncasted : a.getEdges()) {
				UndirectedEdge e1 = (UndirectedEdge) e1Uncasted;
				UndirectedNode b = e1.getDifferingNode(a);
				for (IElement e2Uncasted : a.getEdges()) {
					UndirectedEdge e2 = (UndirectedEdge) e2Uncasted;
					UndirectedNode c = e2.getDifferingNode(a);
					if (b.equals(c)) {
						continue;
					}
					this.nodePotentialCount[a.getIndex()]++;
					if (b.hasEdge(new UndirectedEdge(b, c))) {
						this.nodeTriangleCount[a.getIndex()]++;
					}
				}
			}
			this.nodeTriangleCount[a.getIndex()] /= 2;
			this.nodePotentialCount[a.getIndex()] /= 2;

			this.update(a.getIndex());
		}

		this.update();
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());

		return true;
	}

	public boolean computeDirected() {

		for (IElement aUncasted : g.getNodes()) {
			DirectedNode a = (DirectedNode) aUncasted;
			this.nodeTriangleCount[a.getIndex()] = 0;
			this.nodePotentialCount[a.getIndex()] = 0;

			for (IElement bUncasted : a.getNeighbors()) {
				DirectedNode b = (DirectedNode) bUncasted;
				for (IElement cUncasted : a.getNeighbors()) {
					DirectedNode c = (DirectedNode) cUncasted;
					if (b.equals(c)) {
						continue;
					}
					this.nodePotentialCount[a.getIndex()]++;
					if (b.hasNeighbor(c)) {
						this.nodeTriangleCount[a.getIndex()]++;
					}
				}
			}
			this.nodeTriangleCount[a.getIndex()] /= 2;
			this.nodePotentialCount[a.getIndex()] /= 2;

			this.update(a.getIndex());
		}

		this.update();

		return true;
	}

	private void update(int index) {
		this.triangleCount += this.nodeTriangleCount[index];
		this.potentialCount += this.nodePotentialCount[index];
		if (this.nodePotentialCount[index] == 0) {
			this.localCC.setValue(index, 0);
		} else {
			this.localCC.setValue(index, (double) this.nodeTriangleCount[index]
					/ (double) this.nodePotentialCount[index]);
		}
	}

	private void update() {
		if (this.potentialCount == 0) {
			this.globalCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
		}
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC.getValues());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof UndirectedClusteringCoefficient;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

}
