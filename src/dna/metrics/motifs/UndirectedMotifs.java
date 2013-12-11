package dna.metrics.motifs;

import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class UndirectedMotifs extends Metric {

	protected DistributionInt motifs;

	public static final String motifsName = "undirectedMotifs";

	public UndirectedMotifs(String name, ApplicationType type,
			MetricType metricType) {
		super(name, type, metricType);
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
		Value m1 = new Value("m1", this.motifs.getIntValues()[1]);
		Value m2 = new Value("m2", this.motifs.getIntValues()[2]);
		Value m3 = new Value("m3", this.motifs.getIntValues()[3]);
		Value m4 = new Value("m4", this.motifs.getIntValues()[4]);
		Value m5 = new Value("m5", this.motifs.getIntValues()[5]);
		Value m6 = new Value("m6", this.motifs.getIntValues()[6]);
		return new Value[] { m1, m2, m3, m4, m5, m6 };
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
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m instanceof UndirectedMotifs;
	}

}
