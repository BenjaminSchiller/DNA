package dna.metricsNew.richClub;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class DegreeRichClub {
	public int degree;

	public DegreeRichClub previous;

	public DegreeRichClub next;

	public int edgeCount;

	public int nodeCount;

	private HashSet<Node> nodes;

	private DegreeRichClubs rcs;

	public DegreeRichClub(DegreeRichClubs rcs, int degree) {
		this.rcs = rcs;
		this.degree = degree;

		this.previous = null;
		this.next = null;
		this.edgeCount = 0;
		this.nodeCount = 0;

		this.nodes = new HashSet<Node>();
	}

	public String toString() {
		return "RC-" + this.degree + " @ " + this.size() + " (e="
				+ this.edgeCount + ", n=" + this.nodeCount + ")";
	}

	public int size() {
		return this.nodes.size();
	}

	public void addNode(Node n) {
		this.nodes.add(n);
		this.rcs.nodeClubs.put(n, this);
		this.propagateNodeCount(1);

		// incr edge count for connected clubs
		for (IElement e_ : n.getEdges()) {
			Node neighbor = ((Edge) e_).getDifferingNode(n);
			DegreeRichClub rc = this.rcs.nodeClubs.get(neighbor);

			// skip processing of edge if neighbor not processed yet
			if (rc == null) {
				continue;
			}

			if (this.degree <= rc.degree) {
				this.propagateEdgeCount(1);
			} else {
				rc.propagateEdgeCount(1);
			}
		}
	}

	public void removeNode(Node n) {
		this.nodes.remove(n);
		this.rcs.nodeClubs.remove(n);
		this.propagateNodeCount(-1);

		// decr edge count for connected clubs
		for (IElement e_ : n.getEdges()) {
			Node neighbor = ((Edge) e_).getDifferingNode(n);
			DegreeRichClub rc = this.rcs.nodeClubs.get(neighbor);

			// skip processing of edge if neighbor not processed yet
			if (rc == null) {
				continue;
			}

			if (this.degree <= rc.degree) {
				this.propagateEdgeCount(-1);
			} else {
				rc.propagateEdgeCount(-1);
			}
		}
	}

	public void propagateEdgeCount(int change) {
		this.edgeCount += change;
		if (this.next != null) {
			this.next.propagateEdgeCount(change);
		}
	}

	public void propagateNodeCount(int change) {
		this.nodeCount += change;
		if (this.next != null) {
			this.next.propagateNodeCount(change);
		}
	}
}
