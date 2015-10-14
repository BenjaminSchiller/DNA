package dna.depr.metrics.apsp;

import dna.depr.metrics.MetricOld;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.distr.IntDistr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public abstract class AllPairsShortestPaths extends MetricOld {

	protected IntDistr apsp;

	public AllPairsShortestPaths(String name, ApplicationType type,
			Metric.MetricType metricType, Parameter... p) {
		super(name, type, metricType, p);
	}

	@Override
	public void init_() {
		this.apsp = new IntDistr("APSP");
	}

	@Override
	public void reset_() {
		this.apsp = new IntDistr("APSP");
	}

	@Override
	public Value[] getValues() {
		this.apsp.truncate();

		Value v1 = new Value("existingPaths", this.apsp.getDenominator());
		Value v2 = new Value("possiblePaths", this.g.getNodeCount()
				* (this.g.getNodeCount() - 1));
		Value v3 = new Value("characteristicPathLength",
				this.apsp.computeAverage());
		Value v4 = new Value("diameter", this.apsp.getMaxNonZeroIndex());

		return new Value[] { v1, v2, v3, v4 };
	}

	@Override
	public Distr<?>[] getDistributions() {
		return new Distr<?>[] { this.apsp };
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
		return this.isComparableTo(m)
				&& ArrayUtils.equals(this.apsp.getValues(),
						((AllPairsShortestPaths) m).apsp.getValues(), "APSP");
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof AllPairsShortestPaths;
	}

}
