package dna.depr.metrics.similarityMeasures.jaccard;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.depr.metrics.MetricOld;
import dna.depr.metrics.similarityMeasures.Matrix;
import dna.depr.metrics.similarityMeasures.MeasuresUndirectedDoubleWeighted;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;

/**
 * Computes the jaccard similarity measure for graphs with
 * {@link UndirectedNode} s and weighted {@link UndirectedEdge}s. The jaccard
 * similarity of two nodes <i>n</i>, <i>m</i> is defined as the number of
 * elements in the intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i>
 * divided by the elements of the union of <i>neighbors(n)</i> and
 * <i>neighbors(m)</i>.
 * <p>
 * <i>Note that due to {@code double} imprecisions, this metric may calculate
 * wrong results when input edge weights or intermedia results are too
 * small.</i>
 * </p>
 * 
 * @see JaccardUndirectedDoubleWeightedR
 * @see JaccardUndirectedDoubleWeightedU
 */
public abstract class JaccardUndirectedDoubleWeighted extends
		MeasuresUndirectedDoubleWeighted {

	/** Contains the neighbors to each node */
	protected HashMap<Node, HashMap<UndirectedNode, Double>> neighborNodes;

	/**
	 * Initializes {@link JaccardUndirectedDoubleWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * 
	 */
	public JaccardUndirectedDoubleWeighted(String name,
			ApplicationType applicationType) {
		super(name, applicationType);
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<UndirectedNode, Double> neighbors1, neighbors2;
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

				double intersection = getMapValueSum(getMatching(neighbors1,
						neighbors2));
				// # union
				double denominator = getMapValueSum(getUnion(neighbors1,
						neighbors2));

				double fraction;
				if (intersection == 0 || denominator == 0)
					fraction = 0.0;
				else
					fraction = intersection / denominator;

				this.matching.put(node1, node2, intersection);

				this.result.put(node1, node2, fraction);
				this.binnedDistribution.incr(fraction);
				nodeIndex2++;
			}

			nodeIndex1++;
		}

		return true;
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m != null && m instanceof JaccardUndirectedDoubleWeighted)

			return ((JaccardUndirectedDoubleWeighted) m).result.equals(
					this.result, 1.0E-4)
					&& ((JaccardUndirectedDoubleWeighted) m).matching.equals(
							this.matching, 1.0E-4);

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
	protected HashMap<UndirectedNode, Double> getUnion(
			HashMap<UndirectedNode, Double> neighbors1,
			HashMap<UndirectedNode, Double> neighbors2) {

		if (neighbors1 == null)
			return neighbors2;
		else if (neighbors2 == null)
			return neighbors1;

		final HashMap<UndirectedNode, Double> union = new HashMap<UndirectedNode, Double>(
				neighbors1);

		for (Entry<UndirectedNode, Double> entry : neighbors2.entrySet())
			if (union.containsKey(entry.getKey())
					&& union.get(entry.getKey()) >= entry.getValue())
				continue;
			else
				union.put(entry.getKey(), entry.getValue());

		return union;
	}

	@Override
	public void init_() {
		this.result = new Matrix();
		this.matching = new Matrix();
		this.neighborNodes = new HashMap<Node, HashMap<UndirectedNode, Double>>();
		this.binnedDistribution = new BinnedDistributionLong(
				"JaccardUndirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof JaccardUndirectedDoubleWeighted;
	}

	@Override
	public void reset_() {
		this.result = new Matrix();
		this.matching = new Matrix();
		this.neighborNodes = new HashMap<Node, HashMap<UndirectedNode, Double>>();
		this.binnedDistribution = new BinnedDistributionLong(
				"JaccardUndirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

}
