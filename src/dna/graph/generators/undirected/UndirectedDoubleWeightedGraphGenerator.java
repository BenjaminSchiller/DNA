package dna.graph.generators.undirected;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedDoubleWeightedNode;
import dna.util.parameters.Parameter;

/**
 * Generator for graphs with undirected, weighted nodes
 * 
 * @author Nico
 * 
 */
public abstract class UndirectedDoubleWeightedGraphGenerator extends UndirectedGraphGenerator {
	public UndirectedDoubleWeightedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}

	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		return UndirectedDoubleWeightedNode.class.isAssignableFrom(nodeType);
	}
}
