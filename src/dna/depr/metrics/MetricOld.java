package dna.depr.metrics;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.series.data.MetricData;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class MetricOld extends ParameterList {

	public static enum ApplicationType {
		BeforeBatch, AfterBatch, BeforeAndAfterBatch, BeforeUpdate, AfterUpdate, BeforeAndAfterUpdate, BatchAndUpdates, Recomputation
	}

	public boolean isAppliedBeforeBatch() {
		return this.type == ApplicationType.BeforeBatch
				|| this.type == ApplicationType.BeforeAndAfterBatch
				|| this.type == ApplicationType.BatchAndUpdates;
	}

	public boolean isAppliedAfterBatch() {
		return this.type == ApplicationType.AfterBatch
				|| this.type == ApplicationType.BeforeAndAfterBatch
				|| this.type == ApplicationType.BatchAndUpdates;
	}

	public boolean isAppliedBeforeUpdate() {
		return this.type == ApplicationType.BeforeUpdate
				|| this.type == ApplicationType.BeforeAndAfterUpdate
				|| this.type == ApplicationType.BatchAndUpdates;
	}

	public boolean isAppliedAfterUpdate() {
		return this.type == ApplicationType.AfterUpdate
				|| this.type == ApplicationType.BeforeAndAfterUpdate
				|| this.type == ApplicationType.BatchAndUpdates;
	}

	public boolean isRecomputed() {
		return this.type == ApplicationType.Recomputation;
	}

	public MetricOld(String name, ApplicationType type,
			Metric.MetricType metricType, Parameter... p) {
		super(name, p);
		this.type = type;
		this.metricType = metricType;
		this.timestamp = Long.MIN_VALUE;
	}

	public MetricOld(String name, ApplicationType type,
			Metric.MetricType metricType, Parameter[] params, Parameter... p) {
		super(name, combine(params, p));
		this.type = type;
		this.metricType = metricType;
		this.timestamp = Long.MIN_VALUE;
	}

	protected static Parameter[] combine(Parameter[] p1, Parameter[] p2) {
		if (p2.length == 0) {
			return p1;
		} else if (p1.length == 0) {
			return p2;
		}
		Parameter[] p = new Parameter[p1.length + p2.length];
		for (int i = 0; i < p1.length; i++) {
			p[i] = p1[i];
		}
		for (int i = 0; i < p2.length; i++) {
			p[p1.length + i] = p2[i];
		}
		return p;
	}

	protected ApplicationType type;

	public ApplicationType getApplicationType() {
		return this.type;
	}

	private long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	protected Graph g;

	public Graph getGraoh() {
		return this.g;
	}

	public void setGraph(Graph g) {
		this.g = g;
	}

	protected Metric.MetricType metricType;

	public Metric.MetricType getMetricType() {
		return this.metricType;
	}

	/*
	 * APPLICATION
	 */

	/**
	 * called before the batch is applied to the graph
	 * 
	 * @param b
	 *            batch of changes
	 * @return true, if successful; false otherwise
	 */
	public abstract boolean applyBeforeBatch(Batch b);

	/**
	 * called after the batch is applied to the graph
	 * 
	 * @param b
	 *            batch of changes
	 * @return true, if successful; false otherwise
	 */
	public abstract boolean applyAfterBatch(Batch b);

	/**
	 * called before the update is applied to the graph
	 * 
	 * @param u
	 *            update
	 * @return true, if successful; false otherwise
	 */
	public abstract boolean applyBeforeUpdate(Update u);

	/**
	 * called after the update is applied to the graph
	 * 
	 * @param u
	 *            update
	 * @return true, if successful; false otherwise
	 */
	public abstract boolean applyAfterUpdate(Update u);

	/**
	 * performs the initial computation of the metric for the initial graph
	 * 
	 * @return true, if successful; false otherwise
	 */
	public abstract boolean compute();

	/*
	 * INIT
	 */

	/**
	 * initialization of data structures
	 */
	public void init() {
		this.init_();
	}

	/**
	 * initialization of data structures
	 */
	public abstract void init_();

	/*
	 * RESET
	 */

	/**
	 * reset of all data structures
	 */
	public void reset() {
		this.timestamp = Long.MIN_VALUE;
		this.reset_();
	}

	/**
	 * reset of all data structures
	 */
	public abstract void reset_();

	/*
	 * DATA
	 */

	/**
	 * 
	 * @return all data computed by this metric
	 */
	public MetricData getData() {
		return new MetricData(this.getName(), this.getMetricType(),
				this.getValues(), this.getDistributions(),
				this.getNodeValueLists(), this.getNodeNodeValueLists());
	}

	/**
	 * 
	 * @return all the values computed by this metric
	 */
	public abstract Value[] getValues();

	/**
	 * 
	 * @return all the distributions computed by this metric
	 */
	public abstract Distr<?, ?>[] getDistributions();

	/**
	 * 
	 * @return all the nodevaluelists computed by this metric
	 */
	public abstract NodeValueList[] getNodeValueLists();

	/**
	 * 
	 * @return all the nodenodevaluelists computed by this metric
	 */
	public abstract NodeNodeValueList[] getNodeNodeValueLists();

	/*
	 * EQUALS
	 */

	/**
	 * 
	 * @param m
	 *            metric to compare to
	 * @return true, if the metric is of the same type and all computed values
	 *         are equal (can be used to compare different implementations of
	 *         the same metric)
	 */
	public abstract boolean equals(MetricOld m);

	/**
	 * 
	 * @param g
	 *            graph to check for applicability
	 * @return true, if the metric can be applied to the given graph
	 */
	public abstract boolean isApplicable(Graph g);

	/**
	 * 
	 * @param b
	 *            batch to check for applicability
	 * @return true, if the batch can be applied to this graph (also false in
	 *         case of a re-computation metric)
	 */
	public abstract boolean isApplicable(Batch b);

	/**
	 * 
	 * @param m
	 * @return true, if the metric can be compared, i.e., they compute the same
	 *         properties of a graph
	 */
	public abstract boolean isComparableTo(MetricOld m);

}
