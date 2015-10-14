package dna.depr.metrics.sampling;

import dna.depr.metrics.MetricOld;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric.MetricType;
import dna.series.data.Value;
import dna.series.data.distr2.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.DataUtils;

/**
 * This metric will measure the fraction between sample and original graph. It
 * will generate two values: SamplingModularityV1 and SamplingModularityV2. SMV1
 * compares the amount of edges in the sample with the amount of edges in the
 * original graph. SMV2 compares the amount of edges between sampled and not
 * sampled nodes, with the amount of edges in the original graph.
 * 
 * @author Benedict Jahn
 * 
 */
public abstract class SamplingModularity extends MetricOld {

	/**
	 * Creates an instance of the sampling modularity metric
	 * 
	 * @param name
	 *            the name of the metric
	 * @param graph
	 *            the original graph
	 */
	public SamplingModularity(String name, ApplicationType type,
			MetricType metrictype, Graph graph) {
		super(name, type, metrictype);
		this.graph = graph;
		if (DirectedNode.class.isAssignableFrom(this.graph
				.getGraphDatastructures().getNodeType())) {
			directed = true;
		} else {
			directed = false;
		}
	}

	private boolean directed;
	private Graph graph;
	protected int edgesInSample;
	protected int edgesInGraph;
	// Sum of the degree that the nodes of the sample have in the original graph
	protected int degreeSum;

	// degreeSum - 2x edgesInGraph computes the degree amount of edges between
	// the sample and the original graph

	@Override
	public boolean compute() {
		edgesInSample = g.getEdgeCount();
		degreeSum = 0;
		for (IElement e : g.getNodes()) {
			degreeSum += getDegreeFromOriginalNode((Node) e);
		}
		return true;
	}

	@Override
	public void init_() {
		this.edgesInSample = 0;
		this.edgesInGraph = graph.getEdgeCount();
		this.degreeSum = 0;
	}

	@Override
	public void reset_() {
		this.edgesInSample = 0;
		this.edgesInGraph = graph.getEdgeCount();
		this.degreeSum = 0;
	}

	@Override
	public Value[] getValues() {
		double v1 = (double) edgesInSample / (double) edgesInGraph;
		int v2pre = degreeSum - (2 * edgesInSample);
		double v2 = (double) edgesInSample / (double) v2pre;
		Value samplingModularityV1 = new Value("Sampling_Modularity_V1", v1);
		Value samplingModularityV2 = new Value("Sampling_Modularity_V2", v2);
		return new Value[] { samplingModularityV1, samplingModularityV2 };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m == null || !(m instanceof SamplingModularity)) {
			return false;
		}
		SamplingModularity sm = (SamplingModularity) m;
		boolean success = true;
		success &= DataUtils.equals(this.edgesInGraph, sm.edgesInGraph,
				"Edges_In_Graph");
		success &= DataUtils.equals(this.edgesInSample, sm.edgesInSample,
				"Edges_In_Sample");
		success &= DataUtils.equals(this.degreeSum, sm.degreeSum, "Degree_Sum");
		return success;
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return (m instanceof SamplingModularity);
	}

	/**
	 * Computes the degree of this node in the original graph, NOT the degree of
	 * the node in the sample
	 * 
	 * @param n
	 *            the node from the sample
	 * @return the degree of the same node in the original graph
	 */
	protected int getDegreeFromOriginalNode(Node n) {
		Node node = graph.getNode(n.getIndex());
		if (directed) {
			return ((DirectedNode) node).getDegree();
		} else {
			return ((UndirectedNode) node).getDegree();
		}
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

}
