package dna.metrics.similarityMeasures.jaccard;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.depr.metrics.MetricOld.ApplicationType;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.similarityMeasures.Matrix;
import dna.metrics.similarityMeasures.MeasuresDirectedIntWeighted;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;
import dna.util.parameters.Parameter;

/**
 * Computes the jaccard similarity measure for graphs with {@link DirectedNode}
 * s and weighted {@link DirectedEdge}s. The jaccard similarity of two nodes
 * <i>n</i>, <i>m</i> is defined as the number of elements in the intersection
 * of <i>neighbors(n)</i> and <i>neighbors(m)</i> divided by the elements of the
 * union of <i>neighbors(n)</i> and <i>neighbors(m)</i>. You can choose between
 * the jaccard of incoming and outgoing edges
 * <p>
 * <i>Note that due to {@code double} imprecisions, this metric may calculate
 * wrong results when input edge weights or intermedia results are too
 * small.</i>
 * </p>
 * 
 * @see JaccardDirectedIntWeightedR
 * @see JaccardDirectedIntWeightedU
 */
public abstract class JaccardDirectedIntWeighted extends
		MeasuresDirectedIntWeighted {

	/** Contains the neighbors to each node */
	protected HashMap<Node, HashMap<DirectedNode, Integer>> neighborNodes;

	/**
	 * Initializes {@link JaccardDirectedIntWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public JaccardDirectedIntWeighted(String name) {
		super(name);
	}

	/**
	 * Initializes {@link JaccardDirectedIntWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public JaccardDirectedIntWeighted(String name, Parameter directedDegreeType) {
		super(name, directedDegreeType);
	}

	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		DirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<DirectedNode, Integer> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using
		// jaccardSimilarity(1,2) = jaccardSimilarity(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (DirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			this.neighborNodes.put(node1, neighbors1);
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// jaccard is equal to equivalent calculated before
					// (jaccardSimilarity(1,2) = jaccardSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodes(node2);

				double numerator = getMapValueSum(getMatching(neighbors1,
						neighbors2));
				double denominator = getMapValueSum(getUnion(neighbors1,
						neighbors2));
				double fraction;
				if (numerator == 0 || denominator == 0)
					fraction = 0.0;
				else
					fraction = numerator / denominator;

				this.matching.put(node1, node2, numerator);

				this.result.put(node1, node2, fraction);
				this.binnedDistribution.incr(fraction);

				nodeIndex2++;
			}

			nodeIndex1++;
		}

		return true;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m != null && m instanceof JaccardDirectedIntWeighted) {
			return ((JaccardDirectedIntWeighted) m).result.equals(this.result,
					1.0E-4);
		}
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
	 * Computes the union between the neighbors of two {@link Node}s.
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
	protected HashMap<DirectedNode, Integer> getUnion(
			HashMap<DirectedNode, Integer> neighbors1,
			HashMap<DirectedNode, Integer> neighbors2) {

		if (neighbors1 == null && neighbors2 != null)
			return neighbors2;
		else if (neighbors2 == null && neighbors1 != null)
			return neighbors1;
		else if (neighbors1 == null & neighbors2 == null)
			return null;
		else {
			final HashMap<DirectedNode, Integer> union = new HashMap<DirectedNode, Integer>(
					neighbors1);
			for (Entry<DirectedNode, Integer> entry : neighbors2.entrySet())
				if (union.containsKey(entry.getKey())
						&& union.get(entry.getKey()) >= entry.getValue())
					continue;
				else
					union.put(entry.getKey(), entry.getValue());
			return union;
		}

	}

	public void init_() {
		this.result = new Matrix();
		this.matching = new Matrix();
		this.neighborNodes = new HashMap<Node, HashMap<DirectedNode, Integer>>();
		this.binnedDistribution = new BinnedDistributionLong(
				"JaccardDirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null
				&& m instanceof JaccardDirectedIntWeighted
				&& (((JaccardDirectedIntWeighted) m).isOutgoingMeasure() == this
						.isOutgoingMeasure());
	}

	public void reset_() {
		this.result = new Matrix();
		this.matching = new Matrix();
		this.neighborNodes = new HashMap<Node, HashMap<DirectedNode, Integer>>();
		this.binnedDistribution = new BinnedDistributionLong(
				"JaccardDirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}
}
