package dna.metrics.clusterCoefficient;

import dna.graph.Graph;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.util.ArrayUtils;

@SuppressWarnings("rawtypes")
public abstract class ClosedTriangleClusteringCoefficient extends
		ClusteringCoefficient {

	public ClosedTriangleClusteringCoefficient(String name, ApplicationType type) {
		super(name, type);
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

		UndirectedGraph g = (UndirectedGraph) this.g;

		for (UndirectedNode a : g.getNodes()) {
			this.nodeTriangleCount[a.getIndex()] = 0;
			this.nodePotentialCount[a.getIndex()] = 0;

			for (UndirectedEdge e1 : a.getEdges()) {
				UndirectedNode b = e1.getDifferingNode(a);
				for (UndirectedEdge e2 : a.getEdges()) {
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
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC);

		return true;
	}

	public boolean computeDirected() {

		DirectedGraph g = (DirectedGraph) this.g;

		for (DirectedNode a : g.getNodes()) {
			this.nodeTriangleCount[a.getIndex()] = 0;
			this.nodePotentialCount[a.getIndex()] = 0;

			for (DirectedNode b : a.getNeighbors()) {
				for (DirectedNode c : a.getNeighbors()) {
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
			this.localCC[index] = 0;
		} else {
			this.localCC[index] = (double) this.nodeTriangleCount[index]
					/ (double) this.nodePotentialCount[index];
		}
	}

	private void update() {
		if (this.potentialCount == 0) {
			this.globalCC = 0;
		} else {
			this.globalCC = (double) this.triangleCount
					/ (double) this.potentialCount;
		}
		this.averageCC = ArrayUtils.avgIgnoreNaN(this.localCC);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof ClosedTriangleClusteringCoefficient;
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
