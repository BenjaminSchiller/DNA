package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

//TODO:bei dyn noch bei bisher leeres degree vom vorherigem abziehen
public abstract class RCCPerDegree extends Metric {
	protected HashMap<Integer, Set<Node>> richClubs;
	protected HashMap<Integer, Double> richClubCoefficienten;
	protected HashMap<Integer, Integer> richClubEdges;

	protected int highestDegree;

	public RCCPerDegree(String name, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		super(name, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
	}

	@Override
	protected void init(Graph g) {
		this.richClubs = new HashMap<Integer, Set<Node>>();
		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
	}

	@Override
	public void reset_() {
		this.richClubs = new HashMap<Integer, Set<Node>>();
		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
	}

	@Override
	protected boolean compute_() {

		for (Node n : this.g.getNodes()) {
			int degree = n.getOut().size();
			this.highestDegree = Math.max(highestDegree, degree);
			if (richClubs.containsKey(degree)) {
				this.richClubs.get(degree).add(n);
			} else {
				Set<Node> temp = new HashSet<Node>();
				temp.add(n);
				this.richClubs.put(degree, temp);
			}
		}

		HashSet<Node> currentrichclub = new HashSet<Node>();
		for (int currentDegree : this.richClubs.keySet()) {
			int edges = 0;
			for (Node n : richClubs.get(currentDegree)) {

				for (Node d : n.getOut()) {
					if (d.getOut().size() >= currentDegree) {
						edges++;
					}
				}
				for (Node d : n.getIn()) {
					if (d.getOut().size() >= currentDegree) {
						edges++;
					}
				}
			}
			richClubEdges.put(currentDegree, edges);
			currentrichclub.addAll(richClubs.get(currentDegree));
		}

		calculateRCC();

		return true;
	}

	protected void calculateRCC() {
		int edges = 0;
		int richClubCount = 0;
		for (int i = this.highestDegree; i > 0; i--) {
			if (richClubs.keySet().contains(i) && richClubEdges.containsKey(i)) {
				edges += this.richClubEdges.get(i);
				richClubCount += this.richClubs.get(i).size();
				double divisor = richClubCount * (richClubCount - 1);

				double rCC = edges / divisor;
				this.richClubCoefficienten.put(i, rCC);
			}
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
	protected Value[] getValues() {
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("rCC#Members",
				this.makeDistribution(this.richClubCoefficienten));
		Distribution d2 = new Distribution("Degrees", degrees());
		return new Distribution[] { d1, d2 };
	}

	private double[] degrees() {
		double[] result = new double[this.g.getNodes().length];
		for (Node n : this.g.getNodes()) {
			result[n.getIndex()] = n.getOut().size();
		}
		return result;
	}

	private double[] makeDistribution(
			HashMap<Integer, Double> richClubCoefficienten2) {

		double[] result = new double[richClubCoefficienten2.keySet().size()];
		int temp = 0;
		for (int i = this.highestDegree; i > 0; i--) {
			if (richClubCoefficienten2.keySet().contains(i)) {
				result[temp] = richClubCoefficienten2.get(i);
				temp++;
			}
		}

		return result;
	}
}
