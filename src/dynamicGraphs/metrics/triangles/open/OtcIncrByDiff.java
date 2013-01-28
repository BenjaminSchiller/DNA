package dynamicGraphs.metrics.triangles.open;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.Node;
import dynamicGraphs.metrics.Metric;

public class OtcIncrByDiff extends Metric {

	public OtcIncrByDiff(Graph g) {
		super(g, "OTC_INCR_BY_DIFF", true, false, true);
		this.triangles = new ArrayList<Set<OpenTriangle>>(g.getNodes().length);
		this.potentialTriangles = new ArrayList<Set<OpenTriangle>>(
				g.getNodes().length);
		for (int i = 0; i < g.getNodes().length; i++) {
			this.triangles.add(new HashSet<OpenTriangle>());
			this.potentialTriangles.add(new HashSet<OpenTriangle>());
		}
		this.allTriangles = new HashSet<OpenTriangle>();
		this.allPotentialTriangles = new HashSet<OpenTriangle>();
	}

	private ArrayList<Set<OpenTriangle>> triangles;

	private ArrayList<Set<OpenTriangle>> potentialTriangles;

	private Set<OpenTriangle> allTriangles;

	private Set<OpenTriangle> allPotentialTriangles;

	public Set<OpenTriangle> getAllTriangles() {
		return this.allTriangles;
	}

	public Set<OpenTriangle> getAllPotentialTriangles() {
		return this.allPotentialTriangles;
	}

