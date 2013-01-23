package dynamicGraphs.metrics;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Graph;

public abstract class Metric {
	public Metric(Graph g, String key, boolean incremental) {
		this.g = g;
		this.timestamp = Long.MIN_VALUE;
		this.key = key;
		this.incremental = incremental;
	}
	
	public String toString(){
		return this.key + " @ " + this.timestamp;
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
	
	private boolean incremental;
	
	public boolean isIncremental(){
		return this.incremental;
	}

	protected String getFilename(String key) {
		return this.key + "__" + key + "__" + this.timestamp + ".txt";
	}

	public boolean applyBefore(Diff d) throws DiffNotApplicableException {
		if (!this.isApplicableBefore(d)) {
			throw new DiffNotApplicableException(this, d);
		}
		this.timestamp = d.getTo();
		return this.applyDiffBefore(d);
	}

	public boolean applyAfter(Diff d) throws DiffNotApplicableException {
		if (!this.isApplicableAfter(d)) {
			throw new DiffNotApplicableException(this, d);
		}
		return this.applyDiffAfter(d);
	}

	public boolean compute() {
		this.timestamp = this.g.getTimestamp();
		return this.computeMetric();
	}

	protected Graph g;

	public Graph getGraph() {
		return this.g;
	}

	public int getNodes() {
		return this.g.getNodes().length;
	}

	public boolean isApplicableBefore(Diff d) {
		return d.getNodes() == this.getNodes()
				&& d.getFrom() == this.getTimestamp();
	}

	public boolean isApplicableAfter(Diff d) {
		return d.getNodes() == this.getNodes()
				&& d.getTo() == this.getTimestamp();
	}

	protected abstract boolean computeMetric();

	protected abstract boolean applyDiffBefore(Diff d)
			throws DiffNotApplicableException;

	protected abstract boolean applyDiffAfter(Diff d)
			throws DiffNotApplicableException;

	public abstract boolean equals(Metric m);
}
