package dna.series.data.distr2;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.Data;
import dna.series.lists.DistributionList;

public abstract class Distr<T, V> extends Data {
	public enum DistrType {
		BINNED_DOUBLE, BINNED_INT, BINNED_LONG, QUALITY_DOUBLE, QUALITY_INT, QUALITY_LONG
	};

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

	/** Returns the distribution type. **/
	public abstract DistrType getDistrType();

	public static Distr<?, ?> read(String dir, String filename, String name,
			DistrType type, boolean readValues) throws IOException {
		switch (type) {
		case BINNED_DOUBLE:
			return BinnedDistr.read(dir, filename, name, readValues,
					BinnedDoubleDistr.class);
		case BINNED_INT:
			return BinnedDistr.read(dir, filename, name, readValues,
					BinnedIntDistr.class);
		case BINNED_LONG:
			return BinnedDistr.read(dir, filename, name, readValues,
					BinnedLongDistr.class);
		case QUALITY_DOUBLE:
			return QualityDistr.read(dir, filename, name, readValues,
					QualityDoubleDistr.class);
		case QUALITY_INT:
			return QualityDistr.read(dir, filename, name, readValues,
					QualityIntDistr.class);
		case QUALITY_LONG:
			return QualityDistr.read(dir, filename, name, readValues,
					QualityLongDistr.class);
		default:
			return null;
		}
	}

	public static Distr<?, ?> read(String dir, String filename,
			boolean readValues) throws IOException {
		DistrType type = Files.getDistributionTypeFromFilename(filename);
		return read(dir, filename,
				Files.getDistributionNameFromFilename(filename, type), type,
				readValues);
	}

	/**
	 * Compares the two distributions and adds an absolute and a relative
	 * quality distribution to the distribution-list.
	 **/
	public static void compareDistributionsAndAddToList(DistributionList list,
			Distr<?, ?> d1, Distr<?, ?> d2) {
		// TODO: FILL WITH CODE
	}

	/**
	 * Compares the two distributions and adds an absolute and a relative
	 * quality distribution to the distribution-list.
	 **/
	// public static void compareDistributionsAndAddToList(DistributionList
	// list,
	// Distr<?> d1, Distr<?> d2) {
	// // compare DistributionDouble objects
	// long[] values1 = d1.getValues();
	// long[] values2 = d2.getValues();
	//
	// long[] diffAbs = new long[Math.max(values1.length, values2.length)];
	// long diffAbsDenom = 0;
	// // long[] diffRel = new long[diffAbs.length];
	//
	// for (int i = 0; i < diffAbs.length; i++) {
	// long v1 = 0;
	// long v2 = 0;
	// try {
	// v1 = values1[i];
	// } catch (ArrayIndexOutOfBoundsException e) {
	// }
	// try {
	// v2 = values2[i];
	// } catch (ArrayIndexOutOfBoundsException e) {
	// }
	// diffAbs[i] = v1 - v2;
	// diffAbsDenom += v1;
	//
	// // TODO: RELATIVE QUALITY DISTRIBUTION
	// // if (v2 == 0)
	// // diffRel[i] = Double.MAX_VALUE;
	// // else
	// // diffRel[i] = v1 / v2;
	// }
	//
	// // add absolute comparison
	// list.add(new LongDistr(
	// Files.getDistributionName(d1.getName()) + "_abs", diffAbsDenom,
	// diffAbs));
	//
	// // TODO: RELATIVE QUALITY DISTRIBUTION
	// // add relative comparison
	// // list.add(new
	// // DistributionDouble(Files.getDistributionName(d1.getName())
	// // + "_rel", diffRel));
	// }
}
