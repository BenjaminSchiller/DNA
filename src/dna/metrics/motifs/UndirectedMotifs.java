package dna.metrics.motifs;

import dna.graph.Graph;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class UndirectedMotifs extends Metric {
	
	public static enum UndirectedMotifType {
		PRE1, PRE2, PRE3, UM1, UM2, UM3, UM4, UM5, UM6
	};

	protected DistributionInt motifs;

	public static final String motifsName = "undirectedMotifs";

	public UndirectedMotifs(String name, ApplicationType type,
			MetricType metricType) {
		super(name, type, metricType);
	}

	@Override
	public boolean compute() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void init_() {
		this.motifs = new DistributionInt(motifsName, new int[11], 0);
	}

	@Override
	public void reset_() {
		this.motifs = null;
	}

	@Override
	public Value[] getValues() {
		Value m1 = new Value("um1", this.motifs.getIntValues()[1]);
		Value m2 = new Value("um2", this.motifs.getIntValues()[2]);
		Value m3 = new Value("um3", this.motifs.getIntValues()[3]);
		Value m4 = new Value("um4", this.motifs.getIntValues()[4]);
		Value m5 = new Value("um5", this.motifs.getIntValues()[5]);
		Value m6 = new Value("um6", this.motifs.getIntValues()[6]);
		Value m7 = new Value("pre1", this.motifs.getIntValues()[8]);
		Value m8 = new Value("pre2", this.motifs.getIntValues()[9]);
		Value m9 = new Value("pre3", this.motifs.getIntValues()[10]);
		return new Value[] { m1, m2, m3, m4, m5, m6, m7, m8, m9 };
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
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof UndirectedMotifs)) {
			return false;
		}
		UndirectedMotifs um = (UndirectedMotifs) m;
		boolean success = true;
		success &= ArrayUtils.equals(this.motifs.getIntValues(),
				um.motifs.getIntValues(), "UM/" + motifsName);
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
		case PRE1:
			return 8;
		case PRE2:
			return 9;
		case PRE3:
			return 10;
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
