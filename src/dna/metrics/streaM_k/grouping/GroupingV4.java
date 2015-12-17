package dna.metrics.streaM_k.grouping;

import dna.graph.IGraph;
import dna.graph.nodes.Node;

public class GroupingV4 extends GroupingV3 {

	@Override
	public String getKey(IGraph g, Node a, Node b, String group) {
		String[] temp = group.split(";");

		Node c = g.getNode(Integer.parseInt(temp[0]));
		Node d = g.getNode(Integer.parseInt(temp[1]));

		return "" + (a.hasEdge(a, c) ? '1' : '0')
				+ (a.hasEdge(a, d) ? '1' : '0') + (b.hasEdge(b, c) ? '1' : '0')
				+ (b.hasEdge(b, d) ? '1' : '0') + (c.hasEdge(c, d) ? '1' : '0');

	}

	@Override
	public int[] getKeys(IGraph g, Node a, Node b, String group) {
		int key0 = 0;
		int key1 = 2;

		String[] temp = group.split(";");

		Node c = g.getNode(Integer.parseInt(temp[0]));
		Node d = g.getNode(Integer.parseInt(temp[1]));

		if (a.hasEdge(a, c)) {
			key0++;
			key1++;
		}
		key0 = key0 << 1;
		key1 = key1 << 1;

		if (a.hasEdge(a, d)) {
			key0++;
			key1++;
		}
		key0 = key0 << 1;
		key1 = key1 << 1;

		if (b.hasEdge(b, c)) {
			key0++;
			key1++;
		}
		key0 = key0 << 1;
		key1 = key1 << 1;

		if (b.hasEdge(b, d)) {
			key0++;
			key1++;
		}
		key0 = key0 << 1;
		key1 = key1 << 1;

		if (c.hasEdge(c, d)) {
			key0++;
			key1++;
		}

		return new int[] { key0, key1 };
	}
}
