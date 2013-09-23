package dna.metrics.util;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.Batch;
import dna.updates.Update;
import dna.util.parameters.IntParameter;

public class Recompute extends Metric {

	private Metric m;

	private int steps;

	private int step;

	public Recompute(Metric m, int steps) {
		super(m.getName() + "_RECOMPUTE", ApplicationType.BatchAndUpdates,
				MetricType.heuristic, new IntParameter("steps", steps));
		if (m.getApplicationType() == ApplicationType.Recomputation) {
			throw new IllegalArgumentException("cannot use metric '"
					+ m.getName() + "' of type '" + m.getApplicationType()
					+ "' as parameter for Recompute metric");
		}
		this.m = m;
		this.steps = steps;
		this.step = 0;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		this.step = (this.step + 1) % this.steps;
		if (this.step == 0) {
			return true;
		}
		if (this.m.isAppliedBeforeBatch()) {
			return this.m.applyBeforeBatch(b);
		}
		return true;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		if (this.step == 0) {
			return true;
		}
		if (this.m.isAppliedAfterBatch()) {
			return this.m.applyAfterBatch(b);
		}
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		if (this.step == 0) {
			return true;
		}
		if (this.m.isAppliedBeforeUpdate()) {
			return this.m.applyBeforeUpdate(u);
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (this.step == 0) {
			return this.m.compute();
		}
		if (this.m.isAppliedAfterUpdate()) {
			return this.m.applyAfterUpdate(u);
		}
		return true;
	}

	@Override
	public boolean compute() {
		return this.m.compute();
	}

	@Override
	public void init_() {
		this.m.init_();
	}

	@Override
	public void reset_() {
		this.m.reset_();
	}

	@Override
	public Value[] getValues() {
		return this.m.getValues();
	}

	@Override
	public Distribution[] getDistributions() {
		return this.m.getDistributions();
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return this.m.getNodeValueLists();
	}

	@Override
	public boolean equals(Metric m) {
		return this.m.equals(m);
	}

	@Override
	public boolean isApplicable(Graph g) {
		return this.m.isApplicable(g);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return this.m.isApplicable(b);
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return this.m.isComparableTo(m);
	}

	@Override
	public void setGraph(Graph g) {
		super.setGraph(g);
		this.m.setGraph(g);
	}
}
