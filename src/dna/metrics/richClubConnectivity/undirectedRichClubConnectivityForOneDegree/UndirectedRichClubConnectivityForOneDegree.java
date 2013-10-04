package dna.metrics.richClubConnectivity.undirectedRichClubConnectivityForOneDegree;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class UndirectedRichClubConnectivityForOneDegree extends Metric {

	protected int minDegree;
	protected int richClubEdges;
	protected Set<UndirectedNode> richClub;

	public UndirectedRichClubConnectivityForOneDegree(String name,
			ApplicationType type, int minDegree) {
		super(name, type, MetricType.exact);
		this.minDegree = minDegree;
	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			UndirectedNode n = (UndirectedNode) ie;
			int degree = n.getDegree();
			if (degree >= this.minDegree) {
				this.richClub.add(n);
			}
		}
		for (UndirectedNode n : this.richClub) {
			for (IElement iE : n.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iE;
				UndirectedNode d = ed.getDifferingNode(n);
				if (richClub.contains(d)) {
					this.richClubEdges++;
				}
			}
		}
		return true;
	}

	/**
	 * calculate the RichClub Connectivity Value
	 * 
	 * @return double
	 */
	private double calculateRCC() {
		int richClubMembers = richClub.size();
		return (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof UndirectedRichClubConnectivityForOneDegree)) {
			return false;
		}

		UndirectedRichClubConnectivityForOneDegree rCC = (UndirectedRichClubConnectivityForOneDegree) m;
		boolean success = true;

		if (this.richClubEdges != rCC.richClubEdges) {
			System.out.println("diff @ richClubEges expected "
					+ this.richClubEdges + " is " + rCC.richClubEdges);
			success = false;
		}

		if (!this.richClub.equals(rCC.richClub)) {
			System.out.println("diff @ richClub containsAll "
					+ this.richClub.containsAll(rCC.richClub)
					+ " expected size " + this.richClub.size() + " is "
					+ rCC.richClub.size());
			success = false;
		}

		return success;
	}

	@Override
	public void init_() {
		this.richClubEdges = 0;
		this.richClub = new HashSet<UndirectedNode>();

	}

	@Override
	public void reset_() {
		this.richClubEdges = 0;
		this.richClub = new HashSet<UndirectedNode>();
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("RichClubCoeffizient", this.calculateRCC());
		Value v2 = new Value("RichClubSize", this.richClub.size());
		Value v3 = new Value("EdgesBetweenRichClub", this.richClubEdges);
		return new Value[] { v1, v2, v3 };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
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

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null
				&& m instanceof UndirectedRichClubConnectivityForOneDegree;

	}

}
