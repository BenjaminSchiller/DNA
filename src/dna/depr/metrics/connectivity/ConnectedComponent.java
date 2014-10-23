package dna.depr.metrics.connectivity;

import java.util.Collection;
import java.util.HashSet;

import dna.graph.nodes.Node;

/**
 * 
 * this class represents a strongly/weakly connected component. besides an index
 * (used for identification) each component holds a set of nodes which are part
 * of this connected component.
 * 
 * @author benni
 * 
 */
public class ConnectedComponent implements Comparable<ConnectedComponent> {
	private int index;

	private HashSet<Node> nodes;

	public ConnectedComponent(int index) {
		this.index = index;
		this.nodes = new HashSet<Node>();
	}

	public ConnectedComponent(int index, Collection<Node> nodes) {
		this.index = index;
		this.nodes = new HashSet<Node>();
		for (Node n : nodes) {
			this.nodes.add(n);
		}
	}

	public void addNode(Node node) {
		this.nodes.add(node);
	}

	public void addNodes(Collection<Node> nodes) {
		for (Node node : nodes) {
			this.addNode(node);
		}
	}

	public void removeNode(Node node) {
		this.nodes.remove(node);
	}

	public void removeNodes(Collection<Node> nodes) {
		this.nodes.removeAll(nodes);
	}

	public int getIndex() {
		return this.index;
	}

	public int getSize() {
		return this.nodes.size();
	}

	public boolean containsNode(Node node) {
		return this.nodes.contains(node);
	}

	@Override
	public int compareTo(ConnectedComponent o) {
		return o.getSize() - this.getSize();
	}

	public boolean equals(Object obj) {
		return obj != null && obj instanceof ConnectedComponent
				&& ((ConnectedComponent) obj).getIndex() == this.index;
	}

	public Collection<Node> getNodes() {
		return this.nodes;
	}

	public String toString() {
		return this.index + "@" + this.getSize() + " @@ " + this.nodes;
	}
}
