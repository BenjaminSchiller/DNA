package dna.metrics.similarityMeasures.dice;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.metrics.similarityMeasures.Matrix;
import dna.metrics.similarityMeasures.Measures;
import dna.series.data.distr2.BinnedDoubleDistr;
import dna.series.data.distr2.Distr;

/**
 * Computes the dice similarity measure for graphs. The dice similarity of two
 * nodes <i>n</i>, <i>m</i> is defined as the number of elements in the
 * intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i> multiplied by 2
 * and divided by elements of <i>neighbors(n)</i> + elements of
 * <i>neighbors(m)</i>.
 * 
 * @see DiceR
 * @see DiceU
 */
public abstract class Dice extends Measures {

	/** Contains the number of neighbors for each node */
	protected HashMap<Node, Double> amountOfNeighbors;

	/**
	 * Initializes {@link Dice}.
	 * 
	 * @param name
	 *            The name of the metric.
	 */
	public Dice(String name) {
		super(name);
	}

	/**
	 * Initializes {@link Dice}.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>DiceR</i> for the Dice
	 *            Recomputation and <i>DiceU</i> for the Dice Updates.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 * @param edgeWeightType
	 *            <i>weighted</i> or <i>unweighted</i>, determining whether to
	 *            use edge weights in weighted graphs or not. Will be ignored
	 *            for unweighted graphs.
	 */
	public Dice(String name, DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super(name, directedDegreeType, edgeWeightType);
	}

	/**
	 * Static computation of the dice similarity.
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
		HashSet<Node> neighbors1;
		// neighbors for node1, node2:
		HashSet<Node> neighbors2;

		int nodeIndex1 = 0, nodeIndex2;

		for (IElement iElement1 : nodesOfGraph) {
			node1 = (DirectedNode) iElement1;
			neighbors1 = this.getNeighborNodesDirectedUnweighted(node1);
			// number of neighbors
			amountOfNeighbors.put(node1, (double) neighbors1.size());

			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// diceSimilarity is equal to equivalent calculated before
					// (diceSimilarity(1,2) = diceSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesDirectedUnweighted(node2);

				HashSet<Node> intersection = getMatchingUnweighted(neighbors1,
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
				neighbors2 = this.getNeighborNodesDirectedWeighted(node2);

				double intersection = getMapValueSum(getMatchingWeighted(
						neighbors1, neighbors2));

				this.matching.put(node1, node2, intersection);

				double fraction = getFractionDW(neighbors1, neighbors2);
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
			nodeIndex2 = 0;
			amountOfNeighbors.put(node1, (double) neighbors1.size());
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// diceSimilarity is equal to equivalent calculated before
					// (diceSimilarity(1,2) = diceSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesUndirectedUnweighted(node2);

				HashSet<Node> intersection = getIntersection(neighbors1,
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
			this.amountOfNeighbors.put(node1, this.getMapValueSum(this
					.getNeighborNodesUndirectedWeighted(node1)));
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// dice is equal to equivalent calculated before
					// (diceSimilarity(1,2) = diceSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesUndirectedWeighted(node2);

				// intersection
				double sum = getMapValueSum(getMatchingWeighted(neighbors1,
						neighbors2));

				this.matching.put(node1, node2, sum);

				double fraction = getFractionUW(neighbors1, neighbors2);
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
				&& ((Dice) m).result.equals(this.result,
						ACCEPTED_ERROR_FOR_EQUALITY);
	}

	@Override
	public Distr<?, ?>[] getDistributions() {

		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDoubleDistr(
				"BinnedDiceEveryNodeToOtherNodes", 0.01, new long[] {}, 0);

		for (IElement iterable_element : this.g.getNodes()) {

			double index = this.result.getRowSum((Node) iterable_element)
					/ this.g.getNodeCount();
			this.binnedDistributionEveryNodeToOtherNodes.incr(index);
		}
		this.binnedDistribution.truncate();
		this.binnedDistributionEveryNodeToOtherNodes.truncate();

		return new Distr<?, ?>[] { this.binnedDistribution,
				this.binnedDistributionEveryNodeToOtherNodes };
	}

	/**
	 * Computes the dice similarity of two nodes <i>n</i>, <i>m</i> for an
	 * directed weighted graph. The dice similarity is defined as the number of
	 * elements in the intersection of <i>neighbors(n)</i> and
	 * <i>neighbors(m)</i> multiplied by 2 and divided by elements of
	 * <i>neighbors(n)</i> + elements of <i>neighbors(m)</i>.
	 * 
	 * @param neighbors1
	 *            The neighbors of {@link Node}1.
	 * 
	 * @param neighbors2
	 *            The neighbors of {@link Node}2.
	 * 
	 * @return The dice similarity of the two {@link Node}s.
	 */
	private double getFractionDW(HashMap<Node, Double> neighbors1,
			HashMap<Node, Double> neighbors2) {
		double intersection = getMapValueSum(getMatchingWeighted(neighbors1,
				neighbors2));
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

	/**
	 * Computes the dice similarity of two nodes <i>n</i>, <i>m</i> for an
	 * undirected weighted graph. The dice similarity is defined as the number
	 * of elements in the intersection of <i>neighbors(n)</i> and
	 * <i>neighbors(m)</i> multiplied by 2 and divided by elements of
	 * <i>neighbors(n)</i> + elements of <i>neighbors(m)</i>.
	 * 
	 * @param neighbors1
	 *            The neighbors of {@link Node}1.
	 * 
	 * @param neighbors2
	 *            The neighbors of {@link Node}2.
	 * 
	 * @return The dice similarity of the two {@link Node}s.
	 */
	private double getFractionUW(HashMap<Node, Double> neighbors1,
			HashMap<Node, Double> neighbors2) {
		double intersection = getMapValueSum(getMatchingWeighted(neighbors1,
				neighbors2));
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
		this.amountOfNeighbors = new HashMap<Node, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDoubleDistr("BinnedDice", 0.01,
				new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDoubleDistr(
				"BinnedDiceEveryNodeToOtherNodes", 0.01, new long[] {}, 0);
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null
				&& m instanceof Dice
				&& ((Dice) m).directedDegreeType
						.equals(this.directedDegreeType)
				&& ((Dice) m).edgeWeightType.equals(this.edgeWeightType);
	}

	public void reset_() {
		this.result = new Matrix();
		this.amountOfNeighbors = new HashMap<Node, Double>();
		this.matching = new Matrix();
		this.binnedDistribution = new BinnedDoubleDistr("BinnedDice", 0.01,
				new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDoubleDistr(
				"BinnedDiceEveryNodeToOtherNodes", 0.01, new long[] {}, 0);
	}

}
