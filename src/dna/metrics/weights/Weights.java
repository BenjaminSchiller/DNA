package dna.metrics.weights;

import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distributions.BinnedDistributionDouble;
import dna.series.data.distributions.Distribution;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;
import dna.util.parameters.DoubleParameter;

public abstract class Weights extends Metric {

	protected double binSize;

	protected BinnedDistributionDouble distr;

	public Weights(String name, double binSize) {
		super(name, new DoubleParameter("BinSize", binSize));
		this.binSize = binSize;
	}

	@Override
	public Value[] getValues() {
		Value avg = new Value("AverageWeight", this.binSize
				* this.distr.computeAverage());
		Value max = new Value("MaxWeight", this.binSize * this.distr.getMax());
		Value min = new Value("MinWeight", this.binSize * this.distr.getMin());
		return new Value[] { avg, max, min };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] { this.distr };
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
		return ArrayUtils.equals(this.distr.getValues(),
				((Weights) m).distr.getValues(), "Weights.Distribution");
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
