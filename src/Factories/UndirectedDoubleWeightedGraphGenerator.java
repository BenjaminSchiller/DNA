package Factories;

import DataStructures.GraphDataStructure;
import Graph.Nodes.DirectedNode;
import Graph.Nodes.UndirectedDoubleWeightedNode;
import Utils.parameters.Parameter;

public abstract class UndirectedDoubleWeightedGraphGenerator extends UndirectedGraphGenerator {
	public final static Class<UndirectedDoubleWeightedNode> nodeType = UndirectedDoubleWeightedNode.class;
	public UndirectedDoubleWeightedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}

}
