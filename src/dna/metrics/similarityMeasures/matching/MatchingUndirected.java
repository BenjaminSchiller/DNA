package dna.metrics.similarityMeasures.matching;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metrics.similarityMeasures.Matrix;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;


/**
 * Computes the similarity matching measure for graphs with
 * {@link UndirectedNode} s and unweighted {@link UndirectedEdge}s. The
 * similarity of two nodes <i>n</i>, <i>m</i> is defined as the number of
 * elements in the intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i>.
 * 
 * @see MatchingUndirectedR
 * @see MatchingUndirectedU
 */

public abstract class MatchingUndirected extends Metric {

	/** Contains the result for each matching. */
	protected Matrix matching;

	/** Binned Distribution */
	protected BinnedDistributionLong matchingUndirectedD;

	/** Average per Node Distribution */
	protected BinnedDistributionLong binnedDistributionEveryNodeToOtherNodes;

	/**
	 * Initializes {@link MatchingUndirected}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * 
	 */
	public MatchingUndirected(String name, ApplicationType applicationType) {
		super(name, applicationType, MetricType.exact);
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;

		// neighbors for node1, node2:
		HashSet<UndirectedNode> neighbors1, neighbors2;

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
				neighbors2.retainAll(neighbors1);
				this.matching.put(node1, node2, (double) neighbors2.size());
				this.matchingUndirectedD.incr((double) neighbors2.size());
				nodeIndex2++;
			}
			nodeIndex1++;
		}
		return true;
	}

	@Override
	public boolean equals(Metric m) {
		if (m != null && m instanceof MatchingUndirected)
			return ((MatchingUndirected) m).matching.equals(this.matching,
					1.0E-4);
		return false;
	}

	@Override
	public Distribution[] getDistributions() {
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);

		for (IElement iterable_element : this.g.getNodes()) {

			double index = this.matching.getRowSum((Node) iterable_element)
					/ this.g.getNodeCount();
			this.binnedDistributionEveryNodeToOtherNodes.incr(index);
		}
		this.matchingUndirectedD.truncate();
		this.binnedDistributionEveryNodeToOtherNodes.truncate();

		return new Distribution[] { this.matchingUndirectedD,
				this.binnedDistributionEveryNodeToOtherNodes };
	}

	/**
	 * @param node
	 *            The {@link UndirectedNode} which neighbors are wanted.
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
		// "MatchingUndirected", numberOfNodesInGraph);
		// Double matching12;
		// int node1Index, node2Index;
		// for (IElement nodeOne : this.g.getNodes()) {
		// UndirectedNode node1 = (UndirectedNode) nodeOne;
		// node1Index = node1.getIndex();
		// for (IElement nodeTwo : this.g.getNodes()) {
		// UndirectedNode node2 = (UndirectedNode) nodeTwo;
		// node2Index = node2.getIndex();
		// matching12 = this.matching.get(node1, node2);
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
				this.matchingUndirectedD.computeAverage());
		return new Value[] { v1 };
	}

	@Override
	public void init_() {
		this.matching = new Matrix();
		this.matchingUndirectedD = new BinnedDistributionLong(
				"MatchingUndirectedD", 1, new long[] {}, 0);
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
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof MatchingUndirected;
	}

	@Override
	public void reset_() {
		this.matching = new Matrix();
		this.matchingUndirectedD = new BinnedDistributionLong(
				"MatchingUndirectedD", 1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 1, new long[] {}, 0);
	}

}
