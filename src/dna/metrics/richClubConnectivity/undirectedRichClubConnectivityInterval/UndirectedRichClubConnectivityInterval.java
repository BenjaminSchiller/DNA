package dna.metrics.richClubConnectivity.undirectedRichClubConnectivityInterval;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class UndirectedRichClubConnectivityInterval extends Metric {

	protected HashMap<UndirectedNode, Integer> nodesRichClub;
	protected int richClubIntervall;
	protected HashMap<Integer, HashMap<Integer, LinkedList<UndirectedNode>>> richClubs;
	protected HashMap<Integer, Integer> richClubEdges;

	public UndirectedRichClubConnectivityInterval(String name,
			ApplicationType type, int interval) {
		super(name, type, MetricType.exact);
		this.richClubIntervall = interval;
	}

	@Override
	public void init_() {
		this.nodesRichClub = new HashMap<UndirectedNode, Integer>();
		this.richClubs = new HashMap<Integer, HashMap<Integer, LinkedList<UndirectedNode>>>();
		this.richClubEdges = new HashMap<Integer, Integer>();
	}

	@Override
	public boolean compute() {

		TreeSet<Integer> degrees = new TreeSet<>();
		HashMap<Integer, LinkedList<UndirectedNode>> nodesPerDegree = new HashMap<>();
		for (IElement iE : g.getNodes()) {
			UndirectedNode n = (UndirectedNode) iE;
			int degree = n.getDegree();
			degrees.add(degree);

			if (nodesPerDegree.containsKey(degree)) {
				nodesPerDegree.get(degree).add(n);
			} else {
				LinkedList<UndirectedNode> newDegreeSet = new LinkedList<UndirectedNode>();
				newDegreeSet.add(n);
				nodesPerDegree.put(degree, newDegreeSet);
			}

		}

		// List of Nodes sorted By Degree
		LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
		int size = degrees.size();
		for (int j = 0; j < size; j++) {
			int currentDegree = degrees.last();
			degrees.remove(currentDegree);
			temp.addAll(nodesPerDegree.get(currentDegree));
		}

		for (int i = 0; i < Math.round(this.g.getNodeCount()
				/ this.richClubIntervall); i++) {
			this.richClubs.put(i,
					new HashMap<Integer, LinkedList<UndirectedNode>>());
		}
		int rcCounter = -1;
		int edges = 0;
		for (int i = 0; i < temp.size(); i++) {
			if (i % this.richClubIntervall == 0) {
				rcCounter++;
			}
			this.nodesRichClub.put(temp.get(i), rcCounter);
			if (this.richClubs.get(rcCounter).containsKey(
					temp.get(i).getDegree())) {
				this.richClubs.get(rcCounter).get(temp.get(i).getDegree())
						.add(temp.get(i));
			} else {
				LinkedList<UndirectedNode> l = new LinkedList<UndirectedNode>();
				l.add(temp.get(i));
				this.richClubs.get(rcCounter).put(temp.get(i).getDegree(), l);

			}
			this.richClubEdges.put(rcCounter, edges);
			for (IElement ie : temp.get(i).getEdges()) {
				UndirectedNode n = ((UndirectedEdge) ie).getDifferingNode(temp
						.get(i));
				if (this.nodesRichClub.containsKey(n)) {
					edges++;
				}
			}
		}
		return true;
	}

	private double[] calculateRCC() {

		double[] result = new double[this.richClubs.size()];
		int edgesBetweenRichClubNodes = 0;
		for (int i = 0; i < richClubs.size(); i++) {

			edgesBetweenRichClubNodes += this.richClubEdges.get(i);
			result[i] = (double) edgesBetweenRichClubNodes
					/ (double) ((i + 1) * this.richClubIntervall * ((i + 1)
							* this.richClubIntervall - 1));
		}
		return result;
	}

	@Override
	public boolean equals(Metric m) {

		if (m == null || !(m instanceof UndirectedRichClubConnectivityInterval)) {
			return false;
		}
		UndirectedRichClubConnectivityInterval rcc = (UndirectedRichClubConnectivityInterval) m;

		if (this.richClubs.size() != rcc.richClubs.size()) {
			System.out.println("diff richclub size expected "
					+ this.richClubs.size() + " is " + rcc.richClubs.size());
			return false;
		}
		for (int i = 0; i < this.richClubs.size(); i++) {
			if (richClubs.keySet().containsAll(rcc.richClubs.keySet())) {
				for (int j : this.richClubs.get(i).keySet()) {
					if (this.richClubs.get(i).keySet()
							.containsAll(rcc.richClubs.get(i).keySet())) {
						if (this.richClubs.get(i).get(j).size() != rcc.richClubs
								.get(i).get(j).size()) {
							System.out
									.println("diff richclub size expected for i "
											+ i
											+ " j "
											+ j
											+ " "
											+ this.richClubs.get(i).get(j)
													.size()
											+ " is "
											+ rcc.richClubs.get(i).get(j)
													.size());
							return false;
						}
					} else {
						System.out.println("diff at keyset for rc " + i);
						System.out.println("expected "
								+ this.richClubs.get(i).keySet());
						System.out.println("is "
								+ rcc.richClubs.get(i).keySet());

						for (int k : rcc.richClubs.get(i).keySet()) {
							System.out.println(rcc.richClubs.get(i).get(k)
									.size());
						}
						System.out.println();
						return false;
					}
				}
			} else {
				System.out.println("diff at keyset for rc ");
				return false;
			}
		}
		return true;
	}

	@Override
	public void reset_() {
		this.nodesRichClub = new HashMap<UndirectedNode, Integer>();
		this.richClubs = new HashMap<Integer, HashMap<Integer, LinkedList<UndirectedNode>>>();
		this.richClubEdges = new HashMap<Integer, Integer>();
	}

	@Override
	public Value[] getValues() {
		return new Value[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution d1 = new Distribution("rCC#Members", this.calculateRCC());
		return new Distribution[] { d1 };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof UndirectedRichClubConnectivityInterval;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
