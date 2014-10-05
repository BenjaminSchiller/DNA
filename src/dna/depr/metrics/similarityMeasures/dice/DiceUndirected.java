package dna.depr.metrics.similarityMeasures.dice;

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
 * Computes the dice similarity measure for graphs with {@link UndirectedNode}s
 * and unweighted {@link UndirectedEdge}s. The dice similarity of two nodes
 * <i>n</i>, <i>m</i> is defined as the number of elements in the intersection
 * of <i>neighbors(n)</i> and <i>neighbors(m)</i> multiplied by 2 and divided by
 * elements of <i>neighbors(n)</i> + elements of <i>neighbors(m)</i>.
 * 
 * @see DiceUndirectedR
 * @see DiceUndirectedU
 */
public abstract class DiceUndirected extends MeasuresUndirectedUnweighted {

	/** Contains the number of neighbors for each node */
	protected HashMap<Node, Integer> amountOfNeighbors;

	/**
	 * Initializes {@link DiceUndirected}.
	 * 
	 * @param name
	 *            The name of the metric.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * 
	 */
	public DiceUndirected(String name, ApplicationType applicationType) {
		super(name, applicationType);
	}

	@Override
	public boolean compute() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashSet<UndirectedNode> neighbors1, neighbors2;
		// indices for both for-loops to save some time with using
		// diceSimilarity(1,2) = diceSimilarity(2,1)
		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (UndirectedNode) iElement1;
			neighbors1 = this.getNeighborNodes(node1);
			nodeIndex2 = 0;
			amountOfNeighbors.put(node1, neighbors1.size());
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// diceSimilarity is equal to equivalent calculated before
					// (diceSimilarity(1,2) = diceSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodes(node2);

				HashSet<UndirectedNode> intersection = getIntersection(
						neighbors1, neighbors2);

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
	public boolean equals(MetricOld m) {
		if (m != null && m instanceof DiceUndirected) {
			return ((DiceUndirected) m).result.equals(this.result, 1.0E-4);
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
		this.amountOfNeighbors = new HashMap<Node, Integer>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong("diceUndirectedD",
				0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof DiceUndirected;
	}

	@Override
	public void reset_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<Node, Integer>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong("diceUndirectedD",
				0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedDistributionEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}
}
