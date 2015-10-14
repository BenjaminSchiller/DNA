package dna.depr.metrics.weights;

import dna.depr.metrics.MetricOld;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;
import dna.metrics.IMetric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;
import dna.util.parameters.DoubleParameter;

public abstract class Weights extends MetricOld {

	protected double binSize;

	protected BinnedDoubleDistr distr;

	public Weights(String name, ApplicationType type,
			IMetric.MetricType metricType, double binSize) {
		super(name, type, metricType, new DoubleParameter("BinSize", binSize));
		// super(name, type, metricType);
		this.binSize = binSize;
	}

	@Override
	public void init_() {
		this.distr = new BinnedDoubleDistr("WeightsDistribution", this.binSize);
	}

	@Override
	public void reset_() {
		this.distr = new BinnedDoubleDistr("WeightsDistribution", this.binSize);
	}

	@Override
	public Value[] getValues() {
		Value avg = new Value("AverageWeight", this.distr.computeAverage());
		Value max = new Value("MaxWeight", this.distr.getMaxNonZeroIndex());
		Value min = new Value("MinWeight", this.distr.getMinNonZeroIndex());
		return new Value[] { avg, max, min };
	}

	@Override
	public Distr<?>[] getDistributions() {
		return new Distr<?>[] { this.distr };
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
		if (m == null || !(m instanceof Weights)) {
			return false;
		}
		return ArrayUtils.equals(this.distr.getValues(),
				((Weights) m).distr.getValues(), "Weights.Distrbution");
	}

	protected double getWeight(Weight w) {
		if (w instanceof IntWeight) {
			return (double) ((IntWeight) w).getWeight();
		} else if (w instanceof DoubleWeight) {
			return ((DoubleWeight) w).getWeight();
		} else {
			return Double.NaN;
		}
	}
}
