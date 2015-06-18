package dna.visualization.graph;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeightedEdge;
import dna.graph.weights.IWeightedNode;
import dna.graph.weights.Weight;

public aspect GraphVisualizationAspects {
	
	/*
	 * GRAPH
	 */
	
	pointcut graphInit() :  
		if(GraphVisualization.isEnabled()) &&
		call(* GraphGenerator.newGraphInstance());

	Graph around() : graphInit() {
		Graph g = proceed();
		GraphVisualization.init(g);
		return g;
	}

	/*
	 * NODE
	 */

	pointcut nodeAddition(Graph g, Node n) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(n) &&
		call(* Graph.addNode(Node));

	after(Graph g, Node n) : nodeAddition(g, n) {
		GraphVisualization.addNode(g, n);
	}

	pointcut nodeRemoval(Graph g, Node n) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(n) &&
		call(* Graph.removeNode(Node));

	after(Graph g, Node n) : nodeRemoval(g, n) {
		GraphVisualization.removeNode(g, n);
	}

	pointcut nodeWeight(IWeightedNode n, Weight w) :
		if(GraphVisualization.isEnabled()) &&
		target(n) &&
		args(w) &&
		call(* IWeightedNode.setWeight(Weight));
	
	before(IWeightedNode n, Weight w) : nodeWeight(n, w) {
		GraphVisualization.changeNodeWeight(n, w);
	}

	/*
	 * EDGE
	 */

	pointcut edgeAddition(Graph g, Edge e) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(e) &&
		call(* Graph.addEdge(Edge));

	after(Graph g, Edge e) : edgeAddition(g, e) {
		GraphVisualization.addEdge(g, e);
	}

	pointcut edgeRemoval(Graph g, Edge e) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(e) &&
		call(* Graph.removeEdge(Edge));

	after(Graph g, Edge e) : edgeRemoval(g, e) {
		GraphVisualization.removeEdge(g, e);
	}

	pointcut edgeWeight(IWeightedEdge e, Weight w) :
		if(GraphVisualization.isEnabled()) &&
		target(e) &&
		args(w) &&
		call(* IWeightedEdge.setWeight(Weight));
	
	before(IWeightedEdge e, Weight w) : edgeWeight(e, w) {
		GraphVisualization.changeEdgeWeight(e, w);
	}
}
