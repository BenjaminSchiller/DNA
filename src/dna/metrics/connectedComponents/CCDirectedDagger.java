package dna.metrics.connectedComponents;

import dna.graph.Graph;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.updates.Update;

public class CCDirectedDagger extends CCDirected {

	public CCDirectedDagger() {
		super("CCDirectedDagger", ApplicationType.AfterUpdate);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void init_() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

}
