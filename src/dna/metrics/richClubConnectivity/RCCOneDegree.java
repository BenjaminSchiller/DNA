package dna.metrics.richClubConnectivity;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

public abstract class RCCOneDegree extends Metric {

	protected int k;
	protected double richClubCoeffizient;
	protected int richClubEdges;
	protected Set<Node> richClub;

	public RCCOneDegree(String name, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		super(name, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
	}

	@Override
	protected void init(Graph g) {
		this.k = 15;
		this.richClubCoeffizient = 0d;
		this.richClubEdges = 0;
		this.richClub = new HashSet<Node>();
	}

	@Override
	protected boolean compute_() {
		for (Node n : this.g.getNodes()) {
			int degree = n.getOut().size();
			if (degree >= k) {
				this.richClub.add(n);

			}
		}
		for (Node n : this.richClub) {
			for (Node w : n.getOut()) {
				if (richClub.contains(w)) {
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
		if (!(m instanceof RCCOneDegree)) {
			return false;
		}

		RCCOneDegree rCC = (RCCOneDegree) m;

		if (this.richClubCoeffizient != rCC.getRichClubCoeffizient()) {
			System.out.println("diff @ richClubCoeffizient");
			return false;
		}

		if (this.richClubEdges != rCC.getRichClubEdges()) {
			System.out.println("diff @ richClubEdges");
			return false;
		}

		if (!this.richClub.equals(rCC.getRichClub())) {
			System.out.println("diff @ richClub");
			return false;
		}

		return true;
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

	public Set<Node> getRichClub() {
		return richClub;
	}

	@Override
	public boolean cleanupApplication() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset_() {
		this.k = 15;
		this.richClubCoeffizient = 0d;
		this.richClubEdges = 0;
		this.richClub = new HashSet<Node>();
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

}
