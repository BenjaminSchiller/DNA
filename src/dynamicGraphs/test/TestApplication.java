package dynamicGraphs.test;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.io.DiffWriter;
import dynamicGraphs.io.GraphWriter;
import dynamicGraphs.metrics.degree.DegreeDistribution;
import dynamicGraphs.util.ArrayUtils;

public class TestApplication {

	/**
	 * @param args
	 * @throws DiffNotApplicableException
	 */
	public static void main(String[] args) throws DiffNotApplicableException {
		Graph g = TestApplication.getGraph();

		GraphWriter.write(g);
		DegreeDistribution dd = new DegreeDistribution(g);
		dd.compute();
		print(g, dd);

		for (int i = 0; i < 4; i++) {
			System.out.println();
			Diff d = getDiff(i, g);
			dd.applyBefore(d);
			g.apply(d);
			dd.applyAfter(d);
			print(g, dd);
			GraphWriter.write(g);
			DiffWriter.write(d);
		}
	}

	public static void print(Graph g, DegreeDistribution dd) {
		System.out.println("Edges: " + ArrayUtils.toString(g.getEdges()));
		DegreeDistribution dd2 = new DegreeDistribution(g);
		dd2.compute();
		System.out.println(dd.equals(dd2) ? "OK" : "DIFF!!!!!!!!");
	}

	public static Graph getGraph() {
		Graph g = new Graph("test", 5, 0);
		g.addEdge(new Edge(g.getNode(0), g.getNode(2)));
		return g;
	}

	public static Diff getDiff(int step, Graph g) {
		Diff d = new Diff(g.getNodes().length, step, step + 1);
		d.addAddedEdges(new Edge(g.getNode(step), g.getNode(step + 1)));
		if (step > 0)
			d.addRemovedEdge(new Edge(g.getNode(step - 1), g.getNode(step)));
		return d;
	}

}
