package dna.metrics.richClub;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public abstract class RichClubConnectivityByDegree extends Metric {

	protected int[] edgeCount;
	protected int[] nodeCount;
	protected int[] size;

	public RichClubConnectivityByDegree(String name, Parameter... p) {
		super(name, p);
	}

	@Override
	public Value[] getValues() {
		double[] fractions = new double[] { 0.01, 0.05, 0.1, 0.5, 1.0 };
		Value[] v = new Value[fractions.length];
		for (int i = 0; i < fractions.length; i++) {
			int index = this.getIndexForFraction(fractions[i]);
			v[i] = new Value("RC-" + (int) (fractions[i] * 100),
					this.getCoefficient(index));
		}
		return v;
	}

	public int getIndexForFraction(double fraction) {
		int totalNodes = this.g.getNodeCount();
		int limit = (int) (fraction * totalNodes);

		for (int i = this.nodeCount.length - 1; i >= 0; i--) {
			if (this.nodeCount[i] < limit) {
				if ((i + 1) == this.nodeCount.length) {
					return i;
				} else {
					return i + 1;
				}
			}
		}

		return 0;

		// for (int i = 0; i < this.nodeCount.length; i++) {
		// if (this.nodeCount[i] >= limit) {
		// return i - 1;
		// }
		// }
	}

	private double getCoefficient(int index) {
		if (this.nodeCount[index] <= 1) {
			return 0;
		}
		if (this.g.isDirected()) {
			return (double) this.edgeCount[index]
					/ (this.nodeCount[index] * (this.nodeCount[index] - 1));
		} else {
			return (double) this.edgeCount[index]
					/ (this.nodeCount[index] * (this.nodeCount[index] - 1) / 2);
		}
	}

	@Override
	public Distribution[] getDistributions() {
		double[] v = new double[this.edgeCount.length];
		for (int i = 0; i < this.edgeCount.length; i++) {
			v[i] = this.getCoefficient(i);
		}
		Distribution d = new Distribution("RichClubConnectivity", v);
		Distribution edges = new DistributionInt("edges", this.edgeCount, 1);
		Distribution nodes = new DistributionInt("nodes", this.nodeCount, 1);
		Distribution size = new DistributionInt("size", this.size, 1);
		return new Distribution[] { d, edges, nodes, size };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[0];
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof RichClubConnectivityByDegree;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof RichClubConnectivityByDegree)) {
			return false;
		}
		RichClubConnectivityByDegree rcc = (RichClubConnectivityByDegree) m;
		boolean success = true;
		success &= ArrayUtils
				.equals(this.edgeCount, rcc.edgeCount, "RCC-edges");
		success &= ArrayUtils
				.equals(this.nodeCount, rcc.nodeCount, "RCC-total");
		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	protected DegreeRichClubs compute() {
		DegreeRichClubs rcs = new DegreeRichClubs();

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			DegreeRichClub rc = rcs.getClubByDegree(n.getDegree());
			rc.addNode(n);
		}

		this.fill(rcs);

		return rcs;
	}

	protected void fill(DegreeRichClubs rcs) {
		this.edgeCount = new int[rcs.clubs.size()];
		this.nodeCount = new int[rcs.clubs.size()];
		this.size = new int[rcs.clubs.size()];

		for (DegreeRichClub rc : rcs.clubs.values()) {
			this.edgeCount[rc.degree] = rc.edgeCount;
			this.nodeCount[rc.degree] = rc.nodeCount;
			this.size[rc.degree] = rc.size();
		}

	}

}
