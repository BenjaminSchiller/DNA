package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.Map;

import dna.graph.Graph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class RCCPerDegreeUndirected extends Metric {
	protected Map<Integer, Integer> richClubs;
	protected Map<Integer, Double> richClubCoefficienten;
	protected Map<Integer, Integer> richClubEdges;

	protected int highestDegree;

	public RCCPerDegreeUndirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init_() {
		this.richClubs = new HashMap<Integer, Integer>();
		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
	}

	@Override
	public void reset_() {
		this.richClubs = new HashMap<Integer, Integer>();
		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
	}

	@Override
	public boolean compute() {
		UndirectedGraph g = (UndirectedGraph) this.g;

		for (UndirectedNode n : g.getNodes()) {
			int degree = n.getDegree();
			this.highestDegree = Math.max(highestDegree, degree);

			int edges = 0;
			for (UndirectedEdge ed : n.getEdges()) {
				UndirectedNode node = ed.getDifferingNode(n);
				if (node.getDegree() > degree) {
					edges += 2;
				}
				if (node.getDegree() == degree) {
					edges += 1;
				}
			}

			if (richClubs.containsKey(degree)) {
				this.richClubs.put(degree, this.richClubs.get(degree) + 1);
				this.richClubEdges.put(degree, this.richClubEdges.get(degree)
						+ edges);
			} else {

				this.richClubs.put(degree, 1);
				this.richClubEdges.put(degree, edges);
			}
		}

		calculateRCC();

		return true;

	}

	protected void calculateRCC() {
		int richClubCount = 0;
		int edges = 0;
		for (int i = this.highestDegree; i > 0; i--) {
			if (richClubs.keySet().contains(i) && richClubEdges.containsKey(i)) {
				edges += this.richClubEdges.get(i);
				richClubCount += this.richClubs.get(i);
				double divisor = richClubCount * (richClubCount - 1);

				double rCC = edges / divisor;
				this.richClubCoefficienten.put(i, rCC);
			}
		}
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof RCCPerDegreeUndirected)) {
			return false;
		}
		RCCPerDegreeUndirected rcc = (RCCPerDegreeUndirected) m;

		boolean success = true;
		if (!this.richClubCoefficienten.equals(rcc.richClubCoefficienten)) {

			System.out.println("diff @ richClubCoeffizient");
			success = false;
		}

		if (!this.richClubEdges.equals(rcc.richClubEdges)) {
			System.out.println("diff @ richClubEdges");
			success = false;
		}

		if (!this.richClubs.equals(rcc.richClubs)) {
			System.out.println("diff @ richClub");
			success = false;
		}
		return success;

	}

	@Override
	protected Value[] getValues() {
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("rCC#Coefficient",
				this.makeDistribution2(this.richClubCoefficienten));
		Distribution d2 = new Distribution("rCC#Size",
				this.makeDistribution1(this.richClubs));
		return new Distribution[] { d1, d2 };
	}

	private double[] makeDistribution1(Map<Integer, Integer> richClubs2) {
		double[] result = new double[richClubs2.keySet().size()];
		int temp = 0;
		for (int i = this.highestDegree; i > 0; i--) {
			if (richClubs2.keySet().contains(i)) {
				result[temp] = richClubs2.get(i);
				temp++;
			}
		}

		return result;

	}

	private double[] makeDistribution2(
			Map<Integer, Double> richClubCoefficienten2) {

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

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RCCPerDegreeUndirected;
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
}
