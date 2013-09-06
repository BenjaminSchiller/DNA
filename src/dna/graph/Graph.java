package dna.graph;

import java.util.Collection;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.IEdgeListDatastructureReadable;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.datastructures.INodeListDatastructureReadable;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
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
		this.nodes = gds.newNodeList();
		this.edges = gds.newGraphEdgeList();
		this.gds = gds;
	}

	public Graph(String name, long timestamp, GraphDataStructure gds, int nodeSize, int edgeSize) {
		this(name, timestamp, gds);
		this.nodes.reinitializeWithSize(nodeSize);
		this.edges.reinitializeWithSize(edgeSize);
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
	public Collection<IElement> getNodes() {
		if (!gds.isReadable(nodes))
			throw new RuntimeException("This is not a readable graph");
		return ((INodeListDatastructureReadable) nodes).getElements();
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
	}

	public boolean containsEdge(Edge e) {
		return edges.contains(e);
	}

	/**
	 * Get an edge by a generated dummy edge (see
	 * {@link IEdgeListDatastructureReadable#get(IElement)} for details)
	 * 
	 * @param e
	 * @return
	 */
	public Edge getEdge(Edge e) {
		if (!gds.isReadable(edges))
			throw new RuntimeException("This is not a readable graph");
		return ((IEdgeListDatastructureReadable) edges).get(e);
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
	public Collection<IElement> getEdges() {
		if (!gds.isReadable(edges))
			throw new RuntimeException("This is not a readable graph");
		return ((IEdgeListDatastructureReadable) edges).getElements();
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
	 * @return
	 */
	public boolean isDirected() {
		return gds.createsDirected();
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
		return this.getName() + " @ " + this.getTimestamp() + " (" + this.getNodeCount() + "/" + this.getEdgeCount()
				+ ")";
	}

	public void print() {
		System.out.println(this.toString());
		System.out.println("  V = " + this.getNodes());
		System.out.println("  E = " + this.getEdges());
	}

}
