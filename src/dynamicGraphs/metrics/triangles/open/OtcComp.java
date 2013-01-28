package dynamicGraphs.metrics.triangles.open;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.Node;
import dynamicGraphs.metrics.triangles.ClusteringCoefficient;
import dynamicGraphs.util.ArrayUtils;

public class OtcComp extends ClusteringCoefficient {

	public OtcComp(Graph g) {
		super(g, "OTC_COMP", false, false, false);
	}

	@Override
	protected boolean compute_() {
		int triangles = 0;
		int potential = 0;
		for (Node n : this.g.getNodes()) {
			int nTriangles = 0;
			int nPotential = 0;
			for (Node u : n.getNeighbors()) {
				for (Node v : n.getNeighbors()) {
					if (u.equals(v)) {
						continue;
					}
					nPotential++;
					if (v.hasOut(u)) {
						nTriangles++;
					}
				}
			}
			this.localCC[n.getIndex()] = (double) nTriangles / nPotential;
			triangles += nTriangles;
			potential += nPotential;
		}
		this.globalCC = (double) triangles / potential;
		this.averageCC = ArrayUtils.avg(this.localCC);
		return true;
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
