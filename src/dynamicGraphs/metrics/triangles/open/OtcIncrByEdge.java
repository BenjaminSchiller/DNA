package dynamicGraphs.metrics.triangles.open;

import java.util.HashSet;
import java.util.Set;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.Node;
import dynamicGraphs.metrics.triangles.ClusteringCoefficient;

public class OtcIncrByEdge extends ClusteringCoefficient {

	public OtcIncrByEdge(Graph g) {
		super(g, "OTC_INCR_BY_EDGE", false, true, false);
	}

	protected long allTriangles;

	protected long allPotentialTriangles;

	protected long[] triangles;

	protected long[] potentialTriangles;

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
		System.out.println(".........");
		// TODO implement
		return false;
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

	private Set<Node> intersect(Set<Node> s1, Set<Node> s2) {
		Set<Node> s = new HashSet<Node>();
		s.addAll(s1);
		s.retainAll(s2);
		return s;
	}

}
