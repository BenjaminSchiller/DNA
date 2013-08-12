package Factories;

import DataStructures.DArrayList;
import DataStructures.DHashSet;
import Graph.Graph;
import Graph.Node;

public class DataStructuresFactory {
	public Graph GraphALHS(String name, long timestamp, Class<? extends Node> nodeType) {
		Graph g = new Graph(name, timestamp, DArrayList.class, DHashSet.class, DHashSet.class, nodeType);
		return g;
	}
}
