package dna.graph.directed;

import java.util.ArrayList;
import java.util.Collection;

public class DirectedNodeAl extends DirectedNode {

	private ArrayList<DirectedEdge> in;

	private ArrayList<DirectedEdge> out;

	public DirectedNodeAl(int index) {
		super(index);
		this.in = new ArrayList<DirectedEdge>();
		this.out = new ArrayList<DirectedEdge>();
	}

	@Override
	public Collection<DirectedEdge> getIncomingEdges() {
		return this.in;
	}

	@Override
	public Collection<DirectedEdge> getOutgoingEdges() {
		return this.out;
	}

	@Override
	public int getInDegree() {
		return this.in.size();
	}

	@Override
	public int getOutDegree() {
		return this.out.size();
	}

	@Override
	public boolean hasEdge(DirectedEdge e) {
		return e.getSrc().getIndex() == this.index && this.out.contains(e)
				|| e.getDst().getIndex() == this.index && this.in.contains(e);
	}

	@Override
	public boolean addEdge(DirectedEdge e) {
		if (e.getSrc().getIndex() == this.index) {
			return this.out.add(e);
		}
		if (e.getDst().getIndex() == this.index) {
			return this.in.add(e);
		}
		return false;
	}

	@Override
	public boolean removeEdge(DirectedEdge e) {
		if (e.getSrc().getIndex() == this.index) {
			return this.out.remove(e);
		}
		if (e.getDst().getIndex() == this.index) {
			return this.in.remove(e);
		}
		return false;
	}

}
