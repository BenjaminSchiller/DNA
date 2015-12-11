package dna.metrics.streaM_k.groupingWithGroups;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class Grouping2V1 extends Grouping2 {

	@Override
	protected HashSet<Group> getGroups3(Edge e) {
		HashSet<Group> set = new HashSet<Group>();
		Node n1 = e.getN1();
		Node n2 = e.getN2();

		for (IElement a_ : n1.getEdges()) {
			Node a = ((Edge) a_).getDifferingNode(n1);
			set.add(new Group(a));
		}

		for (IElement b_ : n2.getEdges()) {
			Node b = ((Edge) b_).getDifferingNode(n2);
			set.add(new Group(b));
		}

		return set;
	}

	@Override
	protected HashSet<Group> getGroups4(Edge e) {
		HashSet<Group> set = new HashSet<Group>();
		Node n1 = e.getN1();
		Node n2 = e.getN2();

		// AB
		for (IElement a_ : n1.getEdges()) {
			Node a = ((Edge) a_).getDifferingNode(n1);
			for (IElement b_ : n2.getEdges()) {
				Node b = ((Edge) b_).getDifferingNode(n2);
				if (!a.equals(b)) {
					set.add(new Group(a, b));
				}
			}
		}

		// AA2
		int counter = 0;
		for (IElement a_ : n1.getEdges()) {
			Node a = ((Edge) a_).getDifferingNode(n1);
			int counter2 = 0;
			for (IElement a2_ : n1.getEdges()) {
				if (counter2++ <= counter) {
					continue;
				}
				Node a2 = ((Edge) a2_).getDifferingNode(n1);
				set.add(new Group(a, a2));
			}
			counter++;
		}

		// BB2
		counter = 0;
		for (IElement b_ : n2.getEdges()) {
			Node b = ((Edge) b_).getDifferingNode(n2);
			int counter2 = 0;
			for (IElement b2_ : n2.getEdges()) {
				if (counter2++ <= counter) {
					continue;
				}
				Node b2 = ((Edge) b2_).getDifferingNode(n2);
				set.add(new Group(b, b2));
			}
			counter++;
		}

		// AC
		for (IElement a_ : n1.getEdges()) {
			Node a = ((Edge) a_).getDifferingNode(n1);
			for (IElement c_ : a.getEdges()) {
				Node c = ((Edge) c_).getDifferingNode(a);
				if (!c.equals(n1) && !c.equals(n2)) {
					set.add(new Group(a, c));
				}
			}
		}

		// BD
		for (IElement b_ : n2.getEdges()) {
			Node b = ((Edge) b_).getDifferingNode(n2);
			for (IElement d_ : b.getEdges()) {
				Node d = ((Edge) d_).getDifferingNode(b);
				if (!d.equals(n1) && !d.equals(n2)) {
					set.add(new Group(b, d));
				}
			}
		}

		return set;
	}

	@Override
	protected HashSet<Group> getGroups5(Edge e) {
		HashSet<Group> set = new HashSet<Group>();

		Node n1 = e.getN1();
		Node n2 = e.getN2();

		addAAA(set, n1, n2);
		addAAB(set, n1, n2);
		addABB(set, n1, n2);
		addABC(set, n1, n2);
		addAAD(set, n1, n2);
		addABD(set, n1, n2);

		addAAA(set, n2, n1);
		addAAB(set, n2, n1);
		addABB(set, n2, n1);
		addABC(set, n2, n1);
		addAAD(set, n2, n1);
		addABD(set, n2, n1);

		return set;
	}

	protected void add(Node n1, Node n2, HashSet<Group> set, Group g) {
		set.add(g);
		boolean ok = true;
		ok &= !n1.equals(g.nodes[0]);
		ok &= !n1.equals(g.nodes[1]);
		ok &= !n1.equals(g.nodes[2]);
		ok &= !n2.equals(g.nodes[0]);
		ok &= !n2.equals(g.nodes[1]);
		ok &= !n2.equals(g.nodes[2]);
		ok &= !g.nodes[0].equals(g.nodes[1]);
		ok &= !g.nodes[0].equals(g.nodes[2]);
		ok &= !g.nodes[1].equals(g.nodes[2]);
		if (!ok) {
			throw new IllegalStateException("NOT OK!!!: " + n1 + " / " + n2
					+ " => " + g);
		}
	}

	protected void addAAA(HashSet<Group> set, Node n1, Node n2) {
		for (IElement a1_ : n1.getEdges()) {
			Node a1 = ((Edge) a1_).getDifferingNode(n1);
			for (IElement a2_ : n1.getEdges()) {
				Node a2 = ((Edge) a2_).getDifferingNode(n1);
				if (a2.equals(a1)) {
					continue;
				}
				for (IElement a3_ : n1.getEdges()) {
					Node a3 = ((Edge) a3_).getDifferingNode(n1);
					// set.add(new Group(a1, a2, a3));
					if (!a3.equals(a1) && !a3.equals(a2)) {
						add(n1, n2, set, new Group(a1, a2, a3));
					}
				}
			}
		}
	}

	protected void addAAB(HashSet<Group> set, Node n1, Node n2) {
		int counter1 = 0;
		for (IElement a1_ : n1.getEdges()) {
			Node a1 = ((Edge) a1_).getDifferingNode(n1);
			for (IElement a2_ : n1.getEdges()) {
				Node a2 = ((Edge) a2_).getDifferingNode(n1);
				if (a2.equals(a1)) {
					continue;
				}
				for (IElement b_ : a1.getEdges()) {
					Node b = ((Edge) b_).getDifferingNode(a1);
					if (!b.equals(a2) && !b.equals(n1) && !b.equals(n2)) {
						// set.add(new Group(a1, a2, b));
						add(n1, n2, set, new Group(a1, a2, b));
					}
				}
			}
		}
	}

	protected void addABB(HashSet<Group> set, Node n1, Node n2) {
		for (IElement a_ : n1.getEdges()) {
			Node a = ((Edge) a_).getDifferingNode(n1);
			for (IElement b1_ : a.getEdges()) {
				Node b1 = ((Edge) b1_).getDifferingNode(a);
				if (b1.equals(n1) || b1.equals(n2)) {
					continue;
				}
				for (IElement b2_ : a.getEdges()) {
					Node b2 = ((Edge) b2_).getDifferingNode(a);
					if (b2.equals(b1)) {
						continue;
					}
					if (!b2.equals(n1) && !b2.equals(n2) && !b2.equals(b1)) {
						// set.add(new Group(a, b1, b2));
						// System.out.println(n1 + " // " + n2);
						// System.out.println(a + " / " + b1 + " / " + b2);
						// System.out.println(counter1 + " - " + counter2);
						add(n1, n2, set, new Group(a, b1, b2));
					}
				}
			}
		}
	}

	protected void addABC(HashSet<Group> set, Node n1, Node n2) {
		for (IElement a_ : n1.getEdges()) {
			Node a = ((Edge) a_).getDifferingNode(n1);
			for (IElement b_ : a.getEdges()) {
				Node b = ((Edge) b_).getDifferingNode(a);
				if (b.equals(n1) || b.equals(n2)) {
					continue;
				}
				for (IElement c_ : b.getEdges()) {
					Node c = ((Edge) c_).getDifferingNode(b);
					if (!c.equals(a) && !c.equals(n1) && !c.equals(n2)) {
						// set.add(new Group(a, b, c));
						add(n1, n2, set, new Group(a, b, c));
					}
				}
			}
		}
	}

	protected void addAAD(HashSet<Group> set, Node n1, Node n2) {
		for (IElement a1_ : n1.getEdges()) {
			Node a1 = ((Edge) a1_).getDifferingNode(n1);
			int counter2 = 0;
			for (IElement a2_ : n1.getEdges()) {
				Node a2 = ((Edge) a2_).getDifferingNode(n1);
				if (a2.equals(a1)) {
					continue;
				}
				for (IElement d_ : n2.getEdges()) {
					Node d = ((Edge) d_).getDifferingNode(n2);
					if (!d.equals(a1) && !d.equals(a2)) {
						// set.add(new Group(a1, a2, d));
						add(n1, n2, set, new Group(a1, a2, d));
					}
				}
			}
		}
	}

	protected void addABD(HashSet<Group> set, Node n1, Node n2) {
		for (IElement a_ : n1.getEdges()) {
			Node a = ((Edge) a_).getDifferingNode(n1);
			for (IElement b_ : a.getEdges()) {
				Node b = ((Edge) b_).getDifferingNode(a);
				if (b.equals(n1) || b.equals(n2)) {
					continue;
				}
				for (IElement d_ : n2.getEdges()) {
					Node d = ((Edge) d_).getDifferingNode(n2);
					if (!d.equals(a) && !d.equals(b)) {
						// set.add(new Group(a, b, d));
						add(n1, n2, set, new Group(a, b, d));
					}
				}
			}
		}
	}

	@Override
	protected HashSet<Group> getGroups6(Edge e) {
		// TODO Auto-generated method stub
		return null;
	}

}
