package dna.metrics.similarityMeasures.matching;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.Metric;
import dna.metrics.similarityMeasures.Matrix;
import dna.metricsNew.IMetricNew;
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
 * s and unweighted {@link DirectedEdge}s. The similarity of two nodes <i>n</i>,
 * <i>m</i> is defined as the number of elements in the intersection of
 * <i>neighbors(n)</i> and <i>neighbors(m)</i>. You can choose between the
 * matching of incoming and outgoing edges
 * 
 * @see MatchingDirectedR
 * @see MatchingDirectedU
 */
public abstract class MatchingDirected extends Metric {

	/** Contains the result for each matching. */
	protected Matrix matchings;
	/** If matching is for Incoming or Outgoing edges */
	private String directedDegreeType;
	/** Binned Distribution */
	protected BinnedDistributionLong matchingDirectedD;
	/** Average per Node Distribution */
	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	/**
	 * Initializes {@link MatchingDirected}. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public MatchingDirected(String name, ApplicationType applicationType) {
		this(name, applicationType, new StringParameter("directedDegreeType",
				"out"));
	}

	/**
	 * Initializes {@link MatchingDirected}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public MatchingDirected(String name, ApplicationType type,
			Parameter directedDegreeType) {
		super(name, type, IMetricNew.MetricType.exact, directedDegreeType);
		this.directedDegreeType = this.getParameters()[0].getValue();
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		DirectedNode node1, node2;
		// neighbors for node1, node2:
		HashSet<DirectedNode> neighbors1, neighbors2;
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

				// intersection
				neighbors2.retainAll(neighbors1);
				this.matchings.put(node1, node2, (double) neighbors2.size());
				this.matchingDirectedD.incr((double) neighbors2.size());
				nodeIndex2++;
			}

			nodeIndex1++;
		}

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		if (m != null
				&& m instanceof MatchingDirected
				&& isOutgoingMatching() == ((MatchingDirected) m)
						.isOutgoingMatching()) {
			return ((MatchingDirected) m).matchings.equals(this.matchings,
					1.0E-4);
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
		this.matchingDirectedD.truncate();
		this.binnedDistributionEveryNodeToOtherNodes.truncate();
		return new Distribution[] { this.matchingDirectedD,
				this.binnedDistributionEveryNodeToOtherNodes };
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
		if (isOutgoingMatching())
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

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		// final int numberOfNodesInGraph = this.g.getNodeCount();
		// final NodeNodeValueList nodeNodeValueList = new NodeNodeValueList(
		// "MatchingDirected", numberOfNodesInGraph);
		// Double matching12;
		// int node1Index, node2Index;
		// for (IElement nodeOne : this.g.getNodes()) {
		// DirectedNode node1 = (DirectedNode) nodeOne;
		// node1Index = node1.getIndex();
		// for (IElement nodeTwo : this.g.getNodes()) {
		// DirectedNode node2 = (DirectedNode) nodeTwo;
		// node2Index = node2.getIndex();
		// ;
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
		Value v1 = new Value("avarage", this.matchingDirectedD.computeAverage());
		return new Value[] { v1 };
	}

	@Override
	public void init_() {
		this.matchings = new Matrix();
		this.matchingDirectedD = new BinnedDistributionLong(
				"MatchingDirectedD", 1, new long[] {}, 0);
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
	public boolean isComparableTo(Metric m) {
		return m != null
				&& m instanceof MatchingDirected
				&& (((MatchingDirected) m).isOutgoingMatching() == this
						.isOutgoingMatching());
	}

	/**
	 * Returns for which type of directed edges the matching is.
	 * 
	 * @return true, if the matching is for outgoing edges; false for incoming
	 */
	protected boolean isOutgoingMatching() {
		if (this.directedDegreeType.equals("out"))
			return true;
		return false;
	}

	@Override
	public void reset_() {
		this.matchings = new Matrix();
		this.matchingDirectedD = new BinnedDistributionLong(
				"MatchingDirectedD", 1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);
	}

}
