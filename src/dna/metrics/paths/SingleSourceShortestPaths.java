package dna.metrics.paths;

import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr2.BinnedIntDistr;
import dna.series.data.distr2.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public abstract class SingleSourceShortestPaths extends Metric {

	protected int sourceIndex;

	protected BinnedIntDistr sssp;

	public SingleSourceShortestPaths(String name, int sourceIndex) {
		super(name, new Parameter[] { new IntParameter("SourceIndex",
				sourceIndex) });
		this.sourceIndex = sourceIndex;
	}

	@Override
	public Value[] getValues() {
		this.sssp.truncate();

		Value v1 = new Value("existingPaths", this.sssp.getDenominator());
		Value v2 = new Value("possiblePaths", this.g.getNodeCount() - 1);
		Value v3 = new Value("characteristicPathLength",
				this.sssp.computeAverage());
		Value v4 = new Value("diameter", this.sssp.getMaxNonZeroIndex());

		return new Value[] { v1, v2, v3, v4 };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] { this.sssp };
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
	public boolean equals(IMetric m) {
		return this.isComparableTo(m)
				&& ArrayUtils.equals(this.sssp.getValues(),
						((SingleSourceShortestPaths) m).sssp.getValues(),
						"SSSP");
	}

}
