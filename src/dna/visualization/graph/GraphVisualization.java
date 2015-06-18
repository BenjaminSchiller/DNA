package dna.visualization.graph;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;

public class GraphVisualization {
	protected static boolean enabled = false;

	public static void enable() {
		enabled = true;
	}

	public static void disable() {
		enabled = false;
	}

	public static boolean isEnabled() {
		return enabled;
	}
	
	/*
	 * GRAPH
	 */

	public static void init(Graph g) {
		System.out.println("GraphVis - init graph: " + g);
	}
	
	/*
	 * NODE
	 */

	public static void addNode(Graph g, Node n) {
		System.out.println("GraphVis - addNode: " + n);
	}

	public static void removeNode(Graph g, Node n) {
		System.out.println("GraphVis - removeNode: " + n);
	}

	public static void changeNodeWeight(IWeightedNode n, Weight w) {
		System.out.println("GraphVis - changeNodeWeight: " + w + " of " + n);
	}
	
	/*
	 * EDGE
	 */

	public static void addEdge(Graph g, Edge e) {
		System.out.println("GraphVis - addEdge: " + e);
	}

	public static void removeEdge(Graph g, Edge e) {
		System.out.println("GraphVis - removeEdge: " + e);
	}

	public static void changeEdgeWeight(IWeightedEdge e, Weight w) {
		System.out.println("GraphVis - changeNodeWeight: " + w + " of " + e);
	}
}
