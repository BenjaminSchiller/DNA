package dna.depr.metrics.similarityMeasures.overlap;

import java.util.HashMap;
import java.util.HashSet;

import dna.depr.metrics.MetricOld;
import dna.depr.metrics.similarityMeasures.Matrix;
import dna.depr.metrics.similarityMeasures.MeasuresUndirectedUnweighted;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.series.data.BinnedDistributionLong;
import dna.series.data.Distribution;

/**
 * Computes the overlap similarity measure for graphs with
 * {@link UndirectedNode}s and unweighted {@link UndirectedEdge}s. The overlap
 * similarity of two nodes <i>n</i>, <i>m</i> is defined as the number of
 * elements in the intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i>
 * divided by min(|<i>neighbors(n)</i>|,|<i>neighbors(m)</i>|).
 * 
 * @see OverlapUndirectedR
 * @see OverlapUndirectedU
 */
public abstract class OverlapUndirected extends MeasuresUndirectedUnweighted {

	/** Contains the number of neighbors for each node */
	protected HashMap<UndirectedNode, Integer> amountOfNeighbors;

	/**
	 * Initializes {@link OverlapUndirected}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * 
	 */
	public OverlapUndirected(String name, ApplicationType applicationType) {
		super(name, applicationType);
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashSet<UndirectedNode> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using overlap(1,2)
		// = overlap(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (UndirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			this.amountOfNeighbors.put(node1, neighbors1.size());
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
				HashSet<UndirectedNode> intersection = this.getIntersection(
						neighbors1, neighbors2);

				this.matching.put(node1, node2, (double) intersection.size());

				int min = Math.min(neighbors1.size(), neighbors2.size());

				double fraction;
				if (intersection.size() == 0 || min == 0)
					fraction = 0.0;
				else
					fraction = (double) intersection.size() / (double) min;

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
		if (m != null && m instanceof OverlapUndirected) {
			return ((OverlapUndirected) m).result.equals(this.result, 1.0E-4);
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

	@Override
	public void init_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<UndirectedNode, Integer>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"JaccardUndirectedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof OverlapUndirected;
	}

	@Override
	public void reset_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<UndirectedNode, Integer>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"OverlapUndirectedD", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}
}
