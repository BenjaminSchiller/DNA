package dna.metrics.richClub;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeNR;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;

public class RichClubCoefficientU extends RichClubCoefficient implements
		IBeforeEA, IAfterER, IBeforeNR {

	public RichClubCoefficientU(int k) {
		super("RichClubCoefficientU", k);
	}

	@Override
	public boolean init() {
		return this.compute();
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		if (ea.getEdge().getN1().getDegree() == k) {
			this.addToRC(ea.getEdge().getN1());
		}
		if (ea.getEdge().getN2().getDegree() == k) {
			this.addToRC(ea.getEdge().getN2());
		}
		if (ea.getEdge().getN1().getDegree() >= k
				&& ea.getEdge().getN2().getDegree() >= k) {
			this.edges++;
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		if (er.getEdge().getN1().getDegree() == k) {
			this.removeFromRC(er.getEdge().getN1());
		}
		if (er.getEdge().getN2().getDegree() == k) {
			this.removeFromRC(er.getEdge().getN2());
		}
		if (er.getEdge().getN1().getDegree() >= k
				&& er.getEdge().getN2().getDegree() >= k) {
			this.edges--;
		}
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		Node v = (Node) nr.getNode();

		// remove node from RC
		if (v.getDegree() > k) {
			nodes--;
			// remove direct edges to other RC members
			for (IElement e_ : v.getEdges()) {
				Node w = ((Edge) e_).getDifferingNode(v);
				if (w.getDegree() > k) {
					edges--;
				}
			}
		}

		// remove edges of other removed nodes
		for (IElement e_ : v.getEdges()) {
			Node w = ((Edge) e_).getDifferingNode(v);
			if (w.getDegree() == k + 1) {
				nodes--;
				for (IElement e__ : w.getEdges()) {
					Node u = ((Edge) e__).getDifferingNode(w);
					if (u.equals(v)) {
						continue;
					}

					// \lIf{$d(u) > k \land u \notin inc(v) \land d(u) \neq k+1
					// \land id(u) < id(w))$}{

					// if (u.getDegree() > k && !v.hasEdge(v, u)
					// && u.getDegree() != k + 1
					// && u.getIndex() < w.getIndex()) {
					// edges--;
					// }

					if (u.getDegree() > k
							&& (u.getDegree() != k + 1
									|| u.getIndex() < w.getIndex() || !u
										.hasEdge(u, v))) {
						edges--;
					}
				}
			}
		}
		return true;
	}

	protected void addToRC(Node v) {
		nodes++;
		for (IElement e_ : v.getEdges()) {
			Node w = ((Edge) e_).getDifferingNode(v);
			if (w.getDegree() > k) {
				edges++;
			}
		}
	}

	protected void removeFromRC(Node v) {
		nodes--;
		for (IElement e_ : v.getEdges()) {
			Node w = ((Edge) e_).getDifferingNode(v);
			if (w.getDegree() > k) {
				edges--;
			}
		}
	}

}
