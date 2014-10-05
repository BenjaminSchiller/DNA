package dna.depr.metrics.similarityMeasures.matching;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.depr.metrics.MetricOld;
import dna.depr.metrics.similarityMeasures.Matrix;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.IMetric;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

/**
 * Computes the similarity matching measure for graphs with
 * {@link UndirectedNode}s and weighted {@link UndirectedEdge}s. The similarity
 * of two nodes <i>n</i>, <i>m</i> is defined as the number of elements in the
 * intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i>.
 * 
 * @see MatchingUndirectedIntWeightedR
 * @see MatchingUndirectedIntWeightedU
 */
public abstract class MatchingUndirectedIntWeighted extends MetricOld {
	/** Contains the result for each matching. */
	protected Matrix matchings;
	/** Binned Distribution */
	protected BinnedDistributionLong matchingUndirectedWeightedD;
	/** Average per Node Distribution */
	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	/**
	 * Initializes {@link MatchingUndirectedIntWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * 
	 */
	public MatchingUndirectedIntWeighted(String name,
			ApplicationType applicationType) {
		super(name, applicationType, IMetric.MetricType.exact);
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<UndirectedNode, Integer> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using matching(1,2)
		// = matching(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (UndirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// matching is equal to equivalent calculated before
					// (matching(1,2) = matching(2,1))

					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodes(node2);

				// intersection
				double sum = getMapValueSum(getMatching(neighbors1, neighbors2));

				this.matchings.put(node1, node2, sum);
				this.matchingUndirectedWeightedD.incr(sum);

				nodeIndex2++;
			}

			nodeIndex1++;
		}

		return true;
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m != null && m instanceof MatchingUndirectedIntWeighted)
			return ((MatchingUndirectedIntWeighted) m).matchings.equals(
					this.matchings, 1.0E-4);
		return false;
	}

	@Override
	public Distribution[] getDistributions() {
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);

		for (IElement iterable_element : this.g.getNodes()) {

			double index = this.matchings.getRowSum((Node) iterable_element)
					/ this.g.getNodeCount();
			this.binnedDistributionEveryNodeToOtherNodes.incr(index);
		}
		this.binnedDistributionEveryNodeToOtherNodes.truncate();
		this.matchingUndirectedWeightedD.truncate();
		return new Distribution[] { this.matchingUndirectedWeightedD,
				this.binnedDistributionEveryNodeToOtherNodes };
	}

	/**
	 * Sums the values of a Map.
	 * 
	 * @param neighbors
	 *            A {@link Map} containing the neighbors with their frequency.
	 * @return The sums of values.
	 */
	private double getMapValueSum(HashMap<UndirectedNode, Integer> neighbors) {
		double sum = 0;
		for (Entry<UndirectedNode, Integer> e : neighbors.entrySet())
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
	private HashMap<UndirectedNode, Integer> getMatching(
			HashMap<UndirectedNode, Integer> neighbors1,
			HashMap<UndirectedNode, Integer> neighbors2) {

		final HashMap<UndirectedNode, Integer> neighbors = new HashMap<UndirectedNode, Integer>();
		for (Entry<UndirectedNode, Integer> e : neighbors1.entrySet())
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
	protected HashMap<UndirectedNode, Integer> getNeighborNodes(
			UndirectedNode node) {
		final HashMap<UndirectedNode, Integer> neighbors = new HashMap<UndirectedNode, Integer>();

		UndirectedWeightedEdge edge;
		// iterate over all edges and ...
		for (IElement iEdge : node.getEdges()) {
			edge = (UndirectedWeightedEdge) iEdge;

			// ... add the node which is not the given one to the neighbors
			if (edge.getNode1().equals(node))
				neighbors.put(edge.getNode2(),
						((IntWeight) edge.getWeight()).getWeight());
			else
				neighbors.put(edge.getNode1(),
						((IntWeight) edge.getWeight()).getWeight());
		}

		return neighbors;
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// final int numberOfNodesInGraph = this.g.getNodeCount();
		// final NodeNodeValueList nodeNodeValueList = new NodeNodeValueList(
		// "MatchingUndirectedWeighted", numberOfNodesInGraph);
		// Double matching12;
		// int node1Index, node2Index;
		// for (IElement nodeOne : this.g.getNodes()) {
		// UndirectedNode node1 = (UndirectedNode) nodeOne;
		// node1Index = node1.getIndex();
		// for (IElement nodeTwo : this.g.getNodes()) {
		// UndirectedNode node2 = (UndirectedNode) nodeTwo;
		// node2Index = node2.getIndex();
		// matching12 = this.matchings.get(node1, node2);
		// matching12 = matching12 == null ? 0.0 : matching12;
		// nodeNodeValueList.setValue(node1Index, node2Index, matching12);
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
				this.matchingUndirectedWeightedD.computeAverage());
		return new Value[] { v1 };
	}

	@Override
	public void init_() {
		this.matchings = new Matrix();
		this.matchingUndirectedWeightedD = new BinnedDistributionLong(
				"MatchingUndirectedWeightedD", 1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);

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

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof MatchingUndirectedIntWeighted;
	}

	@Override
	public void reset_() {
		this.matchings = new Matrix();
		this.matchingUndirectedWeightedD = new BinnedDistributionLong(
				"MatchingUndirectedWeightedD", 1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);
	}

}
