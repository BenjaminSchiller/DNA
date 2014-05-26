package dna.metrics.clusterCoefficient;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

/**
 * 
 * Abstract super class of all metrics that compute the undirected clustering
 * coefficient, i.e., a potential triangle is a triplet a-b-c. An actual
 * triangle exists if the edge a-c exists as well. This metric can be applied to
 * DIRECTED or UNDIRECTED graphs. In case of a directed graph, potential
 * triangles are of the form a<->b<->c and actual triangles exist if a->c and
 * a<-c exist (a<->c).
 * 
 * @author benni
 * 
 */
public abstract class UndirectedClusteringCoefficient extends
		ClusteringCoefficient {
	
	GraphDataStructure gds;

	public UndirectedClusteringCoefficient(String name, ApplicationType type,
			MetricType mType) {
		super(name, type, mType);
	}

	@Override
	public boolean compute() {
		gds = g.getGraphDatastructures();
		this.triangleCount = 0;
		this.potentialCount = 0;
		if (g.isDirected()) {
			return this.computeDirected();
		} else if (!g.isDirected()) {
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
				UndirectedNode b = (UndirectedNode) e1.getDifferingNode(a);
				for (IElement e2Uncasted : a.getEdges()) {
					UndirectedEdge e2 = (UndirectedEdge) e2Uncasted;
					UndirectedNode c = (UndirectedNode) e2.getDifferingNode(a);
					if (b.equals(c)) {
						continue;
					}
					this.nodePotentialCount[a.getIndex()]++;
					if (b.hasEdge(b, c)) {
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
