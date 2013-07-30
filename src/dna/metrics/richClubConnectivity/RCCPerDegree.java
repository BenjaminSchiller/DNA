package dna.metrics.richClubConnectivity;

import java.util.HashMap;
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
// TODO:bei dyn noch bei bisher leeres degree vom vorherigem abziehen
public abstract class RCCPerDegree extends Metric {
	protected HashMap<Integer, Set<DirectedNode>> richClubs;
	protected HashMap<Integer, Double> richClubCoefficienten;
	protected HashMap<Integer, Integer> richClubEdges;

	protected int highestDegree;

	public RCCPerDegree(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init_() {
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

	@Override
	public boolean compute() {
		DirectedGraph g = (DirectedGraph) this.g;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : g.getNodes()) {
				int degree = n.getOutDegree();
				this.highestDegree = Math.max(highestDegree, degree);
				if (richClubs.containsKey(degree)) {
					this.richClubs.get(degree).add(n);
				} else {
					Set<DirectedNode> temp = new HashSet<DirectedNode>();
					temp.add(n);
					this.richClubs.put(degree, temp);
				}
			}

			HashSet<DirectedNode> currentrichclub = new HashSet<DirectedNode>();
			for (int currentDegree : this.richClubs.keySet()) {
				int edges = 0;
				for (DirectedNode n : richClubs.get(currentDegree)) {

					for (DirectedEdge ed : n.getOutgoingEdges()) {
						if (ed.getDst().getOutDegree() >= currentDegree) {
							edges++;
						}
					}
					for (DirectedEdge ed : n.getIncomingEdges()) {
						if (ed.getSrc().getOutDegree() >= currentDegree) {
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
		return false;
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

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RCCPerDegree;
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
