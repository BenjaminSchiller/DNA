package dna.examples;

import dna.datastructures.DArrayList;
import dna.datastructures.DHashSet;
import dna.datastructures.GraphDataStructure;
import dna.graph.Graph;
import dna.graph.nodes.DirectedNode;

public class Tester {

	public static void main(String[] args) {
		GraphDataStructure gds = new GraphDataStructure(DArrayList.class, DHashSet.class, DHashSet.class, DirectedNode.class);
		Graph g = new Graph("name", 0L, gds);
	}
}
