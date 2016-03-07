package dna.graph;

import java.math.BigInteger;
import java.util.Iterator;

import com.google.common.collect.Iterables;

import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.weights.NodeTypeFilter;
import dna.util.Log;

/**
 * Class for graphs. Methods that need special data structures are also defined
 * here, but they might throw exceptions if the wrong data structures are used
 * (eg. a data structure might not allow distinguishable access to the stored
 * elements, but the graph will perform such calls)
 * 
 * @author Nico
 * 
 */
public class Graph {
	public INodeListDatastructure nodes;
	public IEdgeListDatastructure edges;
	private String name;
	private long timestamp;
	protected GraphDataStructure gds;

	public Graph(String name, long timestamp, GraphDataStructure gds) {
		this.name = name;
		this.timestamp = timestamp;
		this.nodes = (INodeListDatastructure) gds
				.newList(ListType.GlobalNodeList);
		this.edges = (IEdgeListDatastructure) gds
				.newList(ListType.GlobalEdgeList);
		this.gds = gds;
	}

	public Graph(String name, long timestamp, GraphDataStructure gds,
			int nodeSize, int edgeSize) {
		this(name, timestamp, gds);
	}

	public boolean addNode(Node n) {
		return nodes.add(n);
	}

	public boolean containsNode(Node n) {
		return nodes.contains(n);
	}

	/**
	 * Retrieve a node by its index
	 * 
	 * @param index
	 * @return
	 */
	public Node getNode(int index) {
		if (!gds.isReadable(nodes))
			throw new RuntimeException("This is not a readable graph");
		return ((INodeListDatastructureReadable) this.nodes).get(index);
	}

	/**
	 * Retrieve a random node
	 * 
	 * @return
	 */
	public Node getRandomNode() {
		if (!gds.isReadable(nodes))
			throw new RuntimeException("This is not a readable graph");
		return (Node) ((INodeListDatastructureReadable) nodes).getRandom();
	}

	/**
	 * Retrieve a collection of all nodes within this graph
	 * 
	 * @return
	 */
	public Iterable<IElement> getNodes() {
		if (!gds.isReadable(nodes))
			throw new RuntimeException("This is not a readable graph");
		return (INodeListDatastructureReadable) nodes;
	}

	public Iterable<IElement> getNodes(String type) {
		NodeTypeFilter filter = new NodeTypeFilter(type);
		return Iterables.filter((Iterable<IElement>) nodes, filter);
	}

	public boolean removeNode(Node n) {
		return nodes.remove(n);
	}

	/**
	 * Retrieve the highest node index within this graph
	 * 
	 * @return
	 */
	public int getMaxNodeIndex() {
		return nodes.getMaxNodeIndex();
	}

	/**
	 * Retrieve the number of nodes within this graph
	 * 
	 * @return
	 */
	public int getNodeCount() {
		return nodes.size();
	}

	public boolean addEdge(Edge e) {
		return edges.add(e);
		// return this.containsNodes(e) && edges.add(e);
	}

	public boolean containsEdge(Node n1, Node n2) {
		return containsEdge(gds.getDummyEdge(n1, n2));
	}

	public boolean containsEdge(int n1, int n2) {
		return containsEdge(gds.getDummyEdge(n1, n2));
	}

	public boolean containsEdge(Edge e) {
		return edges.contains(e);
	}

	public boolean containsNodes(Edge e) {
		if (e instanceof DirectedEdge) {
			return this.containsNode(((DirectedEdge) e).getSrc())
					&& this.containsNode(((DirectedEdge) e).getDst());
		} else if (e instanceof UndirectedEdge) {
			return this.containsNode(((UndirectedEdge) e).getNode1())
					&& this.containsNode(((UndirectedEdge) e).getNode2());
		} else {
			Log.error("containsNode() for unsupported edge type: "
					+ e.getClass());
			return false;
		}
	}

	/**
	 * Get an edge by its attached nodes
	 * 
	 * @param Node
	 *            n1, Node n2
	 */
	public Edge getEdge(Node n1, Node n2) {
		if (!gds.isReadable(edges))
			throw new RuntimeException("This is not a readable graph");
		return ((IEdgeListDatastructureReadable) edges).get(gds.getDummyEdge(
				n1, n2));
	}

