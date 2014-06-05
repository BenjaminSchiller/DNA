package dna.metrics.similarityMeasures;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.Metric;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public abstract class MeasuresDirectedIntWeighted extends Metric {

	/** Contains the result for each similarity measure. */
	protected Matrix result;
	/** Contains the result for each matching measure */
	protected Matrix matching;
	/** Binned Distribution */
	protected BinnedDistributionLong binnedDistribution;
	/** Average per Node Distribution */
	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	/**
	 * Is either "out" (default) or "in".
	 */
	protected String directedDegreeType;

	/**
	 * Initializes {@link DirectedIntWeightedEdge}. Implicitly sets degree type
	 * for directed graphs to outdegree.
	 * 
	 * @param name
	 *            The name of the metric
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public MeasuresDirectedIntWeighted(String name,
			ApplicationType applicationType) {
		this(name, applicationType, new StringParameter("directedDegreeType",
				"out"));
	}

	/**
	 * Initializes {@link DirectedIntWeighted} measure.
	 * 
	 * @param name
	 *            The name of the metric
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public MeasuresDirectedIntWeighted(String name, ApplicationType type,
			Parameter directedDegreeType) {
		super(name, type, MetricType.exact, directedDegreeType);
		this.directedDegreeType = this.getParameters()[0].getValue();
	}

	/**
	 * Decreases the matching between the given nodes by the min weight.
	 */
	protected void decreaseMatching(DirectedNode node1, Integer value1,
			DirectedNode node2, Integer value2) {
		this.matching.put(node1, node2,
				this.matching.get(node1, node2) - Math.min(value1, value2));
	}

	/**
	 * Decreases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void decreaseMatching(HashMap<DirectedNode, Integer> map,
			DirectedNode node) {
		for (Entry<DirectedNode, Integer> node1 : map.entrySet()) {
			this.decreaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));
		}
	}

	/**
	 * Decrease the matching measure if a node is to be removed.
	 * 
	 * @param neighborNodes
	 *            The neighbors of the node to be removed.
	 * @see #decreaseMatching(DirectedNode, DirectedNode)
	 */
	protected void decreaseMatchingNodeRemove(
			HashMap<DirectedNode, Integer> neighbors) {
		for (Entry<DirectedNode, Integer> node1 : neighbors.entrySet())
			for (Entry<DirectedNode, Integer> node2 : neighbors.entrySet()) {
				if (node1.getKey().getIndex() > node2.getKey().getIndex())
					continue;
				this.decreaseMatching(node1.getKey(), node1.getValue(),
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
	protected double getMapValueSum(HashMap<DirectedNode, Integer> neighbors) {
		double sum = 0.0;
		for (Entry<DirectedNode, Integer> e : neighbors.entrySet())
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
	protected HashMap<DirectedNode, Integer> getMatching(
			HashMap<DirectedNode, Integer> neighbors1,
			HashMap<DirectedNode, Integer> neighbors2) {
		final HashMap<DirectedNode, Integer> neighbors = new HashMap<DirectedNode, Integer>();
		for (Entry<DirectedNode, Integer> e : neighbors1.entrySet())
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
	protected HashMap<DirectedNode, Integer> getNeighborNodes(DirectedNode node) {
		final HashMap<DirectedNode, Integer> neighbors = new HashMap<DirectedNode, Integer>();

		if (isOutgoingMeasure()) {
			for (IElement iEdge : node.getOutgoingEdges()) {
				DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
				neighbors.put(edgeD.getDst(),
						((IntWeight) edgeD.getWeight()).getWeight());
			}

		} else {
			for (IElement iEdge : node.getIncomingEdges()) {
				DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
				neighbors.put(edgeD.getSrc(),
						((IntWeight) edgeD.getWeight()).getWeight());
			}

		}
		return neighbors;
	}

	/**
	 * Get all incoming neighbors for a given node.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all incoming neighbors of given node.
	 */
	protected HashMap<DirectedNode, Integer> getNeighborsIn(DirectedNode node) {
		final HashMap<DirectedNode, Integer> neighbors = new HashMap<DirectedNode, Integer>();
		for (IElement iEdge : node.getIncomingEdges()) {
			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getSrc(),
					((IntWeight) edgeD.getWeight()).getWeight());
		}

		return neighbors;
	}

	/**
	 * Get all outgoing neighbors for a given node.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all outgoing neighbors of given node.
	 */
	protected HashMap<DirectedNode, Integer> getNeighborsOut(DirectedNode node) {
		final HashMap<DirectedNode, Integer> neighbors = new HashMap<DirectedNode, Integer>();
		for (IElement iEdge : node.getOutgoingEdges()) {
			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getDst(),
					((IntWeight) edgeD.getWeight()).getWeight());
		}

		return neighbors;
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// final int numberOfNodesInGraph = this.g.getNodeCount();
		// final NodeNodeValueList nodeNodeValueList = new NodeNodeValueList(
		// "DiceDirectedWeighted", numberOfNodesInGraph);
		// Double dice12;
		// int node1Index, node2Index;
		// for (IElement nodeOne : this.g.getNodes()) {
		// DirectedNode node1 = (DirectedNode) nodeOne;
		// node1Index = node1.getIndex();
		// for (IElement nodeTwo : this.g.getNodes()) {
		// DirectedNode node2 = (DirectedNode) nodeTwo;
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

	protected void increaseMatching(DirectedNode node1, Integer value1,
			DirectedNode node2, Integer value2) {
		Double matchingG = this.matching.get(node1, node2);
		this.matching.put(
				node1,
				node2,
				matchingG == null ? Math.min(value1, value2) : matchingG
						+ Math.min(value1, value2));

	}

	/**
	 * Increases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #increaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void increaseMatching(HashMap<DirectedNode, Integer> map,
			DirectedNode node) {
		for (Entry<DirectedNode, Integer> node1 : map.entrySet()) {
			this.increaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));
		}
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	/**
	 * Returns for which type of directed edges the similarity measure is.
	 * 
	 * @return true, if the dice similarity measure is for outgoing edges; false
	 *         for incoming
	 */
	public boolean isOutgoingMeasure() {
		if (this.directedDegreeType.equals("out"))
			return true;
		return false;
	}

	/**
	 * Update method that will be replaced by the respective specific similarity
	 * measure method.
	 */
	protected void update(DirectedNode directed1, DirectedNode directed3) {
	}

	protected void update(DirectedWeightedEdge edge,
			HashMap<DirectedNode, Integer> neighborsIn,
			HashMap<DirectedNode, Integer> neighborsOut) {
		this.updateDirectedNeighborsMeasure(neighborsOut);
		this.updateDirectedNeighborsMeasure(neighborsIn);

		for (DirectedNode direct1 : neighborsIn.keySet())
			this.updateDirectedNeighborsMeasure(this.getNeighborsOut(direct1));

		for (DirectedNode direct1 : neighborsOut.keySet())
			this.updateDirectedNeighborsMeasure(this.getNeighborsIn(direct1));
	}

	/**
	 * Update the measure incoming if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateDirectedNeighborsMeasure(
			HashMap<DirectedNode, Integer> map) {
		for (DirectedNode node1 : map.keySet())
			for (DirectedNode node2 : map.keySet()) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				this.update(node1, node2);
			}
	}

	/**
	 * Update the measure incoming if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasuresIncoming(DirectedNode nodeToRemove) {
		for (DirectedNode directed1 : this.getNeighborsOut(nodeToRemove)
				.keySet())
			for (DirectedNode directed2 : this.getNeighborsIn(directed1)
					.keySet())
				if (!directed2.equals(nodeToRemove))
					for (DirectedNode directed3 : this.getNeighborsOut(
							directed2).keySet())
						if (!directed3.equals(directed1)
								&& !directed3.equals(nodeToRemove)) {
							update(directed1, directed3);
						}

	}

	/**
	 * Update the measure outgoing if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasuresOutgoing(DirectedNode nodeToRemove) {
		for (DirectedNode directed1 : this.getNeighborsIn(nodeToRemove)
				.keySet())
			for (DirectedNode directed2 : this.getNeighborsOut(directed1)
					.keySet())
				if (!directed2.equals(nodeToRemove))
					for (DirectedNode directed3 : this
							.getNeighborsIn(directed2).keySet())
						if (!directed3.equals(directed1)
								&& !directed3.equals(nodeToRemove)) {
							update(directed1, directed3);
						}

	}

}
