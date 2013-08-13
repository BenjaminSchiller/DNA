package Graph;

import java.lang.reflect.InvocationTargetException;

import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;

public class UndirectedNode extends Node {
	private IEdgeListDatastructure edges;
	public final static Class<? extends Edge> edgeType = UndirectedEdge.class;

	public UndirectedNode(int index, Class<? extends IEdgeListDatastructure> edgeListType, Class<? extends INodeListDatastructure> nodeListType) {
		super(index, edgeListType, nodeListType);
	}

	public UndirectedNode(String str, Class<? extends IEdgeListDatastructure> edgeListType, Class<? extends INodeListDatastructure> nodeListType) {
		super(str, edgeListType, nodeListType);
	}
	
	@Override
	protected void init() {
		try {
			this.edges = this.edgeListType.getConstructor().newInstance();
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
	}

	public int getDegree() {
		return this.edges.size();
	}

	public void print() {
		System.out.println(this.toString());
		System.out.println("Edges: " + this.getEdges());
	}

	@Override
	public boolean hasEdge(Edge e) {
		return this.edges.contains(e);
	}

	@Override
	public boolean addEdge(Edge e) {
		return !this.edges.contains(e) && this.edges.add(e);
	}

	@Override
	public boolean removeEdge(Edge e) {
		return this.edges.removeEdge(e);
	}

	@Override
	public Iterable<Edge> getEdges() {
		return this.edges;
	}

}
