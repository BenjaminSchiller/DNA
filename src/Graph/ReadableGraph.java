package Graph;

import java.util.Collection;

import DataStructures.GraphDataStructure;
import DataStructures.IEdgeListDatastructureReadable;
import DataStructures.INodeListDatastructureReadable;
import Graph.Edges.Edge;
import Graph.Nodes.Node;

public class ReadableGraph extends Graph {
	INodeListDatastructureReadable nodes;
	IEdgeListDatastructureReadable edges;

	public ReadableGraph(String name, long timestamp, GraphDataStructure gds) {
		super(name, timestamp, gds);
		
		if ( this.nodes instanceof INodeListDatastructureReadable) {
			this.nodes = (INodeListDatastructureReadable) nodes;
		} else {
			throw new RuntimeException("Cannot initialize graph with a node list of type " + this.nodes.getClass());
		}
		
		if ( this.edges instanceof IEdgeListDatastructureReadable) {
			this.edges = (IEdgeListDatastructureReadable) edges;
		} else {
			throw new RuntimeException("Cannot initialize graph with a edge list of type " + this.nodes.getClass());
		}		
	}

	public ReadableGraph(String name, long timestamp, GraphDataStructure gds, int nodeSize, int edgeSize) {
		super(name, timestamp, gds, nodeSize, edgeSize);
		// TODO Auto-generated constructor stub
	}
	
	public Collection<IElement> getNodes() {
		return nodes.getElements();
	}
	

	public Node getRandomNode() {
		return (Node) nodes.getRandom();
	}
	
	public Collection<IElement> getEdges() {
		return edges.getElements();
	}
	
	public Edge getRandomEdge() {
		return (Edge) edges.getRandom();
	}
	
	public void print() {
		System.out.println(this.toString());
		System.out.println("  V = " + this.getNodes());
		System.out.println("  E = " + this.getEdges());
	}
	
	public Node getNode(int index) {
		return nodes.get(index);
	}
	
	public Edge getEdge(Edge e) {
		return edges.get(e);
	}

}
