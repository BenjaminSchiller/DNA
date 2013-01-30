package dynamicGraphs.test;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.diff.generator.RandomDiff;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.generator.RandomGraph;
import dynamicGraphs.io.DiffReader;
import dynamicGraphs.io.GraphReader;
import dynamicGraphs.metrics.triangles.open.OtcIncrByDiff;
import dynamicGraphs.util.Timer;

public class TestOpenTriangleCounting {

	/**
	 * @param args
	 * @throws DiffNotApplicableException
	 */
	public static void main(String[] args) throws DiffNotApplicableException {
		for (int i = 0; i < 10; i++) {
			Timer t = new Timer("");
			random(true);
			System.out.println(t.end());
		}
	}

	public static void random(boolean compute)
			throws DiffNotApplicableException {
		Graph g = RandomGraph.generate(1000, 10000, false);
		OtcIncrByDiff otc = new OtcIncrByDiff(g);
		otc.compute();
		for (int i = 0; i < 10; i++) {
			Diff d = RandomDiff.generate(g, 1100, 10000, true);
			otc.applyBeforeDiff(d);
			g.apply(d);
			otc.applyAfterDiff(d);
			if (compute) {
				OtcIncrByDiff otc2 = new OtcIncrByDiff(g);
				// System.out.println(g.getTimestamp() + ": " + otc.toString());
				otc2.compute();
				System.out.println(otc.equals(otc2) ? "OK" : ":-(");
			}
		}
	}

	public static void example() throws DiffNotApplicableException {
		Graph g0 = GraphReader.read("data/otc/g-13-0.txt");
		Graph g1 = GraphReader.read("data/otc/g-13-1.txt");
		Graph g2 = GraphReader.read("data/otc/g-13-2.txt");
		Graph g3 = GraphReader.read("data/otc/g-13-3.txt");
		Graph[] graphs = new Graph[] { g0, g1, g2, g3 };
		for (Graph g : graphs) {
			OtcIncrByDiff otc = new OtcIncrByDiff(g);
			otc.compute();
			System.out.println(g.getTimestamp() + ": " + otc.toString());
		}

		System.out.println("\n");

		Graph g = GraphReader.read("data/otc/g-13-0.txt");
		OtcIncrByDiff otc = new OtcIncrByDiff(g);
		otc.compute();
		Diff d0 = DiffReader.read("data/otc/d-13-0-1.txt", g);
		Diff d1 = DiffReader.read("data/otc/d-13-1-2.txt", g);
		Diff d2 = DiffReader.read("data/otc/d-13-2-3.txt", g);
		Diff[] diffs = new Diff[] { d0, d1, d2 };
		for (Diff d : diffs) {
			otc.applyBeforeDiff(d);
			g.apply(d);
			otc.applyAfterDiff(d);
			OtcIncrByDiff otc2 = new OtcIncrByDiff(g);
			otc2.compute();
			System.out.println(g.getTimestamp() + ": " + otc.toString());
			System.out.println(g.getTimestamp() + ": " + otc2.toString());
			System.out.println(otc.equals(otc2) ? "OK" : ":-(");
		}
	}

}
