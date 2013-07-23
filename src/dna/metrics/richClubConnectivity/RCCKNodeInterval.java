package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

public abstract class RCCKNodeInterval extends Metric {

	protected double[] richClubCoefficienten;
	protected int[] nodesRichClub;

	protected SortedSet<Integer> degrees;
	protected Map<Integer, LinkedList<Node>> nodesPerDegree;
	protected int richClubIntervall;
	protected Map<Integer, LinkedList<Node>> richClubs;
	protected int[] richClubEdges;

	public RCCKNodeInterval(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init() {
		this.richClubIntervall = 10;
		this.degrees = new TreeSet<Integer>();
		this.nodesPerDegree = new HashMap<Integer, LinkedList<Node>>();
		this.nodesRichClub = new int[(this.g.getNodes().size() / richClubIntervall)];

		this.richClubCoefficienten = new double[(this.g.getNodes().size() / richClubIntervall)];
		this.richClubs = new HashMap<Integer, LinkedList<Node>>();
		this.richClubEdges = new int[(this.g.getNodes().size() / richClubIntervall)];
	}

	@Override
	public boolean equals(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset_() {
		this.richClubIntervall = 10;
		this.degrees = new TreeSet<Integer>();
		this.nodesPerDegree = new HashMap<Integer, LinkedList<Node>>();
		this.richClubCoefficienten = new double[(this.g.getNodes().size() / richClubIntervall) + 1];
		this.richClubs = new HashMap<Integer, LinkedList<Node>>();
		this.richClubEdges = new int[(this.g.getNodes().size() / richClubIntervall) + 1];
		this.nodesRichClub = new int[(this.g.getNodes().size() / richClubIntervall)];

	}

	@Override
	protected Value[] getValues() {
		// TODO Auto-generated method stub
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("rCC#Members",
				this.richClubCoefficienten);
		return new Distribution[] { d1 };
	}

}
