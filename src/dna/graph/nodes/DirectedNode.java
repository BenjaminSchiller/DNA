package dna.graph.nodes;

import com.google.common.collect.Iterables;

import dna.graph.IElement;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;

public class DirectedNode extends Node {
	private IEdgeListDatastructure in;
	private IEdgeListDatastructure out;
	private Iterable<IElement> all;

	private INodeListDatastructure neighbors;

	public DirectedNode(int i, GraphDataStructure gds) {
		super(i, gds);
	}

	public DirectedNode(String str, GraphDataStructure gds) {
		super(str, gds);
	}

	@Override
	public void init(GraphDataStructure gds) {
		this.in = (IEdgeListDatastructure) gds.newList(ListType.LocalEdgeList);
		this.out = (IEdgeListDatastructure) gds.newList(ListType.LocalEdgeList);
		this.neighbors = (INodeListDatastructure) gds.newList(ListType.LocalNodeList);
		this.all = Iterables.unmodifiableIterable(Iterables.concat(in, out));
	}

	@Override
	public boolean hasEdge(Edge eIn) {
		if (!(eIn instanceof DirectedEdge))
			return false;
		DirectedEdge e = (DirectedEdge) eIn;
		return e.getSrc().getIndex() == this.index && this.out.contains(e)
				|| e.getDst().getIndex() == this.index && this.in.contains(e);
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

	public String toString() {
		return super.toString() + " (" + this.in.size() + "/" + this.out.size()
				+ ")";
	}

	@Override
	public void switchDataStructure(ListType type,
			IDataStructure newDatastructure) {
		switch (type) {
		case GlobalEdgeList:
		case GlobalNodeList:
			System.err.println("A node is not responsible for changing global lists!");
			break;
		case LocalEdgeList:
			this.in = (IEdgeListDatastructure) ((IEdgeListDatastructureReadable) this.in).switchTo(newDatastructure);
			this.out = (IEdgeListDatastructure) ((IEdgeListDatastructureReadable) this.out).switchTo(newDatastructure);
			break;
		case LocalNodeList:
			this.neighbors = (INodeListDatastructure) ((INodeListDatastructureReadable) this.neighbors).switchTo(newDatastructure);
			break;		
		}
	}
}
