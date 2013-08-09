package genericsWithTest;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import genericsWithTest.DataStructures.DataStructure;
import genericsWithTest.DataStructures.IEdgeListDatastructure;
import genericsWithTest.DataStructures.INodeListDatastructure;

public class Graph {
	public INodeListDatastructure nodes;
	public IEdgeListDatastructure edges;
	public Class<?> nodeEdgeListType;
	private String name;
	private long timestamp;

	/*
	 * First parameter: type of the list that stores all nodes (needs to be accessible by index)
	 * Second parameter: type of the *global* edge list (needs to be accessible by another edge)
	 * Third parameter: local per-node edge list
	 */
	public Graph(String name, long timestamp, Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType) {
		this.name = name;
		this.timestamp = timestamp;
		try {
			this.nodes = (INodeListDatastructure) nodeListType.getConstructor().newInstance();
			this.edges = (IEdgeListDatastructure) graphEdgeListType.getConstructor().newInstance();
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
		this.nodeEdgeListType = nodeEdgeListType;
	}
	
	public String getName() {
		return this.name;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public String toString() {
		return this.getName() + " @ " + this.getTimestamp() + " ("
				+ this.getNodeCount() + "/" + this.getEdgeCount() + ")";
	}

	public void print() {
		System.out.println(this.toString());
		System.out.println("  V = " + this.getNodes());
		System.out.println("  E = " + this.getEdges());
	}

	public Node getNode(int index) {
		return nodes.get(index);
	}

	public int getMaxNodeIndex() {
		return nodes.getMaxNodeIndex();
	}

	public abstract int getNodeCount();

	public abstract Collection<Node> getNodes();

	public abstract boolean addNode(Node n);

	public abstract boolean removeNode(Node n);

	public abstract boolean containsNode(Node n);

	public abstract Node getRandomNode();

	public Edge getEdge(Edge e) {
		return edges.get(e);
	}

	public abstract int getEdgeCount();

	public abstract Collection<Edge> getEdges();

	public abstract boolean addEdge(Edge e);

	public abstract boolean removeEdge(Edge e);

	public abstract boolean containsEdge(Edge e);

	public abstract Edge getRandomEdge();
	
}
