package Factories;

import DataStructures.DArrayList;
import DataStructures.DHashSet;
import DataStructures.GraphDataStructure;
import Graph.Graph;
import Graph.ReadableGraph;
import Graph.Nodes.Node;

public class DataStructuresFactory {
	public Graph GraphALHS(String name, long timestamp, Class<? extends Node> nodeType) {
		GraphDataStructure gds = new GraphDataStructure(ReadableGraph.class, DArrayList.class, DHashSet.class, DHashSet.class, nodeType);
		Graph g = new Graph(name, timestamp, gds);
		return g;
	}
}
