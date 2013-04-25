package dna.metrics.triangles.open;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.old.OldEdge;
import dna.metrics.triangles.ClusteringCoefficient;

public class OtcComp extends ClusteringCoefficient {

	public OtcComp() {
		super("otcComp", false, false, false);
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getName()
				+ " - cannot be applied before diff");
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, OldEdge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getName()
				+ " - cannot be applied after edge addition");
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, OldEdge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getName()
				+ " - cannot be applied after edge removal");
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getName()
				+ " - cannot be applied after diff");
	}

}
