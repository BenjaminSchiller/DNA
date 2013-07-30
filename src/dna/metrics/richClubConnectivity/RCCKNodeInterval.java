package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.metrics.clusterCoefficient.ClosedTriangleClusteringCoefficient;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class RCCKNodeInterval extends Metric {

	protected double[] richClubCoefficienten;
	protected int[] nodesRichClub;

	protected SortedSet<Integer> degrees;
	protected Map<Integer, LinkedList<DirectedNode>> nodesPerDegree;
	protected int richClubIntervall;
	protected Map<Integer, LinkedList<DirectedNode>> richClubs;
	protected int[] richClubEdges;

	public RCCKNodeInterval(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init_() {
		this.richClubIntervall = 10;
		this.degrees = new TreeSet<Integer>();
		this.nodesPerDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.nodesRichClub = new int[(this.g.getNodes().size() / richClubIntervall)];

		this.richClubCoefficienten = new double[(this.g.getNodes().size() / richClubIntervall)];
		this.richClubs = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.richClubEdges = new int[(this.g.getNodes().size() / richClubIntervall)];
	}

	@Override
	public boolean compute() {
		DirectedGraph g = (DirectedGraph) this.g;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : g.getNodes()) {
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
			LinkedList<DirectedNode> temp2 = (LinkedList<DirectedNode>) temp
					.subList(i, i * richClubIntervall);
			for (DirectedNode node : temp2) {
				this.nodesRichClub[node.getIndex()] = i;
			}
			richClubs.put(i, temp2);
		}

		calculateRCC();

		return true;
	}

	private void calculateRCC() {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			LinkedList<DirectedNode> cRC = new LinkedList<DirectedNode>();
			int edgesBetweenRichClubNodes = 0;
			for (int i = 0; i < richClubs.size(); i++) {
				for (DirectedNode n : richClubs.get(i)) {
					DirectedNode dn = (DirectedNode) n;
					for (DirectedEdge ed : dn.getOutgoingEdges()) {
						if (cRC.contains(ed.getDst())
								|| richClubs.get(i).contains(ed.getDst())) {
							edgesBetweenRichClubNodes++;
						}
					}
					for (DirectedEdge ed : dn.getIncomingEdges()) {
						if (cRC.contains(ed.getSrc())) {
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
		this.nodesPerDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.richClubCoefficienten = new double[(this.g.getNodes().size() / richClubIntervall) + 1];
		this.richClubs = new HashMap<Integer, LinkedList<DirectedNode>>();
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
