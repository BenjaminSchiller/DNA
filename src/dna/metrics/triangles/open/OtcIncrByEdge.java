package dna.metrics.triangles.open;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;
import dna.metrics.triangles.ClusteringCoefficient;

public class OtcIncrByEdge extends ClusteringCoefficient {

	public OtcIncrByEdge() {
		super("otcIncrByEdge", false, true, false);
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("...");
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e)
			throws DiffNotApplicableException {
		Node v = e.getSrc();
		Node w = e.getDst();
		// (1)
		for (Node x : v.getNeighbors()) {
			if (w.hasNeighbor(x)) {
				this.add(x);
			}
		}
		if (!v.hasIn(w)) {
			return true;
		}
		// (2)
		for (Node x : v.getNeighbors()) {
			if (w.hasIn(x)) {
				this.add(v);
			}
		}
		// (3)
		for (Node x : v.getNeighbors()) {
			if (w.hasOut(x)) {
				this.add(v);
			}
		}
		// (4)
		for (Node x : w.getNeighbors()) {
			if (v.hasIn(x)) {
				this.add(w);
			}
		}
		// (5)
		for (Node x : w.getNeighbors()) {
			if (v.hasOut(x)) {
				this.add(w);
			}
		}
		// (6)
		this.addPotentials(v, 2 * v.getNeighbors().size() - 2);
		// (7)
		this.addPotentials(w, 2 * w.getNeighbors().size() - 2);
		return true;
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e)
			throws DiffNotApplicableException {
		Node v = e.getSrc();
		Node w = e.getDst();
		// (1)
		for (Node x : v.getNeighbors()) {
			if (w.hasNeighbor(x)) {
				this.remove(x);
			}
		}
		if (!v.hasIn(w)) {
			return true;
		}
		// (2)
		for (Node x : v.getNeighbors()) {
			if (w.hasIn(x)) {
				this.remove(v);
			}
		}
		// (3)
		for (Node x : v.getNeighbors()) {
			if (w.hasOut(x)) {
				this.remove(v);
			}
		}
		// (4)
		for (Node x : w.getNeighbors()) {
			if (v.hasIn(x)) {
				this.remove(w);
			}
		}
		// (5)
		for (Node x : w.getNeighbors()) {
			if (v.hasOut(x)) {
				this.remove(w);
			}
		}
		// (6)
		this.removePotentials(v, 2 * v.getNeighbors().size());
		// (7)
		this.removePotentials(w, 2 * w.getNeighbors().size());
		return true;
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		throw new DiffNotApplicableException("...");
	}

	private void add(Node origin) {
		this.triangleCount++;
		this.nodeTriangleCount[origin.getIndex()]++;
	}

	private void remove(Node origin) {
		this.triangleCount--;
		this.nodeTriangleCount[origin.getIndex()]--;
	}

	private void addPotentials(Node origin, int count) {
		this.potentialCount += count;
		this.nodePotentialCount[origin.getIndex()] += count;
	}

	private void removePotentials(Node origin, int count) {
		this.potentialCount -= count;
		this.nodePotentialCount[origin.getIndex()] -= count;
	}

}
