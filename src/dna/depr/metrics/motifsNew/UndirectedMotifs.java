package dna.depr.metrics.motifsNew;

import java.util.ArrayList;
import java.util.Arrays;

import dna.depr.metrics.Metric;
import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metricsNew.MetricNew;
import dna.series.data.Distribution;
import dna.series.data.DistributionLong;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class UndirectedMotifs extends Metric {

	public static enum UndirectedMotifType {
		UM1, UM2, UM3, UM4, UM5, UM6
	};

	protected DistributionLong motifs;

	public static final String motifsName = "undirectedMotifs";
	
	GraphDataStructure gds;

	public UndirectedMotifs(String name, ApplicationType type,
			MetricNew.MetricType metricType) {
		super(name, type, metricType);
	}

	@Override
	public boolean compute() {
		gds = g.getGraphDatastructures();
		for (IElement a_ : this.g.getNodes()) {
			UndirectedNode a = (UndirectedNode) a_;

			// 1, 3
			for (IElement b_ : a.getEdges()) {
				UndirectedNode b = (UndirectedNode) ((UndirectedEdge) b_).getDifferingNode(a);
				for (IElement c_ : a.getEdges()) {
					UndirectedNode c = (UndirectedNode) ((UndirectedEdge) c_)
							.getDifferingNode(a);
					if (b.getIndex() == c.getIndex()) {
						continue;
					}
					if (b.hasEdge(b, c)) {
						continue;
					}
					for (IElement d_ : b.getEdges()) {
						UndirectedNode d = (UndirectedNode) ((UndirectedEdge) d_)
								.getDifferingNode(b);
						if (d.hasEdge(a, d)) {
							continue;
						}
						if (!d.hasEdge(c, d)) {
							if (a.getIndex() < b.getIndex()) {
								this.incr(UndirectedMotifType.UM1);
							}
						} else if (b.getIndex() < c.getIndex()) {
							if (a.getIndex() < b.getIndex()
									&& a.getIndex() < c.getIndex()
									&& a.getIndex() < d.getIndex()) {
								this.incr(UndirectedMotifType.UM3);
							}
						}

					}
				}
			}

			// 2, 4, 5, 6
			UndirectedNode[] neighbors = this.getNeighborsSorted(a);
			for (int i = 0; i < neighbors.length; i++) {
				UndirectedNode b = neighbors[i];
				for (int j = i + 1; j < neighbors.length; j++) {
					UndirectedNode c = neighbors[j];
					boolean bc = c.hasEdge(b, c);
					for (int k = j + 1; k < neighbors.length; k++) {
						UndirectedNode d = neighbors[k];
						boolean bd = d.hasEdge(b, d);
						boolean cd = d.hasEdge(c, d);

						int sum = (bc ? 1 : 0) + (bd ? 1 : 0) + (cd ? 1 : 0);

						if (sum == 0) {
							this.incr(UndirectedMotifType.UM2);
						} else if (sum == 1) {
							this.incr(UndirectedMotifType.UM4);
						} else if (sum == 2) {
							if (bc && bd && a.getIndex() < b.getIndex()) {
								this.incr(UndirectedMotifType.UM5);
							}
							if (bc && cd && a.getIndex() < c.getIndex()) {
								this.incr(UndirectedMotifType.UM5);
							}
							if (bd && cd && a.getIndex() < d.getIndex()) {
								this.incr(UndirectedMotifType.UM5);
							}
						} else if (sum == 3) {
							if (a.getIndex() < b.getIndex()
									&& a.getIndex() < c.getIndex()
									&& a.getIndex() < d.getIndex()) {
								this.incr(UndirectedMotifType.UM6);
							}
						}
					}
				}
			}
		}

		return true;
	}

	private UndirectedNode[] getNeighborsSorted(UndirectedNode n) {
		ArrayList<UndirectedNode> unsorted = new ArrayList<UndirectedNode>(
				n.getDegree());
		for (IElement e : n.getEdges()) {
			UndirectedNode neighbor = (UndirectedNode) ((UndirectedEdge) e).getDifferingNode(n);
			// if (neighbor.getIndex() > n.getIndex()) {
			unsorted.add(neighbor);
			// }
		}
		UndirectedNode[] sorted = unsorted.toArray(new UndirectedNode[unsorted
				.size()]);
		Arrays.sort(sorted);
		return sorted;
	}

	protected UndirectedMotifType getType(boolean ab, boolean ac, boolean ad,
			boolean bc, boolean bd, boolean cd) {

		return null;
	}

	protected UndirectedMotifType getType(boolean bc, boolean bd, boolean cd) {
		int sum = (bc ? 1 : 0) + (bd ? 1 : 0) + (cd ? 1 : 0);

		if (sum == 0) {
			return UndirectedMotifType.UM2;
		} else if (sum == 1) {
			return UndirectedMotifType.UM4;
		} else if (sum == 2) {
			return UndirectedMotifType.UM5;
		} else if (sum == 3) {
			return UndirectedMotifType.UM6;
		}

		return null;
	}

	protected void incr(UndirectedMotifType type) {
		this.motifs.incr(UndirectedMotifs.getIndex(type));
	}

	@Override
	public void init_() {
		this.motifs = new DistributionLong(motifsName, new long[7], 0);
	}

	@Override
	public void reset_() {
		this.motifs = null;
	}

	@Override
	public Value[] getValues() {
		Value m0 = new Value("TOTAL", this.motifs.getDenominator());
		Value m1 = new Value("UM1", (double) this.motifs.getLongValues()[1]
				/ (double) this.motifs.getDenominator());
		Value m2 = new Value("UM2", (double) this.motifs.getLongValues()[2]
				/ (double) this.motifs.getDenominator());
		Value m3 = new Value("UM3", (double) this.motifs.getLongValues()[3]
				/ (double) this.motifs.getDenominator());
		Value m4 = new Value("UM4", (double) this.motifs.getLongValues()[4]
				/ (double) this.motifs.getDenominator());
		Value m5 = new Value("UM5", (double) this.motifs.getLongValues()[5]
				/ (double) this.motifs.getDenominator());
		Value m6 = new Value("UM6", (double) this.motifs.getLongValues()[6]
				/ (double) this.motifs.getDenominator());
		return new Value[] { m0, m1, m2, m3, m4, m5, m6 };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] { this.motifs };
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
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof UndirectedMotifs)) {
			return false;
		}
		UndirectedMotifs um = (UndirectedMotifs) m;
		boolean success = true;
		success &= ArrayUtils.equals(this.motifs.getLongValues(),
				um.motifs.getLongValues(), "UM/" + motifsName);
		return success;
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
		return m instanceof UndirectedMotifs;
	}

	public static int getIndex(UndirectedMotifType type) {
		switch (type) {
		case UM1:
			return 1;
		case UM2:
			return 2;
		case UM3:
			return 3;
		case UM4:
			return 4;
		case UM5:
			return 5;
		case UM6:
			return 6;
		default:
			return 0;
		}
	}

}
