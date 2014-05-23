package dna.metrics.similarityMeasures;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.metrics.Metric;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class MeasuresUndirectedDoubleWeighted extends Metric {

	/** Contains the result for each dice similarity measure. */
	protected Matrix result;

	/** Contains the result for each matching measure */
	protected Matrix matching;

	protected BinnedDistributionLong binnedDistribution;

	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	public MeasuresUndirectedDoubleWeighted(String name,
			ApplicationType applicationType) {
		super(name, applicationType, MetricType.exact);
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @param node
	 * 
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void decreaseMatching(HashMap<UndirectedNode, Double> map,
			UndirectedNode node) {
		for (Entry<UndirectedNode, Double> node1 : map.entrySet()) {
			this.decreaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));
		}
	}

	protected void decreaseMatching(UndirectedNode node1, Double value1,
			UndirectedNode node2, Double value2) {
		double matchingG = this.matching.get(node1, node2)
				- Math.min(value1, value2);
		if (matchingG < 0.0 && Math.abs(matchingG) <= 1.0E-4 || matchingG > 0.0
				&& matchingG < 1.0E-6) {
			System.err.println("Matching war beim erhöhen -- Dec");
			matchingG = 0.0;
		}
		this.matching.put(node1, node2, matchingG);
	}

	/**
	 * Decrease the matching measure if a node is to be removed.
	 * 
	 * @param neighborNodes
	 *            The neighbors of the node to be removed.
	 * @see #decreaseMatching(Entry, Entry)
	 */
	protected void decreaseMatchingNodeRemove(
			HashMap<UndirectedNode, Double> map) {
		for (Entry<UndirectedNode, Double> node1 : map.entrySet())
			for (Entry<UndirectedNode, Double> node2 : map.entrySet()) {
				if (node1.getKey().getIndex() > node2.getKey().getIndex())
					continue;
				decreaseMatching(node1.getKey(), node1.getValue(),
						node2.getKey(), node2.getValue());
			}
	}

	/**
	 * Sums the values of a Map.
	 * 
	 * @param neighbors
	 *            A {@link Map} containing the neighbors with their frequency.
	 * @return The sums of values.
	 */
	protected double getMapValueSum(HashMap<UndirectedNode, Double> neighbors) {
		double sum = 0;
		for (Entry<UndirectedNode, Double> e : neighbors.entrySet())
			sum = sum + e.getValue();

		return sum;
	}

	/**
	 * Computes the intersection between the neighbors of two nodes.
	 * 
	 * @param neighbors1
	 *            A {@link Map} includes the neighbors of the first node with
	 *            their frequency.
	 * @param neighbors2
	 *            A {@link Map} includes the neighbors of the second node with
	 *            their frequency.
	 * @return A {@link Map} containing the intersection of neighbors1 and
	 *         neighbors2.
	 */
	protected HashMap<UndirectedNode, Double> getMatching(
			HashMap<UndirectedNode, Double> neighbors1,
			HashMap<UndirectedNode, Double> neighbors2) {
		final HashMap<UndirectedNode, Double> neighbors = new HashMap<UndirectedNode, Double>();
		for (Entry<UndirectedNode, Double> e : neighbors1.entrySet())
			if (neighbors2.containsKey(e.getKey()))
				if (neighbors2.get(e.getKey()) <= e.getValue())
					neighbors.put(e.getKey(), neighbors2.get(e.getKey()));
				else
					neighbors.put(e.getKey(), e.getValue());

		return neighbors;
	}

	/**
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Map} containing all neighbors of given node with their
	 *         frequency.
	 */
	protected HashMap<UndirectedNode, Double> getNeighborNodes(
			UndirectedNode node) {
		final HashMap<UndirectedNode, Double> neighbors = new HashMap<UndirectedNode, Double>();

		UndirectedWeightedEdge edge;
		// iterate over all edges and ...
		for (IElement iEdge : node.getEdges()) {
			edge = (UndirectedWeightedEdge) iEdge;

			// ... add the node which is not the given one to the neighbors
			if (edge.getNode1().equals(node))
				neighbors.put(edge.getNode2(),
						((DoubleWeight) edge.getWeight()).getWeight());
			else
				neighbors.put(edge.getNode1(),
						((DoubleWeight) edge.getWeight()).getWeight());
		}

		return neighbors;
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// final int numberOfNodesInGraph = this.g.getNodeCount();
		// final NodeNodeValueList nodeNodeValueList = new NodeNodeValueList(
		// "DiceUndirectedWeighted", numberOfNodesInGraph);
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
	 * @param node
	 * 
	 * @see #increaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void increaseMatching(HashMap<UndirectedNode, Double> map,
			UndirectedNode node) {
		for (Entry<UndirectedNode, Double> node1 : map.entrySet()) {
			this.increaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));
		}
	}

	protected void increaseMatching(UndirectedNode node1, Double value1,
			UndirectedNode node2, Double value2) {
		Double matchingG = this.matching.get(node1, node2);
		this.matching.put(
				node1,
				node2,
				matchingG == null ? Math.min(value1, value2) : matchingG
						+ Math.min(value1, value2));

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

	// TODO DOK
	protected void update(UndirectedWeightedEdge edge,
			HashMap<UndirectedNode, Double> neighborsNode1,
			HashMap<UndirectedNode, Double> neighborsNode2) {
		// update the dice Measure of every neighbor of each adjacent with the
		// new calculated values.
		this.updateDirectNeighborsMeasure(neighborsNode1, edge.getNode2());
		this.updateDirectNeighborsMeasure(neighborsNode2, edge.getNode1());
		this.updateMeasureMatching(edge.getNode1(), edge.getNode2());
		this.updateMeasureMatching(edge.getNode2(), edge.getNode1());

	}

	protected void update(UndirectedNode undirected1, UndirectedNode undirected3) {
	}

	/**
	 * Calculates the new dice similarity measure for each node.
	 * 
	 * @param undirectedNode
	 */
	protected void updateDirectNeighborsMeasure(
			HashMap<UndirectedNode, Double> map, UndirectedNode node) {
		for (UndirectedNode node1 : map.keySet())
			for (UndirectedNode node2 : map.keySet()) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				this.update(node1, node2);
			}

	}

	/**
	 * Update the dice Measure in the matching range.
	 */
	protected void updateMeasureMatching(UndirectedNode node1,
			UndirectedNode node2) {
		for (UndirectedNode undirected1 : this.getNeighborNodes(node1).keySet())
			if (!undirected1.equals(node2))
				for (UndirectedNode undirected2 : this.getNeighborNodes(
						undirected1).keySet())
					this.update(node1, undirected2);

	}

	/**
	 * Update the dice measure if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasure(UndirectedNode nodeToRemove) {
		for (UndirectedNode undirected1 : this.getNeighborNodes(nodeToRemove)
				.keySet())
			for (UndirectedNode undirected2 : this
					.getNeighborNodes(undirected1).keySet())
				if (!undirected2.equals(nodeToRemove))
					for (UndirectedNode undirected3 : this.getNeighborNodes(
							undirected2).keySet())
						if (!undirected3.equals(undirected1)
								&& !undirected3.equals(nodeToRemove)) {
							update(undirected1, undirected3);
						}

	}
}
