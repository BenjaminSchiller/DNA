package dna.metrics.similarityMeasures.jaccard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.metrics.similarityMeasures.Matrix;
import dna.metrics.similarityMeasures.Measures;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.Distr;
import dna.updates.batch.Batch;

/**
 * Computes the jaccard similarity measure for graphs. The jaccard similarity of
 * two nodes <i>n</i>, <i>m</i> is defined as the number of elements in the
 * intersection of <i>neighbors(n)</i> and <i>neighbors(m)</i> divided by the
 * elements of the union of <i>neighbors(n)</i> and <i>neighbors(m)</i>.
 * 
 * @see JaccardR
 * @see JaccardU
 */
public abstract class Jaccard extends Measures {

	/** Contains the neighbors to each node for unweighted graphs */
	protected HashMap<Node, HashSet<Node>> neighborNodesUnweighted;

	/** Contains the neighbors to each node for weighted graphs */
	protected HashMap<Node, HashMap<Node, Double>> neighborNodesWeighted;

	/**
	 * Initializes {@link Jaccard}.
	 * 
	 * @param name
	 *            The name of the metric.
	 */
	public Jaccard(String name) {
		super(name);
	}

	/**
	 * Initializes {@link Jaccard}.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>JaccardR</i> for the Dice
	 *            Recomputation and <i>JaccardU</i> for the Jaccard Updates.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 * @param edgeWeightType
	 *            <i>weighted</i> or <i>unweighted</i>, determining whether to
	 *            use edge weights in weighted graphs or not. Will be ignored
	 *            for unweighted graphs.
	 */
	public Jaccard(String name, DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super(name, directedDegreeType, edgeWeightType);
	}

	/**
	 * Static computation of the jaccard similarity.
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
			this.neighborNodesUnweighted.put(node1, neighbors1);
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// jaccardSimilarity is equal to equivalent calculated
					// before (jaccardSimilarity(1,2) =jaccardSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesDirectedUnweighted(node2);

				HashSet<Node> intersection = this.getMatchingUnweighted(
						neighbors1, neighbors2);

				this.matching.put(node1, node2, (double) intersection.size());
				this.matching.put(node2, node1, (double) intersection.size());

				HashSet<Node> union = this.getUnionUnweighted(neighbors1,
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
			this.neighborNodesWeighted.put(node1, neighbors1);
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// jaccard is equal to equivalent calculated before
					// (jaccardSimilarity(1,2) = jaccardSimilarity(2,1))
					nodeIndex2++;
					continue;
				}

				node2 = (DirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesDirectedWeighted(node2);

				double numerator = getMapValueSum(getMatchingWeighted(
						neighbors1, neighbors2));
				double denominator = getMapValueSum(getUnionWeighted(
						neighbors1, neighbors2));
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
			this.neighborNodesUnweighted.put(node1, neighbors1);

			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// jaccardSimilarity is equal to equivalent calculated
					// before (jaccardSimilarity(1,2) = jaccardSimilarity(2,1))

					nodeIndex2++;
					continue;
				}
				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesUndirectedUnweighted(node2);

				HashSet<Node> intersection = this.getIntersection(neighbors1,
						neighbors2);

				this.matching.put(node1, node2, (double) intersection.size());

				HashSet<Node> union = this.getUnionUnweighted(neighbors1,
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
			this.neighborNodesWeighted.put(node1, neighbors1);
			nodeIndex2 = 0;
			for (IElement iElement2 : nodesOfGraph) {
				if (nodeIndex2 < nodeIndex1) {
					// jaccardSimilarity is equal to equivalent calculated
					// before (jaccardSimilarity(1,2) = jaccardSimilarity(2,1))

					nodeIndex2++;
					continue;
				}

				node2 = (UndirectedNode) iElement2;
				neighbors2 = this.getNeighborNodesUndirectedWeighted(node2);

				double intersection = getMapValueSum(getMatchingWeighted(
						neighbors1, neighbors2));
				// # union
				double denominator = getMapValueSum(getUnionWeighted(
						neighbors1, neighbors2));

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
	public boolean equals(IMetric m) {
		return this.isComparableTo(m)
				&& ((Jaccard) m).result.equals(this.result,
						ACCEPTED_ERROR_FOR_EQUALITY);
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDoubleDistr(
				"BinnedJaccardEveryNodeToOtherNodes", 0.01, new long[] {}, 0);

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
	 * Computes the union between the neighbors of two nodes for unweighted
	 * graphs.
	 * 
	 * @param neighbors1
	 *            A {@link Set} includes the neighbors of the first node.
	 * @param neighbors2
	 *            A {@link Set} includes the neighbors of the second node.
	 * @return A {@link Set} containing the union of neighbors1 and neighbors2.
	 */
	protected HashSet<Node> getUnionUnweighted(HashSet<Node> neighbors1,
			HashSet<Node> neighbors2) {
		if (neighbors1 == null && neighbors2 != null)
			return neighbors2;
		else if (neighbors2 == null && neighbors1 != null)
			return neighbors1;
		else if (neighbors1 == null & neighbors2 == null)
			return null;
		else {
			HashSet<Node> union = new HashSet<Node>(neighbors1);
			union.addAll(neighbors2);
			return union;
		}
	}

	/**
	 * Computes the union between the neighbors of two {@link Node}s for
	 * weighted graphs.
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
	protected HashMap<Node, Double> getUnionWeighted(
			HashMap<Node, Double> neighbors1, HashMap<Node, Double> neighbors2) {

		if (neighbors1 == null && neighbors2 != null)
			return neighbors2;
		else if (neighbors2 == null && neighbors1 != null)
			return neighbors1;
		else if (neighbors1 == null & neighbors2 == null)
			return null;
		else {
			final HashMap<Node, Double> union = new HashMap<Node, Double>(
					neighbors1);
			for (Entry<Node, Double> entry : neighbors2.entrySet())
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
		if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
			// directed weighted
			this.neighborNodesWeighted = new HashMap<Node, HashMap<Node, Double>>();
		else
			// directed unweighted
			this.neighborNodesUnweighted = new HashMap<Node, HashSet<Node>>();
		this.binnedDistribution = new BinnedDoubleDistr("BinnedJaccard", 0.1,
				new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDoubleDistr(
				"BinnedJaccardEveryNodeToOtherNodes", 0.01, new long[] {}, 0);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null
				&& m instanceof Jaccard
				&& ((Jaccard) m).directedDegreeType
						.equals(this.directedDegreeType)
				&& ((Jaccard) m).edgeWeightType.equals(this.edgeWeightType);
	}

	public void reset_() {
		this.result = new Matrix();
		this.matching = new Matrix();
		if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
			// directedWeighted
			this.neighborNodesWeighted = new HashMap<Node, HashMap<Node, Double>>();
		else
			// undirectedWeighted
			this.neighborNodesUnweighted = new HashMap<Node, HashSet<Node>>();
		this.binnedDistribution = new BinnedDoubleDistr("BinnedJaccard", 0.1,
				new long[] {}, 0);
		this.binnedDistributionEveryNodeToOtherNodes = new BinnedDoubleDistr(
				"BinnedJaccardEveryNodeToOtherNodes", 0.01, new long[] {}, 0);
	}

}
