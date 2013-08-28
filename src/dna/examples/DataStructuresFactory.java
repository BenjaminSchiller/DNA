package dna.examples;

import dna.graph.Graph;
import dna.graph.datastructures.DArrayList;
import dna.graph.datastructures.DHashSet;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.nodes.Node;

public class DataStructuresFactory {
	public Graph GraphALHS(String name, long timestamp, Class<? extends Node> nodeType) {
		GraphDataStructure gds = new GraphDataStructure(DArrayList.class, DHashSet.class, DHashSet.class, nodeType);
		Graph g = new Graph(name, timestamp, gds);
		return g;
	}
}
