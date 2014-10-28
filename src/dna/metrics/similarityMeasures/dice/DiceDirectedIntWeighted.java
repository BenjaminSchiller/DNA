package dna.metrics.similarityMeasures.dice;

import java.util.HashMap;

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
 * Computes the dice similarity measure for graphs with {@link DirectedNode}s
 * and weighted {@link DirectedEdge}s. The dice similarity of two nodes
 * <i>n</i>, <i>m</i> is defined as the number of elements in the intersection
 * of <i>neighbors(n)</i> and <i>neighbors(m)</i> multiplied by 2 and divided by
 * elements of <i>neighbors(n)</i> + elements of <i>neighbors(m)</i>. You can
 * choose between the dice of incoming and outgoing edges
 * <p>
 * <i>Note that due to {@code double} imprecisions, this metric may calculate
 * wrong results when input edge weights or intermedia results are too
 * small.</i>
 * </p>
 * 
 * @see DiceDirectedIntWeightedR
 * @see DiceDirectedIntWeightedU
 */
public abstract class DiceDirectedIntWeighted extends
		MeasuresDirectedIntWeighted {

	/** Contains the number of neighbors for each node */
	protected HashMap<DirectedNode, Double> amountOfNeighbors;

	/**
	 * Initializes {@link DiceDirectedIntWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public DiceDirectedIntWeighted(String name) {
		super(name);

	}

	/**
	 * Initializes {@link DiceDirectedIntWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs
	 */
	public DiceDirectedIntWeighted(String name, Parameter directedDegreeType) {
		super(name, directedDegreeType);
	}

	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		DirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<DirectedNode, Integer> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using
		// diceSimilarity(1,2)
		// = diceSimilarity(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (DirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			amountOfNeighbors.put(node1, this.getMapValueSum(neighbors1));
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

				double intersection = getMapValueSum(getMatching(neighbors1,
						neighbors2));

				this.matching.put(node1, node2, intersection);

				double fraction = getFraction(neighbors1, neighbors2);
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
		if (m != null && m instanceof DiceDirectedIntWeighted) {
			return ((DiceDirectedIntWeighted) m).result.equals(this.result,
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
	 * Computes the dice similarity of two nodes <i>n</i>, <i>m</i>. The dice
	 * similarity is defined as the number of elements in the intersection of
	 * <i>neighbors(n)</i> and <i>neighbors(m)</i> multiplied by 2 and divided
	 * by elements of <i>neighbors(n)</i> + elements of <i>neighbors(m)</i>.
	 * 
	 * @param neighbors1
	 *            The neighbors of {@link Node}1.
	 * 
	 * @param neighbors2
	 *            The neighbors of {@link Node}2.
	 * 
	 * @return The dice similarity of the two {@link Node}s.
	 */
	private double getFraction(HashMap<DirectedNode, Integer> neighbors1,
			HashMap<DirectedNode, Integer> neighbors2) {
		double intersection = getMapValueSum(getMatching(neighbors1, neighbors2));
		double numerator = 2 * intersection;
		double denominator = getMapValueSum(neighbors1)
				+ getMapValueSum(neighbors2);
		double fraction;
		if (numerator == 0 || denominator == 0)
			fraction = 0;
		else
			fraction = numerator / denominator;
		return fraction;
	}

	public void init_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<DirectedNode, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"DiceDirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null
				&& m instanceof DiceDirectedIntWeighted
				&& (((DiceDirectedIntWeighted) m).isOutgoingMeasure() == this
						.isOutgoingMeasure());
	}

	public void reset_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<DirectedNode, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"DiceDirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}
}
