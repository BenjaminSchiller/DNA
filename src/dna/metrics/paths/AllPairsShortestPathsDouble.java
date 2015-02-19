package dna.metrics.paths;

import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.BinnedDistributionDouble;
import dna.series.data.Distribution;
import dna.series.data.DistributionLong;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.util.ArrayUtils;

/**
 * works nearly similar to AllPairsShortestPathsDouble except it
 * uses BinnedDistributionDouble instead of DistributionLong
 * 
 * maybe this can be combined with the parent class distribution
 * @author barracuda317 (Maurice Wendt)
 * @date 25.10.2014
 */
public abstract class AllPairsShortestPathsDouble extends Metric {

	// TODO INIT!!!
	// this.apsp = new DistributionLong("APSP");
	double binsize;

	protected BinnedDistributionDouble apsp;

	public AllPairsShortestPathsDouble(String name) {
		super(name);
	}
	public AllPairsShortestPathsDouble(String name,double binsize) {
		super(name);
		this.binsize = binsize;
	}

	@Override
	public Value[] getValues() {
		this.apsp.truncate();

		Value v1 = new Value("existingPaths", this.apsp.getDenominator());
		Value v2 = new Value("possiblePaths", this.g.getNodeCount()
				* (this.g.getNodeCount() - 1));
		Value v3 = new Value("characteristicPathLength",
				this.apsp.computeAverage()*binsize);
		Value v4 = new Value("diameter", this.apsp.getMax()*binsize);

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
	public boolean equals(IMetric m) {
		return this.isComparableTo(m)
				&& ArrayUtils.equals(this.apsp.getDoubleValues(),
						((AllPairsShortestPathsDouble) m).apsp.getDoubleValues(),
						"APSP");
	}

}
