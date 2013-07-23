package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.directed.DirectedNode;
import dna.metrics.Metric;
import dna.metrics.degree.DegreeDistribution;
import dna.series.data.Distribution;
import dna.series.data.Value;

public abstract class RCCFirstKNodes extends Metric {
	protected SortedSet<Integer> degrees;
	protected LinkedList<DirectedNode> richClub;
	protected LinkedList<DirectedNode> rest;
	protected Map<Integer, LinkedList<DirectedNode>> nodesSortedByDegree;
	protected int richClubSize;
	protected int edgesBetweenRichClub;
	protected double rCC;

	public RCCFirstKNodes(String name, ApplicationType type) {
		super(name, type);
	}

	protected void caculateRCC() {
		this.rCC = (double) this.edgesBetweenRichClub
				/ (double) (this.richClubSize * (this.richClubSize - 1));
	}

	@Override
	public boolean equals(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 500;
		this.rCC = 0d;
		this.richClub = new LinkedList<DirectedNode>();
		this.rest = new LinkedList<DirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.degrees = new TreeSet<Integer>();

	}

	@Override
	protected Value[] getValues() {
		Value v1 = new Value("RichClubCoeffizient", this.rCC);
		Value v2 = new Value("RichClubSize", this.richClub.size());
		Value v3 = new Value("EdgesBetweenRichClub", this.edgesBetweenRichClub);
		return new Value[] { v1, v2, v3 };
	}

	@Override
	protected Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	protected void init_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 500;
		this.rCC = 0d;
		this.richClub = new LinkedList<DirectedNode>();
		this.rest = new LinkedList<DirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.degrees = new TreeSet<Integer>();
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof DegreeDistribution;
	}

}
