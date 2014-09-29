package dna.depr.metrics.similarityMeasures.overlap;

import java.util.HashMap;
import java.util.Map;

import dna.depr.metrics.Metric;
import dna.depr.metrics.similarityMeasures.Matrix;
import dna.depr.metrics.similarityMeasures.MeasuresUndirectedDoubleWeighted;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;

/**
 * Computes the overlap similarity measure for graphs with
 * {@link UndirectedNode}s and weighted {@link UndirectedEdge}s. The overlap
 * similarity of two nodes <i>n</i>, <i>m</i> is defined as the number of
 * elements in the intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i>
 * divided by min(|<i>neighbors(n)</i>|,|<i>neighbors(m)</i>|).
 * <p>
 * <i>Note that due to {@code double} imprecisions, this metric may calculate
 * wrong results when input edge weights or intermedia results are too
 * small.</i>
 * </p>
 * 
 * @see OverlapUndirectedDoubleWeightedR
 * @see OverlapUndirectedDoubleWeightedU
 */
public abstract class OverlapUndirectedDoubleWeighted extends
		MeasuresUndirectedDoubleWeighted {

	/** Contains the number of neighbors for each node */
	protected HashMap<UndirectedNode, Double> amountOfNeighbors;

	/**
	 * Initializes {@link OverlapUndirectedDoubleWeighted}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * 
	 */
	public OverlapUndirectedDoubleWeighted(String name,
			ApplicationType applicationType) {
		super(name, applicationType);
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<UndirectedNode, Double> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using overlap(1,2)
		// = overlap(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (UndirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			amountOfNeighbors.put(node1,
					this.getMapValueSum(this.getNeighborNodes(node1)));
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// overlap is equal to equivalent calculated before
					// (overlap(1,2) = overlap(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodes(node2);

				// numerator and denominator of the fraction
				// #intersection
				double numerator = getMapValueSum(getMatching(neighbors1,
						neighbors2));
				double denominator = getMin(neighbors1, neighbors2);
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
	public boolean equals(Metric m) {
		if (m != null && m instanceof OverlapUndirectedDoubleWeighted) {
			return ((OverlapUndirectedDoubleWeighted) m).result.equals(
					this.result, 1.0E-4);
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
	 * Computes the minimum of two {@link Map}'s
	 */
	private double getMin(HashMap<UndirectedNode, Double> neighbors1,
			HashMap<UndirectedNode, Double> neighbors2) {
		if (getMapValueSum(neighbors1) <= getMapValueSum(neighbors2))
			return getMapValueSum(neighbors1);
		else
			return getMapValueSum(neighbors2);
	}

	@Override
	public void init_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<UndirectedNode, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"OverlapUndirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof OverlapUndirectedDoubleWeighted;
	}

	@Override
	public void reset_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<UndirectedNode, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"OverlapUndirectedWeightedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}
}
