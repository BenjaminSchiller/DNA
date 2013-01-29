package dynamicGraphs.metrics.triangles.open;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.metrics.triangles.ClusteringCoefficient;

public class OtcComp extends ClusteringCoefficient {

	public OtcComp(Graph g) {
		super(g, "OTC_COMP", false, false, false);
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getKey()
				+ " - cannot be applied before diff");
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getKey()
				+ " - cannot be applied after edge addition");
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getKey()
				+ " - cannot be applied after edge removal");
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getKey()
				+ " - cannot be applied after diff");
	}

}
