package dna.metrics.extent;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.updates.walkingAlgorithms.WalkingAlgorithm;

/**
 * @author Benedict
 * 
 */
public class ExtentR extends Metric {

	private WalkingAlgorithm algorithm;
	private int seenNodes;
	private int unseenNodes;
	private int visitedNodes;
	private int visitedAndSeenNodes;

	/**
	 * @param name
	 * @param type
	 * @param metricType
	 * @param p
	 */
	public ExtentR(String name, WalkingAlgorithm algorithm) {
		super(name, ApplicationType.Recomputation, MetricType.exact);
		this.algorithm = algorithm;
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
		return (this == m);
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
		return m instanceof ExtentR;
	}

}
