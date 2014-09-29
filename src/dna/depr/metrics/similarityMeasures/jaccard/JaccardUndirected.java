package dna.depr.metrics.similarityMeasures.jaccard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dna.depr.metrics.Metric;
import dna.depr.metrics.similarityMeasures.Matrix;
import dna.depr.metrics.similarityMeasures.MeasuresUndirectedUnweighted;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;

/**
 * Computes the jaccard similarity measure for graphs with
 * {@link UndirectedNode} s and unweighted {@link UndirectedEdge}s. The jaccard
 * similarity of two nodes <i>n</i>, <i>m</i> is defined as the number of
 * elements in the intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i>
 * divided by the elements of the union of <i>neighbors(n)</i> and
 * <i>neighbors(m)</i>.
 * 
 * @see JaccardUndirectedR
 * @see JaccardUndirectedU
 */
public abstract class JaccardUndirected extends MeasuresUndirectedUnweighted {

	/** Contains the neighbors to each node */
	protected HashMap<Node, HashSet<UndirectedNode>> neighborNodes;

	/**
	 * Initializes {@link JaccardUndirected}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * 
	 */
	public JaccardUndirected(String name, ApplicationType applicationType) {
		super(name, applicationType);
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashSet<UndirectedNode> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using
		// jaccardSimilarity(1,2) = jaccardSimilarity(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (UndirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			this.neighborNodes.put(node1, neighbors1);

			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// jaccardSimilarity is equal to equivalent calculated
					// before (jaccardSimilarity(1,2) = jaccardSimilarity(2,1))

					nodeIndex2++;
					continue;
				}
				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodes(node2);

				HashSet<UndirectedNode> intersection = this.getIntersection(
						neighbors1, neighbors2);

				this.matching.put(node1, node2, (double) intersection.size());

				HashSet<UndirectedNode> union = this.getUnion(neighbors1,
						neighbors2);

				double fraction;
				if (intersection.size() == 0 || union.size() == 0)
					fraction = 0;
				else
					fraction = ((double) intersection.size() / (double) union
							.size());

				this.result.put(node1, node2, fraction);
				this.binnedDistribution.incr(fraction);

				nodeIndex2++;
			}

			nodeIndex1++;
		}

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		if (m != null && m instanceof JaccardUndirected)
			return ((JaccardUndirected) m).result.equals(this.result, 1.0E-4);

		return false;
	}

	@Override
	public Distribution[] getDistributions() {
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);

		for (IElement iterable_element : this.g.getNodes()) {

			double index = this.result.getRowSum((Node) iterable_element)
					/ this.g.getNodeCount();
			this.binnedDistributionEveryNodeToOtherNodes.incr(index);
		}
		this.binnedDistribution.truncate();
		this.binnedDistributionEveryNodeToOtherNodes.truncate();

		return new Distribution[] { this.binnedDistribution,
				this.binnedDistributionEveryNodeToOtherNodes };
	}

	/**
	 * Computes the union between the neighbors of two nodes.
	 * 
	 * @param neighbors1
	 *            A {@link Set} includes the neighbors of the first node.
	 * @param neighbors2
	 *            A {@link Set} includes the neighbors of the second node.
	 * @return A {@link set} containing the union of neighbors1 and neighbors2.
	 */
	protected HashSet<UndirectedNode> getUnion(
			HashSet<UndirectedNode> neighbors1,
			HashSet<UndirectedNode> neighbors2) {
		if (neighbors1 == null && neighbors2 != null)
			return neighbors2;
		else if (neighbors2 == null && neighbors1 != null)
			return neighbors1;
		else if (neighbors1 == null & neighbors2 == null)
			return null;
		else {
			HashSet<UndirectedNode> union = new HashSet<UndirectedNode>(
					neighbors1);
			union.addAll(neighbors2);
			return union;
		}
	}

	@Override
	public void init_() {
		this.result = new Matrix();
		this.matching = new Matrix();
		this.neighborNodes = new HashMap<Node, HashSet<UndirectedNode>>();
		this.binnedDistribution = new BinnedDistributionLong(
				"JaccardUndirectedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof JaccardUndirected;
	}

	@Override
	public void reset_() {
		this.result = new Matrix();
		this.matching = new Matrix();
		this.neighborNodes = new HashMap<Node, HashSet<UndirectedNode>>();
		this.binnedDistribution = new BinnedDistributionLong(
				"JaccardUndirectedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

}
