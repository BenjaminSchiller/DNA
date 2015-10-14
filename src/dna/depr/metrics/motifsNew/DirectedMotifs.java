package dna.depr.metrics.motifsNew;

import java.util.HashSet;

import dna.depr.metrics.MetricOld;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr2.BinnedIntDistr;
import dna.series.data.distr2.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

/**
 * 
 * base class of metrics for counting the occurrence of directed 3-node motifs.
 * the metric returns a distribution containing the occurrences of each motif as
 * well as values for the occurrence of each motif (including a total motif
 * count).
 * 
 * @author benni
 * 
 */
public abstract class DirectedMotifs extends MetricOld {
	public static enum DirectedMotifType {
		DM01, DM02, DM03, DM04, DM05, DM06, DM07, DM08, DM09, DM10, DM11, DM12, DM13
	}

	protected BinnedIntDistr motifs;

	public static final String motifsName = "directedMotifs";

	protected GraphDataStructure gds;

	public DirectedMotifs(String name, ApplicationType type,
			Metric.MetricType metricType) {
		super(name, type, metricType);
	}

	@Override
	public boolean compute() {
		gds = g.getGraphDatastructures();
		for (IElement element : this.g.getNodes()) {
			DirectedNode a = (DirectedNode) element;
			HashSet<DirectedNode> a_ = this.getConnectedNodes(a);
			for (DirectedNode b : a_) {
				boolean ab = a.hasEdge(a, b);
				boolean ba = a.hasEdge(b, a);

				for (DirectedNode c : a_) {
					boolean ac = a.hasEdge(a, c);
					boolean ca = a.hasEdge(c, a);
					boolean bc = b.hasEdge(b, c);
					boolean cb = b.hasEdge(c, b);

					if (!bc && !cb) {
						if (b.getIndex() < c.getIndex()) {
							this.incr(this.getType(ab, ba, ac, ca));
						}
					} else {
						if (a.getIndex() < b.getIndex()
								&& b.getIndex() < c.getIndex()) {
							this.incr(this.getType(ab, ba, ac, ca, bc, cb));
						}
					}
				}
			}
		}
		return true;
	}

	protected DirectedMotifType getType(boolean ab, boolean ba, boolean ac,
			boolean ca) {
		if (ab && ba && ac && ca) {
			return DirectedMotifType.DM11;
		} else if (!ab && ba && ac && ca) {
			return DirectedMotifType.DM06;
		} else if (ab && !ba && ac && ca) {
			return DirectedMotifType.DM05;
		} else if (ab && ba && !ac && ca) {
			return DirectedMotifType.DM06;
		} else if (ab && ba && ac && !ca) {
			return DirectedMotifType.DM05;
		} else if (!ab && ba && !ac && ca) {
			return DirectedMotifType.DM02;
		} else if (!ab && ba && ac && !ca) {
			return DirectedMotifType.DM03;
		} else if (ab && !ba && !ac && ca) {
			return DirectedMotifType.DM03;
		} else if (ab && !ba && ac && !ca) {
			return DirectedMotifType.DM01;
		}
		return null;
	}

