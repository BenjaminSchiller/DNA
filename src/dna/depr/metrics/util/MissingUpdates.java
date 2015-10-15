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
import dna.util.Rand;
import dna.util.parameters.DoubleParameter;

public class MissingUpdates extends MetricOld {

	private MetricOld m;

	private double missProb = 0.0;

	public MissingUpdates(MetricOld m, double missProb) {
		super(m.getName() + "_MISSING_UPDATES", m.getApplicationType(),
				IMetric.MetricType.heuristic, m.getParameters(),
				new DoubleParameter("missProb", missProb));
		this.m = m;
		this.missProb = missProb;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return this.m.applyBeforeBatch(b);
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return this.m.applyAfterBatch(b);
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		if (Rand.rand.nextDouble() < this.missProb) {
			return true;
		}
		return this.m.applyBeforeUpdate(u);
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (Rand.rand.nextDouble() < this.missProb) {
			return true;
		}
		return this.m.applyAfterUpdate(u);
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
