package dna.metrics.triangles.closed;

import java.util.HashSet;
import java.util.Set;

import dna.graph.old.OldGraph;
import dna.graph.old.OldNode;
import dna.metrics.triangles.open.OpenTriangle;

public class ClosedTriangle {
	public ClosedTriangle(OldNode v1, OldNode v2, OldNode v3) {
		this.nodes = new HashSet<OldNode>();
		this.nodes.add(v1);
		this.nodes.add(v2);
		this.nodes.add(v3);
	}

	private Set<OldNode> nodes;

	public Set<OldNode> getNodes() {
		return this.nodes;
	}

	public boolean containsNode(OldNode node) {
		return this.nodes.contains(node);
	}

	public String toString() {
		StringBuffer buff = new StringBuffer();
		for (OldNode node : this.nodes) {
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
		for (OldNode node : this.nodes) {
			if (buff.length() == 0) {
				buff.append(node.getIndex());
			} else {
				buff.append("," + node.getIndex());
			}
		}
		return buff.toString();
	}

	public static ClosedTriangle fromString(String s, OldGraph g) {
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
