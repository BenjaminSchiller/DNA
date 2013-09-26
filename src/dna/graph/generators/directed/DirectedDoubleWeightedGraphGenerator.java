package dna.graph.generators.directed;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedDoubleWeightedNode;
import dna.graph.nodes.Node;
import dna.util.parameters.Parameter;

public abstract class DirectedDoubleWeightedGraphGenerator extends DirectedGraphGenerator {
	public DirectedDoubleWeightedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}

	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		return DirectedDoubleWeightedNode.class.isAssignableFrom(nodeType);
	}
	
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		return DirectedDoubleWeightedEdge.class.isAssignableFrom(edgeType);
	}	
}