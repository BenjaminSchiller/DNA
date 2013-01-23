package dynamicGraphs.util;

import java.util.Random;

import dynamicGraphs.graph.Edge;
import dynamicGraphs.graph.Graph;

public class Rand {
	public static long seed = System.currentTimeMillis();

	public static Random rand = new Random(seed);

	public static void init(long seed) {
		Rand.seed = seed;
		Rand.rand = new Random(seed);
	}

	public static Edge edge(Graph g) {
		int src = Rand.rand.nextInt(g.getNodes().length);
		int dst = Rand.rand.nextInt(g.getNodes().length);
		if (src != dst) {
			return new Edge(g.getNode(src), g.getNode(dst));
		}
		return Rand.edge(g);
	}
}
