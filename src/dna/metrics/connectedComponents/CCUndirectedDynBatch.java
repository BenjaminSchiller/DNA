package dna.metrics.connectedComponents;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;

public class CCUndirectedDynBatch extends CCUndirected {

	public CCUndirectedDynBatch() {
		super("CCdirectedComp", true, false, true);
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d) {
		// throws DiffNotApplicableException {
		// throw new DiffNotApplicableException("before diff");
		return true;
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("before edge addition");
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("before edge removal");
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		int r = 0;
		boolean[][] a = new boolean[this.g.getNodes().length][this.g.getNodes().length];
		for (Edge e : d.getRemovedEdges()) {
			Node src = e.getSrc();
			Node dst = e.getDst();
			SpanningTreeNode dstTreeElement = this.nodesTreeElement[dst
					.getIndex()];
			SpanningTreeNode srcTreeElement = this.nodesTreeElement[src
					.getIndex()];
			if (srcTreeElement.getChildren().contains(dstTreeElement)) {

				boolean foundNeighbour = false;
				for (Node n : src.getNeighbors()) {
					a[src.getIndex()][n.getIndex()] = true;
				}
				for (Node n : e.getDst().getNeighbors()) {
					if (a[src.getIndex()][n.getIndex()]) {
						foundNeighbour = true;
						break;
					}
				}

				if (!foundNeighbour) {
					r += 1;
				}
			}
		}
		// TODO: r thres zeigt an ab wann neuberechnet wird
		if (r > 0) {
			r = 0;
			this.reset_();
			this.compute_();
		} else {
			int f = 0;
			for (Edge e : d.getAddedEdges()) {
				Node src = e.getSrc();
				Node dst = e.getDst();
				SpanningTreeNode dstTreeElement = this.nodesTreeElement[dst
						.getIndex()];
				SpanningTreeNode srcTreeElement = this.nodesTreeElement[src
						.getIndex()];

				if (this.nodeComponentMembership[src.getIndex()] != this.nodeComponentMembership[dst
						.getIndex()]) {
					f += 1;
				}
			}

			if (f > 100) {
				this.reset_();
				this.compute_();
			}
		}

		return true;
	}

}
