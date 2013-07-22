package dna.metrics.connectedComponents;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;

public class CCUndirectedComp extends CCUndirected {

	public CCUndirectedComp() {
		super("CCUndirectedDyn", false, false, false);
	}

	@Override
	protected boolean compute_() {
		reset_();
		return super.compute_();
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e)
			throws DiffNotApplicableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e)
			throws DiffNotApplicableException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		// TODO Auto-generated method stub
		return false;
	}

}
