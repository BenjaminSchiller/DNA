package Factories;

import DataStructures.GraphDataStructure;
import Graph.Nodes.DirectedNode;
import Utils.parameters.Parameter;

public abstract class DirectedGraphGenerator extends GraphGenerator {
	public final static Class<DirectedNode> nodeType = DirectedNode.class;	
	public DirectedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}
}
