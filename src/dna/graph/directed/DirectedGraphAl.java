package dna.graph.directed;

import java.util.ArrayList;
import java.util.Collection;

public abstract class DirectedGraphAl extends DirectedGraph {

	private ArrayList<DirectedNode> nodes;

	public DirectedGraphAl(String name, long timestamp, int nodes) {
		super(name, timestamp);
		this.nodes = new ArrayList<DirectedNode>(nodes);
	}

	@Override
	public DirectedNode getNode(int index) {
		return this.nodes.get(index);
	}

	@Override
	public int getNodeCount() {
		return this.nodes.size();
	}

	@Override
	public Collection<DirectedNode> getNodes() {
		return this.nodes;
	}

	@Override
	public boolean addNode(DirectedNode n) {
		if (this.nodes.size() < n.getIndex() + 1
				|| this.nodes.get(n.getIndex()) == null) {
			this.nodes.add(n.getIndex(), n);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeNode(DirectedNode n) {
		return this.nodes.remove(n);
	}

}