	@Override
	protected boolean compute_() {
		try {
			for (Node n : this.g.getNodes()) {
				for (Node u : n.getNeighbors()) {
					for (Node v : n.getNeighbors()) {
						if (u.getIndex() == v.getIndex()) {
							continue;
						}
						OpenTriangle t;
						t = new OpenTriangle(n, u, v);
						this.allPotentialTriangles.add(t);
						if (u.hasOut(v)) {
							this.allTriangles.add(t);
						}
					}
				}
			}
			return true;
		} catch (InvalidOpenTriangleException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d) {
		try {
			for (Edge e : d.getRemovedEdges()) {
				Node v = e.getSrc();
				Node w = e.getDst();
				// System.out.println("removing edge: " + e);
				// (1)
				for (Node x : intersect(v.getNeighbors(), w.getNeighbors())) {
					this.remove(new OpenTriangle(x, v, w), 1);
				}
				if (!this.g.containsEdge(new Edge(w, v))) {
					// System.out.println("continue...");
					continue;
				}
				// (2)
				for (Node x : intersect(v.getNeighbors(), w.getIn())) {
					this.remove(new OpenTriangle(v, x, w), 2);
				}
				// (3)
				for (Node x : intersect(v.getNeighbors(), w.getOut())) {
					this.remove(new OpenTriangle(v, w, x), 3);
				}
				// (4)
				for (Node x : intersect(w.getNeighbors(), v.getIn())) {
					this.remove(new OpenTriangle(w, x, v), 4);
				}
				// (5)
				for (Node x : intersect(w.getNeighbors(), v.getOut())) {
					this.remove(new OpenTriangle(w, v, x), 5);
				}
				// (6)
				for (Node x : v.getNeighbors()) {
					if (x.equals(w)) {
						continue;
					}
					this.removePotential(new OpenTriangle(v, w, x), 6);
					this.removePotential(new OpenTriangle(v, x, w), 6);
				}
				// (7)
				for (Node x : w.getNeighbors()) {
					if (x.equals(v)) {
						continue;
					}
					this.removePotential(new OpenTriangle(w, v, x), 7);
					this.removePotential(new OpenTriangle(w, x, v), 7);
				}
			}
			return true;
		} catch (InvalidOpenTriangleException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) {
		try {
			for (Edge e : d.getAddedEdges()) {
				Node v = e.getSrc();
				Node w = e.getDst();
				// System.out.println("adding edge: " + e);
				// (1)
				for (Node x : intersect(v.getNeighbors(), w.getNeighbors())) {
					this.add(new OpenTriangle(x, v, w), 1);
				}
				if (!this.g.containsEdge(new Edge(w, v))) {
					// System.out.println("continue...");
					continue;
				}
				// (2)
				for (Node x : intersect(v.getNeighbors(), w.getIn())) {
					this.add(new OpenTriangle(v, x, w), 2);
				}
				// (3)
				for (Node x : intersect(v.getNeighbors(), w.getOut())) {
					this.add(new OpenTriangle(v, w, x), 3);
				}
				// (4)
				for (Node x : intersect(w.getNeighbors(), v.getIn())) {
					this.add(new OpenTriangle(w, x, v), 4);
				}
				// (5)
				for (Node x : intersect(w.getNeighbors(), v.getOut())) {
					this.add(new OpenTriangle(w, v, x), 5);
				}
				// (6)
				for (Node x : v.getNeighbors()) {
					if (x.equals(w)) {
						continue;
					}
					this.addPotential(new OpenTriangle(v, w, x), 6);
					this.addPotential(new OpenTriangle(v, x, w), 6);
				}
				// (7)
				for (Node x : w.getNeighbors()) {
					if (x.equals(v)) {
						continue;
					}
					this.addPotential(new OpenTriangle(w, v, x), 7);
					this.addPotential(new OpenTriangle(w, x, v), 7);
				}
			}
			return true;
		} catch (InvalidOpenTriangleException e1) {
			e1.printStackTrace();
			return false;
		}
	}

	private boolean remove(OpenTriangle t, int type) {
		if (this.allTriangles.remove(t)) {
			// System.out.println("  (" + type + ") removing " + t);
			return true;
		}
		return false;
	}

	private boolean add(OpenTriangle t, int type) {
		if (this.allTriangles.add(t)) {
			// System.out.println("  (" + type + ") adding " + t);
			return true;
		}
		return false;
	}

	private boolean removePotential(OpenTriangle t, int type) {
		if (this.allPotentialTriangles.remove(t)) {
			// System.out.println("  (" + type + ") removing potential " + t);
			return true;
		}
		return false;
	}

	private boolean addPotential(OpenTriangle t, int type) {
		if (this.allPotentialTriangles.add(t)) {
			// System.out.println("  (" + type + ") adding potential " + t);
			return true;
		}
		return false;
	}

	private Set<Node> intersect(Set<Node> s1, Set<Node> s2) {
		Set<Node> s = new HashSet<Node>();
		s.addAll(s1);
		s.retainAll(s2);
		return s;
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof OtcIncrByDiff)) {
			return false;
		}
		OtcIncrByDiff otc = (OtcIncrByDiff) m;
		if (this.allTriangles.size() != otc.allTriangles.size()
				|| !this.allTriangles.containsAll(otc.allTriangles)) {
			System.out.println(this.allTriangles.size() + " != "
					+ otc.allTriangles.size());
			return false;
		}
		if (this.allPotentialTriangles.size() != otc.allPotentialTriangles
				.size()
				|| !this.allPotentialTriangles
						.containsAll(otc.allPotentialTriangles)) {
			return false;
		}
		if (this.triangles.size() != otc.triangles.size()
				|| this.potentialTriangles.size() != otc.potentialTriangles
						.size()) {
			return false;
		}
		for (int i = 0; i < this.triangles.size(); i++) {
			if (this.triangles.get(i).size() != otc.triangles.get(i).size()
					|| !this.triangles.get(i).containsAll(otc.triangles.get(i))) {
				return false;
			}
		}
		for (int i = 0; i < this.triangles.size(); i++) {
			if (this.potentialTriangles.get(i).size() != otc.potentialTriangles
					.get(i).size()
					|| !this.potentialTriangles.get(i).containsAll(
							otc.potentialTriangles.get(i))) {
				return false;
			}
		}
		return true;
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
				+ " - cannot be applied after edge deletion");
	}
}
