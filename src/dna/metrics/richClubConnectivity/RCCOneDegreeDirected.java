package dna.metrics.richClubConnectivity;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class RCCOneDegreeDirected extends Metric {

	protected int k;
	protected double richClubCoeffizient;
	protected int richClubEdges;
	protected Set<DirectedNode> richClub;

	public RCCOneDegreeDirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init_() {
		this.k = 5;
		this.richClubCoeffizient = 0d;
		this.richClubEdges = 0;
		this.richClub = new HashSet<DirectedNode>();
	}

	@Override
	public boolean compute() {
		DirectedGraph g = (DirectedGraph) this.g;

		for (DirectedNode n : g.getNodes()) {

			int degree = n.getOutDegree();
			if (degree >= k) {
				this.richClub.add(n);

			}
		}
		for (DirectedNode n : this.richClub) {
			for (DirectedEdge w : n.getOutgoingEdges()) {
				if (richClub.contains(w.getDst())) {
					this.richClubEdges++;
				}
			}
		}

		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
		return true;

	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof RCCOneDegreeDirected)) {
			return false;
		}

		RCCOneDegreeDirected rCC = (RCCOneDegreeDirected) m;
		boolean success = true;
		if (this.richClubCoeffizient != rCC.getRichClubCoeffizient()) {
			System.out.println("diff @ richClubCoeffizient");
			success = false;
		}

		if (this.richClubEdges != rCC.getRichClubEdges()) {

			success = false;
		}

		if (!this.richClub.equals(rCC.getRichClub())) {
			System.out.println("diff @ richClub");
			success = false;
		}

		return success;
	}

	public int getK() {
		return k;
	}

	public double getRichClubCoeffizient() {
		return richClubCoeffizient;
	}

	public int getRichClubEdges() {
		return richClubEdges;
	}

	public Set<DirectedNode> getRichClub() {
		return richClub;
	}

	@Override
	public void reset_() {
		this.k = 5;
		this.richClubCoeffizient = 0d;
		this.richClubEdges = 0;
		this.richClub = new HashSet<DirectedNode>();
	}

	@Override
	protected Value[] getValues() {
		Value v1 = new Value("RichClubCoeffizient", this.richClubCoeffizient);
		Value v2 = new Value("RichClubSize", this.richClub.size());
		Value v3 = new Value("EdgesBetweenRichClub", this.richClubEdges);
		return new Value[] { v1, v2, v3 };
	}

	@Override
	protected Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RCCOneDegreeDirected;

	}

}
