package dna.util;

import java.util.Random;

import dna.graph.old.OldEdge;
import dna.graph.old.OldGraph;

public class Rand {
	public static long seed = System.currentTimeMillis();

	public static Random rand = new Random(seed);

	public static void init(long seed) {
		Rand.seed = seed;
		Rand.rand = new Random(seed);
	}

	public static OldEdge edge(OldGraph g) {
		int src = Rand.rand.nextInt(g.getNodes().length);
		int dst = Rand.rand.nextInt(g.getNodes().length);
		if (src != dst) {
			return new OldEdge(g.getNode(src), g.getNode(dst));
		}
		return Rand.edge(g);
	}
}
