package dna.metrics.connectivity;

import java.util.HashSet;

import dna.graph.nodes.Node;

public class WCComponent {
	public HashSet<Node> nodes = new HashSet<Node>();
	public int index;

	public WCComponent(int index) {
		this.index = index;
	}

	public String toString() {
		return "C" + this.index + ":" + this.nodes.size();
	}
}
