package dna.metrics.util;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.update.Update;
import dna.util.Rand;
import dna.util.parameters.DoubleParameter;

public class MissingBatches extends Metric {

	private Metric m;

	private double missProb = 0.0;

	public MissingBatches(Metric m, double missProb) {
		super(m.getName() + "_MISSING_BATCHES", m.getApplicationType(),
				MetricType.heuristic, m.getParameters(), new DoubleParameter(
						"missProb", missProb));
		this.m = m;
		this.missProb = missProb;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		if (Rand.rand.nextDouble() < this.missProb) {
			return true;
		}
		return this.m.applyBeforeBatch(b);
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		if (Rand.rand.nextDouble() < this.missProb) {
			return true;
		}
		return this.m.applyAfterBatch(b);
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return this.m.applyBeforeUpdate(u);
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
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
