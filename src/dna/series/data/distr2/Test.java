package dna.series.data.distr2;

public class Test {

	public static void main(String[] args) throws Exception {
		test(new BinnedIntDistr("int"), new Integer[] { 1, 3, 5, 2, 2, 2 },
				new Integer[] { 5, 3 });
		test(new BinnedIntDistr("binnedInt", 3), new Integer[] { 1, 2, 3, 4, 5,
				6, 7, 10, 10, 11 }, new Integer[] { 2 });
		test(new BinnedDoubleDistr("double"), new Double[] { 0.1, 0.9, 1.0,
				3.0, 3.9, 3.99999 }, new Double[] { 3.5 });
		test(new BinnedDoubleDistr("binnedDouble", 0.1), new Double[] { 0.0,
				0.1, 0.02, 0.05, 0.2, 0.21, 0.22, 1.3024 },
				new Double[] { 1.3024 });

		testIO();

		// testEquality();
		testEqualityBinned();
	}

	public static void test(BinnedDistr distr, Object[] incr_, Object[] decr_) {
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

	public static void testIO() throws Exception {
		String dir = "data/test-distr/";

		BinnedDistr d1 = new BinnedDoubleDistr("name", 0.5, new long[] { 0, 1,
				0, 4, 0, 3, 2 }, 10);
		d1.write(dir, "binnedDouble.txt");
		BinnedDistr d1_ = BinnedDistr.read(dir, "binnedDouble.txt", "name",
				true, BinnedDoubleDistr.class);
		d1_.write(dir, "binnedDouble_.txt");
		System.out.println();
		System.out.println(d1.equalsVerbose(d1_));
		System.out.println(d1_.equalsVerbose(d1));
		System.out.println(d1.equalsVerbose(d1));
		System.out.println(d1_.equalsVerbose(d1_));

		QualityDistr d2 = new QualityDoubleDistr("name", 2.5, new double[] { 0,
				0.4, 0.1, 0.0, 0.7, 0.6 });
		d2.write(dir, "qualityDouble.txt");
		QualityDistr d2_ = QualityDistr.read(dir, "qualityDouble.txt", "name",
				true, QualityDoubleDistr.class);
		d2_.write(dir, "qualityDouble_.txt");
		System.out.println();
		System.out.println(d2.equalsVerbose(d2_));
		System.out.println(d2_.equalsVerbose(d2));
		System.out.println(d2.equalsVerbose(d2));
		System.out.println(d2_.equalsVerbose(d2_));
	}

	public static void testEqualityBinned() {
		BinnedDistr d1, d2, d3, d4, d5, d6, d7;
		d1 = new BinnedIntDistr("name", 2, new long[] { 0, 1, 2, 3, 4 }, 10);
		d2 = new BinnedIntDistr("name_", 2, new long[] { 0, 1, 2, 3, 4 }, 10);
		d3 = new BinnedIntDistr("name", 3, new long[] { 0, 1, 2, 3, 4 }, 10);
		d4 = new BinnedIntDistr("name", 2, new long[] { 0, 1, 2, 3, 4 }, 11);
		d5 = new BinnedIntDistr("name", 2, new long[] { 0, 1, 2, 3, 4, 0 }, 10);
		d6 = new BinnedIntDistr("name", 2, new long[] { 0, 1, 2, 2, 5 }, 10);
		d7 = new BinnedLongDistr("name", 2l, new long[] { 0, 1, 2, 3, 4 }, 10);

		System.out.println(d1.equalsVerbose(d2));
		System.out.println(d1.equalsVerbose(d3));
		System.out.println(d1.equalsVerbose(d4));
		System.out.println(d1.equalsVerbose(d5));
		System.out.println(d1.equalsVerbose(d6));
		System.out.println(d1.equalsVerbose(d7));

		System.out.println();

		System.out.println(d2.equalsVerbose(d1));
		System.out.println(d3.equalsVerbose(d1));
		System.out.println(d4.equalsVerbose(d1));
		System.out.println(d5.equalsVerbose(d1));
		System.out.println(d6.equalsVerbose(d1));
		System.out.println(d7.equalsVerbose(d1));

		System.out.println();

		System.out.println(d2.equalsVerbose(d2));
		System.out.println(d3.equalsVerbose(d3));
		System.out.println(d4.equalsVerbose(d4));
		System.out.println(d5.equalsVerbose(d5));
		System.out.println(d6.equalsVerbose(d6));
		System.out.println(d7.equalsVerbose(d7));
	}

}
