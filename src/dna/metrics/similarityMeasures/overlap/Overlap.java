package dna.metrics.similarityMeasures.overlap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.metrics.similarityMeasures.Matrix;
import dna.metrics.similarityMeasures.Measures;
import dna.series.data.distributions.BinnedDistributionLong;
import dna.series.data.distributions.Distribution;

/**
 * Computes the overlap similarity measure for graphs. The overlap similarity of
 * two nodes <i>n</i>, <i>m</i> is defined as the number of elements in the
 * intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i> divided by
 * min(|<i>neighbors(n)</i>|,|<i>neighbors(m)</i>|).
 * 
 * @see OverlapR
 * @see OverlapU
 */
public abstract class Overlap extends Measures {
	/** Contains the number of neighbors for each node */
	protected HashMap<Node, Double> amountOfNeighbors;

	/**
	 * Initializes {@link Overlap}.
	 * 
	 * @param name
	 *            The name of the metric.
	 */
	public Overlap(String name) {
		super(name);
	}

	/**
	 * Initializes {@link Overlap}.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>OverlapR</i> for the Dice
	 *            Recomputation and <i>Overlap</i> for the Overlap Updates.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 * @param edgeWeightType
	 *            <i>weighted</i> or <i>unweighted</i>, determining whether to
	 *            use edge weights in weighted graphs or not. Will be ignored
	 *            for unweighted graphs.
	 */
	public Overlap(String name, DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super(name, directedDegreeType, edgeWeightType);
	}

	/**
	 * Static computation of the overlap similarity.
	 * 
	 * @return True if something was computed and false if no computation was
	 *         done because graph does not fit.
	 */
	boolean compute() {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			// directed weighted graph

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return this.computeForDirectedWeightedGraph();
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return this.computeForDirectedUnweightedGraph();

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			// undirected weighted graph

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return this.computeForUndirectedWeightedGraph();
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return this.computeForUndirectedUnweightedGraph();

		} else if (DirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			// directed unweighted graph
			return this.computeForDirectedUnweightedGraph();

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			// undirected unweighted graph
			return this.computeForUndirectedUnweightedGraph();

		}
		return false;
	}

	/**
	 * Computing for graphs with directed unweighted edges based only on current
	 * snapshot.
	 */
	public boolean computeForDirectedUnweightedGraph() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		DirectedNode node1, node2;
		// neighbors for node1, node2:
		HashSet<Node> neighbors1, neighbors2;

		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (DirectedNode) iElement1;
			neighbors1 = this.getNeighborNodesDirectedUnweighted(node1);
			this.amountOfNeighbors.put(node1, (double) neighbors1.size());
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// overlap is equal to equivalent calculated before
					// (overlap(1,2) = overlap(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesDirectedUnweighted(node2);

				HashSet<Node> intersection = getMatchingUnweighted(neighbors1,
						neighbors2);

				this.matching.put(node1, node2, (double) intersection.size());

				double numerator = intersection.size();
				double denominator = Math.min(neighbors1.size(),
						neighbors2.size());
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

	/**
	 * Computing for graphs with directed weighted edges based only on current
	 * snapshot.
	 */
	public boolean computeForDirectedWeightedGraph() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		DirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<Node, Double> neighbors1, neighbors2;

		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (DirectedNode) iElement1;
			neighbors1 = this.getNeighborNodesDirectedWeighted(node1);
			amountOfNeighbors.put(node1, this.getMapValueSum(this
					.getNeighborNodesDirectedWeighted(node1)));
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// (overlap(1,2) = overlap(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesDirectedWeighted(node2);

				// numerator and denominator of the fraction
				// #intersection
				double numerator = getMapValueSum(getMatchingWeighted(
						neighbors1, neighbors2));
				double denominator = getMinWeighted(neighbors1, neighbors2);

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

	/**
	 * Computing for graphs with undirected unweighted edges based only on
	 * current snapshot.
	 */
	public boolean computeForUndirectedUnweightedGraph() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashSet<Node> neighbors1, neighbors2;

		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (UndirectedNode) iElement1;
			neighbors1 = this.getNeighborNodesUndirectedUnweighted(node1);
			this.amountOfNeighbors.put(node1, (double) neighbors1.size());
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// overlap is equal to equivalent calculated before
					// (overlap(1,2) = overlap(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesUndirectedUnweighted(node2);

				// numerator and denominator of the fraction
				HashSet<Node> intersection = this.getIntersection(neighbors1,
						neighbors2);

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

	/**
	 * Computing for graphs with undirected weighted edges based only on current
	 * snapshot.
	 */
	public boolean computeForUndirectedWeightedGraph() {
		final Iterable<IElement> nodesOfGraph = this.g.getNodes();

		UndirectedNode node1, node2;
		// neighbors for node1, node2:
		HashMap<Node, Double> neighbors1, neighbors2;

		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (UndirectedNode) iElement1;
			neighbors1 = this.getNeighborNodesUndirectedWeighted(node1);
			amountOfNeighbors.put(node1, this.getMapValueSum(this
					.getNeighborNodesUndirectedWeighted(node1)));
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// overlap is equal to equivalent calculated before
					// (overlap(1,2) = overlap(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesUndirectedWeighted(node2);

				// numerator and denominator of the fraction
				// #intersection
				double numerator = getMapValueSum(getMatchingWeighted(
						neighbors1, neighbors2));
				double denominator = getMinWeighted(neighbors1, neighbors2);
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
		return this.isComparableTo(m)
				&& ((Overlap) m).result.equals(this.result,
						ACCEPTED_ERROR_FOR_EQUALITY);
	}

	@Override
	public Distribution[] getDistributions() {
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedOverlapEveryNodeToOtherNodes", 0.01, new long[] {},
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
	private Double getMinWeighted(HashMap<Node, Double> neighbors1,
			HashMap<Node, Double> neighbors2) {
		if (getMapValueSum(neighbors1) <= getMapValueSum(neighbors2))
			return getMapValueSum(neighbors1);
		else
			return getMapValueSum(neighbors2);
	}

	public void init_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<Node, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"BinnedOverlap", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedOverlapEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null
				&& m instanceof Overlap
				&& ((Overlap) m).directedDegreeType
						.equals(this.directedDegreeType)
				&& ((Overlap) m).edgeWeightType.equals(this.edgeWeightType);
	}

	public void reset_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<Node, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDistributionLong(
				"BinnedOverlap", 0.1, new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDistributionLong(
				"BinnedOverlapEveryNodeToOtherNodes", 0.01, new long[] {},
				0);
	}
}
