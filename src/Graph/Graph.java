package Graph;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;

public class Graph {
	public INodeListDatastructure nodes;
	public IEdgeListDatastructure edges;
	public Class<? extends IEdgeListDatastructure> nodeEdgeListType;
	public Class<? extends Node> nodeType;
	public Class<? extends Edge> edgeType;
	private String name;
	private long timestamp;

	/*
	 * First parameter: type of the list that stores all nodes (needs to be accessible by index)
	 * Second parameter: type of the *global* edge list (needs to be accessible by another edge)
	 * Third parameter: local per-node edge list
	 */
	@SuppressWarnings("unchecked")
	public Graph(String name, long timestamp, Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType) {
		this.name = name;
		this.timestamp = timestamp;
		try {
			this.edgeType = (Class<? extends Edge>) nodeType.getField("edgeType").get(null);
			this.nodes = (INodeListDatastructure) nodeListType.getConstructor(nodeType.getClass())
					.newInstance(nodeType);
			this.edges = (IEdgeListDatastructure) graphEdgeListType.getConstructor(edgeType.getClass()).newInstance(
					edgeType);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException
				| InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
		}
		this.nodeEdgeListType = nodeEdgeListType;
		this.nodeType = nodeType;
	}
	
	public Graph(String name, long timestamp, Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, Class<? extends Node> nodeType, int nodeSize,
			int edgeSize) {
		this(name, timestamp, nodeListType, graphEdgeListType, nodeEdgeListType, nodeType);
		this.nodes.reinitializeWithSize(nodeSize);
		this.edges.reinitializeWithSize(edgeSize);
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

	public int getNodeCount() {
		return nodes.size();
	}

	public Collection<IElement> getNodes() {
		return nodes.getElements();
	}

	public boolean addNode(Node n) {
		return nodes.add(n);
	}

	public boolean removeNode(Node n) {
		return nodes.removeNode(n);
	}

	public boolean containsNode(Node n) {
		return nodes.contains(n);
	}

	public Node getRandomNode() {
		return (Node) nodes.getRandom();
	}

	public Edge getEdge(Edge e) {
		return edges.get(e);
	}

	public int getEdgeCount() {
		return edges.size();
	}

	public Collection<IElement> getEdges() {
		return edges.getElements();
	}

	public boolean addEdge(Edge e) {
		return edges.add(e);
	}

	public boolean removeEdge(Edge e) {
		return edges.removeEdge(e);
	}

	public boolean containsEdge(Edge e) {
		return edges.contains(e);
	}

	public Edge getRandomEdge() {
		return (Edge) edges.getRandom();
	}
	
}