	protected DirectedMotifType getType(boolean ab, boolean ba, boolean ac,
			boolean ca, boolean bc, boolean cb) {
		// 1
		if (!ab && !ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM01;
		}
		if (!ab && !ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM01;
		}

		// 2
		if (ab && !ac && !ba && !bc && !ca && cb) {
			return DirectedMotifType.DM02;
		}
		if (!ab && ac && !ba && bc && !ca && !cb) {
			return DirectedMotifType.DM02;
		}

		// 3
		if (ab && !ac && !ba && bc && !ca && !cb) {
			return DirectedMotifType.DM03;
		}
		if (!ab && ac && !ba && !bc && !ca && cb) {
			return DirectedMotifType.DM03;
		}
		if (!ab && !ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM03;
		}
		if (!ab && !ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM03;
		}

		// 4
		if (ab && ac && !ba && bc && !ca && !cb) {
			return DirectedMotifType.DM04;
		}
		if (ab && ac && !ba && !bc && !ca && cb) {
			return DirectedMotifType.DM04;
		}
		if (!ab && ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM04;
		}
		if (!ab && !ac && ba && bc && ca && !cb) {
			return DirectedMotifType.DM04;
		}
		if (ab && !ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM04;
		}
		if (!ab && !ac && ba && !bc && ca && cb) {
			return DirectedMotifType.DM04;
		}

		// 5
		if (ab && !ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM05;
		}
		if (!ab && !ac && ba && bc && !ca && cb) {
			return DirectedMotifType.DM05;
		}
		if (!ab && ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM05;
		}
		if (!ab && !ac && !ba && bc && ca && cb) {
			return DirectedMotifType.DM05;
		}

		// 6
		if (ab && !ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM06;
		}
		if (!ab && ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM06;
		}
		if (ab && !ac && !ba && bc && !ca && cb) {
			return DirectedMotifType.DM06;
		}
		if (!ab && ac && !ba && bc && !ca && cb) {
			return DirectedMotifType.DM06;
		}

		// 7
		if (ab && !ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM07;
		}
		if (!ab && ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM07;
		}

		// 8
		if (ab && ac && !ba && bc && !ca && cb) {
			return DirectedMotifType.DM08;
		}
		if (!ab && ac && ba && bc && ca && !cb) {
			return DirectedMotifType.DM08;
		}
		if (ab && !ac && ba && !bc && ca && cb) {
			return DirectedMotifType.DM08;
		}

		// 9
		if (!ab && !ac && ba && bc && ca && cb) {
			return DirectedMotifType.DM09;
		}
		if (ab && ac && !ba && !bc && ca && cb) {
			return DirectedMotifType.DM09;
		}
		if (ab && ac && ba && bc && !ca && !cb) {
			return DirectedMotifType.DM09;
		}

		// 10
		if (ab && !ac && !ba && bc && ca && cb) {
			return DirectedMotifType.DM10;
		}
		if (!ab && ac && ba && bc && !ca && cb) {
			return DirectedMotifType.DM10;
		}
		if (!ab && ac && ba && !bc && ca && cb) {
			return DirectedMotifType.DM10;
		}
		if (ab && ac && !ba && bc && ca && !cb) {
			return DirectedMotifType.DM10;
		}
		if (ab && !ac && ba && bc && ca && !cb) {
			return DirectedMotifType.DM10;
		}
		if (ab && ac && ba && !bc && !ca && cb) {
			return DirectedMotifType.DM10;
		}

		// 11
		if (!ab && ac && !ba && bc && ca && cb) {
			return DirectedMotifType.DM11;
		}
		if (ab && !ac && ba && bc && !ca && cb) {
			return DirectedMotifType.DM11;
		}

		int sum = (ab ? 1 : 0) + (ac ? 1 : 0) + (ba ? 1 : 0) + (bc ? 1 : 0)
				+ (ca ? 1 : 0) + (cb ? 1 : 0);

		// 12
		if (sum == 5) {
			return DirectedMotifType.DM12;
		}
		// 13
		if (sum == 6) {
			return DirectedMotifType.DM13;
		}

		// 1
		if (ab && ac && !ba && !bc && !ca && !cb) {
			return DirectedMotifType.DM01;
		}
		// 2
		if (!ab && !ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM02;
		}
		// 3
		if (!ab && ac && ba && !bc && !ca && !cb) {
			return DirectedMotifType.DM03;
		}
		if (ab && !ac && !ba && !bc && ca && !cb) {
			return DirectedMotifType.DM03;
		}
		// 5
		if (ab && ac && ba && !bc && !ca && !cb) {
			return DirectedMotifType.DM05;
		}
		if (ab && ac && !ba && !bc && ca && !cb) {
			return DirectedMotifType.DM05;
		}
		// 6
		if (ab && !ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM06;
		}
		if (!ab && ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM06;
		}
		// 11
		if (ab && ac && ba && !bc && ca && !cb) {
			return DirectedMotifType.DM11;
		}

		return null;

	}

	protected void incr(DirectedMotifType type) {
		this.motifs.incr(DirectedMotifs.getIndex(type));
	}

	protected void decr(DirectedMotifType type) {
		this.motifs.decr(DirectedMotifs.getIndex(type));
	}

