package dna.series.data.distr;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.series.lists.DistributionList;
import dna.util.Config;
import dna.util.Log;

public abstract class BinnedDistr<T> extends Distr<T, long[]> {
	protected long denominator;

	public long getDenominator() {
		return this.denominator;
	}

	public BinnedDistr(String name, T binSize) {
		this(name, binSize, new long[0], 0);
	}

	public BinnedDistr(String name, T binSize, long[] values, long denominator) {
		super(name, binSize, values);
		this.denominator = denominator;
	}

	/**
	 * 
	 * @param value
	 *            value that should be mapped to an index for storing its
	 *            occurrences in an array
	 * @return the index this value is mapped to
	 */
	protected abstract int getIndex(T value);

	public abstract T[] getBin(int index);

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

	/**
	 * computes an upper bound for the given percent of values, e.g.: when
	 * percent = 95, the returned value will be an upper bound for 95% of the
	 * values.
	 * 
	 * @return upper bound for the given percent of values (-1 in case it is
	 *         undefined)
	 */
	public double computeUpperBound(double percent) {
		long number = (long) Math
				.floor((double) this.denominator * (1 - percent));
		int maxIndex = this.getMaxNonZeroIndex();

		long count = 0;
		int index = 0;
		// iterate from maxIndex downwards
		for (int i = maxIndex; i >= 0; i--) {
			count += this.values[i];
			index = i;
			if(count >= number)
				break;
		}
		
		return index;
	}

	@Override
	public void print() {
		System.out.println("name: " + this.getName());
		System.out.println("denominator: " + this.denominator);
		System.out.println("@ binSize: " + this.binSize);
		for (int i = 0; i < this.values.length; i++) {
			T[] bin = this.getBin(i);
			System.out.println("    " + i + ": " + this.values[i] + " ("
					+ bin[0] + "," + bin[1] + "]");
		}
	}

	@Override
	public void write(String dir, String filename) throws IOException {
		Writer w = Writer.getWriter(dir, filename);
		w.writeln(this.denominator);
		w.writeln(this.binSize.toString());
		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Config.get("DISTRIBUTION_DELIMITER") + this.values[i]);
		}
		w.close();
	}

	/**
	 * reads a binned distribution from the specified file. in case the
	 * readValues flag is false, an empty distribution of the specified type
	 * with the given name is returned. otherwise, denominator and values are
	 * read from the file.
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
	public static BinnedDistr read(String dir, String filename, String name,
			boolean readValues, Class<? extends BinnedDistr> type)
			throws IOException {
		if (!readValues) {
			try {
				return (BinnedDistr) type.getConstructor(String.class)
						.newInstance(name);
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

		// binSize
		String binSize = r.readString();

		// values
		ArrayList<Long> list = new ArrayList<Long>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Long.parseLong(temp[1]));
			index++;
		}
		long[] values = new long[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		try {
			return (BinnedDistr) type.getConstructor(String.class,
					String.class, long[].class, long.class).newInstance(name,
					binSize, values, denominator);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			System.err.println("could not read distribution of type '"
					+ type.getSimpleName() + "' from " + dir + filename);
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof BinnedDistr)) {
			return false;
		}
		if (!obj.getClass().equals(this.getClass())) {
			return false;
		}
		BinnedDistr d = (BinnedDistr) obj;
		if (!d.getName().equals(this.getName())) {
			return false;
		}
		if (!d.binSize.equals(this.binSize)) {
			return false;
		}
		if (d.denominator != this.denominator) {
			return false;
		}
		if (((long[]) d.values).length != this.values.length) {
			return false;
		}
		for (int i = 0; i < this.values.length; i++) {
			if (((long[]) d.values)[i] != this.values[i]) {
				return false;
			}
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public boolean equalsVerbose(BinnedDistr d) {
		if (!d.getClass().equals(this.getClass())) {
			Log.warn("distribution type differs: (" + this.getName() + ") "
					+ this.getClass().getSimpleName() + " != "
					+ d.getClass().getSimpleName() + " (" + d.getName() + ")");
			return false;
		}
		if (!d.getName().equals(this.getName())) {
			Log.warn("name differs: " + this.getName() + " != " + d.getName());
			return false;
		}
		if (!d.binSize.equals(this.binSize)) {
			Log.warn(this.getName() + " - binSize differs: "
					+ this.getBinSize() + " != " + d.getBinSize());
			return false;
		}
		if (d.denominator != this.denominator) {
			Log.warn(this.getName() + " - denominator differs: "
					+ this.getDenominator() + " != " + d.getDenominator());
			return false;
		}
		if (((long[]) d.values).length != this.values.length) {
			Log.warn(this.getName() + " - length of values differs: "
					+ this.values.length + " != " + ((long[]) d.values).length);
			return false;
		}
		for (int i = 0; i < this.values.length; i++) {
			if (((long[]) d.values)[i] != this.values[i]) {
				Log.warn(this.getName() + " - value differs at index " + i
						+ ": " + this.values[i] + " != "
						+ ((long[]) d.values)[i]);
				return false;
			}
		}
		return true;
	}

	/**
	 * Compares the two distributions and adds an absolute and a relative
	 * quality distribution to the distribution-list.
	 **/
	public static void compareDistributionsAndAddToList(DistributionList list,
			BinnedDistr<?> d1, BinnedDistr<?> d2) {
		if (!d1.getBinSize().equals(d2.getBinSize()))
			return;

		long[] values1 = d1.getValues();
		long[] values2 = d2.getValues();

		double[] diffAbs = new double[Math.max(values1.length, values2.length)];
		double[] diffRel = new double[diffAbs.length];

		long denom1 = d1.getDenominator();
		long denom2 = d2.getDenominator();

		// calc differences
		for (int i = 0; i < diffAbs.length; i++) {
			double v1 = 0;
			double v2 = 0;
			try {
				v1 = values1[i] * 1.0 / denom1;
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			try {
				v2 = values2[i] * 1.0 / denom2;
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			diffAbs[i] = v1 - v2;

			if (v2 == 0)
				diffRel[i] = Double.MAX_VALUE;
			else
				diffRel[i] = v1 / v2;
		}

		switch (d1.getDistrType()) {
		case BINNED_DOUBLE:
			list.add(new QualityDoubleDistr(d1.getName() + "_abs",
					((BinnedDoubleDistr) d1).getBinSize(), diffAbs));
			list.add(new QualityDoubleDistr(d1.getName() + "_rel",
					((BinnedDoubleDistr) d1).getBinSize(), diffRel));
			break;
		case BINNED_INT:
			list.add(new QualityIntDistr(d1.getName() + "_abs",
					((BinnedIntDistr) d1).getBinSize(), diffAbs));
			list.add(new QualityIntDistr(d1.getName() + "_rel",
					((BinnedIntDistr) d1).getBinSize(), diffRel));
			break;
		case BINNED_LONG:
			list.add(new QualityLongDistr(d1.getName() + "_abs",
					((BinnedLongDistr) d1).getBinSize(), diffAbs));
			list.add(new QualityLongDistr(d1.getName() + "_rel",
					((BinnedLongDistr) d1).getBinSize(), diffRel));
			break;
		}
	}
}
