package dna.graph.generators.directed;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.util.parameters.Parameter;

/**
 * Generator for graphs with directed nodes
 * 
 * @author Nico
 * 
 */
public abstract class DirectedGraphGenerator extends GraphGenerator {
	public DirectedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}

	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		return DirectedNode.class.isAssignableFrom(nodeType);
	}
	
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		return DirectedEdge.class.isAssignableFrom(edgeType);
	}	
}
