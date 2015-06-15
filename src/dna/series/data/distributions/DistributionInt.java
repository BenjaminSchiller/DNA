package dna.series.data.distributions;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.filesystem.Files;
import dna.series.lists.DistributionList;
import dna.util.ArrayUtils;
import dna.util.Config;

/**
 * DistributionInt is an object which represents an distribution by whole
 * numbers and its denominator. Integer data-structures are used. For larger
 * numbers see DistributionLong. Additional values are used for compared
 * distributions.
 * 
 * @author Rwilmes
 * @date 17.06.2013
 */
public class DistributionInt extends Distribution {

	// class variables
	private int[] values;
	private int denominator;

	// constructors
	public DistributionInt(String name, int[] values, int denominator) {
		super(name);
		this.values = values;
		this.denominator = denominator;
	}

	public DistributionInt(String name) {
		super(name);
		this.values = new int[0];
		this.denominator = 0;
	}

	// class methods
	public String toString() {
		return "distributionInt(" + super.getName() + ")";
	}

	// get methods
	public int[] getValues() {
		return this.values;
	}

	public int getDenominator() {
		return this.denominator;
	}

	public void setDenominator(int denominator) {
		this.denominator = denominator;
	}

	public void incrDenominator() {
		this.incrDenominator(1);
	}

	public void incrDenominator(int count) {
		this.denominator += count;
	}

	public void decrDenominator() {
		this.decrDenominator(1);
	}

	public void decrDenominator(int count) {
		this.denominator -= count;
	}

	public int getMin() {
		int y = 0;
		if (values.length == 0) {
			return -1;
		}
		while (values[y] <= 0) {
			y++;
			if (y == values.length) {
				break;
			}
		}
		return y;
	}

	public int getMax() {
		return values.length - 1;
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
	 * value array. Note: Not affecting denominator.
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
	public void set(int index, int value) {
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
	public static DistributionInt read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new DistributionInt(name, null, 0);
		}
		Reader r = Reader.getReader(dir, filename);
		ArrayList<Integer> list = new ArrayList<Integer>();
		String line = null;
		int index = 0;

		line = r.readString();
		int denominator = Integer.parseInt(line);

		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Integer.parseInt(temp[1]));
			index++;
		}
		int[] values = new int[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new DistributionInt(name, values, denominator);
	}

	/**
	 * @param d1
	 *            distribution with integer datastructures
	 * @param d2
	 *            distribution with integer datastructures to compare equality
	 * @return true if both distributions have the same denominator, amount of
	 *         values and all values are equal
	 */
	public static boolean equals(DistributionInt d1, DistributionInt d2) {
		if (d1.getDenominator() != d2.getDenominator())
			return false;
		return ArrayUtils.equals(d1.getValues(), d2.getValues());
	}

	public double computeAverage() {
		double avg = 0;
		for (int i = 0; i < this.values.length; i++) {
			avg += i * this.values[i];
		}
		return avg / this.denominator;
	}

	/**
	 * Compares the two distributions and adds an absolute and a relative
	 * quality distribution to the distribution-list.
	 **/
	public static void compareDistributionAndAddToList(DistributionList list,
			DistributionInt d1, DistributionInt d2) {
		// compare DistributionInt objects
		int[] values1 = d1.getValues();
		int[] values2 = d2.getValues();
		int[] diffAbs = new int[Math.max(values1.length, values2.length)];
		double[] diffRel = new double[diffAbs.length];

		int denom1 = d1.getDenominator();
		int denom2 = d2.getDenominator();

		for (int i = 0; i < diffAbs.length; i++) {
			int v1 = 0;
			int v2 = 0;

			try {
				v1 = values1[i] * denom2;
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			try {
				v2 = values2[i] * denom1;
			} catch (ArrayIndexOutOfBoundsException e) {
			}

			diffAbs[i] = v1 - v2;

			if (v2 == 0) {
				diffRel[i] = Double.MAX_VALUE;
			} else {
				diffRel[i] = v1 / v2;
			}
		}
		// add absolute comparison
		list.add(new DistributionInt(Files.getDistributionName(d1.getName())
				+ "_abs", diffAbs, denom1 * denom2));

		// add relative comparison
		list.add(new DistributionDouble(Files.getDistributionName(d1.getName())
				+ "_rel", diffRel));
	}

}
