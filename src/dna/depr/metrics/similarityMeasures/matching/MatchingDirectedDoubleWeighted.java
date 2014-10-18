package dna.depr.metrics.similarityMeasures.matching;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.depr.metrics.MetricOld;
import dna.depr.metrics.similarityMeasures.Matrix;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.DoubleWeight;
import dna.metrics.IMetric;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

/**
 * Computes the similarity matching measure for graphs with {@link DirectedNode}
 * s and weighted {@link DirectedEdge}s. The similarity of two nodes <i>n</i>,
 * <i>m</i> is defined as the number of elements in the intersection of
 * <i>neighbors(n)</i> and <i>neighbors(m)</i>.
 * <p>
 * <i>Note that due to {@code double} imprecisions, this metric may calculate
 * wrong results when input edge weights or intermedia results are too
 * small.</i>
 * </p>
 * 
 * @see MatchingDirectedDoubleWeightedR
 * @see MatchingDirectedDoubleWeightedU
 */
public abstract class MatchingDirectedDoubleWeighted extends MetricOld {

	/** Contains the result for each matching. */
	protected Matrix matchings;
	/** If matching is for Incoming or Outgoing edges */
	private String directedDegreeType;
	/** Binned Distribution */
	protected BinnedDistributionLong matchingDirectedWeightedD;
	/** Average per Node Distribution */
	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	/**
	 * Initializes {@link MatchingDirectedDoubleWeighted}. Implicitly sets
	 * degree type for directed graphs to outdegree.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public MatchingDirectedDoubleWeighted(String name,
			ApplicationType applicationType) {
		this(name, applicationType, new StringParameter("directedDegreeType",
				"out"));
	}

	/**
	 * Initializes {@link MatchingDirectedDoubleWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public MatchingDirectedDoubleWeighted(String name, ApplicationType type,
			Parameter directedDegreeType) {
		super(name, type, IMetric.MetricType.exact, directedDegreeType);
		this.directedDegreeType = this.getParameters()[0].getValue();
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		DirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<DirectedNode, Double> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using matching(1,2)
		// = matching(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (DirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);

			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// matching is equal to equivalent calculated before
					// (matching(1,2) = matching(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodes(node2);

				// #intersection
				double sum = getMapValueSum(getMatching(neighbors1, neighbors2));

				this.matchings.put(node1, node2, sum);
				this.matchingDirectedWeightedD.incr(sum);

				nodeIndex2++;
			}

			nodeIndex1++;
		}

		return true;
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m != null && m instanceof MatchingDirectedDoubleWeighted) {
			return ((MatchingDirectedDoubleWeighted) m).matchings.equals(
					this.matchings, 1.0E-4);
		}

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
		this.matchingDirectedWeightedD.truncate();
		this.binnedDistributionEveryNodeToOtherNodes.truncate();
		return new Distribution[] { this.matchingDirectedWeightedD,
				this.binnedDistributionEveryNodeToOtherNodes };
	}

	/**
	 * Sums the values of a Map.
	 * 
	 * @param neighbors
	 *            A {@link Map} containing the neighbors with their frequency.
	 * @return The sums of values.
	 */
	private double getMapValueSum(HashMap<DirectedNode, Double> neighbors) {
		double sum = 0;
		for (Entry<DirectedNode, Double> e : neighbors.entrySet()) {
			sum = sum + e.getValue();
		}
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
	private HashMap<DirectedNode, Double> getMatching(
			HashMap<DirectedNode, Double> neighbors1,
			HashMap<DirectedNode, Double> neighbors2) {
		final HashMap<DirectedNode, Double> neighbors = new HashMap<DirectedNode, Double>();
		for (Entry<DirectedNode, Double> e : neighbors1.entrySet()) {
			if (neighbors2.containsKey(e.getKey())) {
				if (neighbors2.get(e.getKey()) <= e.getValue()) {
					neighbors.put(e.getKey(), neighbors2.get(e.getKey()));
				} else {
					neighbors.put(e.getKey(), e.getValue());
				}
			}
		}
		return neighbors;
	}

	/**
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Map} containing all neighbors of given node with their
	 *         frequency.
	 */
	protected HashMap<DirectedNode, Double> getNeighborNodes(DirectedNode node) {
		final HashMap<DirectedNode, Double> neighbors = new HashMap<DirectedNode, Double>();

		if (isOutgoingMatching())
			for (IElement iEdge : node.getOutgoingEdges()) {
				DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
				neighbors.put(edgeD.getDst(),
						((DoubleWeight) edgeD.getWeight()).getWeight());

			}
		else
			for (IElement iEdge : node.getIncomingEdges()) {

				DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
				neighbors.put(edgeD.getSrc(),
						((DoubleWeight) edgeD.getWeight()).getWeight());

			}
		return neighbors;
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// final int numberOfNodesInGraph = this.g.getNodeCount();
		// final NodeNodeValueList nodeNodeValueList = new NodeNodeValueList(
		// "MatchingDirectedWeighted", numberOfNodesInGraph);
		// Double matching12;
		// int node1Index, node2Index;
		// for (IElement nodeOne : this.g.getNodes()) {
		// DirectedNode node1 = (DirectedNode) nodeOne;
		// node1Index = node1.getIndex();
		// for (IElement nodeTwo : this.g.getNodes()) {
		// DirectedNode node2 = (DirectedNode) nodeTwo;
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
				this.matchingDirectedWeightedD.computeAverage());
		return new Value[] { v1 };
	}

	@Override
	public void init_() {
		this.matchings = new Matrix();
		this.matchingDirectedWeightedD = new BinnedDistributionLong(
				"MatchingDirectedWeightedD", 1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);
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

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null
				&& m instanceof MatchingDirectedDoubleWeighted
				&& (((MatchingDirectedDoubleWeighted) m).isOutgoingMatching() == this
						.isOutgoingMatching());
	}

	/**
	 * Returns for which type of directed edges the matching is.
	 * 
	 * @return true, if the matching is for outgoing edges; false for incoming
	 */
	public boolean isOutgoingMatching() {
		if (this.directedDegreeType.equals("out"))
			return true;
		return false;
	}

	@Override
	public void reset_() {
		this.matchings = new Matrix();
		this.matchingDirectedWeightedD = new BinnedDistributionLong(
				"MatchingDirectedWeightedD", 1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);
	}

}
