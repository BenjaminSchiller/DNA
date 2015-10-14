package dna.metrics.parallelization.collation;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.metrics.parallelization.partitioning.Partition;
import dna.series.data.Value;
import dna.series.data.distr2.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.Timer;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class Collation<T extends Metric> extends ParameterList {
	protected T metric;

	protected Timer t;

	public Timer getTimer() {
		return this.t;
	}

	public Collation(String name, Parameter... parameters) {
		super(name, parameters);
		this.metric = null;
		this.t = new Timer();
	}

	@SuppressWarnings("unchecked")
	public void setMetric(Metric metric) {
		this.metric = (T) metric;
	}

	public T getMetric() {
		return this.metric;
	}

	public Value[] getValues() {
		Value[] results = this.metric.getValues();
		Value[] values = new Value[results.length + 1];
		values[0] = new Value("collationRuntime",
				(double) this.t.getDutation() / 1000000.0);
		System.arraycopy(results, 0, values, 1, results.length);
		return values;
	}

	public Distr<?, ?>[] getDistributions() {
		return this.metric.getDistributions();
	}

	public NodeValueList[] getNodeValueLists() {
		return this.metric.getNodeValueLists();
	}

	public NodeNodeValueList[] getNodeNodeValueLists() {
		return this.metric.getNodeNodeValueLists();
	}

	public abstract boolean collate(Graph g, Partition[] partitions);

	public abstract boolean isCollatable(Metric m);

	public abstract Collation<T> clone();
}
