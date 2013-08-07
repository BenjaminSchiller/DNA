package genericsWithTest;

import genericsWithTest.DataStructures.DArrayList;

public class Tester {

	public static void main(String[] args) {
		DArrayList<Node> listForNodes = new DArrayList<>();
		DArrayList<Edge> listForEdges = new DArrayList<>();
		
		Graph<DArrayList<Node>, DArrayList<Edge>> g = new Graph<>();
	}

}
