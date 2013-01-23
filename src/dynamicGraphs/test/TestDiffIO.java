package dynamicGraphs.test;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.io.DiffReader;
import dynamicGraphs.io.DiffWriter;

public class TestDiffIO {
	public static void main(String[] args) {
		Graph g1 = TestGraphIO.getGraph1();
		
		Diff d1 = TestDiffIO.getDiff1(g1);
		
		DiffWriter.write(d1, "d1.txt");
		Diff d2 = DiffReader.read("d1.txt", g1);
		DiffWriter.write(d2, "d2.txt");
		Diff d3 = DiffReader.read("d2.txt", g1);
		DiffWriter.write(d3, "d3.txt");
	}

	public static Diff getDiff1(Graph g) {
		Diff d = new Diff(g.getNodes().length, g.getTimestamp(),
				g.getTimestamp() + 1);
		
		d.addAddedEdges(new Edge(g.getNode(0), g.getNode(1)));
		d.addAddedEdges(new Edge(g.getNode(0), g.getNode(2)));
		d.addAddedEdges(new Edge(g.getNode(0), g.getNode(4)));
		
		d.addRemovedEdge(new Edge(g.getNode(1), g.getNode(0)));
		d.addRemovedEdge(new Edge(g.getNode(2), g.getNode(0)));
		d.addRemovedEdge(new Edge(g.getNode(3), g.getNode(0)));
		d.addRemovedEdge(new Edge(g.getNode(4), g.getNode(0)));
		
		return d;
	}
}
