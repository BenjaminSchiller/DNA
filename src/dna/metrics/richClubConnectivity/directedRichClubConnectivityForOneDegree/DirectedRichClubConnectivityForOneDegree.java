package dna.metrics.richClubConnectivity.directedRichClubConnectivityForOneDegree;

import java.util.HashSet;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class DirectedRichClubConnectivityForOneDegree extends Metric {

	protected int minDegree;
	protected int richClubEdges;
	protected Set<DirectedNode> richClub;

	public DirectedRichClubConnectivityForOneDegree(String name,
			ApplicationType type, int minDegree) {
		super(name, type, MetricType.exact);
		this.minDegree = minDegree;
	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			DirectedNode n = (DirectedNode) ie;
			int degree = n.getOutDegree();
			if (degree >= this.minDegree) {
				this.richClub.add(n);

			}
		}
		for (DirectedNode n : this.richClub) {
			for (IElement ie : n.getOutgoingEdges()) {
				DirectedEdge w = (DirectedEdge) ie;
				if (this.richClub.contains(w.getDst())) {
					this.richClubEdges++;
				}
			}
		}

		return true;

	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof DirectedRichClubConnectivityForOneDegree)) {
			return false;
		}

		DirectedRichClubConnectivityForOneDegree rCC = (DirectedRichClubConnectivityForOneDegree) m;
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
		this.richClub = new HashSet<DirectedNode>();
	}

	@Override
	public void reset_() {
		this.richClubEdges = 0;
		this.richClub = new HashSet<DirectedNode>();
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
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null
				&& m instanceof DirectedRichClubConnectivityForOneDegree;

	}

}
