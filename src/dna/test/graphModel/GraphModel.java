package dna.test.graphModel;

import dna.io.network.netflow.NetflowEvent.NetflowDirection;
import dna.io.network.netflow.NetflowEvent.NetflowEventField;
import dna.metrics.Metric;
import dna.util.network.NetflowAnalysis.EdgeWeightValue;
import dna.util.network.NetflowAnalysis.NodeWeightValue;

public class GraphModel {
	protected NetflowEventField[][] edges;
	protected NetflowDirection[] edgeDirections;
	protected EdgeWeightValue[] edgeWeights;
	protected NodeWeightValue[] nodeWeights;
	protected Metric[] metrics;

	public GraphModel(NetflowEventField[][] edges, NetflowDirection[] edgeDirections, EdgeWeightValue[] edgeWeights,
			NodeWeightValue[] nodeWeights, Metric[] metrics) {
		this.edges = edges;
		this.edgeDirections = edgeDirections;
		this.edgeWeights = edgeWeights;
		this.nodeWeights = nodeWeights;
		this.metrics = metrics;
	}

	public NetflowEventField[][] getEdges() {
		return edges;
	}

	public NetflowDirection[] getEdgeDirections() {
		return edgeDirections;
	}

	public EdgeWeightValue[] getEdgeWeights() {
		return edgeWeights;
	}

	public NodeWeightValue[] getNodeWeights() {
		return nodeWeights;
	}

	public Metric[] getMetrics() {
		return metrics;
	}

}
