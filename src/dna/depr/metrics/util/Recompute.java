package dna.depr.metrics.util;

import dna.depr.metrics.MetricOld;
import dna.graph.Graph;
import dna.metrics.IMetric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.parameters.IntParameter;

public class Recompute extends MetricOld {

	private MetricOld m;

	private int steps;

	private int step;

	public Recompute(MetricOld m, int steps) {
		super(m.getName() + "_RECOMPUTE", ApplicationType.BatchAndUpdates,
				IMetric.MetricType.heuristic, new IntParameter("steps", steps));
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
	public Distr<?, ?>[] getDistributions() {
		return this.m.getDistributions();
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return this.m.getNodeValueLists();
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return this.m.getNodeNodeValueLists();
	}

	@Override
	public boolean equals(MetricOld m) {
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
	public boolean isComparableTo(MetricOld m) {
		return this.m.isComparableTo(m);
	}

	@Override
	public void setGraph(Graph g) {
		super.setGraph(g);
		this.m.setGraph(g);
	}
}
