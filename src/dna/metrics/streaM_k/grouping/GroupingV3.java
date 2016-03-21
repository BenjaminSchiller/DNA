package dna.metrics.streaM_k.grouping;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class GroupingV3 extends GroupingV1 {

	@Override
	protected String getKey(IGraph g, Node a, Node b, String group) {
		String[] temp = group.split(";");

		Node c = g.getNode(Integer.parseInt(temp[0]));
		Node d = g.getNode(Integer.parseInt(temp[1]));

		return "" + (a.hasEdge(a, c) ? '1' : '0')
				+ (a.hasEdge(a, d) ? '1' : '0') + (b.hasEdge(b, c) ? '1' : '0')
				+ (b.hasEdge(b, d) ? '1' : '0') + (c.hasEdge(c, d) ? '1' : '0');
	}

	/*
	 * 4 4 4 4 4 4 4 4 4 4 4 4 4 4 4
	 */

	@Override
	protected HashSet<String> getGroups4(Edge e) {
		HashSet<String> set = new HashSet<String>();

		Node a = e.getN1();
		Node b = e.getN2();

		for (IElement c_ : a.getEdges()) {
			Node c = ((Edge) c_).getDifferingNode(a);

			// I.a
			for (IElement d_ : c.getEdges()) {
				Node d = ((Edge) d_).getDifferingNode(c);
				if (!d.equals(a) && !d.equals(b)) {
					add(set, c, d);
				}
			}

			// II.a
			for (IElement d_ : a.getEdges()) {
				Node d = ((Edge) d_).getDifferingNode(a);
				if (c.getIndex() < d.getIndex()) {
					add(set, c, d);
				}
			}

			// III
			for (IElement d_ : b.getEdges()) {
				Node d = ((Edge) d_).getDifferingNode(b);
				if (!d.equals(c)) {
					add(set, c, d);
				}
			}
		}

		for (IElement c_ : b.getEdges()) {
			Node c = ((Edge) c_).getDifferingNode(b);

			// I.b
			for (IElement d_ : c.getEdges()) {
				Node d = ((Edge) d_).getDifferingNode(c);
				if (!d.equals(a) && !d.equals(b)) {
					add(set, c, d);
				}
			}

			// II.b
			for (IElement d_ : b.getEdges()) {
				Node d = ((Edge) d_).getDifferingNode(b);
				if (c.getIndex() < d.getIndex()) {
					add(set, c, d);
				}
			}
		}

		return set;
	}

	protected static void add(HashSet<String> set, Node n1, Node n2) {
		if (n1.getIndex() < n2.getIndex()) {
			set.add(n1.getIndex() + ";" + n2.getIndex());
		} else {
			set.add(n2.getIndex() + ";" + n1.getIndex());
		}
	}

	protected static void add4(HashSet<String> set, Node l, Node r) {
		// L1, L1
		int counter1 = 0;
		for (IElement l1_ : l.getEdges()) {
			Node l1 = ((Edge) l1_).getDifferingNode(l);
			int counter2 = 0;
			for (IElement l11_ : l.getEdges()) {
				if (counter2 > counter1) {
					Node l11 = ((Edge) l11_).getDifferingNode(l);
					add(set, l, r, l1, l11);
				}
				counter2++;
			}
			counter1++;
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
}
