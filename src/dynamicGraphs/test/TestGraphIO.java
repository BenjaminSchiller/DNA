package dynamicGraphs.test;

import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.io.GraphReader;
import dynamicGraphs.io.GraphWriter;

public class TestGraphIO {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Graph g1 = TestGraphIO.getGraph1();

		GraphWriter.write(g1, "g1.txt");

		Graph g2 = GraphReader.read("g1.txt");
		GraphWriter.write(g2, "g2.txt");

		Graph g3 = GraphReader.read("g2.txt");
		GraphWriter.write(g3, "g3.txt");
	}

	public static Graph getGraph1() {
		Graph g = new Graph("the name", 5, 17);
		g.addEdge(new Edge(g.getNode(0), g.getNode(1)));
		g.addEdge(new Edge(g.getNode(1), g.getNode(2)));
		g.addEdge(new Edge(g.getNode(2), g.getNode(3)));
		g.addEdge(new Edge(g.getNode(3), g.getNode(4)));
		g.addEdge(new Edge(g.getNode(4), g.getNode(0)));
		g.addEdge(new Edge(g.getNode(4), g.getNode(2)));
		g.addEdge(new Edge(g.getNode(4), g.getNode(4)));
		return g;
	}

}
