package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

public abstract class RCCKNodeInterval extends Metric {

	protected double[] richClubCoefficienten;
	protected int[] nodesRichClub;

	private SortedSet<Integer> degrees;
	private Map<Integer, LinkedList<Node>> nodesPerDegree;
	protected int richClubIntervall;
	protected Map<Integer, LinkedList<Node>> richClubs;
	protected int[] richClubEdges;

	public RCCKNodeInterval(String name, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		super(name, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
	}

	@Override
	protected void init(Graph g) {
		this.richClubIntervall = 10;
		this.degrees = new TreeSet<Integer>();
		this.nodesPerDegree = new HashMap<Integer, LinkedList<Node>>();
		this.nodesRichClub = new int[(this.g.getNodes().length / richClubIntervall)];

		this.richClubCoefficienten = new double[(this.g.getNodes().length / richClubIntervall)];
		this.richClubs = new HashMap<Integer, LinkedList<Node>>();
		this.richClubEdges = new int[(this.g.getNodes().length / richClubIntervall)];
	}

	@Override
	protected boolean compute_() {
		for (Node n : this.g.getNodes()) {
			int degree = n.getIn().size();
			this.degrees.add(degree);

			if (this.nodesPerDegree.containsKey(degree)) {
				this.nodesPerDegree.get(degree).add(n);
			} else {
				LinkedList<Node> newDegreeSet = new LinkedList<Node>();
				newDegreeSet.add(n);
				this.nodesPerDegree.put(degree, newDegreeSet);
			}

		}

		// List of Nodes sorted By Degree
		LinkedList<Node> temp = new LinkedList<Node>();
		int size = this.degrees.size();
		for (int j = 0; j < size; j++) {
			int currentDegree = this.degrees.last();
			this.degrees.remove(currentDegree);
			temp.addAll(nodesPerDegree.get(currentDegree));
		}

		// Divide Temp List into RichClubs
		int numberOfRichClubs = this.g.getNodes().length / richClubIntervall;
		for (int i = 0; i < numberOfRichClubs; i++) {
			LinkedList<Node> temp2 = (LinkedList<Node>) temp.subList(i, i
					* richClubIntervall);
			for (Node node : temp2) {
				this.nodesRichClub[node.getIndex()] = i;
			}
			richClubs.put(i, temp2);
		}

		calculateRCC();

		return true;
	}

	private void calculateRCC() {
		LinkedList<Node> cRC = new LinkedList<Node>();
		int edgesBetweenRichClubNodes = 0;
		for (int i = 0; i < richClubs.size(); i++) {
			for (Node n : richClubs.get(i)) {
				for (Node d : n.getOut()) {
					if (cRC.contains(d) || richClubs.get(i).contains(d)) {
						edgesBetweenRichClubNodes++;
					}
				}
				for (Node d : n.getIn()) {
					if (cRC.contains(d)) {
						edgesBetweenRichClubNodes++;
					}
				}
			}

			if (i >= richClubEdges.length) {
				continue;
			}
			cRC.addAll(richClubs.get(i));
			richClubEdges[i] = edgesBetweenRichClubNodes;
			richClubCoefficienten[i] = (double) edgesBetweenRichClubNodes
					/ (double) (cRC.size() * (cRC.size() - 1));
		}

	}

	@Override
	public boolean equals(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cleanupApplication() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset_() {
		this.richClubIntervall = 10;
		this.degrees = new TreeSet<Integer>();
		this.nodesPerDegree = new HashMap<Integer, LinkedList<Node>>();
		this.richClubCoefficienten = new double[(this.g.getNodes().length / richClubIntervall) + 1];
		this.richClubs = new HashMap<Integer, LinkedList<Node>>();
		this.richClubEdges = new int[(this.g.getNodes().length / richClubIntervall) + 1];
		this.nodesRichClub = new int[(this.g.getNodes().length / richClubIntervall)];

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
