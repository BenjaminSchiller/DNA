package dna.metrics;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Graph;
import dna.series.data.Distribution;
import dna.series.data.MetricData;
import dna.series.data.Value;

public abstract class Metric {
	public Metric(String name, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		this.timestamp = Long.MIN_VALUE;
		this.name = name;
		this.appliedBeforeDiff = appliedBeforeDiff;
		this.appliedAfterEdge = appliedAfterEdge;
		this.appliedAfterDiff = appliedAfterDiff;
	}

	public String toString() {
		return this.name + " @ " + this.timestamp;
	}

	protected String getFilename(String key) {
		return this.name + "__" + key + "__" + this.timestamp + ".txt";
	}

	private long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	protected void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	protected Graph g;

	public Graph getGraph() {
		return this.g;
	}

	public int getNodes() {
		return this.g.getNodes().length;
	}

	public void setGraph(Graph g) {
		this.g = g;
		this.init(g);
	}

	public abstract boolean equals(Metric m);

	/*
	 * APPLY BEFORE DIFF
	 */

	private boolean appliedBeforeDiff;

	public boolean isAppliedBeforeDiff() {
		return this.appliedBeforeDiff;
	}

	public boolean applyBeforeDiff(Diff d) throws DiffNotApplicableException {
		if (d.getNodes() != this.getNodes()
				|| d.getFrom() != this.getTimestamp()) {
			throw new DiffNotApplicableException(this, d);
		}
		this.timestamp = d.getTo();
		return this.applyBeforeDiff_(d);
	}

	protected abstract boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException;

	/*
	 * APPLY AFTER EDGE
	 */

	private boolean appliedAfterEdge;

	public boolean isAppliedAfterEdge() {
		return this.appliedAfterEdge;
	}

	public boolean applyAfterEdgeAddition(Diff d, Edge e)
			throws DiffNotApplicableException {
		this.timestamp = d.getTo();
		return this.applyAfterEdgeAddition_(d, e);
	}

	protected abstract boolean applyAfterEdgeAddition_(Diff d, Edge e)
			throws DiffNotApplicableException;

	public boolean applyAfterEdgeRemoval(Diff d, Edge e)
			throws DiffNotApplicableException {
		this.timestamp = d.getTo();
		return this.applyAfterEdgeRemoval_(d, e);
	}

	protected abstract boolean applyAfterEdgeRemoval_(Diff d, Edge e)
			throws DiffNotApplicableException;

	/*
	 * APPLY AFTER DIFF
	 */

	private boolean appliedAfterDiff;

	public boolean isAppliedAfterDiff() {
		return this.appliedAfterDiff;
	}

	public boolean applyAfterDiff(Diff d) throws DiffNotApplicableException {
		if (d.getNodes() != this.getNodes() || d.getTo() != this.getTimestamp()) {
			throw new DiffNotApplicableException(this, d);
		}
		this.timestamp = d.getTo();
		return this.applyAfterDiff_(d);
	}

	protected abstract boolean applyAfterDiff_(Diff d)
			throws DiffNotApplicableException;

	/*
	 * APPLY CLEANUP
	 */

	public abstract boolean cleanupApplication();

	/*
	 * COMPUTE
	 */

	public boolean isComputed() {
		return !this.isAppliedBeforeDiff() && !this.isAppliedAfterEdge()
				&& !this.isAppliedAfterDiff();
	}

	public boolean compute() {
		this.timestamp = this.g.getTimestamp();
		return this.compute_();
	}

	protected abstract boolean compute_();

	/*
	 * RESET
	 */

	public void reset() {
		this.g = null;
		this.reset_();
	}

	public abstract void reset_();

	/*
	 * METRIC DATA
	 */

	public MetricData getData() {
		return new MetricData(this.name, this.getValues(),
				this.getDistributions());
	}

	protected abstract Value[] getValues();

	protected abstract Distribution[] getDistributions();

	protected abstract void init(Graph g);
}
