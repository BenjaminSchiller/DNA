package dna.metrics.assortativity;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.Metric;
import dna.metrics.Metric.ApplicationType;
import dna.metricsNew.IMetricNew;
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
 * <i>Note that due to the limited range of {@code int}, this metric may
 * calculate wrong results for big edge weights as these get multiplied and
 * summed up.</i>
 * </p>
 * 
 * @see AssortativityDoubleWeightedR This metric as Recompuation
 * @see AssortativityDoubleWeightedU This metric with Updates
 * @see Assortativity A version of this metric without using edgeweights
 */
public abstract class AssortativityIntWeighted extends Metric {

	/**
	 * Is either "out" (default) or "in", depending on the {@link Parameter} in
	 * {@link #Assortativity(String, ApplicationType, Parameter)}. This value
	 * determines whether nodes in directed graphs are compared by there in- or
	 * outdegree and is ignored for undirected graphs.
	 */
	String directedDegreeType;

	/** The sum of all edge weights in the graph. */
	long totalEdgeWeight;

	// For each edge between node u,v:
	// sum of {edgeWeight(u,v) * [weightedDegree(u) * weightedDegree(v)]}
	long sum1;
	// sum of {edgeWeight(u,v) * [weightedDegree(u) + weightedDegree(v)]}
	long sum2;
	// sum of {edgeWeight(u,v) * [weightedDegree(u)^2 + weightedDegree(v)^2]}
	long sum3;

	/** The assortativity value, normally abbreviated with r. */
	double r;

	/**
	 * Initializes {@link AssortativityIntWeighted}. Implicitly sets degree type
	 * for directed graphs to outdegree.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>AssortativityWeightedR</i> for
	 *            the Assortativity Recomputation and
	 *            <i>AssortativityWeightedU</i> for the Assortativity Updates.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public AssortativityIntWeighted(String name, ApplicationType applicationType) {
		this(name, applicationType, new StringParameter("directedDegreeType",
				"out"));
	}

	/**
	 * Initializes {@link AssortativityIntWeighted}.
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
	public AssortativityIntWeighted(String name,
			ApplicationType applicationType, Parameter directedDegreeType) {
		super(name, applicationType, IMetricNew.MetricType.exact, directedDegreeType);

		this.directedDegreeType = this.getParameters()[0].getValue();
	}

	@Override
	public boolean compute() {
		if (IntWeight.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getEdgeWeightType()))
			if (DirectedWeightedEdge.class.isAssignableFrom(this.g
					.getGraphDatastructures().getEdgeType()))
				return this.computeForDirectedIntWeightedGraph();
			else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
					.getGraphDatastructures().getEdgeType()))
				return this.computeForUndirectedIntWeightedGraph();

		return false;
	}

	/**
	 * {@link #compute()} for graphs with directed {@code int} weighted edges.
	 */
	abstract boolean computeForDirectedIntWeightedGraph();

	/**
	 * {@link #compute()} for graphs with undirected {@code int} weighted edges.
	 */
	abstract boolean computeForUndirectedIntWeightedGraph();

	/**
	 * @return The weighted degree of the given node, i.e. the sum of all
	 *         weights of outgoing or incoming edges of this node.
	 */
	long directedIntWeightedDegree(DirectedNode node) {
		long weightedDegree = 0;

		if (this.directedDegreeType.equals("out"))
			for (IElement iEdge : node.getOutgoingEdges())
				weightedDegree += ((IntWeight) ((DirectedWeightedEdge) iEdge)
						.getWeight()).getWeight();
		else if (this.directedDegreeType.equals("in"))
			for (IElement iEdge : node.getIncomingEdges())
				weightedDegree += ((IntWeight) ((DirectedWeightedEdge) iEdge)
						.getWeight()).getWeight();
		else
			Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");

		return weightedDegree;
	}

	@Override
	public boolean equals(Metric m) {
		if (m != null
				&& m instanceof AssortativityIntWeighted
				&& ((AssortativityIntWeighted) m).directedDegreeType
						.equals(this.directedDegreeType))
			return ((AssortativityIntWeighted) m).r == this.r;

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

	/**
	 * @return The weighted degree of the given node, i.e. the sum of all
	 *         weights of edges connected to this node.
	 */
	long intWeightedDegree(UndirectedNode node) {
		long weightedDegree = 0;

		for (IElement iEdge : node.getEdges())
			weightedDegree += ((IntWeight) ((UndirectedWeightedEdge) iEdge)
					.getWeight()).getWeight();

		return weightedDegree;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return IntWeight.class.isAssignableFrom(b.getGraphDatastructures()
				.getEdgeWeightType());
	}

	@Override
	public boolean isApplicable(Graph g) {
		return IntWeight.class.isAssignableFrom(g.getGraphDatastructures()
				.getEdgeWeightType());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null
				&& m instanceof AssortativityIntWeighted
				&& ((AssortativityIntWeighted) m).directedDegreeType
						.equals(this.directedDegreeType);
	}

	/**
	 * Computes {@link #r} based upon {@link #totalEdgeWeight} and {@link #sum1}
	 * , {@link #sum2}, {@link #sum3}.
	 */
	void setR() {
		if (this.totalEdgeWeight == 0) {
			this.r = 0;
			return;
		}

		final double sum1m = (double) this.sum1 / (double) this.totalEdgeWeight;

		double sum2m = (double) this.sum2 / (2 * (double) this.totalEdgeWeight);
		sum2m *= sum2m;

		final double sum3m = (double) this.sum3
				/ (2 * (double) this.totalEdgeWeight);

		final double enumerator = sum1m - sum2m;
		final double denominator = sum3m - sum2m;

		this.r = enumerator == denominator ? 1 : enumerator / denominator;
	}

}
