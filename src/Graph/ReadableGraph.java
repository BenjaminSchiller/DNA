package Graph;

import java.util.Arrays;
import java.util.Collection;

import DataStructures.GraphDataStructure;
import DataStructures.IEdgeListDatastructureReadable;
import DataStructures.INodeListDatastructureReadable;
import DataStructures.IReadable;
import Graph.Edges.Edge;
import Graph.Nodes.Node;

public class ReadableGraph extends Graph {
	public ReadableGraph(String name, long timestamp, GraphDataStructure gds) {
		super(name, timestamp, gds);
		
		if ( INodeListDatastructureReadable.class.isAssignableFrom(this.gds.getNodeListType())) {
			this.nodes = (INodeListDatastructureReadable) gds.newNodeList();
		} else {
			throw new RuntimeException("Cannot initialize graph with a node list of type " + this.nodes.getClass());
		}
		
		if ( IEdgeListDatastructureReadable.class.isAssignableFrom(this.gds.getGraphEdgeListType())) {
			this.edges = (IEdgeListDatastructureReadable) gds.newGraphEdgeList();
		} else {
			throw new RuntimeException("Cannot initialize graph with a edge list of type " + this.nodes.getClass());
		}		
	}

	public ReadableGraph(String name, long timestamp, GraphDataStructure gds, int nodeSize, int edgeSize) {
		super(name, timestamp, gds, nodeSize, edgeSize);
		
		if ( INodeListDatastructureReadable.class.isAssignableFrom(this.gds.getNodeListType())) {
			this.nodes = (INodeListDatastructureReadable) gds.newNodeList();
		} else {
			throw new RuntimeException("Cannot initialize graph with a node list of type " + this.nodes.getClass());
		}
		
		if ( IEdgeListDatastructureReadable.class.isAssignableFrom(this.gds.getGraphEdgeListType())) {
			this.edges = (IEdgeListDatastructureReadable) gds.newGraphEdgeList();
		} else {
			throw new RuntimeException("Cannot initialize graph with a edge list of type " + this.nodes.getClass());
		}			
	}
	
	public Collection<IElement> getNodes() {
		return ((INodeListDatastructureReadable) nodes).getElements();
	}
	

	public Node getRandomNode() {
		return (Node) ((INodeListDatastructureReadable) nodes).getRandom();
	}
	
	public Collection<IElement> getEdges() {
		return ((IEdgeListDatastructureReadable) edges).getElements();
	}
	
	public Edge getRandomEdge() {
		return (Edge) ((IEdgeListDatastructureReadable) edges).getRandom();
	}
	
	public void print() {
		System.out.println(this.toString());
		System.out.println("  V = " + this.getNodes());
		System.out.println("  E = " + this.getEdges());
	}
	
	public Node getNode(int index) {
		return ((INodeListDatastructureReadable) this.nodes).get(index);
	}
	
	public Edge getEdge(Edge e) {
		return ((IEdgeListDatastructureReadable) edges).get(e);
	}

}
