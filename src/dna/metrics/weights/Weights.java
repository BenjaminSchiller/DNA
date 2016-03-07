package dna.metrics.weights;

import dna.graph.weights.Weight;
import dna.graph.weights.doubleW.DoubleWeight;
import dna.graph.weights.intW.IntWeight;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedDoubleDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.parameters.DoubleParameter;

public abstract class Weights extends Metric {

	protected double binSize;

	protected BinnedDoubleDistr distr;

	public Weights(String name, double binSize) {
		super(name, MetricType.exact, new DoubleParameter("BinSize", binSize));
		this.binSize = binSize;
	}

	@Override
	public Value[] getValues() {
		Value avg = new Value("AverageWeight", this.binSize
				* this.distr.computeAverage());
		Value max = new Value("MaxWeight", this.binSize
				* this.distr.getMaxNonZeroIndex());
		Value min = new Value("MinWeight", this.binSize
				* this.distr.getMinNonZeroIndex());
		return new Value[] { avg, max, min };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] { this.distr };
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
		if (m == null || !(m instanceof Weights)) {
			return false;
		}
		return this.distr.equalsVerbose(((Weights) m).distr);
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
