package dna.metrics.streaM_k.grouping;

import dna.graph.Graph;
import dna.graph.nodes.Node;

public class GroupingV5 extends GroupingV3 {

	@Override
	public int[] getKeys(Graph g, Node a, Node b, String group) {
		int key0 = 0;

		String[] temp = group.split(";");

		Node[] nodes = new Node[temp.length + 2];
		nodes[0] = a;
		nodes[1] = b;
		for (int i = 0; i < temp.length; i++) {
			nodes[i + 2] = g.getNode(Integer.parseInt(temp[i]));
		}

		int offset = 1;

		for (int i = 0; i < nodes.length; i++) {
			for (int j = i + 1; j < nodes.length; j++) {
				if (j == 1) {
					continue;
				}
				key0 = key0 << 1;
				if (nodes[i].hasEdge(nodes[i], nodes[j])) {
					key0++;
				}
				offset = offset << 1;
			}
		}
		return new int[] { key0, key0 + offset };
	}
}
