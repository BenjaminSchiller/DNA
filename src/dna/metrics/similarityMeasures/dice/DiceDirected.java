package dna.metrics.similarityMeasures.dice;

import java.util.HashMap;
import java.util.HashSet;

import dna.depr.metrics.MetricOld.ApplicationType;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.similarityMeasures.Matrix;
import dna.metrics.similarityMeasures.MeasuresDirectedUnweighted;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;
import dna.util.parameters.Parameter;

/**
 * Computes the dice similarity measure for graphs with {@link DirectedNode}s
 * and unweighted {@link DirectedEdge}s. The dice similarity of two nodes
 * <i>n</i>, <i>m</i> is defined as the number of elements in the intersection
 * of <i>neighbors(n)</i> and <i>neighbors(m)</i> multiplied by 2 and divided by
 * elements of <i>neighbors(n)</i> + elements of <i>neighbors(m)</i>. You can
 * choose between the dice of incoming and outgoing edges
 * 
 * @see DiceDirectedR
 * @see DiceDirectedU
 */
public abstract class DiceDirected extends MeasuresDirectedUnweighted {

	/** Contains the number of neighbors for each node */
	protected HashMap<DirectedNode, Integer> amountOfNeighbors;

	/**
	 * Initializes {@link DiceDirected}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public DiceDirected(String name) {
		super(name);
	}

	/**
	 * Initializes {@link DiceDirected}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public DiceDirected(String name, Parameter directedDegreeType) {
		super(name, directedDegreeType);
	}

	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		DirectedNode node1, node2;
		// neighbors for node1, node2:
		HashSet<DirectedNode> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using
		// diceSimilarity(1,2) = diceSimilarity(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (DirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			// number of neighbors
			amountOfNeighbors.put(node1, neighbors1.size());

			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// diceSimilarity is equal to equivalent calculated before
					// (diceSimilarity(1,2) = diceSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodes(node2);

				HashSet<DirectedNode> intersection = getMatching(neighbors1,
						neighbors2);

				this.matching.put(node1, node2, (double) intersection.size());

				double numerator = 2 * intersection.size();
				double denominator = neighbors1.size() + neighbors2.size();
				double fraction;
				if (numerator == 0 || denominator == 0)
					fraction = 0;
				else
					fraction = numerator / denominator;

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
		if (m != null && m instanceof DiceDirected)
			return ((DiceDirected) m).result.equals(this.result, 1.0E-4);

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

	public void init_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<DirectedNode, Integer>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong("DiceDirectedD",
				0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null
				&& m instanceof DiceDirected
				&& (((DiceDirected) m).isOutgoingMeasure() == this
						.isOutgoingMeasure());
	}

	public void reset_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<DirectedNode, Integer>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong("DiceDirectedD",
				0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}
}
