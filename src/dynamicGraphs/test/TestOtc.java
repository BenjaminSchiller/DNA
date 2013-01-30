package dynamicGraphs.test;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.diff.Series;
import dynamicGraphs.diff.generator.RandomDiff;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.generator.RandomGraph;
import dynamicGraphs.metrics.Metric;
import dynamicGraphs.metrics.triangles.open.OtcComp;
import dynamicGraphs.metrics.triangles.open.OtcIncrByDiff;
import dynamicGraphs.metrics.triangles.open.OtcIncrByEdge;

public class TestOtc {

	/**
	 * @param args
	 * @throws DiffNotApplicableException
	 */
	public static void main(String[] args) throws DiffNotApplicableException {
		Graph g = RandomGraph.generate(1000, 10000, true);
		Metric[] metrics = new Metric[] { new OtcComp(g), new OtcIncrByEdge(g),
				new OtcIncrByDiff(g) };
		int add = 1000;
		int remove = 10000;
		Diff d1 = RandomDiff.generate(g, add, remove, true, 0, 1);
		Diff d2 = RandomDiff.generate(g, add, remove, true, 1, 2);
		Diff d3 = RandomDiff.generate(g, add, remove, true, 2, 3);
		Diff[] diffs = new Diff[] { d1, d2, d3 };

		Series s = new Series(g, diffs, metrics);
		s.process(true);
	}

}
