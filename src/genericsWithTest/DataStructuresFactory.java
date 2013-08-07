package genericsWithTest;

import genericsWithTest.DataStructures.DArrayList;
import genericsWithTest.DataStructures.DHashSet;

@SuppressWarnings("rawtypes")
public class DataStructuresFactory {
	public Graph GraphALHS() {
		Graph<DArrayList<Node>, DHashSet<Edge>> g = new Graph<>();
		return g;
	}
	
	public Graph GraphHSAL() {
		Graph<DHashSet<Node>, DArrayList<Edge>> g = new Graph<>();
		return g;
	}
}
