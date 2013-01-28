package dynamicGraphs.test;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.diff.generator.RandomDiff;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.generator.RandomGraph;
import dynamicGraphs.metrics.degree.DegreeDistribution;

public class TestDegreeDistribution {

	/**
	 * @param args
	 * @throws DiffNotApplicableException
	 */
	public static void main(String[] args) throws DiffNotApplicableException {
		Graph g = TestDegreeDistribution.getGraph();
		DegreeDistribution dd = new DegreeDistribution(g);
		dd.compute();

		for (int i = 0; i < 10; i++) {
			Diff d = TestDegreeDistribution.getDiff(g);
			dd.applyBeforeDiff(d);
			g.apply(d);
			// dd.applyAfterDiff(d);
			DegreeDistribution dd2 = new DegreeDistribution(g);
			dd2.compute();
			System.out.println(dd.equals(dd2));
		}
	}

	public static Graph getGraph() {
		return RandomGraph.generate(100, 1000, true);
	}

	public static Diff getDiff(Graph g) {
		return RandomDiff.generate(g, 110, 1000, true);
	}

}
