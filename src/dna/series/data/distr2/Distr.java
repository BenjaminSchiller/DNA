package dna.series.data.distr2;

import java.io.IOException;

import dna.series.data.Data;

public abstract class Distr<T, V> extends Data {
	protected T binSize;

	public T getBinSize() {
		return this.binSize;
	}

	protected V values;

	public V getValues() {
		return this.values;
	}

	public Distr(String name, T binSize, V values) {
		super(name);
		this.binSize = binSize;
		this.values = values;
	}

	/**
	 * outputs the contents stored in this distribution
	 */
	protected abstract void print();

	/**
	 * writes the contents of the distribution to the specified file in the
	 * specified directory.
	 * 
	 * @param dir
	 *            directory where to store the output
	 * @param filename
	 *            file where to write the data to
	 * @throws IOException
	 */
	public abstract void write(String dir, String filename) throws IOException;
	
	/**
	 * Compares the two distributions and adds an absolute and a relative
	 * quality distribution to the distribution-list.
	 **/
//	public static void compareDistributionsAndAddToList(DistributionList list,
//			Distr<?> d1, Distr<?> d2) {
//		// compare DistributionDouble objects
//		long[] values1 = d1.getValues();
//		long[] values2 = d2.getValues();
//
//		long[] diffAbs = new long[Math.max(values1.length, values2.length)];
//		long diffAbsDenom = 0;
//		// long[] diffRel = new long[diffAbs.length];
//
//		for (int i = 0; i < diffAbs.length; i++) {
//			long v1 = 0;
//			long v2 = 0;
//			try {
//				v1 = values1[i];
//			} catch (ArrayIndexOutOfBoundsException e) {
//			}
//			try {
//				v2 = values2[i];
//			} catch (ArrayIndexOutOfBoundsException e) {
//			}
//			diffAbs[i] = v1 - v2;
//			diffAbsDenom += v1;
//
//			// TODO: RELATIVE QUALITY DISTRIBUTION
//			// if (v2 == 0)
//			// diffRel[i] = Double.MAX_VALUE;
//			// else
//			// diffRel[i] = v1 / v2;
//		}
//
//		// add absolute comparison
//		list.add(new LongDistr(
//				Files.getDistributionName(d1.getName()) + "_abs", diffAbsDenom,
//				diffAbs));
//
//		// TODO: RELATIVE QUALITY DISTRIBUTION
//		// add relative comparison
//		// list.add(new
//		// DistributionDouble(Files.getDistributionName(d1.getName())
//		// + "_rel", diffRel));
//	}
}
