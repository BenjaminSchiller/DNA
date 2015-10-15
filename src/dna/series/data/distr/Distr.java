package dna.series.data.distr;

import java.io.IOException;

import dna.io.filesystem.Files;
import dna.series.data.Data;

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
}
