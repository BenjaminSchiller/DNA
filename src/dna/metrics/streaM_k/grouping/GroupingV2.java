package dna.metrics.streaM_k.grouping;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class GroupingV2 extends GroupingV1 {
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
