package Graph;

import java.lang.reflect.InvocationTargetException;

import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;

import com.google.common.collect.Iterables;

public class DirectedNode extends Node {
	private IEdgeListDatastructure in;
	private IEdgeListDatastructure out;
	private Iterable<Edge> all;
	public final static Class<? extends Edge> edgeType = DirectedEdge.class;
	
	private INodeListDatastructure neighbors;

	public DirectedNode(int i, Class<? extends IEdgeListDatastructure> edgeListType, Class<? extends INodeListDatastructure> nodeListType) {
		super(i, edgeListType, nodeListType);
		init();
	}
	
	public DirectedNode(String str, Class<? extends IEdgeListDatastructure> edgeListType, Class<? extends INodeListDatastructure> nodeListType) {
		super(str, edgeListType, nodeListType);
		init();
	}
	
	protected void init() {
		try {
			this.in = this.edgeListType.getConstructor(Class.class).newInstance(edgeType);
			this.out = this.edgeListType.getConstructor(Class.class).newInstance(edgeType);
			this.neighbors = this.nodeListType.getConstructor(Class.class).newInstance(this.getClass());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.all = Iterables.unmodifiableIterable(Iterables.concat(in, out));
	}

	@Override
	public boolean hasEdge(Edge eIn) {
		if ( !(eIn instanceof DirectedEdge)) return false;
		DirectedEdge e = (DirectedEdge)eIn;
		return e.getSrc().getIndex() == this.index && this.out.contains(e)
				|| e.getDst().getIndex() == this.index && this.in.contains(e);
	}

	@Override
	public boolean addEdge(Edge eIn) {
		if ( !(eIn instanceof DirectedEdge)) return false;
		DirectedEdge e = (DirectedEdge)eIn;
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
		if ( !(eIn instanceof DirectedEdge)) return false;
		DirectedEdge e = (DirectedEdge)eIn;
		if (e.getSrc().getIndex() == this.index) {
			this.neighbors.removeNode(e.getDst());
			return this.out.removeEdge(e);
		}
		if (e.getDst().getIndex() == this.index) {
			this.neighbors.removeNode(e.getSrc());
			return this.in.removeEdge(e);
		}
		return false;
	}

	@Override
	public Iterable<Edge> getEdges() {
		return this.all;
	}
	
	public Iterable<DirectedEdge> getIncomingEdges() {
		return this.in;
	}

	public Iterable<DirectedEdge> getOutgoingEdges() {
		return this.out;
	}

	public Iterable<DirectedNode> getNeighbors() {
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
