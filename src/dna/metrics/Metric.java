package dna.metrics;

import dna.graph.Graph;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.series.data.Value;
import dna.updates.Batch;
import dna.updates.Update;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

@SuppressWarnings("rawtypes")
public abstract class Metric extends ParameterList {

	public static enum ApplicationType {
		BeforeBatch, AfterBatch, BeforeAndAfterBatch, BeforeUpdate, AfterUpdate, BeforeAndAfterUpdate, Recomputation
	};

	public boolean isAppliedBeforeBatch() {
		return this.type == ApplicationType.BeforeBatch
				|| this.type == ApplicationType.BeforeAndAfterBatch;
	}

	public boolean isAppliedAfterBatch() {
		return this.type == ApplicationType.AfterBatch
				|| this.type == ApplicationType.BeforeAndAfterBatch;
	}

	public boolean isAppliedBeforeUpdate() {
		return this.type == ApplicationType.BeforeUpdate
				|| this.type == ApplicationType.BeforeAndAfterUpdate;
	}

	public boolean isAppliedAfterUpdate() {
		return this.type == ApplicationType.AfterUpdate
				|| this.type == ApplicationType.BeforeAndAfterUpdate;
	}

	public boolean isRecomputed() {
		return this.type == ApplicationType.Recomputation;
	}

	public Metric(String name, ApplicationType type) {
		this(name, type, new Parameter[] {});
	}

	public Metric(String name, ApplicationType type, Parameter p1) {
		this(name, type, new Parameter[] { p1 });
	}

	public Metric(String name, ApplicationType type, Parameter p1, Parameter p2) {
		this(name, type, new Parameter[] { p1, p2 });
	}

	public Metric(String name, ApplicationType type, Parameter p1,
			Parameter p2, Parameter p3) {
		this(name, type, new Parameter[] { p1, p2, p3 });
	}

	public Metric(String name, ApplicationType type, Parameter p1,
			Parameter p2, Parameter p3, Parameter p4) {
		this(name, type, new Parameter[] { p1, p2, p3, p4 });
	}

	public Metric(String name, ApplicationType type, Parameter p1,
			Parameter p2, Parameter p3, Parameter p4, Parameter p5) {
		this(name, type, new Parameter[] { p1, p2, p3, p4, p5 });
	}

	public Metric(String name, ApplicationType type, Parameter[] params) {
		super(name, params);
		this.type = type;
		this.timestamp = Long.MIN_VALUE;
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
	protected abstract void init_();

	/*
	 * RESET
	 */

	/**
	 * reset of all data structures
	 */
	public void reset() {
		this.g = null;
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
		return new MetricData(this.getName(), this.getValues(),
				this.getDistributions());
	}

	/**
	 * 
	 * @return all the values computed by this metric
	 */
	protected abstract Value[] getValues();

	/**
	 * 
	 * @return all the distributions computed by this metric
	 */
	protected abstract Distribution[] getDistributions();

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
	public abstract boolean equals(Metric m);

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
	public abstract boolean isComparableTo(Metric m);

}
