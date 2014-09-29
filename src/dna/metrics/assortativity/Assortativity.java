package dna.metrics.assortativity;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
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
 * Implementation of <b>Assortativity Coefficient</b>. Assortartivity is also
 * known as "Homophily" or "assartative mixing (by degree)".
 * <p>
 * The assortativity coefficient is a {@code double} value >= -1 and <= 1. The
 * more nodes with equal degree tend to connect together, the closer this
 * coefficient is at 1. The more nodes with different degree tend to connect
 * together, the closer this coefficient is at -1. For directed graphs either
 * the node in- or outdegree is important (use the right constructor though).
 * </p>
 * 
 * @see AssortativityR This metric as Recompuation
 * @see AssortativityU This metric with Updates
 * @see AssortativityDoubleWeighted A version of this metric using edgeweights
 */
public abstract class Assortativity extends Metric {

	/**
	 * Is either "out" (default) or "in", depending on the {@link Parameter} in
	 * {@link #Assortativity(String, ApplicationType, Parameter)}. This value
	 * determines whether nodes in directed graphs are compared by there in- or
	 * outdegree and is ignored for undirected graphs.
	 */
	String directedDegreeType;

	// For each edge between node u,v:
	int sum1; // sum of [degree(u) * degree(v)]
	int sum2; // sum of [degree(u) + degree(v)]
	int sum3; // sum of [degree(u)^2 + degree(v)^2]

	/** The assortativity value, normally abbreviated with r. */
	private double r;

	/**
	 * Initializes {@link Assortativity}. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>AssortativityR</i> for the
	 *            Assortativity Recomputation and <i>AssortativityU</i> for the
	 *            Assortativity Updates.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 */
	public Assortativity(String name, ApplicationType applicationType) {
		this(name, applicationType, new StringParameter("directedDegreeType",
				"out"));
	}

	/**
	 * Initializes {@link Assortativity}.
	 * 
	 * @param name
	 *            The name of the metric, e.g. <i>AssortativityR</i> for the
	 *            Assortativity Recomputation and <i>AssortativityU</i> for the
	 *            Assortativity Updates.
	 * @param applicationType
	 *            The {@link ApplicationType}, corresponding to the name.
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 */
	public Assortativity(String name, ApplicationType applicationType,
			Parameter directedDegreeType) {
		super(name, applicationType, IMetricNew.MetricType.exact, directedDegreeType);

		this.directedDegreeType = this.getParameters()[0].getValue();
	}

	@Override
	public boolean compute() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType()))
			return this.computeForDirectedGraph();
		else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType()))
			return this.computeForUndirectedGraph();

		return false;
	}

	/**
	 * {@link #compute()} for graphs with directed edges.
	 */
	private boolean computeForDirectedGraph() {
		DirectedEdge edge;
		int srcNodeDegree, dstNodeDegree;
		for (IElement iElement : this.g.getEdges()) {
			edge = (DirectedEdge) iElement;
			if (this.directedDegreeType.equals("out")) {
				srcNodeDegree = edge.getSrc().getOutDegree();
				dstNodeDegree = edge.getDst().getOutDegree();
			} else if (this.directedDegreeType.equals("in")) {
				srcNodeDegree = edge.getSrc().getInDegree();
				dstNodeDegree = edge.getDst().getInDegree();
			} else {
				Log.error("Graph is directed but degree type set is neither 'out' (default) nor 'in'.");
				return false;
			}

			this.sum1 += srcNodeDegree * dstNodeDegree;
			this.sum2 += srcNodeDegree + dstNodeDegree;
			this.sum3 += srcNodeDegree * srcNodeDegree + dstNodeDegree
					* dstNodeDegree;
		}

		this.setR(this.g.getEdgeCount());

		return true;
	}

	/**
	 * {@link #compute()} for graphs with undirected edges.
	 */
	private boolean computeForUndirectedGraph() {
		UndirectedEdge edge;
		int node1Degree, node2Degree;
		for (IElement iElement : this.g.getEdges()) {
			edge = (UndirectedEdge) iElement;
			node1Degree = edge.getNode1().getDegree();
			node2Degree = edge.getNode2().getDegree();

			this.sum1 += node1Degree * node2Degree;
			this.sum2 += node1Degree + node2Degree;
			this.sum3 += node1Degree * node1Degree + node2Degree * node2Degree;
		}

		this.setR(this.g.getEdgeCount());

		return true;
	}

	@Override
	public boolean equals(Metric m) {
		if (m != null && m instanceof Assortativity)
			return ((Assortativity) m).r == this.r;

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
	public void init_() {
		this.sum1 = 0;
		this.sum2 = 0;
		this.sum3 = 0;

		this.r = 0.0;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return Node.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Graph g) {
		return Node.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null
				&& m instanceof Assortativity
				&& ((Assortativity) m).directedDegreeType
						.equals(this.directedDegreeType);
	}

	@Override
	public void reset_() {
		this.sum1 = 0;
		this.sum2 = 0;
		this.sum3 = 0;

		this.r = 0.0;
	}

	/**
	 * Computes {@link #r} based upon given number of edges in graph and
	 * {@link #sum1}, {@link #sum2}, {@link #sum3}.
	 */
	void setR(int numberOfEdges) {
		if (numberOfEdges == 0) {
			this.r = 0.0;
			return;
		}

		final double sum1m = (double) this.sum1 / (double) numberOfEdges;

		double sum2m = (double) this.sum2 / (double) (2 * numberOfEdges);
		sum2m *= sum2m;

		final double sum3m = (double) this.sum3 / (double) (2 * numberOfEdges);

		final double enumerator = sum1m - sum2m;
		final double denominator = sum3m - sum2m;

		this.r = enumerator == denominator ? 1.0 : enumerator / denominator;
	}

}
