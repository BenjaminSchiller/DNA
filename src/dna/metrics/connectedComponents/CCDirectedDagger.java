package dna.metrics.connectedComponents;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;

public class CCDirectedDagger extends CCDirected {

	public CCDirectedDagger() {
		super("CCDirectedDagger", false, true, false);
		// TODO Auto-generated constructor stub
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
