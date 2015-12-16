package dna.metrics.clustering.local;

import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.ArrayUtils;
import dna.util.parameters.Parameter;
import dna.util.parameters.StringParameter;

public abstract class LocalClusteringCoefficient extends Metric {
	protected int[] indexes;

	protected long[] open;
	protected long[] closed;

	public LocalClusteringCoefficient(String name, int... indexes) {
		super(name, MetricType.exact, new Parameter[] { new StringParameter(
				"indexes", asString(indexes)) });
		this.indexes = indexes;
		this.open = new long[indexes.length];
		this.closed = new long[indexes.length];
	}

	protected static String asString(int[] indexes) {
		StringBuffer buff = new StringBuffer();
		for (int index : indexes) {
			if (buff.length() == 0) {
				buff.append(index);
			} else {
				buff.append("_" + index);
			}
		}
		return buff.toString();
	}

	@Override
	public Value[] getValues() {
		Value[] values = new Value[this.indexes.length];
		for (int i = 0; i < values.length; i++) {
			double v = 0;
			if (this.open[i] > 0) {
				v = (double) this.closed[i] / (double) this.open[i];
			}
			values[i] = new Value("LCC" + this.indexes[i], v);
		}
		return values;
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr[0];
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[0];
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof LocalClusteringCoefficient)) {
			return false;
		}
		boolean success = true;
		success &= ArrayUtils.equals(this.open,
				((LocalClusteringCoefficient) m).open, "open");
		success &= ArrayUtils.equals(this.closed,
				((LocalClusteringCoefficient) m).closed, "closed");
		return success;
	}

}
