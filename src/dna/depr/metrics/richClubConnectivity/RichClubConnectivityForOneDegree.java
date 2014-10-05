package dna.depr.metrics.richClubConnectivity;

import java.util.HashSet;
import java.util.Set;

import dna.depr.metrics.MetricOld;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class RichClubConnectivityForOneDegree extends MetricOld {

	protected int minDegree;
	protected int richClubEdges;
	protected Set<Node> richClub;

	public RichClubConnectivityForOneDegree(String name, ApplicationType type,
			int minDegree) {
		super(name, type, IMetric.MetricType.exact);
		this.minDegree = minDegree;
	}

	@Override
	public boolean compute() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			directedCompute();
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			undirectedCompute();
		}

		return true;
	}

	private void directedCompute() {
		for (IElement ie : g.getNodes()) {
			DirectedNode n = (DirectedNode) ie;
			int degree = n.getOutDegree();
			if (degree >= this.minDegree) {
				this.richClub.add(n);

			}
		}
		for (Node n : this.richClub) {
			DirectedNode dN = (DirectedNode) n;
			for (IElement ie : dN.getOutgoingEdges()) {
				DirectedEdge w = (DirectedEdge) ie;
				if (this.richClub.contains(w.getDst())) {
					this.richClubEdges++;
				}
			}
		}

	}

	private void undirectedCompute() {
		for (IElement ie : g.getNodes()) {
			UndirectedNode n = (UndirectedNode) ie;
			int degree = n.getDegree();
			if (degree >= this.minDegree) {
				this.richClub.add(n);
			}
		}
		for (Node n : this.richClub) {
			UndirectedNode uN = (UndirectedNode) n;
			for (IElement iE : n.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iE;
				Node d = ed.getDifferingNode(uN);
				if (richClub.contains(d)) {
					this.richClubEdges++;
				}
			}
		}
	}

	/**
	 * calculate the RichClub Connectivity Value
	 * 
	 * @return double
	 */
	private double calculateRCC() {
		int richClubMembers = richClub.size();
		return (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
	}

	@Override
	public boolean equals(MetricOld m) {
		if (!(m instanceof RichClubConnectivityForOneDegree)) {
			return false;
		}

		RichClubConnectivityForOneDegree rCC = (RichClubConnectivityForOneDegree) m;
		boolean success = true;

		if (this.richClubEdges != rCC.richClubEdges) {
			System.out.println("diff @ richClubEges expected "
					+ this.richClubEdges + " is " + rCC.richClubEdges);
			success = false;
		}

		if (!this.richClub.equals(rCC.richClub)) {
			System.out.println("diff @ richClub containsAll "
					+ this.richClub.containsAll(rCC.richClub)
					+ " expected size " + this.richClub.size() + " is "
					+ rCC.richClub.size());
			success = false;
		}

		return success;
	}

	@Override
	public void init_() {
		this.richClubEdges = 0;
		this.richClub = new HashSet<Node>();

	}

	@Override
	public void reset_() {
		this.richClubEdges = 0;
		this.richClub = new HashSet<Node>();
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("RichClubCoeffizient", this.calculateRCC());
		Value v2 = new Value("RichClubSize", this.richClub.size());
		Value v3 = new Value("EdgesBetweenRichClub", this.richClubEdges);
		return new Value[] { v1, v2, v3 };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| DirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| DirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof RichClubConnectivityForOneDegree;

	}

}
