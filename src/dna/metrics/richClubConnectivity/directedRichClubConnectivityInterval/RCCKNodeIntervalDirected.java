package dna.metrics.richClubConnectivity.directedRichClubConnectivityInterval;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metrics.clusterCoefficient.ClosedTriangleClusteringCoefficient;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class RCCKNodeIntervalDirected extends Metric {

	protected Map<Integer, Double> richClubCoefficienten;
	protected Map<Integer, Integer> nodesRichClub;

	protected SortedSet<Integer> degrees;
	protected Map<Integer, LinkedList<DirectedNode>> nodesPerDegree;
	protected int richClubIntervall;
	protected Map<Integer, LinkedList<DirectedNode>> richClubs;
	protected Map<Integer, Integer> richClubEdges;

	public RCCKNodeIntervalDirected(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		this.richClubIntervall = 100;
		this.degrees = new TreeSet<Integer>();
		this.nodesPerDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.nodesRichClub = new HashMap<Integer, Integer>();

		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubs = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.richClubEdges = new HashMap<Integer, Integer>();
	}

	@Override
	public boolean compute() {

		for (IElement iE : g.getNodes()) {
			DirectedNode n = (DirectedNode) iE;
			int degree = n.getOutDegree();
			this.degrees.add(degree);

			if (this.nodesPerDegree.containsKey(degree)) {
				this.nodesPerDegree.get(degree).add(n);
			} else {
				LinkedList<DirectedNode> newDegreeSet = new LinkedList<DirectedNode>();
				newDegreeSet.add(n);
				this.nodesPerDegree.put(degree, newDegreeSet);
			}

		}

		// List of Nodes sorted By Degree
		LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
		int size = this.degrees.size();
		for (int j = 0; j < size; j++) {
			int currentDegree = this.degrees.last();
			this.degrees.remove(currentDegree);
			temp.addAll(nodesPerDegree.get(currentDegree));
		}

		// Divide Temp List into RichClubs
		int numberOfRichClubs = this.g.getNodes().size() / richClubIntervall;
		for (int i = 0; i < numberOfRichClubs; i++) {
			LinkedList<DirectedNode> temp2 = new LinkedList<DirectedNode>(
					temp.subList(i * richClubIntervall, (i + 1)
							* richClubIntervall));
			for (DirectedNode node : temp2) {
				this.nodesRichClub.put(node.getIndex(), i);
			}
			richClubs.put(i, temp2);
		}
		calculateRCC();

		return true;
	}

	private void calculateRCC() {

		int edgesBetweenRichClubNodes = 0;
		for (int i = 0; i < richClubs.size(); i++) {
			int edges = 0;
			for (DirectedNode n : richClubs.get(i)) {
				for (IElement iE : n.getOutgoingEdges()) {
					DirectedEdge ed = (DirectedEdge) iE;
					if (this.nodesRichClub.get(ed.getDst().getIndex()) >= this.nodesRichClub
							.get(n.getIndex())) {
						edges++;
					}
				}
				for (IElement iE : n.getIncomingEdges()) {
					DirectedEdge ed = (DirectedEdge) iE;
					if (this.nodesRichClub.get(ed.getDst().getIndex()) > this.nodesRichClub
							.get(n.getIndex())) {
						edges++;
					}
				}
			}

			edgesBetweenRichClubNodes += edges;
			richClubEdges.put(i, edges);
			richClubCoefficienten.put(i, (double) edgesBetweenRichClubNodes
					/ (double) ((i + 1) * this.richClubIntervall * ((i + 1)
							* this.richClubIntervall - 1)));
		}

	}

	@Override
	public boolean equals(Metric m) {

		if (m == null || !(m instanceof RCCKNodeIntervalDirected)) {
			return false;
		}
		RCCKNodeIntervalDirected rcc = (RCCKNodeIntervalDirected) m;

		if (this.richClubs.size() != rcc.richClubs.size()) {
			System.out.println("diff richclub size");
			return false;
		}
		System.out.println(this.richClubs.size());
		for (int i = 0; i < this.richClubs.size(); i++) {
			for (int j = 0; j < this.richClubs.get(i).size(); j++) {
				System.out.println(this.richClubs.get(i).get(j).getOutDegree()
						+ " " + rcc.richClubs.get(i).get(j).getOutDegree());
				if (this.richClubs.get(i).get(j).getOutDegree() != rcc.richClubs
						.get(i).get(j).getOutDegree()) {
					System.out.println("diff richclub size");
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public void reset_() {
		this.richClubIntervall = 100;
		this.degrees = new TreeSet<Integer>();
		this.nodesPerDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.nodesRichClub = new HashMap<Integer, Integer>();

		this.richClubCoefficienten = new HashMap<Integer, Double>();
		this.richClubs = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.richClubEdges = new HashMap<Integer, Integer>();
	}

	@Override
	public Value[] getValues() {
		return new Value[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution d1 = new Distribution("rCC#Members",
				makeDistribution(this.richClubCoefficienten));
		return new Distribution[] { d1 };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	private double[] makeDistribution(
			Map<Integer, Double> richClubCoefficienten2) {
		double[] result = new double[richClubCoefficienten2.size()];
		for (int i = 0; i < richClubCoefficienten2.size(); i++) {
			result[i] = richClubCoefficienten2.get(i);
		}
		return result;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof ClosedTriangleClusteringCoefficient;
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
