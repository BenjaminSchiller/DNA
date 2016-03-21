package dna.visualization.graph;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
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
		call(* GraphDataStructure.newGraphInstance(String, long, int, int));

	IGraph around() : graphInit() {
		IGraph g = proceed();
		GraphVisualization.init(g);
		return g;
	}

	/*
	 * TIMESTAMP
	 */

	pointcut graphTimestamp(IGraph g, long newTimestamp) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(newTimestamp) &&
		call(* IGraph.setTimestamp(long));

	void around(IGraph g, long newTimestamp) : graphTimestamp(g, newTimestamp) {
		long oldTimestamp = g.getTimestamp();
		proceed(g, newTimestamp);
		GraphVisualization.getGraphPanel(g).setTimestamp(newTimestamp);
	}

	/*
	 * NODE
	 */

	pointcut nodeAddition(IGraph g, Node n) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(n) &&
		call(* IGraph.addNode(Node));

	after(IGraph g, Node n) : nodeAddition(g, n) {
		GraphVisualization.addNode(g, n);
	}

	pointcut nodeRemoval(IGraph g, Node n) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(n) &&
		call(* IGraph.removeNode(Node));

	after(IGraph g, Node n) : nodeRemoval(g, n) {
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

	pointcut edgeAddition(IGraph g, Edge e) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(e) &&
		call(* IGraph.addEdge(Edge));

	after(IGraph g, Edge e) : edgeAddition(g, e) {
		GraphVisualization.addEdge(g, e);
	}

	pointcut edgeRemoval(IGraph g, Edge e) :
		if(GraphVisualization.isEnabled()) &&
		target(g) &&
		args(e) &&
		call(* IGraph.removeEdge(Edge));

	after(IGraph g, Edge e) : edgeRemoval(g, e) {
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
