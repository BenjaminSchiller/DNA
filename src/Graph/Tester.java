package Graph;

import DataStructures.DArrayList;
import DataStructures.DHashSet;
import DataStructures.GraphDataStructure;

public class Tester {

	public static void main(String[] args) {
		GraphDataStructure gds = new GraphDataStructure(DArrayList.class, DHashSet.class, DHashSet.class, DirectedNode.class);
		Graph g = new Graph("name", 0L, gds);
	}
}
