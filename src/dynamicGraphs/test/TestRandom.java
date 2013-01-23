package dynamicGraphs.test;

import dynamicGraphs.diff.Diff;
import dynamicGraphs.diff.DiffNotApplicableException;
import dynamicGraphs.diff.generator.RandomDiff;
import dynamicGraphs.graph.Graph;
import dynamicGraphs.graph.generator.RandomGraph;
import dynamicGraphs.io.DiffWriter;
import dynamicGraphs.io.GraphWriter;
import dynamicGraphs.util.Rand;

public class TestRandom {
	public static void main(String[] args) throws DiffNotApplicableException {
		Rand.init(1);

		Graph g = RandomGraph.generate(10, 23, false);
		GraphWriter.write(g);
		for (int i = 0; i < 5; i++) {
			Diff d = RandomDiff.generate(g, 10, 10, false);
			g.apply(d);
			DiffWriter.write(d);
			GraphWriter.write(g);
		}

	}
}
