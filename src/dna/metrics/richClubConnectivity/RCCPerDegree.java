package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.Set;

import dna.graph.directed.DirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

//TODO:bei dyn noch bei bisher leeres degree vom vorherigem abziehen
public abstract class RCCPerDegree extends Metric {
	protected HashMap<Integer, Set<DirectedNode>> richClubs;
	protected HashMap<Integer, Double> richClubCoefficienten;
	protected HashMap<Integer, Integer> richClubEdges;

	protected int highestDegree;

	public RCCPerDegree(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init() {
		this.richClubs = new HashMap<Integer, Set<DirectedNode>>();
		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
	}

	@Override
	public void reset_() {
		this.richClubs = new HashMap<Integer, Set<DirectedNode>>();
		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
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
	protected Value[] getValues() {
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("rCC#Members",
				this.makeDistribution(this.richClubCoefficienten));
		return new Distribution[] { d1 };
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
