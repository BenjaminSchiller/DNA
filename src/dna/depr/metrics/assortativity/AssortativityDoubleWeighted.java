package dna.depr.metrics.assortativity;

import dna.depr.metrics.MetricOld;
import dna.depr.metrics.MetricOld.ApplicationType;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.metrics.IMetric;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.Log;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

/**
 * Implementation of <b>Weighted Assortativity Coefficient</b>. Assortartivity
 * is also known as "Homophily" or "assartative mixing (by degree)".
 * <p>
 * The assortativity coefficient is a {@code double} value >= -1 and <= 1. The
 * more nodes with equal degree tend to connect together, the closer this
 * coefficient is at 1. The more nodes with different degree tend to connect
 * together, the closer this coefficient is at -1. For directed graphs the node
 * <u>out</u>degree is important.
 * </p>
 * <p>
 * <i>Note that due to {@code double} imprecisions, this metric may calculate
 * wrong results when input edge weights or intermedia results are too
 * small.</i>
 * </p>
 * 
 * @see AssortativityDoubleWeightedR This metric as Recompuation
 * @see AssortativityDoubleWeightedU This metric with Updates
 * @see Assortativity A version of this metric without using edgeweights
 */
public abstract class AssortativityDoubleWeighted extends MetricOld {

	/**
	 * To check equality of metrics in {@link #equals(MetricOld)}, the
	 * assortativity coefficient {@link #r} is compared. This value is the
	 * allowed difference of two values to still accept them as equal.
	 */
	public static final double ACCEPTED_ERROR_FOR_EQUALITY = 1.0E-4;

	/**
	 * Is either "out" (default) or "in", depending on the {@link Parameter} in
	 * {@link #Assortativity(String, ApplicationType, Parameter)}. This value
	 * determines whether nodes in directed graphs are compared by there in- or
	 * outdegree and is ignored for undirected graphs.
	 */
	String directedDegreeType;

	/** The sum of all edge weights in the graph. */
	double totalEdgeWeight;

	// For each edge between node u,v:
	// sum of {edgeWeight(u,v) * [weightedDegree(u) * weightedDegree(v)]}
	double sum1;
	// sum of {edgeWeight(u,v) * [weightedDegree(u) + weightedDegree(v)]}
	double sum2;
	// sum of {edgeWeight(u,v) * [weightedDegree(u)^2 + weightedDegree(v)^2]}
	double sum3;

	/** The assortativity value, normally abbreviated with r. */
	double r;

	/**
	 * Initializes {@link AssortativityDoubleWeighted}. Implicitly sets degree
	 * type for directed graphs to outdegree.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>AssortativityWeightedR</i> for
	 *            the Assortativity Recomputation and
	 *            <i>AssortativityWeightedU</i> for the Assortativity Updates.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public AssortativityDoubleWeighted(String name,
			ApplicationType applicationType) {
		this(name, applicationType, new StringParameter("directedDegreeType",
				"out"));
	}

	/**
	 * Initializes {@link AssortativityDoubleWeighted}.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>AssortativityWeightedR</i> for
	 *            the Assortativity Recomputation and
	 *            <i>AssortativityWeightedU</i> for the Assortativity Updates.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 */
	public AssortativityDoubleWeighted(String name,
			ApplicationType applicationType, Parameter directedDegreeType) {
		super(name, applicationType, IMetric.MetricType.exact, directedDegreeType);

		this.directedDegreeType = this.getParameters()[0].getValue();
	}

	@Override
	public boolean compute() {
		if (DoubleWeight.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getEdgeWeightType()))
			if (DirectedWeightedEdge.class.isAssignableFrom(this.g
					.getGraphDatastructures().getEdgeType()))
				return this.computeForDirectedDoubleWeightedGraph();
			else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
					.getGraphDatastructures().getEdgeType()))
				return this.computeForUndirectedDoubleWeightedGraph();

		return false;
	}

	/**
	 * {@link #compute()} for graphs with directed {@code double} weighted
	 * edges.
	 */
	abstract boolean computeForDirectedDoubleWeightedGraph();

	/**
	 * {@link #compute()} for graphs with undirected {@code double} weighted
	 * edges.
	 */
	abstract boolean computeForUndirectedDoubleWeightedGraph();

	/**
	 * @return The weighted out degree of the given node, i.e. the sum of all
	 *         weights of outgoing edges of this node.
	 */
	double directedDoubleWeightedDegree(DirectedNode node) {
		double weightedDegree = 0.0;

		if (this.directedDegreeType.equals("out"))
			for (IElement iEdge : node.getOutgoingEdges())
				weightedDegree += ((DoubleWeight) ((DirectedWeightedEdge) iEdge)
						.getWeight()).getWeight();
		else if (this.directedDegreeType.equals("in"))
			for (IElement iEdge : node.getIncomingEdges())
				weightedDegree += ((DoubleWeight) ((DirectedWeightedEdge) iEdge)
						.getWeight()).getWeight();
		else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		return weightedDegree;
	}

	/**
	 * @return The weighted degree of the given node, i.e. the sum of all
	 *         weights of edges connected to this node.
	 */
	double doubleWeightedDegree(UndirectedNode node) {
		double weightedDegree = 0.0;

		for (IElement iEdge : node.getEdges())
			weightedDegree += ((DoubleWeight) ((UndirectedWeightedEdge) iEdge)
					.getWeight()).getWeight();

		return weightedDegree;
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m != null
				&& m instanceof AssortativityDoubleWeighted
				&& ((AssortativityDoubleWeighted) m).directedDegreeType
						.equals(this.directedDegreeType))
			return Math.abs(((AssortativityDoubleWeighted) m).r - this.r) <= ACCEPTED_ERROR_FOR_EQUALITY;

		return false;
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public Value[] getValues() {
		return new Value[] { new Value("AssortativityCoefficient", this.r) };
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DoubleWeight.class.isAssignableFrom(b.getGraphDatastructures()
				.getEdgeWeightType());
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DoubleWeight.class.isAssignableFrom(g.getGraphDatastructures()
				.getEdgeWeightType());
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null
				&& m instanceof AssortativityDoubleWeighted
				&& ((AssortativityDoubleWeighted) m).directedDegreeType
						.equals(this.directedDegreeType);
	}

	/**
	 * Computes {@link #r} based upon {@link #totalEdgeWeight} and {@link #sum1}
	 * , {@link #sum2}, {@link #sum3}.
	 */
	void setR() {
		if (this.totalEdgeWeight == 0.0) {
			this.r = 0.0;
			return;
		}

		final double sum1m = this.sum1 / this.totalEdgeWeight;

		double sum2m = this.sum2 / (2 * this.totalEdgeWeight);
		sum2m *= sum2m;

		final double sum3m = this.sum3 / (2 * this.totalEdgeWeight);

		final double enumerator = sum1m - sum2m;
		final double denominator = sum3m - sum2m;

		this.r = enumerator == denominator ? 1 : enumerator / denominator;
	}

}
