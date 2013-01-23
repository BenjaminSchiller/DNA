package dynamicGraphs.metrics.triangles.closed;

import java.util.HashSet;
import java.util.Set;

import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.Node;
import dynamicGraphs.metrics.triangles.open.OpenTriangle;

public class ClosedTriangle {
	public ClosedTriangle(Node v1, Node v2, Node v3) {
		this.nodes = new HashSet<Node>();
		this.nodes.add(v1);
		this.nodes.add(v2);
		this.nodes.add(v3);
	}

	private Set<Node> nodes;

	public Set<Node> getNodes() {
		return this.nodes;
	}

	public boolean containsNode(Node node) {
		return this.nodes.contains(node);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (Node node : this.nodes) {
			if (buff.length() == 0) {
				buff.append("T(" + node.getIndex());
			} else {
				buff.append("," + node.getIndex());
			}
		}
		buff.append(")");
		return buff.toString();
	}

	public String getStringRepresentation() {
		StringBuffer buff = new StringBuffer();
		for (Node node : this.nodes) {
			if (buff.length() == 0) {
				buff.append(node.getIndex());
			} else {
				buff.append("," + node.getIndex());
			}
		}
		return buff.toString();
	}

	public static ClosedTriangle fromString(String s, Graph g) {
		String[] temp = s.split("|");
		return new ClosedTriangle(g.getNode(Integer.parseInt(temp[0])),
				g.getNode(Integer.parseInt(temp[1])), g.getNode(Integer
						.parseInt(temp[2])));
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof OpenTriangle)) {
			return false;
		}
		return this.nodes.containsAll(((ClosedTriangle) o).getNodes());
	}

	public int hashCode() {
		return this.getStringRepresentation().hashCode();
	}
}
