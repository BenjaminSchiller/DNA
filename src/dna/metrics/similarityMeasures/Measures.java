package dna.metrics.similarityMeasures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.similarityMeasures.dice.Dice;
import dna.metrics.similarityMeasures.jaccard.Jaccard;
import dna.metrics.similarityMeasures.overlap.Overlap;
import dna.series.data.Value;
import dna.series.data.distributions.BinnedDistributionLong;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

/**
 * Abstract class that includes the underlying methods for {@link Jaccard},
 * {@link Dice} and {@link Overlap}.
 */
public abstract class Measures extends Metric implements IMetric {
	/**
	 * Setting for {@link Parameter} "directedDegreeType".
	 */
	public static enum DirectedDegreeType {
		IN("in"), OUT("out");

		private final StringParameter param;

		DirectedDegreeType(String value) {
			this.param = new StringParameter("directedDegreeType", value);
		}

		public StringParameter StringParameter() {
			return this.param;
		}
	}

	/**
	 * Setting for {@link Parameter} "edgeWeightType".
	 */
	public static enum EdgeWeightType {
		IGNORE_WEIGHTS("unweighted"), USE_WEIGHTS("weighted");

		private final StringParameter param;

		EdgeWeightType(String value) {
			this.param = new StringParameter("edgeWeightType", value);
		}

		public StringParameter StringParameter() {
			return this.param;
		}
	}

	/**
	 * Is either "out" (default) or "in", depending on the {@link Parameter} in
	 * {@link #Measures(String, DirectedDegreeType, EdgeWeightType)}. This value
	 * determines whether nodes in directed graphs are compared by there in- or
	 * outdegree and is ignored for undirected graphs.
	 */
	protected DirectedDegreeType directedDegreeType;

	/**
	 * To check equality of metrics in {@link #equals(IMetric)}, the similarity
	 * measure {@link #r} is compared. This value is the allowed difference of
	 * two values to still accept them as equal.
	 */
	public static final double ACCEPTED_ERROR_FOR_EQUALITY = 1.0E-3;

	/**
	 * Is either "unweighted" (default) or "weighted", depending on the
	 * {@link Parameter} in
	 * {@link #Measures(String, DirectedDegreeType, EdgeWeightType)} . This
	 * value determines whether edge weights in weighted graphs are ignored not
	 * (will always be ignored for weighted graphs).
	 */
	protected EdgeWeightType edgeWeightType;

	/** Contains the result for each similarity measure. */
	protected Matrix result;
	/** Contains the result for each matching measure */
	protected Matrix matching;
	/** Binned Distribution */
	protected BinnedDistributionLong binnedDistribution;
	/** Average per Node Distribution */
	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	/**
	 * Initializes {@link Measures}. Implicitly sets degree type for directed
	 * graphs to outdegree and ignore weights.
	 * 
	 * @param name
	 *            The name of the metric.
	 */
	public Measures(String name) {
		this(name, DirectedDegreeType.OUT, EdgeWeightType.IGNORE_WEIGHTS);
	}

	/**
	 * Initializes {@link Measures}.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>JaccardR</i> for the Jaccard
	 *            Recomputation and <i>JaccardU</i> for the Jaccard Updates.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 * @param edgeWeightType
	 *            <i>weighted</i> or <i>unweighted</i>, determining whether to
	 *            use edge weights in weighted graphs or not. Will be ignored
	 *            for unweighted graphs.
	 */
	public Measures(String name, DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super(name, IMetric.MetricType.exact, directedDegreeType
				.StringParameter(), edgeWeightType.StringParameter());

		this.directedDegreeType = directedDegreeType;
		this.edgeWeightType = edgeWeightType;
	}

	/**
	 * Decreases the matching between the given nodes by 1.
	 */
	protected void decreaseMatching(Node node1, Node node2) {
		this.matching.put(node1, node2, this.matching.get(node1, node2) - 1);
	}

