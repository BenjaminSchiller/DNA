package dna.metrics.motifs.directedMotifs;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.metrics.motifs.directedMotifs.DirectedMotif.DirectedMotifType;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class DirectedMotifs extends Metric {

	protected DistributionInt motifs;

	public static final String motifsName = "directedMotifs";

	public DirectedMotifs(String name, ApplicationType type,
			MetricType metricType) {
		super(name, type, metricType);
	}

	@Override
	public void init_() {
		this.motifs = new DistributionInt(motifsName, new int[14], 0);
	}

	@Override
	public void reset_() {
		this.motifs = null;
	}

	@Override
	public Value[] getValues() {
		Value m0 = new Value("TOTAL", this.motifs.getDenominator());
		Value m1 = new Value("DM01", this.motifs.getIntValues()[1]);
		Value m2 = new Value("DM02", this.motifs.getIntValues()[2]);
		Value m3 = new Value("DM03", this.motifs.getIntValues()[3]);
		Value m4 = new Value("DM04", this.motifs.getIntValues()[4]);
		Value m5 = new Value("DM05", this.motifs.getIntValues()[5]);
		Value m6 = new Value("DM06", this.motifs.getIntValues()[6]);
		Value m7 = new Value("DM07", this.motifs.getIntValues()[7]);
		Value m8 = new Value("DM08", this.motifs.getIntValues()[8]);
		Value m9 = new Value("DM09", this.motifs.getIntValues()[9]);
		Value m10 = new Value("DM10", this.motifs.getIntValues()[10]);
		Value m11 = new Value("DM11", this.motifs.getIntValues()[11]);
		Value m12 = new Value("DM12", this.motifs.getIntValues()[12]);
		Value m13 = new Value("DM13", this.motifs.getIntValues()[13]);
		return new Value[] { m0, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11,
				m12, m13 };
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
		if (m == null || !(m instanceof DirectedMotifs)) {
			return false;
		}
		DirectedMotifs dm = (DirectedMotifs) m;
		boolean success = true;
		success &= ArrayUtils.equals(this.motifs.getIntValues(),
				dm.motifs.getIntValues(), "DM/" + motifsName);
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
	public boolean isComparableTo(Metric m) {
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
