package dna.metrics.similarityMeasures;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metricsNew.IMetricNew;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class MeasuresUndirectedUnweighted extends Metric {
	
	/** Contains the result for each similarity measure. */
	protected Matrix result;
	/** Contains the result for each matching measure */
	protected Matrix matching;
	/** Binned Distribution */
	protected BinnedDistributionLong binnedDistribution;
	/** Average per Node Distribution */
	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	/**
	 * Initializes {@link UndirectedEdge} measure.
	 * 
	 * @param name
	 *            The name of the metric
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public MeasuresUndirectedUnweighted(String name,
			ApplicationType applicationType) {
		super(name, applicationType, IMetricNew.MetricType.exact);
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void decreaseMatching(HashSet<UndirectedNode> nodes,
			UndirectedNode node) {
		for (UndirectedNode node1 : nodes) {
			this.decreaseMatching(node1, node);
		}
	}

	/**
	 * Decreases the matching between the given nodes by 1.
	 */
	protected void decreaseMatching(UndirectedNode node1, UndirectedNode node2) {
		this.matching.put(node1, node2, this.matching.get(node1, node2) - 1);
	}

	/**
	 * Decrease the matching measure if a node is to be removed.
	 * 
	 * @param neighborNodes
	 *            The neighbors of the node to be removed.
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void decreaseMatchingNodeRemove(
			HashSet<UndirectedNode> neighborNodes) {
		for (UndirectedNode node1 : neighborNodes)
			for (UndirectedNode node2 : neighborNodes) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				decreaseMatching(node1, node2);
			}
	}

	/**
	 * Computes the intersection between the neighbors of two nodes.
	 * 
	 * @param neighbors1
	 *            A {@link Set} includes the neighbors of the first node.
	 * @param neighbors2
	 *            A {@link Set} includes the neighbors of the second node.
	 * @return A {@link set} containing the intersection of neighbors1 and
	 *         neighbors2.
	 */
	protected HashSet<UndirectedNode> getIntersection(
			HashSet<UndirectedNode> neighbors1,
			HashSet<UndirectedNode> neighbors2) {
		HashSet<UndirectedNode> intersection = new HashSet<>(neighbors1);
		intersection.retainAll(neighbors2);
		return intersection;

	}

	/**
	 * Get all neighbors for a given node.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all neighbors of given node.
	 */
	protected HashSet<UndirectedNode> getNeighborNodes(UndirectedNode node) {
		final HashSet<UndirectedNode> neighbors = new HashSet<UndirectedNode>();

		UndirectedEdge edge;
		// iterate over all edges and ...
		for (IElement iEdge : node.getEdges()) {
			edge = (UndirectedEdge) iEdge;
			// ... add the node which is not the given one to the neighbors
			if (edge.getNode1().equals(node))
				neighbors.add(edge.getNode2());
			else
				neighbors.add(edge.getNode1());
		}

		return neighbors;
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// final int numberOfNodesInGraph = this.g.getNodeCount();
		// final NodeNodeValueList nodeNodeValueList = new NodeNodeValueList(
		// "DiceUndirected", numberOfNodesInGraph);
		// Double dice12;
		// int node1Index, node2Index;
		// for (IElement nodeOne : this.g.getNodes()) {
		// UndirectedNode node1 = (UndirectedNode) nodeOne;
		// node1Index = node1.getIndex();
		// for (IElement nodeTwo : this.g.getNodes()) {
		// UndirectedNode node2 = (UndirectedNode) nodeTwo;
		// node2Index = node2.getIndex();
		// dice12 = this.diceSimilarity.get(node1, node2);
		// dice12 = dice12 == null ? 0.0 : dice12;
		// nodeNodeValueList.setValue(node1Index, node2Index, dice12);
		// }
		// }

		return new NodeNodeValueList[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("avarage",
				this.binnedDistribution.computeAverage());
		return new Value[] { v1 };
	}

	/**
	 * Increases the matching between each pair of the given nodes by 1.
	 * 
	 * @see #increaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void increaseMatching(HashSet<UndirectedNode> nodes,
			UndirectedNode node) {
		for (UndirectedNode node1 : nodes) {
			this.increaseMatching(node1, node);
		}
	}

	/**
	 * Increases the matching between the given nodes by 1.
	 */
	protected void increaseMatching(UndirectedNode node1, UndirectedNode node2) {
		Double matchingG = this.matching.get(node1, node2);
		this.matching.put(node1, node2, matchingG == null ? 1 : matchingG + 1);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	protected void update(UndirectedEdge newEdge,
			HashSet<UndirectedNode> neighborsNode1,
			HashSet<UndirectedNode> neighborsNode2) {
		this.updateDirectNeighborsMeasures(neighborsNode1);
		this.updateDirectNeighborsMeasures(neighborsNode2);
		
		this.updateMeasureMatching(newEdge.getNode1(), newEdge.getNode2());
		this.updateMeasureMatching(newEdge.getNode2(), newEdge.getNode1());
	}
	
	/**
	 * Update method that will be replaced by the respective specific similarity measure method. 
	 */
	protected void update(UndirectedNode node1, UndirectedNode undirected2) {

	}

	/**
	 * Calculates the new similarity measure for each direct neighbor node.
	 * 
	 * @param undirectedNode
	 */
	protected void updateDirectNeighborsMeasures(
			HashSet<UndirectedNode> neighborNodes) {
		for (UndirectedNode node1 : neighborNodes)
			for (UndirectedNode node2 : neighborNodes) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				update(node1, node2);
			}

	}

	/**
	 * Update the similarity measure if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateMeasureMatching(UndirectedNode node1,
			UndirectedNode node2) {
		for (UndirectedNode undirected1 : this.getNeighborNodes(node1))
			if (!undirected1.equals(node2))
				for (UndirectedNode undirected2 : this
						.getNeighborNodes(undirected1))
					update(node1, undirected2);
	}

	/**
	 * Update the dice measure if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasure(UndirectedNode nodeToRemove) {
		for (UndirectedNode undirected1 : this.getNeighborNodes(nodeToRemove))
			for (UndirectedNode undirected2 : this
					.getNeighborNodes(undirected1))
				if (!undirected2.equals(nodeToRemove))
					for (UndirectedNode undirected3 : this
							.getNeighborNodes(undirected2))
						if (!undirected3.equals(undirected1)
								&& !undirected3.equals(nodeToRemove)) {
							update(undirected1, undirected3);
						}

	}

}
