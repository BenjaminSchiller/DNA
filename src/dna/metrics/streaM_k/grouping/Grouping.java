package dna.metrics.streaM_k.grouping;

import java.util.Arrays;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public abstract class Grouping {

	public int[] getKeys(Graph g, Node n1, Node n2, String group) {
		int key0 = Integer.parseInt('0' + this.getKey(g, n1, n2, group), 2);
		int key1 = Integer.parseInt('1' + this.getKey(g, n1, n2, group), 2);
		return new int[] { key0, key1 };
	}

	protected String getKey(Graph g, Node n1, Node n2, String group) {
		String[] temp = group.split(";");
		Node[] nodes = new Node[temp.length + 2];
		nodes[0] = n1;
		nodes[1] = n2;
		for (int i = 0; i < temp.length; i++) {
			nodes[i + 2] = g.getNode(Integer.parseInt(temp[i]));
		}
		String key = "";
		for (int i = 0; i < nodes.length; i++) {
			for (int j = i + 1; j < nodes.length; j++) {
				if ((i == 0 && j == 1)) {
					continue;
				}
				if (g.containsEdge(nodes[i], nodes[j])) {
					key += '1';
				} else {
					key += '0';
				}
			}
		}
		return key;
	}

	protected static void add(HashSet<String> set, Node l, Node r,
			Node... nodes) {
		for (int i = 0; i < nodes.length; i++) {
			if (nodes[i].equals(l) || nodes[i].equals(r)) {
				return;
			}
			for (int j = i + 1; j < nodes.length; j++) {
				if (nodes[i].equals(nodes[j])) {
					return;
				}
			}
		}
		int[] indexes = new int[nodes.length];
		for (int i = 0; i < nodes.length; i++) {
			indexes[i] = nodes[i].getIndex();
		}
		Arrays.sort(indexes);
		StringBuffer buff = new StringBuffer();
		for (int index : indexes) {
			if (buff.length() > 0) {
				buff.append(";");
			}
			buff.append(index);
		}
		set.add(buff.toString());
	}

	public HashSet<String> getGroups(Graph g, int nodes, Edge e) {
		if (nodes == 3) {
			return getGroups3(e);
		} else if (nodes == 4) {
			return getGroups4(e);
		} else if (nodes == 5) {
			return getGroups5(e);
		} else {
			throw new IllegalArgumentException("StreaM_k not implemented for "
					+ nodes + "-vertex motifs yet");
		}
	}

	protected abstract HashSet<String> getGroups3(Edge e);

	protected abstract HashSet<String> getGroups4(Edge e);

	protected abstract HashSet<String> getGroups5(Edge e);

}
