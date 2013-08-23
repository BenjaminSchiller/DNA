package Graph;

import DataStructures.GraphDataStructure;
import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;
import Graph.Edges.Edge;
import Graph.Nodes.Node;

public class Graph {
	public INodeListDatastructure nodes;
	public IEdgeListDatastructure edges;
	private String name;
	private long timestamp;
	private GraphDataStructure gds;

	public Graph(String name, long timestamp, GraphDataStructure gds) {
		this.name = name;
		this.timestamp = timestamp;
		this.nodes = gds.newNodeList();
		this.edges = gds.newGraphEdgeList();
		this.gds = gds;
	}
	
	public Graph(String name, long timestamp, GraphDataStructure gds, int nodeSize,
			int edgeSize) {
		this(name, timestamp, gds);
		this.nodes.reinitializeWithSize(nodeSize);
		this.edges.reinitializeWithSize(edgeSize);
	}
	
	public boolean isDirected() {
		return gds.createsDirected();
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
		System.out.println("  |V| = " + this.getNodeCount());
		System.out.println("  |E| = " + this.getEdgeCount());
	}

	public int getMaxNodeIndex() {
		return nodes.getMaxNodeIndex();
	}

	public int getNodeCount() {
		return nodes.size();
	}

	public boolean addNode(Node n) {
		return nodes.add(n);
	}

	public boolean removeNode(Node n) {
		return nodes.remove(n);
	}

	public boolean containsNode(Node n) {
		return nodes.contains(n);
	}

	public int getEdgeCount() {
		return edges.size();
	}

	public boolean addEdge(Edge e) {
		return edges.add(e);
	}

	public boolean removeEdge(Edge e) {
		return edges.remove(e);
	}

	public boolean containsEdge(Edge e) {
		return edges.contains(e);
	}

	public GraphDataStructure getGraphDatastructures() {
		return this.gds;
	}

	@Override
	public boolean equals(Object obj) {
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
		
		if (edges == null) {
			if (other.edges != null) {
				return false;
			}
		} else if (!this.edges.equals(other.edges)) {
			return false;
		}
		if (nodes == null) {
			if (other.nodes != null) {
				return false;
			}
		} else if (!this.nodes.equals(other.nodes)) {
			return false;
		}
		return true;
	}	
}
