package dna.graph.undirected;

import java.util.ArrayList;
import java.util.Collection;

public abstract class UndirectedGraphAl extends UndirectedGraph {

	private ArrayList<UndirectedNode> nodes;

	public UndirectedGraphAl(String name, long timestamp, int nodes) {
		super(name, timestamp);
		this.nodes = new ArrayList<UndirectedNode>(nodes);
	}

	@Override
	public UndirectedNode getNode(int index) {
		return this.nodes.get(index);
	}

	@Override
	public int getNodeCount() {
		return this.nodes.size();
	}

	@Override
	public Collection<UndirectedNode> getNodes() {
		return this.nodes;
	}

	@Override
	public boolean addNode(UndirectedNode n) {
		if (this.nodes.size() < n.getIndex() + 1
				|| this.nodes.get(n.getIndex()) == null) {
			this.nodes.add(n.getIndex(), n);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeNode(UndirectedNode n) {
		return this.nodes.remove(n);
	}

}
