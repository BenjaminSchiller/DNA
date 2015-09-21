package dna.metrics.parallelization.collation;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.metrics.parallelization.partitioning.Partition;
import dna.series.data.Value;
import dna.series.data.distributions.Distribution;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class Collation<T extends Metric> extends ParameterList {
	protected T metric;

	public Collation(String name, Parameter... parameters) {
		super(name, parameters);
		this.metric = null;
	}

	@SuppressWarnings("unchecked")
	public void setMetric(Metric metric) {
		this.metric = (T) metric;
	}

	public T getMetric() {
		return this.metric;
	}

	public Value[] getValues() {
		return this.metric.getValues();
	}

	public Distribution[] getDistributions() {
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
