package dynamicGraphs.metrics.triangles.open;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.Node;
import dynamicGraphs.metrics.triangles.ClusteringCoefficient;
import dynamicGraphs.util.ArrayUtils;

public class OpenTriangleCCBasic extends ClusteringCoefficient {

	public OpenTriangleCCBasic(Graph g) {
		super(g, "SEP", false);
	}

	@Override
	protected boolean computeMetric() {
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
	protected boolean applyDiffBefore(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getKey()
				+ " cannot be computed incrementally");
	}

	@Override
	protected boolean applyDiffAfter(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException(this.getKey()
				+ " cannot be computed incrementally");
	}

}