	protected HashSet<DirectedNode> getConnectedNodes(DirectedNode node) {
		HashSet<DirectedNode> nodes = new HashSet<DirectedNode>(
				node.getInDegree() + node.getOutDegree());
		for (IElement in : node.getIncomingEdges()) {
			nodes.add(((DirectedEdge) in).getSrc());
		}
		for (IElement out : node.getOutgoingEdges()) {
			nodes.add(((DirectedEdge) out).getDst());
		}
		return nodes;
	}

	@Override
	public void init_() {
		this.motifs = new BinnedIntDistr(motifsName, 1, new long[14], 0);
	}

	@Override
	public void reset_() {
		this.motifs = null;
	}

	@Override
	public Value[] getValues() {
		Value m0 = new Value("TOTAL", this.motifs.getDenominator());
		Value m1 = new Value("DM01", (double) this.motifs.getValues()[1]
				/ (double) this.motifs.getDenominator());
		Value m2 = new Value("DM02", (double) this.motifs.getValues()[2]
				/ (double) this.motifs.getDenominator());
		Value m3 = new Value("DM03", (double) this.motifs.getValues()[3]
				/ (double) this.motifs.getDenominator());
		Value m4 = new Value("DM04", (double) this.motifs.getValues()[4]
				/ (double) this.motifs.getDenominator());
		Value m5 = new Value("DM05", (double) this.motifs.getValues()[5]
				/ (double) this.motifs.getDenominator());
		Value m6 = new Value("DM06", (double) this.motifs.getValues()[6]
				/ (double) this.motifs.getDenominator());
		Value m7 = new Value("DM07", (double) this.motifs.getValues()[7]
				/ (double) this.motifs.getDenominator());
		Value m8 = new Value("DM08", (double) this.motifs.getValues()[8]
				/ (double) this.motifs.getDenominator());
		Value m9 = new Value("DM09", (double) this.motifs.getValues()[9]
				/ (double) this.motifs.getDenominator());
		Value m10 = new Value("DM10", (double) this.motifs.getValues()[10]
				/ (double) this.motifs.getDenominator());
		Value m11 = new Value("DM11", (double) this.motifs.getValues()[11]
				/ (double) this.motifs.getDenominator());
		Value m12 = new Value("DM12", (double) this.motifs.getValues()[12]
				/ (double) this.motifs.getDenominator());
		Value m13 = new Value("DM13", (double) this.motifs.getValues()[13]
				/ (double) this.motifs.getDenominator());
		return new Value[] { m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11,
				m12, m13 };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] { this.motifs };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean equals(MetricOld m) {
		if (m == null || !(m instanceof DirectedMotifs)) {
			return false;
		}
		DirectedMotifs dm = (DirectedMotifs) m;
		boolean success = true;
		success &= ArrayUtils.equals(this.motifs.getValues(),
				dm.motifs.getValues(), "DM/" + motifsName);
		return success;
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
	public boolean isComparableTo(MetricOld m) {
		return m instanceof DirectedMotifs;
	}

	public static int getIndex(DirectedMotifType type) {
		switch (type) {
		case DM01:
			return 1;
		case DM02:
			return 2;
		case DM03:
			return 3;
		case DM04:
			return 4;
		case DM05:
			return 5;
		case DM06:
			return 6;
		case DM07:
			return 7;
		case DM08:
			return 8;
		case DM09:
			return 9;
		case DM10:
			return 10;
		case DM11:
			return 11;
		case DM12:
			return 12;
		case DM13:
			return 13;
		default:
			return 0;

		}
	}

	public static int getEdgeCount(DirectedMotifType type) {
		switch (type) {
		case DM01:
			return 2;
		case DM02:
			return 2;
		case DM03:
			return 2;
		case DM04:
			return 3;
		case DM05:
			return 3;
		case DM06:
			return 3;
		case DM07:
			return 3;
		case DM08:
			return 4;
		case DM09:
			return 4;
		case DM10:
			return 4;
		case DM11:
			return 4;
		case DM12:
			return 5;
		case DM13:
			return 6;
		default:
			return 0;

		}
	}

}
