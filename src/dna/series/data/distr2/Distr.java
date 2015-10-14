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
}
