package dna.metrics.apsp.allPairShortestPath;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public abstract class AllPairShortestPath extends Metric {

	protected DistributionInt apsp;

	public AllPairShortestPath(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		this.apsp = new DistributionInt("ShortestPathDist");
	}

	@Override
	public void reset_() {
		this.apsp = new DistributionInt("ShortestPathDist");
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
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		this.apsp.truncate();
		return new Distribution[] { this.apsp };
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof AllPairShortestPath)) {
			return false;
		}
		return ArrayUtils.equals(this.apsp.getIntValues(),
				((AllPairShortestPath) m).apsp.getIntValues(), "APSP");
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof AllPairShortestPath;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

}