	/**
	 * Decrease the matching measure if a node is to be removed.
	 * 
	 * @param neighborNodes
	 *            The neighbors of the node to be removed.
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	protected void decreaseMatchingNodeRemoveUnweighted(
			HashSet<Node> neighborNodes) {
		for (Node node1 : neighborNodes)
			for (Node node2 : neighborNodes) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				decreaseMatching(node1, node2);
			}
	}

	/**
	 * Decrease the matching measure if a node is to be removed.
	 * 
	 * @param neighborNodess
	 *            The neighbors of the node to be removed.
	 * @see #decreaseMatchingUndirectedWeighted(Entry, Entry)
	 */
	protected void decreaseMatchingNodeRemoveWeighted(HashMap<Node, Double> map) {
		for (Entry<Node, Double> node1 : map.entrySet())
			for (Entry<Node, Double> node2 : map.entrySet()) {
				if (node1.getKey().getIndex() > node2.getKey().getIndex())
					continue;
				decreaseMatchingWeighted(node1.getKey(), node1.getValue(),
						node2.getKey(), node2.getValue());
			}
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @param directedNode
	 * 
	 * @see #decreaseMatchingDU(DirectedNode, DirectedNode)
	 */
	protected void decreaseMatchingUnweighted(HashSet<Node> nodes, Node Node) {
		for (Node node1 : nodes) {
			this.decreaseMatching(node1, Node);

		}
	}

	/**
	 * Decreases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #decreaseMatchingWeighted(UndirectedNode, UndirectedNode)
	 */
	protected void decreaseMatchingWeighted(HashMap<Node, Double> map, Node node) {
		for (Entry<Node, Double> node1 : map.entrySet()) {
			this.decreaseMatchingWeighted(node1.getKey(), node1.getValue(),
					node, map.get(node));
		}
	}

	/**
	 * Decreases the matching between the given nodes by the min value.
	 */
	protected void decreaseMatchingWeighted(Node node1, Double value1,
			Node node2, Double value2) {
		double matchingG = this.matching.get(node1, node2)
				- Math.min(value1, value2);
		if (matchingG < 0.0 && Math.abs(matchingG) <= 1.0E-4 || matchingG > 0.0
				&& matchingG < 1.0E-6) {
			matchingG = 0.0;
		}
		this.matching.put(node1, node2, matchingG);
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
	protected HashSet<Node> getIntersection(HashSet<Node> neighbors1,
			HashSet<Node> neighbors2) {
		HashSet<Node> intersection = new HashSet<>(neighbors1);
		intersection.retainAll(neighbors2);
		return intersection;

	}

	/**
	 * Sums the values of a Map.
	 * 
	 * @param neighbors
	 *            A {@link Map} containing the neighbors with their frequency.
	 * @return The sums of values.
	 */
	protected double getMapValueSum(HashMap<Node, Double> neighbors) {
		double sum = 0;
		for (Entry<Node, Double> e : neighbors.entrySet())
			sum = sum + e.getValue();
		return sum;
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
	protected HashSet<Node> getMatchingUnweighted(HashSet<Node> neighbors1,
			HashSet<Node> neighbors2) {
		if (neighbors1 == null || neighbors2 == null)
			return null;

		HashSet<Node> intersection = new HashSet<Node>(neighbors1);
		intersection.retainAll(neighbors2);
		return intersection;

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
	protected HashMap<Node, Double> getMatchingWeighted(
			HashMap<Node, Double> neighbors1, HashMap<Node, Double> neighbors2) {
		final HashMap<Node, Double> neighbors = new HashMap<Node, Double>();
		for (Entry<Node, Double> e : neighbors1.entrySet())
			if (neighbors2.containsKey(e.getKey()))
				if (neighbors2.get(e.getKey()) <= e.getValue())
					neighbors.put(e.getKey(), neighbors2.get(e.getKey()));
				else
					neighbors.put(e.getKey(), e.getValue());

		return neighbors;
	}

	/**
	 * Get all neighbors of an {@link Node}
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all neighbors of given node.
	 */
	protected HashSet<Node> getNeighborNodesDirectedUnweighted(DirectedNode node) {
		final HashSet<Node> neighbors = new HashSet<Node>();

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
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Map} containing all neighbors of given node with their
	 *         frequency.
	 */
	protected HashMap<Node, Double> getNeighborNodesDirectedWeighted(
			DirectedNode node) {
		final HashMap<Node, Double> neighbors = new HashMap<Node, Double>();

		if (isOutgoingMeasure()) {
			for (IElement iEdge : node.getOutgoingEdges()) {
				DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
				neighbors.put(edgeD.getDst(), weight(edgeD.getWeight()));
			}

		} else {
			for (IElement iEdge : node.getIncomingEdges()) {
				DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
				neighbors.put(edgeD.getSrc(), weight(edgeD.getWeight()));
			}

		}
		return neighbors;
	}

	/**
	 * Get all neighbors for a given node.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all neighbors of given node.
	 */
	protected HashSet<Node> getNeighborNodesUndirectedUnweighted(Node node) {
		final HashSet<Node> neighbors = new HashSet<Node>();

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

	/**
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Map} containing all neighbors of given node with their
	 *         frequency.
	 */
	protected HashMap<Node, Double> getNeighborNodesUndirectedWeighted(Node node) {
		final HashMap<Node, Double> neighbors = new HashMap<Node, Double>();

		UndirectedWeightedEdge edge;
		// iterate over all edges and ...
		for (IElement iEdge : node.getEdges()) {
			edge = (UndirectedWeightedEdge) iEdge;

			// ... add the node which is not the given one to the neighbors
			if (edge.getNode1().equals(node))
				neighbors.put(edge.getNode2(), weight(edge.getWeight()));
			else
				neighbors.put(edge.getNode1(), weight(edge.getWeight()));
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
	protected HashSet<Node> getNeighborsInDirectedUnweighted(Node node) {
		final HashSet<Node> neighbors = new HashSet<Node>();
		DirectedEdge edge;
		for (IElement iEdge : ((DirectedNode) node).getIncomingEdges()) {
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
	protected HashMap<Node, Double> getNeighborsInDirectedWeighted(Node node) {
		final HashMap<Node, Double> neighbors = new HashMap<Node, Double>();
		for (IElement iEdge : ((DirectedNode) node).getIncomingEdges()) {
			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getSrc(), weight(edgeD.getWeight()));
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
	protected HashSet<Node> getNeighborsOutDirectedUnweighted(Node node) {
		final HashSet<Node> neighbors = new HashSet<Node>();
		DirectedEdge edge;
		for (IElement iEdge : ((DirectedNode) node).getOutgoingEdges()) {
			edge = (DirectedEdge) iEdge;
			neighbors.add(edge.getDst());
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
	protected HashMap<Node, Double> getNeighborsOutDirectedWeighted(Node node) {
		final HashMap<Node, Double> neighbors = new HashMap<Node, Double>();
		for (IElement iEdge : ((DirectedNode) node).getOutgoingEdges()) {
			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getDst(), weight(edgeD.getWeight()));
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

	/**
	 * Increases the matching between the given nodes by 1.
	 */
	protected void increaseMatching(Node node1, Node node2) {
		Double matchingG = this.matching.get(node1, node2);
		this.matching.put(node1, node2, matchingG == null ? 1 : matchingG + 1);
	}

	/**
	 * Increases the matching between each pair of the given nodes by 1.
	 * 
	 * @param directedNode
	 * 
	 * @see #increaseMatchingDU(DirectedNode, DirectedNode)
	 */
	protected void increaseMatchingUnweighted(HashSet<Node> nodes, Node node) {
		for (Node node1 : nodes) {
			this.increaseMatching(node1, node);
		}
	}

	/**
	 * Increases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #increaseMatchingDirectedWeighted(UndirectedNode, UndirectedNode)
	 */
	protected void increaseMatchingWeighted(HashMap<Node, Double> map, Node node) {
		for (Entry<Node, Double> node1 : map.entrySet()) {
			this.increaseMatchingWeighted(node1.getKey(), node1.getValue(),
					node, map.get(node));
		}
	}

	/**
	 * Increases the matching between the given nodes by the min weight.
	 */
	protected void increaseMatchingWeighted(Node node1, Double value1,
			Node node2, Double value2) {
		Double matchingG = this.matching.get(node1, node2);
		this.matching.put(
				node1,
				node2,
				matchingG == null ? Math.min(value1, value2) : matchingG
						+ Math.min(value1, value2));

	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	/**
	 * Returns for which type of directed edges the similarity measure is.
	 * 
	 * @return true, if the dice similarity measure is for outgoing edges; false
	 *         for incoming
	 */
	public boolean isOutgoingMeasure() {
		if (this.directedDegreeType.equals(DirectedDegreeType.OUT))
			return true;
		return false;
	}

	protected abstract void update(Node node1, Node node2);

	protected void updateDirectedUnweighted(DirectedEdge edge,
			HashSet<Node> neighborsIn, HashSet<Node> neighborsOut) {
		this.updateDirectNeighborsMeasureUnweighted(neighborsOut);
		this.updateDirectNeighborsMeasureUnweighted(neighborsIn);

		for (Node direct1 : neighborsIn)
			this.updateDirectNeighborsMeasureUnweighted(this
					.getNeighborsOutDirectedUnweighted(direct1));

		for (Node direct1 : neighborsOut)
			this.updateDirectNeighborsMeasureUnweighted(this
					.getNeighborsInDirectedUnweighted(direct1));
	}

	protected void updateDirectedWeighted(DirectedWeightedEdge edge,
			HashMap<Node, Double> neighborsIn,
			HashMap<Node, Double> neighborsOut) {

		this.updateDirectNeighborsMeasureWeighted(neighborsOut);
		this.updateDirectNeighborsMeasureWeighted(neighborsIn);

		for (Node direct1 : neighborsIn.keySet())
			this.updateDirectNeighborsMeasureWeighted(this
					.getNeighborsOutDirectedWeighted(direct1));

		for (Node direct1 : neighborsOut.keySet())
			this.updateDirectNeighborsMeasureWeighted(this
					.getNeighborsInDirectedWeighted(direct1));

	}

	/**
	 * Updates the similarity measure between each pair of the given nodes.
	 * 
	 * @see #increaseMatchingDU(UndirectedNode, UndirectedNode)
	 */
	protected void updateDirectNeighborsMeasureUnweighted(HashSet<Node> set) {
		for (Node node1 : set)
			for (Node node2 : set) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				this.update(node1, node2);
			}
	}

	/**
	 * Updates the similarity measure between each pair of the given nodes. The
	 * given nodes are the direct Neighbors.
	 * 
	 */
	protected void updateDirectNeighborsMeasureWeighted(
			HashMap<Node, Double> map) {
		for (Node node1 : map.keySet())
			for (Node node2 : map.keySet()) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				this.update(node1, node2);
			}

	}

	/**
	 * Update the similarity measure if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateMeasureMatchingUndirectedUnweighted(
			UndirectedNode node1, Node node2) {
		for (Node undirected1 : this
				.getNeighborNodesUndirectedUnweighted(node1))
			if (!undirected1.equals(node2))
				for (Node undirected2 : this
						.getNeighborNodesUndirectedUnweighted(undirected1))
					update(node1, undirected2);
	}

	/**
	 * Update the similarity measure in the matching range.
	 */
	protected void updateMeasureMatchingUndirectedWeighted(
			UndirectedNode node1, UndirectedNode node2) {
		for (Node undirected1 : this.getNeighborNodesUndirectedWeighted(node1)
				.keySet())
			if (!undirected1.equals(node2))
				for (Node undirected2 : this
						.getNeighborNodesUndirectedWeighted(undirected1)
						.keySet())
					this.update(node1, undirected2);

	}

	/**
	 * Update the similarity measure incoming if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasuresIncomingDirectedUnweighted(
			DirectedNode nodeToRemove) {
		for (Node directed1 : this
				.getNeighborsOutDirectedUnweighted(nodeToRemove))
			for (Node directed2 : this
					.getNeighborsInDirectedUnweighted(directed1))
				if (!directed2.equals(nodeToRemove))
					for (Node directed3 : this
							.getNeighborsOutDirectedUnweighted(directed2))
						if (!directed3.equals(directed1)
								&& !directed3.equals(nodeToRemove)) {
							update(directed1, directed3);
						}
	}

	/**
	 * Update the similarity measure incoming if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasuresIncomingDirectedWeighted(
			DirectedNode nodeToRemove) {
		for (Node directed1 : this
				.getNeighborsOutDirectedWeighted(nodeToRemove).keySet())
			for (Node directed2 : this
					.getNeighborsInDirectedWeighted(directed1).keySet())
				if (!directed2.equals(nodeToRemove))
					for (Node directed3 : this.getNeighborsOutDirectedWeighted(
							directed2).keySet())
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
	protected void updateNodeRemoveMeasuresOutgoingDirectedUnweighted(
			DirectedNode nodeToRemove) {
		for (Node directed1 : this
				.getNeighborsInDirectedUnweighted(nodeToRemove))
			for (Node directed2 : this
					.getNeighborsOutDirectedUnweighted(directed1))
				if (!directed2.equals(nodeToRemove))
					for (Node directed3 : this
							.getNeighborsInDirectedUnweighted(directed2))
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
	protected void updateNodeRemoveMeasuresOutgoingDirectedWeighted(
			DirectedNode nodeToRemove) {
		for (Node directed1 : this.getNeighborsInDirectedWeighted(nodeToRemove)
				.keySet())
			for (Node directed2 : this.getNeighborsOutDirectedWeighted(
					directed1).keySet())
				if (!directed2.equals(nodeToRemove))
					for (Node directed3 : this.getNeighborsInDirectedWeighted(
							directed2).keySet())
						if (!directed3.equals(directed1)
								&& !directed3.equals(nodeToRemove)) {
							update(directed1, directed3);
						}
	}

	/**
	 * Update the dice measure if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasureUndirectedUnweighted(
			UndirectedNode nodeToRemove) {
		for (Node undirected1 : this
				.getNeighborNodesUndirectedUnweighted(nodeToRemove))
			for (Node undirected2 : this
					.getNeighborNodesUndirectedUnweighted(undirected1))
				if (!undirected2.equals(nodeToRemove))
					for (Node undirected3 : this
							.getNeighborNodesUndirectedUnweighted(undirected2))
						if (!undirected3.equals(undirected1)
								&& !undirected3.equals(nodeToRemove)) {
							update(undirected1, undirected3);
						}

	}

	/**
	 * Update the similarity measure if a node is to be removed
	 * 
	 * @param nodeToRemove
	 */
	protected void updateNodeRemoveMeasureUndirectedWeighted(
			UndirectedNode nodeToRemove) {
		for (Node undirected1 : this.getNeighborNodesUndirectedWeighted(
				nodeToRemove).keySet())
			for (Node undirected2 : this.getNeighborNodesUndirectedWeighted(
					undirected1).keySet())
				if (!undirected2.equals(nodeToRemove))
					for (Node undirected3 : this
							.getNeighborNodesUndirectedWeighted(undirected2)
							.keySet())
						if (!undirected3.equals(undirected1)
								&& !undirected3.equals(nodeToRemove)) {
							update(undirected1, undirected3);
						}

	}

	protected void updateUndirectedUnweighted(UndirectedEdge newEdge,
			HashSet<Node> neighborsNode1, HashSet<Node> neighborsNode2) {
		this.updateDirectNeighborsMeasureUnweighted(neighborsNode1);
		this.updateDirectNeighborsMeasureUnweighted(neighborsNode2);

		this.updateMeasureMatchingUndirectedUnweighted(newEdge.getNode1(),
				newEdge.getNode2());
		this.updateMeasureMatchingUndirectedUnweighted(newEdge.getNode2(),
				newEdge.getNode1());
	}

	protected void updateUndirectedWeighted(UndirectedWeightedEdge edge,
			HashMap<Node, Double> neighborsNode1,
			HashMap<Node, Double> neighborsNode2) {
		this.updateDirectNeighborsMeasureWeighted(neighborsNode1);
		this.updateDirectNeighborsMeasureWeighted(neighborsNode2);

		this.updateMeasureMatchingUndirectedWeighted(edge.getNode1(),
				edge.getNode2());
		this.updateMeasureMatchingUndirectedWeighted(edge.getNode2(),
				edge.getNode1());
	}

	/**
	 * @param w
	 *            Any {@link Weight}.
	 * @return Given w as double value.
	 */
	protected double weight(Weight w) {
		if (w instanceof IntWeight)
			return (double) ((IntWeight) w).getWeight();
		else if (w instanceof DoubleWeight)
			return ((DoubleWeight) w).getWeight();
		else
			return Double.NaN;
	}

}
