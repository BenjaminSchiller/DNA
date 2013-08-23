package Graph;

import DataStructures.DArrayList;
import DataStructures.DHashSet;
import DataStructures.GraphDataStructure;
import Graph.Nodes.DirectedNode;

public class Tester {

	public static void main(String[] args) {
		GraphDataStructure gds = new GraphDataStructure(ReadableGraph.class, DArrayList.class, DHashSet.class, DHashSet.class, DirectedNode.class);
		Graph g = new Graph("name", 0L, gds);
	}
}
