package dna.depr.metrics.sampling;

import dna.depr.metrics.Metric;
import dna.graph.Graph;
import dna.metrics.IMetricNew.MetricType;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.generators.sampling.SamplingAlgorithm;
import dna.util.DataUtils;

/**
 * This metric will measure to which extent a sampling algorithm has sampled the
 * graph. It will compute the number of seen, unseen and visited nodes in a
 * graph.
 * 
 * @author Benedict Jahn
 * 
 */
public abstract class Extent extends Metric {

	private SamplingAlgorithm algorithm;
	private int seenNodes;
	private int unseenNodes;
	private int visitedNodes;
	private int visitedAndSeenNodes;

	/**
	 * Creates an instance of the Extent metric
	 * 
	 * @param name
	 *            the name of the metric
	 * @param algorithm
	 *            the sampling algorithm, which samples the graph
	 */
	public Extent(String name, ApplicationType type, MetricType metricType,
			SamplingAlgorithm algorithm) {
		super(name, type, metricType);
		this.algorithm = algorithm;
	}

	@Override
	public boolean compute() {
		visitedAndSeenNodes = algorithm.getSeenAndVisitedCount();
		visitedNodes = algorithm.getVisitedCount();
		seenNodes = visitedAndSeenNodes - visitedNodes;
		unseenNodes = algorithm.getNodeCountOfBaseGraph() - visitedAndSeenNodes;
		return true;
	}

	@Override
	public void init_() {
		seenNodes = 0;
		visitedNodes = 0;
		visitedAndSeenNodes = 0;
		unseenNodes = algorithm.getNodeCountOfBaseGraph();
	}

	@Override
	public void reset_() {
		seenNodes = 0;
		visitedNodes = 0;
		visitedAndSeenNodes = 0;
		unseenNodes = algorithm.getNodeCountOfBaseGraph();
	}

	@Override
	public Value[] getValues() {
		Value unseen = new Value("Unseen_Nodes", unseenNodes);
		Value seen = new Value("Seen_Nodes", seenNodes);
		Value visited = new Value("Visited_Nodes", visitedNodes);
		Value seenAndVisited = new Value("Seen_and_Visited_Nodes",
				visitedAndSeenNodes);
		return new Value[] { unseen, seen, visited, seenAndVisited };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] {};
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
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof Extent)) {
			return false;
		}
		Extent ex = (Extent) m;
		boolean success = true;
		success &= DataUtils.equals(this.unseenNodes, ex.unseenNodes,
				"Unseen_Nodes");
		success &= DataUtils.equals(this.seenNodes, ex.seenNodes, "Seen_Nodes");
		success &= DataUtils.equals(this.visitedNodes, ex.visitedNodes,
				"Visited_Nodes");
		success &= DataUtils.equals(this.visitedAndSeenNodes,
				ex.visitedAndSeenNodes, "Seen_and_Visited_Nodes");
		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return (m instanceof Extent);
	}

}
