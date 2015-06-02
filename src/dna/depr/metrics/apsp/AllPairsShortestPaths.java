package dna.depr.metrics.apsp;

import dna.depr.metrics.MetricOld;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionLong;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;

public abstract class AllPairsShortestPaths extends MetricOld {

	protected DistributionLong apsp;

	public AllPairsShortestPaths(String name, ApplicationType type,
			Metric.MetricType metricType, Parameter... p) {
		super(name, type, metricType, p);
	}

	@Override
	public void init_() {
		this.apsp = new DistributionLong("APSP");
	}

	@Override
	public void reset_() {
		this.apsp = new DistributionLong("APSP");
	}

	@Override
	public Value[] getValues() {
		this.apsp.truncate();

		Value v1 = new Value("existingPaths", this.apsp.getDenominator());
		Value v2 = new Value("possiblePaths", this.g.getNodeCount()
				* (this.g.getNodeCount() - 1));
		Value v3 = new Value("characteristicPathLength",
				this.apsp.computeAverage());
		Value v4 = new Value("diameter", this.apsp.getMax());

		return new Value[] { v1, v2, v3, v4 };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] { this.apsp };
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
						((AllPairsShortestPaths) m).apsp.getValues(),
						"APSP");
	}

	@Override
	public boolean isComparableTo(MetricOld m) {
		return m != null && m instanceof AllPairsShortestPaths;
	}

}
