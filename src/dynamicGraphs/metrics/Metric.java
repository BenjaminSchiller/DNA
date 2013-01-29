package dynamicGraphs.metrics;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;

public abstract class Metric {
	public Metric(Graph g, String key, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		this.g = g;
		this.timestamp = Long.MIN_VALUE;
		this.key = key;
		this.appliedBeforeDiff = appliedBeforeDiff;
		// this.appliedBeforeEdge = appliedBeforeEdge;
		this.appliedAfterEdge = appliedAfterEdge;
		this.appliedAfterDiff = appliedAfterDiff;
	}

	public String toString() {
		return this.key + " @ " + this.timestamp;
	}

	protected String getFilename(String key) {
		return this.key + "__" + key + "__" + this.timestamp + ".txt";
	}

	private long timestamp;

	public long getTimestamp() {
		return this.timestamp;
	}

	protected void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	private String key;

	public String getKey() {
		return this.key;
	}

	protected Graph g;

	public Graph getGraph() {
		return this.g;
	}

	public int getNodes() {
		return this.g.getNodes().length;
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
}
