package dna.util;

import java.util.Random;

public class Rand {
	public static long seed = System.currentTimeMillis();

	public static Random rand = new Random(seed);

	public static void init(long seed) {
		Rand.seed = seed;
		Rand.rand = new Random(seed);
	}

}
