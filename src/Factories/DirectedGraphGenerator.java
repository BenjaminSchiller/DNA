package Factories;

import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;
import Graph.DirectedEdge;
import Graph.DirectedNode;
import Graph.Edge;
import Utils.parameters.Parameter;

public abstract class DirectedGraphGenerator extends GraphGenerator {
	public DirectedGraphGenerator(String name, Parameter[] params,
			Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, Class<? extends DirectedNode> nodeType,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, params, nodeListType, graphEdgeListType, nodeEdgeListType, nodeType, timestampInit, nodesInit, edgesInit);
		this.edgeType = DirectedEdge.class;
	}

}
