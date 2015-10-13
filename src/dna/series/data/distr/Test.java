package dna.series.data.distr;

import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
		// test(new IntDistr("int"), new Integer[] { 1, 3, 5, 2, 2, 2 },
		// new Integer[] { 5, 3 });
		// test(new BinnedIntDistr("binnedInt", 3), new Integer[] { 1, 2, 3, 4,
		// 5, 6, 7, 10, 10, 11 }, new Integer[] { 2 });
		// test(new DoubleDistr("double"), new Double[] { 0.1, 0.9, 1.0, 3.0,
		// 3.9, 3.99999 }, new Double[] { 3.5 });
		// test(new BinnedDoubleDistr("binnedDouble", 0.1), new Double[] { 0.0,
		// 0.1, 0.02, 0.05, 0.2, 0.21, 0.22, 1.3024 }, new Double[] {1. 3024});

		String dir = "data/test-distr/";

		Distr d1 = new BinnedDoubleDistr("name", 0.5, 10, new long[] { 0, 1, 0,
				4, 0, 3, 2 });
		d1.write(dir, "binnedDouble.txt");
		Distr d1_ = BinnedDistr.readBinned(dir, "binnedDouble.txt", "name",
				true, BinnedDoubleDistr.class);
		d1_.write(dir, "binnedDouble_.txt");
		System.out.println();
		System.out.println(d1.equals(d1_));
		System.out.println(d1_.equals(d1));
		System.out.println(d1.equals(d1));
		System.out.println(d1_.equals(d1_));

		Distr d2 = new IntDistr("name", 10, new long[] { 0, 1, 0, 4, 0, 3, 2 });
		d2.write(dir, "int.txt");
		Distr d2_ = Distr.read(dir, "int.txt", "name", true, IntDistr.class);
		d2_.write(dir, "int.txt");
		System.out.println();
		System.out.println(d2.equals(d2_));
		System.out.println(d2_.equals(d2));
		System.out.println(d2.equals(d2));
		System.out.println(d2_.equals(d2_));

		System.out.println();
		System.out.println(d1.equals(d2));
		System.out.println(d2.equals(d1));
	}

	public static void test(Distr distr, Object[] incr_, Object[] decr_) {
		for (Object incr : incr_) {
			System.out.println();
			System.out.println("incr: " + incr);
			distr.incr(incr);
			distr.print();
		}
		for (Object decr : decr_) {
			System.out.println();
			System.out.println("decr: " + decr);
			distr.decr(decr);
			distr.print();
		}
		System.out.println("TRUNCATE: " + distr.truncate());
		distr.print();
	}

}
