package dna.series.data.distr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.data.Data;
import dna.util.Config;

public abstract class Distr<T> extends Data {
	protected long denominator;
	protected long[] values;

	public Distr(String name) {
		this(name, 0, new long[0]);
	}

	public Distr(String name, long denominator, long[] values) {
		super(name);
		this.denominator = denominator;
		this.values = values;
	}

	/**
	 * increases the occurrences for the given value by 1. in addition, the
	 * denominator is increased by 1.
	 * 
	 * @param value
	 *            value to increase the number of occurrences for
	 * @return number of occurrences after the increase
	 */
	public long incr(T value) {
		return this.incr(value, 1);
	}

	/**
	 * increases the occurrences for the given value by the specified count. in
	 * addition, the denominator is increased by the same value
	 * 
	 * @param value
	 *            value to increase the number of occurrences for
	 * @param count
	 *            amount to increase by
	 * @return number of occurrences after the increase
	 */
	public long incr(T value, int count) {
		int index = this.getIndex(value);
		if (index >= this.values.length) {
			long[] temp = this.values;
			this.values = new long[index + 1];
			System.arraycopy(temp, 0, this.values, 0, temp.length);
		}
		this.values[index] += count;
		this.denominator += count;
		return this.values[index];
	}

	/**
	 * decreases the occurrences for the given value by 1. in addition, the
	 * denominator is decreased by 1.
	 * 
	 * @param value
	 *            value to decrease the number of occurrences for
	 * @return number of occurrences after the decrease
	 */
	public long decr(T value) {
		return this.decr(value, 1);
	}

	/**
	 * decreases the occurrences for the given value by the specified count. in
	 * addition, the denominator is decreased by the same value
	 * 
	 * @param value
	 *            value to decrease the number of occurrences for
	 * @param count
	 *            amount to decrease by
	 * @return number of occurrences after the decrease
	 */
	public long decr(T value, int count) {
		int index = this.getIndex(value);
		this.values[index] -= count;
		this.denominator -= count;
		return this.values[index];
	}

	/**
	 * truncates the array used to store the occurrences of the respective
	 * values, i.e., removes all tailing entries with a value of 0 occurrences.
	 * 
	 * @return number of elements removed (=0 in case the array was not
	 *         shortened)
	 */
	public int truncate() {
		int cutFrom = this.values.length;
		for (int i = this.values.length - 1; i >= 0; i--) {
			if (this.values[i] == 0) {
				cutFrom = i;
			} else {
				break;
			}
		}
		if (cutFrom == this.values.length) {
			return 0;
		}
		long[] temp = this.values;
		this.values = new long[cutFrom];
		System.arraycopy(temp, 0, this.values, 0, cutFrom);
		return temp.length - cutFrom;
	}

	/**
	 * outputs the contents stored in this distribution
	 */
	public void print() {
		System.out.println("name: " + this.getName());
		System.out.println("denominator: " + this.denominator);
		for (int i = 0; i < this.values.length; i++) {
			System.out.println("    " + i + ": " + this.values[i]);
		}
	}

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
	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);
		w.writeln(this.denominator);
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Config.get("DISTRIBUTION_DELIMITER") + this.values[i]);
		}
		w.close();
	}

	/**
	 * reads a distribution from the specified file. in case the readValues flag
	 * is false, an empty distribution of the specified type with the given name
	 * is returned. otherwise, denominator and values are read from the file.
	 * 
	 * @param dir
	 * @param filename
	 * @param name
	 * @param readValues
	 * @param type
	 * @return the read distribution and null in case an error occurrs.
	 * @throws IOException
	 */
	@SuppressWarnings("rawtypes")
	public static Distr read(String dir, String filename, String name,
			boolean readValues, Class<? extends Distr> type) throws IOException {
		if (!readValues) {
			try {
				return (Distr) type.getConstructor(String.class).newInstance(
						name);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				System.err.println("could not read distribution of type '"
						+ type.getSimpleName() + "' from " + dir + filename);
				e.printStackTrace();
				return null;
			}
		}
		Reader r = Reader.getReader(dir, filename);

		// denominator
		long denominator = r.readLong();

		// values
		ArrayList<Integer> list = new ArrayList<Integer>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Integer.parseInt(temp[1]));
			index++;
		}
		long[] values = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		try {
			return (Distr) type.getConstructor(String.class, long.class,
					long[].class).newInstance(name, denominator, values);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			System.err.println("could not read distribution of type '"
					+ type.getSimpleName() + "' from " + dir + filename);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param value
	 *            value that should be mapped to an index for storing its
	 *            occurrences in an array
	 * @return the index this value is mapped to
	 */
	protected abstract int getIndex(T value);

	public long getDenominator() {
		return this.denominator;
	}

	public long[] getValues() {
		return this.values;
	}

	/**
	 * 
	 * @return minimum index with a non-zero value (-1 in case no non-zero entry
	 *         exists)
	 */
	public int getMinNonZeroIndex() {
		for (int i = 0; i < this.values.length; i++) {
			if (this.values[i] != 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 
	 * @return maximum index with a non-zero value (-1 in case no non-zero entry
	 *         exists)
	 */
	public int getMaxNonZeroIndex() {
		for (int i = this.values.length - 1; i >= 0; i--) {
			if (this.values[i] != 0) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * note that this average is computed with each call of this function.
	 * 
	 * @return average value of the property reflected by this distribution
	 */
	public double computeAverage() {
		long sum = 0;
		for (int i = 1; i < this.values.length; i++) {
			sum += i * this.values[i];
		}
		return (double) sum / (double) this.denominator;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Distr)) {
			return false;
		}
		Distr d = (Distr) obj;
		if (!d.getName().equals(this.getName())) {
			return false;
		}
		if (d.denominator != this.denominator) {
			return false;
		}
		if (d.values.length != this.values.length) {
			return false;
		}
		for (int i = 0; i < this.values.length; i++) {
			if (d.values[i] != this.values[i]) {
				return false;
			}
		}
		return true;
	}

	// TODO add quality distribution generation
	/*
	 * public static void compareDistributionAndAddToList(DistributionList list,
	 * DistributionInt d1, DistributionInt d2) {
	 */

	// TODO add verbose equality check with output (now in ArrayUtils...)
}
