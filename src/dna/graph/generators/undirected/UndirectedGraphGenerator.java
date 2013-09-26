package dna.graph.generators.undirected;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.util.parameters.Parameter;

/**
 * Generator for graphs with undirected nodes
 * 
 * @author Nico
 * 
 */
public abstract class UndirectedGraphGenerator extends GraphGenerator {

	public UndirectedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}
	
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		return UndirectedNode.class.isAssignableFrom(nodeType);
	}	
	
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		return UndirectedEdge.class.isAssignableFrom(edgeType);
	}	
}