	/**
	 * Retrieve a random edge
	 * 
	 * @return
	 */
	public Edge getRandomEdge() {
		if (!gds.isReadable(edges))
			throw new RuntimeException("This is not a readable graph");
		return (Edge) ((IEdgeListDatastructureReadable) edges).getRandom();
	}

	/**
	 * Retrieve a collection of all edges within this graph
	 * 
	 * @return
	 */
	public Iterable<IElement> getEdges() {
		if (!gds.isReadable(edges))
			throw new RuntimeException("This is not a readable graph");
		return (IEdgeListDatastructureReadable) edges;
	}

	public boolean removeEdge(Edge e) {
		return edges.remove(e);
	}

	/**
	 * Retrieve the number of edges within this graph
	 * 
	 * @return
	 */
	public int getEdgeCount() {
		return edges.size();
	}

	/**
	 * Check whether this is a directed graph or not
	 * 
	 * @return true, if the graph is directed; fals otherwise
	 */
	public boolean isDirected() {
		return gds.createsDirected();
	}

	/**
	 * 
	 * i.e., V*(V-1) in case of a directed graph, V*(V-1)/2 in case of an
	 * undirected graph
	 * 
	 * @return maximum number of edges the graph could have with the current
	 *         number of nodes
	 */
	public BigInteger getMaxEdgeCount() {
		int nodeCount = this.getNodeCount();
		BigInteger res = BigInteger.valueOf(nodeCount);
		res = res.multiply(BigInteger.valueOf(nodeCount - 1));
		if (!this.isDirected()) {
			res = res.divide(BigInteger.valueOf(2));
		}
		return res;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public GraphDataStructure getGraphDatastructures() {
		return this.gds;
	}

	@Override
	public boolean equals(Object obj) {
		Log.debug("Running equality check for graphs");

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}

		Graph other = (Graph) obj;

		if (gds == null) {
			if (other.gds != null) {
				return false;
			}
		} else if (!gds.equals(other.gds)) {
			return false;
		}
		if (timestamp != other.timestamp) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}

		Log.debug("Basics equal, going for edges and nodes");

		if (edges == null) {
			if (other.edges != null) {
				return false;
			}
		} else if (!this.edges.equals(other.edges)) {
			Log.debug("Edges not equal (type: " + edges.getClass() + ")");
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!this.nodes.equals(other.nodes)) {
			Log.debug("Nodes not equal (type: " + nodes.getClass() + ")");
			return false;
		}
		return true;
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

	public void printAll() {
		System.out.println(this.toString());
		Iterator<IElement> iter1 = this.nodes.iterator();
		while (iter1.hasNext()) {
			System.out.println("  " + iter1.next());
		}
		Iterator<IElement> iter2 = this.edges.iterator();
		while (iter2.hasNext()) {
			System.out.println("  " + iter2.next());
		}
	}

	public void printV() {
		System.out.println(this.toString());
		Iterator<IElement> iterator = this.nodes.iterator();
		while (iterator.hasNext()) {
			System.out.println("  " + iterator.next());
		}
	}

	public void printE() {
		System.out.println(this.toString());
		Iterator<IElement> iterator = this.edges.iterator();
		while (iterator.hasNext()) {
			System.out.println("  " + iterator.next());
		}
	}

	public void switchDataStructure(ListType type,
			Class<? extends IDataStructure> newDatastructureType) {
		IDataStructure newDatastructure;
		switch (type) {
		case GlobalEdgeList:
			newDatastructure = gds.newList(type, newDatastructureType);
			this.edges = (IEdgeListDatastructure) ((IEdgeListDatastructureReadable) this.edges)
					.switchTo(newDatastructure);
			break;
		case GlobalNodeList:
			newDatastructure = gds.newList(type, newDatastructureType);
			this.nodes = (INodeListDatastructure) ((INodeListDatastructureReadable) this.nodes)
					.switchTo(newDatastructure);
			break;
		case LocalEdgeList:
		case LocalInEdgeList:
		case LocalOutEdgeList:
		case LocalNodeList:
			for (IElement n : this.getNodes()) {
				newDatastructure = gds.newList(type, newDatastructureType);
				((Node) n).switchDataStructure(type, newDatastructure);
			}
		}
	}

}
