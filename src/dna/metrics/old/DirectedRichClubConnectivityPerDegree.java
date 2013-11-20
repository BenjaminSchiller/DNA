package dna.metrics.old;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

public abstract class DirectedRichClubConnectivityPerDegree extends Metric {
	protected Map<Integer, Integer> richClubs;
	protected Map<Integer, Integer> richClubEdges;
	protected int highestDegree;

	public DirectedRichClubConnectivityPerDegree(String name,
			ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		this.richClubs = new HashMap<Integer, Integer>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
	}

	@Override
	public void reset_() {
		this.richClubs = new HashMap<Integer, Integer>();
		this.richClubEdges = new HashMap<Integer, Integer>();
		this.highestDegree = 0;
	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			DirectedNode n = (DirectedNode) ie;
			int degree = n.getOutDegree();
			this.highestDegree = Math.max(highestDegree, degree);

			int edges = 0;
			for (IElement ieEdges : n.getOutgoingEdges()) {
				DirectedEdge ed = (DirectedEdge) ieEdges;
				if (ed.getDst().getOutDegree() >= degree) {
					edges++;
				}
			}
			for (IElement ieEdges : n.getIncomingEdges()) {
				DirectedEdge ed = (DirectedEdge) ieEdges;
				if (ed.getSrc().getOutDegree() > degree) {
					edges++;
				}
			}

			if (richClubs.containsKey(degree)) {
				this.richClubs.put(degree, this.richClubs.get(degree) + 1);
				this.richClubEdges.put(degree, this.richClubEdges.get(degree)
						+ edges);
			} else {
				Set<DirectedNode> temp = new HashSet<DirectedNode>();
				temp.add(n);
				this.richClubs.put(degree, 1);
				this.richClubEdges.put(degree, edges);
			}
		}

		return true;

	}

	private double[] calculateRCC() {
		int richClubCount = 0;
		int edges = 0;
		int counter = 0;
		double[] result = new double[this.richClubs.size()];
		for (int i = this.highestDegree; i > 0; i--) {
			if (richClubs.keySet().contains(i) && richClubEdges.containsKey(i)) {
				edges += this.richClubEdges.get(i);
				richClubCount += this.richClubs.get(i);
				double divisor = richClubCount * (richClubCount - 1);

				double rCC = edges / divisor;
				result[counter++] = rCC;

			}
		}
		return result;
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof DirectedRichClubConnectivityPerDegree)) {
			return false;
		}
		DirectedRichClubConnectivityPerDegree rcc = (DirectedRichClubConnectivityPerDegree) m;

		boolean success = true;

		if (!this.richClubEdges.equals(rcc.richClubEdges)) {
			System.out.println("diff @ richClubEdges");
			System.out.println(this.getName() + " " + this.richClubEdges);
			System.out.println(rcc.getName() + " " + rcc.richClubEdges);

			success = false;
		}

		if (!this.richClubs.equals(rcc.richClubs)) {
			System.out.println("diff @ richClub");
			success = false;
		}
		return success;

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
		return m != null && m instanceof DirectedRichClubConnectivityPerDegree;
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
}
