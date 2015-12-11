package dna.metrics.streaM_k.grouping;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class GroupingV1 extends Grouping {

	/*
	 * 3 3 3 3 3 3 3 3 3 3 3 3 3 3 3
	 */

	@Override
	protected HashSet<String> getGroups3(Edge e) {
		HashSet<String> set = new HashSet<String>();
		for (IElement e_ : e.getN1().getEdges()) {
			set.add(((Edge) e_).getDifferingNode(e.getN1()).getIndex() + "");
		}
		for (IElement e_ : e.getN2().getEdges()) {
			set.add(((Edge) e_).getDifferingNode(e.getN2()).getIndex() + "");
		}
		return set;
	}

	/*
	 * 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4
	 */

	@Override
	protected HashSet<String> getGroups4(Edge e) {
		HashSet<String> set = new HashSet<String>();

		Node l = e.getN1();
		Node r = e.getN2();

		add4(set, e.getN1(), e.getN2());
		add4(set, e.getN2(), e.getN1());

		// L1, R1
		for (IElement l1_ : l.getEdges()) {
			Node l1 = ((Edge) l1_).getDifferingNode(l);
			for (IElement r1_ : r.getEdges()) {
				Node r1 = ((Edge) r1_).getDifferingNode(r);
				add(set, l, r, l1, r1);
			}
		}

		return set;
	}

	protected static void add4(HashSet<String> set, Node l, Node r) {
		// L1, L1
		for (IElement l1_ : l.getEdges()) {
			Node l1 = ((Edge) l1_).getDifferingNode(l);
			for (IElement l11_ : l.getEdges()) {
				Node l11 = ((Edge) l11_).getDifferingNode(l);
				add(set, l, r, l1, l11);
			}
		}
		// L1, L2
		for (IElement l1_ : l.getEdges()) {
			Node l1 = ((Edge) l1_).getDifferingNode(l);
			for (IElement l2_ : l1.getEdges()) {
				Node l2 = ((Edge) l2_).getDifferingNode(l1);
				add(set, l, r, l1, l2);
			}
		}
	}

	/*
	 * 5 5 5 5 5 5 5 5 5 5 5 5 5 5 5
	 */

	@Override
	protected HashSet<String> getGroups5(Edge e) {
		HashSet<String> set = new HashSet<String>();
		add_5(set, e.getN1(), e.getN2());
		add_5(set, e.getN2(), e.getN1());
		return set;
	}

	protected static void add_5(HashSet<String> set, Node l, Node r) {
		add_r1r1r1(set, l, r);
		add_r1r1r2(set, l, r);
		add_r1r2r2(set, l, r);
		add_r1r2r3(set, l, r);
		add_l1r1r1(set, l, r);
		add_l1r1r2(set, l, r);
	}

	protected static void add_r1r1r1(HashSet<String> set, Node l, Node r) {
		for (IElement r1_ : r.getEdges()) {
			Node r1 = ((Edge) r1_).getDifferingNode(r);
			for (IElement r2_ : r.getEdges()) {
				Node r2 = ((Edge) r2_).getDifferingNode(r);
				for (IElement r3_ : r.getEdges()) {
					Node r3 = ((Edge) r3_).getDifferingNode(r);
					add(set, l, r, r1, r2, r3);
				}
			}
		}
	}

	protected static void add_r1r1r2(HashSet<String> set, Node l, Node r) {
		for (IElement r1_ : r.getEdges()) {
			Node r1 = ((Edge) r1_).getDifferingNode(r);
			for (IElement r2_ : r.getEdges()) {
				Node r2 = ((Edge) r2_).getDifferingNode(r);
				for (IElement r3_ : r2.getEdges()) {
					Node r3 = ((Edge) r3_).getDifferingNode(r2);
					add(set, l, r, r1, r2, r3);
				}
			}
		}
	}

	protected static void add_r1r2r2(HashSet<String> set, Node l, Node r) {
		for (IElement r1_ : r.getEdges()) {
			Node r1 = ((Edge) r1_).getDifferingNode(r);
			for (IElement r2_ : r1.getEdges()) {
				Node r2 = ((Edge) r2_).getDifferingNode(r1);
				for (IElement r3_ : r1.getEdges()) {
					Node r3 = ((Edge) r3_).getDifferingNode(r1);
					add(set, l, r, r1, r2, r3);
				}
			}
		}
	}

	protected static void add_r1r2r3(HashSet<String> set, Node l, Node r) {
		for (IElement r1_ : r.getEdges()) {
			Node r1 = ((Edge) r1_).getDifferingNode(r);
			for (IElement r2_ : r1.getEdges()) {
				Node r2 = ((Edge) r2_).getDifferingNode(r1);
				for (IElement r3_ : r2.getEdges()) {
					Node r3 = ((Edge) r3_).getDifferingNode(r2);
					add(set, l, r, r1, r2, r3);
				}
			}
		}
	}

	protected static void add_l1r1r1(HashSet<String> set, Node l, Node r) {
		for (IElement l1_ : l.getEdges()) {
			Node l1 = ((Edge) l1_).getDifferingNode(l);
			for (IElement r1_ : r.getEdges()) {
				Node r1 = ((Edge) r1_).getDifferingNode(r);
				for (IElement r2_ : r.getEdges()) {
					Node r2 = ((Edge) r2_).getDifferingNode(r);
					add(set, l, r, l1, r1, r2);
				}
			}
		}
	}

	protected static void add_l1r1r2(HashSet<String> set, Node l, Node r) {
		for (IElement l1_ : l.getEdges()) {
			Node l1 = ((Edge) l1_).getDifferingNode(l);
			for (IElement r1_ : r.getEdges()) {
				Node r1 = ((Edge) r1_).getDifferingNode(r);
				for (IElement r2_ : r1.getEdges()) {
					Node r2 = ((Edge) r2_).getDifferingNode(r1);
					add(set, l, r, l1, r1, r2);
				}
			}
		}
	}

}
