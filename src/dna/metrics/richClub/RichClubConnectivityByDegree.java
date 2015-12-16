package dna.metrics.richClub;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public abstract class RichClubConnectivityByDegree extends Metric {

	protected long[] edgeCount;
	protected long[] nodeCount;
	protected long[] size;

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
	public Distr<?, ?>[] getDistributions() {
		// TODO fix RCC (adapt to new Distr!!!
		// double[] v = new double[this.edgeCount.length];
		// for (int i = 0; i < this.edgeCount.length; i++) {
		// v[i] = this.getCoefficient(i);
		// }
		// Distribution d = new DistributionDouble("RichClubConnectivity", v);
		BinnedIntDistr rcc = new BinnedIntDistr("RichClubConnectivity");
		BinnedIntDistr edges = new BinnedIntDistr("edgeCount", 1,
				this.edgeCount, 1);
		BinnedIntDistr nodes = new BinnedIntDistr("nodeCount", 1,
				this.nodeCount, 1);
		BinnedIntDistr size = new BinnedIntDistr("size", 1, this.size, 1);
		return new Distr<?, ?>[] { rcc, edges, nodes, size };
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
		this.edgeCount = new long[rcs.clubs.size()];
		this.nodeCount = new long[rcs.clubs.size()];
		this.size = new long[rcs.clubs.size()];

		for (DegreeRichClub rc : rcs.clubs.values()) {
			this.edgeCount[rc.degree] = rc.edgeCount;
			this.nodeCount[rc.degree] = rc.nodeCount;
			this.size[rc.degree] = rc.size();
		}

	}

}
