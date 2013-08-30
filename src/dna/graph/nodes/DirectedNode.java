package dna.graph.nodes;

import com.google.common.collect.Iterables;

import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;

public class DirectedNode extends Node {
	private IEdgeListDatastructure in;
	private IEdgeListDatastructure out;
	private Iterable<IElement> all;

	private INodeListDatastructure neighbors;

	public final static Class<? extends Edge> edgeType = DirectedEdge.class;

	public DirectedNode(int i, GraphDataStructure gds) {
		super(i, gds);
	}

	public DirectedNode(String str, GraphDataStructure gds) {
		super(str, gds);
	}

	public void init() {
		this.in = this.gds.newNodeEdgeList();
		this.out = this.gds.newNodeEdgeList();
		this.neighbors = this.gds.newNodeList();
		this.all = Iterables.unmodifiableIterable(Iterables.concat(in, out));
	}

	@Override
	public boolean hasEdge(Edge eIn) {
		if (!(eIn instanceof DirectedEdge))
			return false;
		DirectedEdge e = (DirectedEdge) eIn;
		return e.getSrc().getIndex() == this.index && this.out.contains(e) || e.getDst().getIndex() == this.index
				&& this.in.contains(e);
	}

	@Override
	public boolean addEdge(Edge eIn) {
		if (!(eIn instanceof DirectedEdge))
			return false;
		DirectedEdge e = (DirectedEdge) eIn;
		if (e.getSrc().getIndex() == this.index) {
			boolean success = !this.out.contains(e) && this.out.add(e);
			if (success && this.in.contains(e.invert())) {
				success &= this.neighbors.add(e.getDst());
			}
			return success;
		}
		if (e.getDst().getIndex() == this.index) {
			boolean success = !this.in.contains(e) && this.in.add(e);
			if (success && this.out.contains(e.invert())) {
				success &= this.neighbors.add(e.getSrc());
			}
			return success;
		}
		return false;
	}

	@Override
	public boolean removeEdge(Edge eIn) {
		if (!(eIn instanceof DirectedEdge))
			return false;
		DirectedEdge e = (DirectedEdge) eIn;
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

	@Override
	public Iterable<IElement> getEdges() {
		return this.all;
	}

	public Iterable<IElement> getIncomingEdges() {
		return this.in;
	}

	public Iterable<IElement> getOutgoingEdges() {
		return this.out;
	}

	public Iterable<IElement> getNeighbors() {
		return this.neighbors;
	}

	public int getNeighborCount() {
		return this.neighbors.size();
	}

	public boolean hasNeighbor(DirectedNode n) {
		return this.neighbors.contains(n);
	}

	public int getDegree() {
		return this.getInDegree() + this.getOutDegree();
	}

	public int getInDegree() {
		return this.in.size();
	}

	public int getOutDegree() {
		return this.out.size();
	}

	public void print() {
		System.out.println(this.toString());
		System.out.println("In: " + this.getIncomingEdges());
		System.out.println("Out: " + this.getOutgoingEdges());
		System.out.println("Neighbors: " + this.getNeighbors());
	}
}
