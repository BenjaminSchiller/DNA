package dna.graph.directed;

import java.util.HashSet;

import com.google.common.collect.Iterables;

public class DirectedNodeHs extends DirectedNode {

	private HashSet<DirectedEdge> in;

	private HashSet<DirectedEdge> out;

	private Iterable<DirectedEdge> all;

	private HashSet<DirectedNode> neighbors;

	public DirectedNodeHs(int index) {
		super(index);
		this.in = new HashSet<DirectedEdge>();
		this.out = new HashSet<DirectedEdge>();
		this.neighbors = new HashSet<DirectedNode>();
		this.all = Iterables.unmodifiableIterable(Iterables.concat(in, out));
	}

	public DirectedNodeHs(String str) {
		super(Integer.parseInt(str));
		this.in = new HashSet<DirectedEdge>();
		this.out = new HashSet<DirectedEdge>();
		this.neighbors = new HashSet<DirectedNode>();
		this.all = Iterables.unmodifiableIterable(Iterables.concat(in, out));
	}

	@Override
	public Iterable<DirectedEdge> getIncomingEdges() {
		return this.in;
	}

	@Override
	public Iterable<DirectedEdge> getOutgoingEdges() {
		return this.out;
	}

	@Override
	public Iterable<DirectedNode> getNeighbors() {
		return this.neighbors;
	}

	@Override
	public int getNeighborCount() {
		return this.neighbors.size();
	}

	@Override
	public boolean hasNeighbor(DirectedNode n) {
		return this.neighbors.contains(n);
	}

	@Override
	public Iterable<DirectedEdge> getEdges() {
		return this.all;
	}

	@Override
	public int getDegree() {
		return this.in.size() + this.out.size();
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
			boolean success = this.out.add(e);
			if (this.in.contains(e.invert())) {
				success &= this.neighbors.add(e.getDst());
			}
			return success;
		}
		if (e.getDst().getIndex() == this.index) {
			boolean success = this.in.add(e);
			if (this.out.contains(e.invert())) {
				success &= this.neighbors.add(e.getSrc());
			}
			return success;
		}
		return false;
	}

	@Override
	public boolean removeEdge(DirectedEdge e) {
		if (e.getSrc().getIndex() == this.index) {
			this.neighbors.remove(e.getDst());
			return this.out.remove(e);
		}
		if (e.getDst().getIndex() == this.index) {
			this.neighbors.remove(e.getSrc());
			return this.in.remove(e);
		}
		return false;
	}

}
