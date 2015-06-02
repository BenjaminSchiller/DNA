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
 * Distribution is a class for representing a distribution. Values are stored in
 * a double array.
 */
public class DistributionDouble extends Distribution {

	// member variables
	private double[] values;

	// constructors
	public DistributionDouble(String name) {
		super(name);
		this.values = new double[0];
	}

	public DistributionDouble(String name, double[] values) {
		super(name);
		this.values = values;
	}

	// class methods
	public String toString() {
		return "distributionDouble(" + super.getName() + ")";
	}

	// get methods
	public double[] getValues() {
		return this.values;
	}

	public int getMin() {
		int y = 0;
		while (values[y] < 0) {
			y++;
		}
		return y;
	}

	public int getMax() {
		return values.length - 1;
	}

	/**
	 * Increments a value of the distribution.
	 * 
	 * @param index
	 *            Index of the value that will be incremented.
	 */
	public void incr(int index) {
		this.values = ArrayUtils.incr(this.values, index);
	}

	/**
	 * Decrements a value of the distribution.
	 * 
	 * @param index
	 *            Index of the value that will be decremented.
	 */
	public void decr(int index) {
		this.values = ArrayUtils.decr(this.values, index);
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
	public void set(int index, double value) {
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
	public static DistributionDouble read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new DistributionDouble(name, null);
		}
		Reader r = Reader.getReader(dir, filename);
		ArrayList<Double> list = new ArrayList<Double>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("DISTRIBUTION_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			list.add(Double.parseDouble(temp[1]));
			index++;
		}
		double[] values = new double[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new DistributionDouble(name, values);
	}

	/**
	 * @param d1
	 *            distribution with double datastructures
	 * @param d2
	 *            distribution with double datastructures to compare equality
	 * @return true if both distributions have the same length and all values
	 *         are equal
	 */
	public static boolean equals(DistributionDouble d1, DistributionDouble d2) {
		return ArrayUtils.equals(d1.getValues(), d2.getValues());
	}

	/**
	 * Compares the two distributions and adds an absolute and a relative
	 * quality distribution to the distribution-list.
	 **/
	public static void compareDistributionsAndAddToList(DistributionList list,
			DistributionDouble d1, DistributionDouble d2) {
		// compare DistributionDouble objects
		double[] values1 = d1.getValues();
		double[] values2 = d2.getValues();

		double[] diffAbs = new double[Math.max(values1.length, values2.length)];
		double[] diffRel = new double[diffAbs.length];

		for (int i = 0; i < diffAbs.length; i++) {
			double v1 = 0;
			double v2 = 0;
			try {
				v1 = values1[i];
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			try {
				v2 = values2[i];
			} catch (ArrayIndexOutOfBoundsException e) {
			}
			diffAbs[i] = v1 - v2;

			if (v2 == 0)
				diffRel[i] = Double.MAX_VALUE;
			else
				diffRel[i] = v1 / v2;
		}

		// add absolute comparison
		list.add(new DistributionDouble(Files.getDistributionName(d1.getName())
				+ "_abs", diffAbs));

		// add relative comparison
		list.add(new DistributionDouble(Files.getDistributionName(d1.getName())
				+ "_rel", diffRel));
	}
}
