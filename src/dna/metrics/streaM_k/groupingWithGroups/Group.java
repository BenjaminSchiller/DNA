package dna.metrics.streaM_k.groupingWithGroups;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import dna.graph.datastructures.GDS;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;

public class Group {
	public Node[] nodes;
	public int hashCode;
	public static final Comparator<Node> comp = new NodeSorter();

	public Group(Node... nodes) {
		this.nodes = nodes;
		Arrays.sort(this.nodes, comp);
		if (nodes.length == 1) {
			this.hashCode = nodes[0].getIndex();
		} else if (nodes.length == 2) {
			this.hashCode = nodes[0].getIndex() + 65536 * nodes[1].getIndex();
		} else if (nodes.length == 3) {
			this.hashCode = nodes[0].getIndex() + 1024 * nodes[1].getIndex()
					+ 1048576 * nodes[2].getIndex();
		} else if (nodes.length == 4) {
			this.hashCode = nodes[0].getIndex() + 256 * nodes[1].getIndex()
					+ 65536 * nodes[2].getIndex() + 16777216
					* nodes[3].getIndex();
		} else {
			throw new IllegalArgumentException("unsupported group size: "
					+ nodes.length);
		}
	}

	@Override
	public boolean equals(Object obj) {
		Group g = (Group) obj;
		if (this.nodes.length == 1) {
			return nodes[0].equals(g.nodes[0]);
		} else if (this.nodes.length == 2) {
			return nodes[0].equals(g.nodes[0]) && nodes[1].equals(g.nodes[1]);
		} else if (this.nodes.length == 3) {
			return nodes[0].equals(g.nodes[0]) && nodes[1].equals(g.nodes[1])
					&& nodes[2].equals(g.nodes[2]);
		} else if (this.nodes.length == 3) {
			return nodes[0].equals(g.nodes[0]) && nodes[1].equals(g.nodes[1])
					&& nodes[2].equals(g.nodes[2])
					&& nodes[3].equals(g.nodes[3]);
		} else if (this.nodes.length == 4) {
			return nodes[0].equals(g.nodes[0]) && nodes[1].equals(g.nodes[1])
					&& nodes[2].equals(g.nodes[2])
					&& nodes[3].equals(g.nodes[3])
					&& nodes[3].equals(g.nodes[4]);
		}
		throw new IllegalArgumentException("unsupported group size: "
				+ nodes.length);
	}

	@Override
	public int hashCode() {
		return this.hashCode;
	}

	public int[] getKeys(Node a, Node b) {
		int key0 = 0;

		if (this.nodes.length == 1) {

			key0 += a.hasEdge(a, nodes[0]) ? 2 : 0;
			key0 += b.hasEdge(b, nodes[0]) ? 1 : 0;

			return new int[] { key0, key0 + 4 };

		} else if (this.nodes.length == 2) {

			key0 += a.hasEdge(a, nodes[0]) ? 16 : 0;
			key0 += a.hasEdge(a, nodes[1]) ? 8 : 0;
			key0 += b.hasEdge(b, nodes[0]) ? 4 : 0;
			key0 += b.hasEdge(b, nodes[1]) ? 2 : 0;
			key0 += nodes[0].hasEdge(nodes[0], nodes[1]) ? 1 : 0;

			return new int[] { key0, key0 + 32 };

		} else if (this.nodes.length == 3) {

			key0 += a.hasEdge(a, nodes[0]) ? 256 : 0;
			key0 += a.hasEdge(a, nodes[1]) ? 128 : 0;
			key0 += a.hasEdge(a, nodes[2]) ? 64 : 0;
			key0 += b.hasEdge(b, nodes[0]) ? 32 : 0;
			key0 += b.hasEdge(b, nodes[1]) ? 16 : 0;
			key0 += b.hasEdge(b, nodes[2]) ? 8 : 0;
			key0 += nodes[0].hasEdge(nodes[0], nodes[1]) ? 4 : 0;
			key0 += nodes[0].hasEdge(nodes[0], nodes[2]) ? 2 : 0;
			key0 += nodes[1].hasEdge(nodes[1], nodes[2]) ? 1 : 0;

			return new int[] { key0, key0 + 512 };

		} else if (this.nodes.length == 4) {

			key0 += a.hasEdge(a, nodes[0]) ? 8192 : 0;
			key0 += a.hasEdge(a, nodes[1]) ? 4096 : 0;
			key0 += a.hasEdge(a, nodes[2]) ? 2048 : 0;
			key0 += a.hasEdge(a, nodes[3]) ? 1024 : 0;
			key0 += b.hasEdge(b, nodes[0]) ? 512 : 0;
			key0 += b.hasEdge(b, nodes[1]) ? 256 : 0;
			key0 += b.hasEdge(b, nodes[2]) ? 128 : 0;
			key0 += b.hasEdge(b, nodes[3]) ? 64 : 0;
			key0 += nodes[0].hasEdge(nodes[0], nodes[1]) ? 32 : 0;
			key0 += nodes[0].hasEdge(nodes[0], nodes[2]) ? 16 : 0;
			key0 += nodes[0].hasEdge(nodes[0], nodes[3]) ? 8 : 0;
			key0 += nodes[1].hasEdge(nodes[1], nodes[2]) ? 4 : 0;
			key0 += nodes[1].hasEdge(nodes[1], nodes[3]) ? 2 : 0;
			key0 += nodes[2].hasEdge(nodes[2], nodes[3]) ? 1 : 0;

			return new int[] { key0, key0 + 16384 };

		} else {
			throw new IllegalArgumentException("unsupported group size: "
					+ nodes.length);
		}
	}

	public String toString() {
		final String sep = "-";
		if (this.nodes.length == 1) {
			return nodes[0].getIndex() + "";
		} else if (this.nodes.length == 2) {
			return nodes[0].getIndex() + sep + nodes[1].getIndex();
		} else if (this.nodes.length == 3) {
			return nodes[0].getIndex() + sep + nodes[1].getIndex() + sep
					+ nodes[2].getIndex();
		} else if (this.nodes.length == 4) {
			return nodes[0].getIndex() + sep + nodes[1].getIndex() + sep
					+ nodes[2].getIndex() + sep + nodes[3].getIndex();
		}
		throw new IllegalArgumentException("unsupported group size: "
				+ nodes.length);
	}

	public static void main(String[] args) {
		GraphDataStructure gds = GDS.undirected();
		Node n1 = gds.newNodeInstance(1);
		Node n2 = gds.newNodeInstance(2);
		Node n3 = gds.newNodeInstance(3);
		Node n4 = gds.newNodeInstance(4);
		Node n5 = gds.newNodeInstance(5);

		Group g1 = new Group(n1, n2);
		Group g2 = new Group(n1, n3);
		Group g3 = new Group(n2, n1);
		Group g4 = new Group(n4, n5);

		System.out.println(g1.equals(g1));
		System.out.println(g1.equals(g2));
		System.out.println(g1.equals(g3));
		System.out.println();
		System.out.println(g2.equals(g1));
		System.out.println(g2.equals(g2));
		System.out.println(g2.equals(g3));
		System.out.println();
		System.out.println(g3.equals(g1));
		System.out.println(g3.equals(g2));
		System.out.println(g3.equals(g3));

		HashSet<Group> groups = new HashSet<Group>();
		groups.add(g1);
		groups.add(g2);
		groups.add(g3);
		groups.add(g4);
		System.out.println(groups);
	}

	public static class NodeSorter implements Comparator<Node> {
		@Override
		public int compare(Node n1, Node n2) {
			return n1.getIndex() - n2.getIndex();
		}
	}
}
