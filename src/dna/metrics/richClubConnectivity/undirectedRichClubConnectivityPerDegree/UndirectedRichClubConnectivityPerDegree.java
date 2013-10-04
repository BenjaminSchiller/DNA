package dna.metrics.richClubConnectivity.undirectedRichClubConnectivityPerDegree;

import java.util.HashMap;
import java.util.Map;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class UndirectedRichClubConnectivityPerDegree extends Metric {
	protected Map<Integer, Integer> richClubs;
	protected Map<Integer, Integer> richClubEdges;

	protected int highestDegree;

	public UndirectedRichClubConnectivityPerDegree(String name,
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
			UndirectedNode n = (UndirectedNode) ie;
			int degree = n.getDegree();
			this.highestDegree = Math.max(highestDegree, degree);

			int edges = 0;
			for (IElement ieEdges : n.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) ieEdges;
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
		return true;
	}

	/**
	 * calculate the RichClub Connectivity Values for the different degrees
	 * 
	 * @return double[]
	 */
	private double[] calculateRCC() {
		int richClubCount = 0;
		int edges = 0;
		int counter = 0;
		double[] result = new double[this.richClubEdges.size()];
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
		if (m == null
				|| !(m instanceof UndirectedRichClubConnectivityPerDegree)) {
			return false;
		}
		UndirectedRichClubConnectivityPerDegree rcc = (UndirectedRichClubConnectivityPerDegree) m;

		boolean success = true;

		if (!this.richClubEdges.equals(rcc.richClubEdges)) {
			System.out.println("diff @ richClubEdges");
			System.out.println(this.richClubEdges);
			System.out.println(rcc.richClubEdges);
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
		Distribution d1 = new Distribution("rCC#Coefficient",
				this.calculateRCC());
		Distribution d2 = new Distribution("rCC#Size",
				this.makeDistribution1(this.richClubs));
		return new Distribution[] { d1, d2 };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
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

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null
				&& m instanceof UndirectedRichClubConnectivityPerDegree;
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
