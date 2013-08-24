package Factories;

import DataStructures.GraphDataStructure;
import Graph.Nodes.DirectedNode;
import Graph.Nodes.Node;
import Utils.parameters.Parameter;

public abstract class DirectedGraphGenerator extends GraphGenerator {
	public DirectedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}
	
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		return DirectedNode.class.isAssignableFrom(nodeType);
	}
}
