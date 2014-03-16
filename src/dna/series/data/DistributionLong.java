package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.util.ArrayUtils;
import dna.util.Config;

/**
 * DistributionLong is an object which represents an distribution by whole
 * numbers and its denominator. Due to the use of long numbers it provides a way
 * to represent distributions with large numbers. Additional values are used for
 * compared distributions.
 * 
 * @author Rwilmes
 * @date 17.06.2013
 */
public class DistributionLong extends Distribution {

	// class variables
	private long[] values;
	private long denominator;

	// values for comparison
	private long comparedSum;
	private long comparedMin;
	private long comparedMax;
	private long comparedMed;
	private double comparedAvg;

	// constructor
	public DistributionLong(String name, long[] values, long denominator) {
		super(name);
		this.values = values;
		this.denominator = denominator;
	}

	public DistributionLong(String name) {
		super(name);
		this.values = new long[0];
		this.denominator = 0;
	}

	public DistributionLong(String name, long[] values, long denominator,
			long sum, long min, long max, long med, double avg) {
		super(name);
		this.values = values;
		this.denominator = denominator;
		this.comparedSum = sum;
		this.comparedMin = min;
		this.comparedMax = max;
		this.comparedMed = med;
		this.comparedAvg = avg;
	}

	// class methods
	public String toString() {
		return "distributionLong(" + super.getName() + ")";
	}

	// get methods
	public long[] getLongValues() {
		return this.values;
	}

	public long getDenominator() {
		return this.denominator;
	}

	public void setDenominator(long denominator) {
		this.denominator = denominator;
	}

	public long getMin() {
		int y = 0;
		while (values[y] < 0) {
			y++;

		}
		return (long) y;
	}

	public long getMax() {
		return (long) values.length - 1;
	}

	public long getComparedSum() {
		return this.comparedSum;
	}

	public long getComparedMin() {
		return this.comparedMin;
	}

	public long getComparedMax() {
		return this.comparedMax;
	}

	public long getComparedMed() {
		return this.comparedMed;
	}

	public double getComparedAvg() {
		return this.comparedAvg;
	}

	/**
	 * Recalculates the denominator value.
	 */
	public void updateDenominator() {
		this.denominator = ArrayUtils.sum(this.values);
	}

	/**
	 * Increments a value of the distribution. Note: Also increments the
	 * denominator!
	 * 
	 * @param index
	 *            Index of the value that will be incremented.
	 */
	public void incr(int index) {
		this.values = ArrayUtils.incr(this.values, index);
		this.denominator++;
	}

	/**
	 * Decrements a value of the distribution. Note: Also decrements the
	 * denominator!
	 * 
	 * @param index
	 *            Index of the value that will be decremented.
	 */
	public void decr(int index) {
		this.values = ArrayUtils.decr(this.values, index);
		this.denominator--;
	}

	/**
	 * Truncates the distribution array by erasing all 0 at the end of it's
	 * value array. Note: Not affecting the denominator!
	 * 
	 * @param index
	 *            Index of the value that will be decremented.
	 */
	public void truncate() {
		this.values = ArrayUtils.truncate(this.values, 0);
	}

	/**
	 * Truncates the value with a chosen index. Note: The denominator is not
	 * updated when calling this function!
	 * 
	 * @param index
	 *            Index of the value that will be decremented.
	 * @param value
	 *            Value the integer will be set to.
	 */
	public void set(int index, long value) {
		this.values = ArrayUtils.set(this.values, index, value, 0);
	}

	// IO Methods
	/**
	 * @param dir
	 *            String which contains the path / directory the Distribution
	 *            will be written to.
	 * 
	 * @param filename
	 *            String representing the desired filename for the Distribution.
	 */
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.getName() + "\" set to be written to " + dir);
		}
		Writer w = Writer.getWriter(dir, filename);

		w.writeln(this.denominator); // write denominator in first line

		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Config.get("DISTRIBUTION_DELIMITER") + this.values[i]);
		}
		w.close();
	}

	/**
	 * @param dir
	 *            String which contains the path to the directory the
	 *            Distribution will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the Distribution will be read
	 *            from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            Distribution will be created.
	 */
	public static DistributionLong read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new DistributionLong(name, null, 0);
		}
		Reader r = Reader.getReader(dir, filename);
		ArrayList<Long> list = new ArrayList<Long>();
		String line = null;
		int index = 0;

		line = r.readString();
		long denominator = Long.parseLong(line);

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
		return new DistributionLong(name, values, denominator);
	}

	/**
	 * @param d1
	 *            distribution with long datastructures
	 * @param d2
	 *            distribution with long datastructures to compare equality
	 * @return true if both distributions have the same denominator, amount of
	 *         values and all values are equal
	 */
	public static boolean equals(DistributionLong d1, DistributionLong d2) {
		if (d1.getDenominator() != d2.getDenominator())
			return false;
		return ArrayUtils.equals(d1.getLongValues(), d2.getLongValues());
	}

	public double computeAverage() {
		double avg = 0;
		for (int i = 0; i < this.values.length; i++) {
			avg += i * this.values[i];
		}
		return avg / this.denominator;
	}

}
