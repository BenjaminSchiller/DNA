package Factories;

import DataStructures.DArrayList;
import DataStructures.DHashSet;
import DataStructures.GraphDataStructure;
import Graph.Graph;
import Graph.Node;

public class DataStructuresFactory {
	public Graph GraphALHS(String name, long timestamp, Class<? extends Node> nodeType) {
		GraphDataStructure gds = new GraphDataStructure(DArrayList.class, DHashSet.class, DHashSet.class, nodeType);
		Graph g = new Graph(name, timestamp, gds);
		return g;
	}
}
