package dna.depr.metrics.similarityMeasures;

import java.util.HashSet;
import java.util.Set;

import dna.depr.metrics.Metric;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetricNew;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public abstract class MeasuresDirectedUnweighted extends Metric {

	/** Contains the result for each similarity measure */
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
	 * Initializes {@link DirectedEdge} measure. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 * 
	 * @param name
	 *            The name of the metric
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public MeasuresDirectedUnweighted(String name,
			ApplicationType applicationType) {
		this(name, applicationType, new StringParameter("directedDegreeType",
				"out"));
	}

	/**
	 * Initializes {@link DirectedEdge} measure.
	 * 
	 * @param name
	 *            The name of the metric
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public MeasuresDirectedUnweighted(String name, ApplicationType type,
			Parameter directedDegreeType) {
		super(name, type, IMetricNew.MetricType.exact, directedDegreeType);
		this.directedDegreeType = this.getParameters()[0].getValue();
	}

	/**
	 * Decreases the matching between the given nodes by 1.
	 */
	protected void decreaseMatching(DirectedNode node1, DirectedNode node2) {
		this.matching.put(node1, node2, this.matching.get(node1, node2) - 1);
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @param directedNode
	 * 
	 * @see #decreaseMatching(DirectedNode, DirectedNode)
	 */
	protected void decreaseMatching(HashSet<DirectedNode> nodes,
			DirectedNode directedNode) {
		for (DirectedNode node1 : nodes) {
			this.decreaseMatching(node1, directedNode);

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
			HashSet<DirectedNode> neighborNodes) {
		for (DirectedNode directedNode1 : neighborNodes)
			for (DirectedNode directedNode2 : neighborNodes) {
				if (directedNode1.getIndex() > directedNode2.getIndex())
					continue;
				decreaseMatching(directedNode1, directedNode2);
			}

	}

	/**
	 * Computes the intersection between the neighbors of two nodes.
	 * 
	 * @param neighbors1
	 *            A {@link Set} includes the neighbors of the first node.
	 * @param neighbors2
	 *            A {@link Set} includes the neighbors of the second node.
	 * @return A {@link Set} containing the intersection of neighbors1 and
	 *         neighbors2.
	 */
	protected HashSet<DirectedNode> getMatching(
			HashSet<DirectedNode> neighbors1, HashSet<DirectedNode> neighbors2) {
		if (neighbors1 == null || neighbors2 == null)
			return null;

		HashSet<DirectedNode> intersection = new HashSet<DirectedNode>(
				neighbors1);
		intersection.retainAll(neighbors2);
		return intersection;

	}

	/**
	 * Get all neighbors of an {@link Node}
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all neighbors of given node.
	 */
	protected HashSet<DirectedNode> getNeighborNodes(DirectedNode node) {
		final HashSet<DirectedNode> neighbors = new HashSet<DirectedNode>();

		DirectedEdge edge;
		if (isOutgoingMeasure())
			for (IElement iEdge : node.getOutgoingEdges()) {
				edge = (DirectedEdge) iEdge;
				neighbors.add(edge.getDst());
			}
		else
			for (IElement iEdge : node.getIncomingEdges()) {
				edge = (DirectedEdge) iEdge;
				neighbors.add(edge.getSrc());
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
	protected HashSet<DirectedNode> getNeighborsIn(DirectedNode node) {
		final HashSet<DirectedNode> neighbors = new HashSet<DirectedNode>();
		DirectedEdge edge;
		for (IElement iEdge : node.getIncomingEdges()) {
			edge = (DirectedEdge) iEdge;
			neighbors.add(edge.getSrc());
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
	protected HashSet<DirectedNode> getNeighborsOut(DirectedNode node) {
		final HashSet<DirectedNode> neighbors = new HashSet<DirectedNode>();
		DirectedEdge edge;
		for (IElement iEdge : node.getOutgoingEdges()) {
			edge = (DirectedEdge) iEdge;
			neighbors.add(edge.getDst());
		}
		return neighbors;
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// final int numberOfNodesInGraph = this.g.getNodeCount();
		// final NodeNodeValueList nodeNodeValueList = new NodeNodeValueList(
		// "DiceDirected", numberOfNodesInGraph);
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

	/**
	 * Increases the matching between the given nodes by 1.
	 */
	protected void increaseMatching(DirectedNode node1, DirectedNode node2) {
		Double matchingG = this.matching.get(node1, node2);
		this.matching.put(node1, node2, matchingG == null ? 1 : matchingG + 1);
	}

	/**
	 * Increases the matching between each pair of the given nodes by 1.
	 * 
	 * @param directedNode
	 * 
	 * @see #increaseMatching(DirectedNode, DirectedNode)
	 */
	protected void increaseMatching(HashSet<DirectedNode> nodes,
			DirectedNode directedNode) {
		for (DirectedNode node1 : nodes) {
			this.increaseMatching(node1, directedNode);
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

	protected void update(DirectedEdge edge, HashSet<DirectedNode> neighborsIn,
			HashSet<DirectedNode> neighborsOut) {
		this.updateDirectedNeighborsMeasure(neighborsOut);
		this.updateDirectedNeighborsMeasure(neighborsIn);

		for (DirectedNode direct1 : neighborsIn)
			this.updateDirectedNeighborsMeasure(this.getNeighborsOut(direct1));

		for (DirectedNode direct1 : neighborsOut)
			this.updateDirectedNeighborsMeasure(this.getNeighborsIn(direct1));
	}

	/**
	 * Update method that will be replaced by the respective specific similarity measure method. 
	 */
	protected void update(DirectedNode directed1, DirectedNode directed3) {
	}

	/**
	 * Updates the similarity measure between each pair of the given
	 * nodes.
	 * 
	 * @see #increaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void updateDirectedNeighborsMeasure(HashSet<DirectedNode> set) {
		for (DirectedNode node1 : set)
			for (DirectedNode node2 : set) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				this.update(node1, node2);
			}
	}

	/**
	 * Update the similarity measure incoming if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasuresIncoming(DirectedNode nodeToRemove) {
		for (DirectedNode directed1 : this.getNeighborsOut(nodeToRemove))
			for (DirectedNode directed2 : this.getNeighborsIn(directed1))
				if (!directed2.equals(nodeToRemove))
					for (DirectedNode directed3 : this
							.getNeighborsOut(directed2))
						if (!directed3.equals(directed1)
								&& !directed3.equals(nodeToRemove)) {
							update(directed1, directed3);
						}

	}

	/**
	 * Update the similarity measure outgoing if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasuresOutgoing(DirectedNode nodeToRemove) {
		for (DirectedNode directed1 : this.getNeighborsIn(nodeToRemove))
			for (DirectedNode directed2 : this.getNeighborsOut(directed1))
				if (!directed2.equals(nodeToRemove))
					for (DirectedNode directed3 : this
							.getNeighborsIn(directed2))
						if (!directed3.equals(directed1)
								&& !directed3.equals(nodeToRemove)) {
							update(directed1, directed3);
						}

	}

}
