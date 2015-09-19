package dna.series.data.distributions;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.util.Config;

/**
 * BinnedDistributionInt is an object which represents a binned-distribution
 * with int values. It accepts ints as indices, which are mapped on their
 * internal indices using the bin-size.
 * 
 * The index mapping works like this: mappedIndex = Math.floor((index/binsize))
 * 
 * @author Rwilmes
 * @date 07.03.2043
 */
public class BinnedDistributionInt extends DistributionInt {

	// class variables
	private int binsize;

	// constructors
	/**
	 * Creates a BinnedDistributionInt with an empty int-array of size zero and
	 * a denominator of zero.
	 **/
	public BinnedDistributionInt(String name, int binsize) {
		super(name, new int[0], 0);
		this.binsize = binsize;
	}

	public BinnedDistributionInt(String name, int binsize, int[] values,
			int denominator) {
		super(name, values, denominator);
		this.binsize = binsize;
	}

	// class methods
	public String toString() {
		return "binnedDistributionInt(" + super.getName() + ")";
	}

	/** Returns the bin size **/
	public int getBinSize() {
		return this.binsize;
	}

	/**
	 * Increments a value of the distribution. Note: Also increments the
	 * denominator!
	 * 
	 * @param index
	 *            Index of the value that will be incremented.
	 */
	public void incr(int index) {
		int mappedIndex = (int) Math.floor((index * (1 / this.binsize)));
		super.incr(mappedIndex);
	}

	/**
	 * Decrements a value of the distribution. Note: Also decrements the
	 * denominator!
	 * 
	 * @param index
	 *            Index of the value that will be decremented.
	 */
	public void decr(int index) {
		int mappedIndex = (int) Math.floor((index * (1 / this.binsize)));
		super.decr(mappedIndex);
	}

	/**
	 * Sets the value with a chosen index. Note: The denominator is not updated
	 * when calling this function!
	 * 
	 * @param index
	 *            Index of the value that will be set.
	 * @param value
	 *            Value the integer will be set to.
	 */
	public void set(int index, int value) {
		int mappedIndex = (int) Math.floor((index * (1 / this.binsize)));
		super.set(mappedIndex, value);
	}

	/**
	 * Returns the value with a chosen index.
	 * 
	 * @param index
	 *            Index of the value that will be returned.
	 * @param value
	 *            Value the integer will be set to.
	 */
	public int get(int index) {
		int mappedIndex = (int) Math.floor((index * (1 / this.binsize)));
		return (super.getValues()[mappedIndex]);
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
		int[] values = this.getValues();
		if (values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.getName() + "\" set to be written to " + dir);
		}
		Writer w = Writer.getWriter(dir, filename);

		w.writeln(this.getDenominator()); // write denominator in first line
		w.writeln(this.binsize); // write binsize in second line

		for (int i = 0; i < values.length; i++) {
			w.writeln(i + Config.get("DISTRIBUTION_DELIMITER") + values[i]);
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
	public static BinnedDistributionInt read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new BinnedDistributionInt(name, 1, null, 0);
		}
		Reader r = Reader.getReader(dir, filename);
		ArrayList<Integer> list = new ArrayList<Integer>();
		String line = null;
		int index = 0;

		line = r.readString();
		int denominator = Integer.parseInt(line);
		line = r.readString();
		int binsize = Integer.parseInt(line);

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
		return new BinnedDistributionInt(name, binsize, values, denominator);
	}

	/**
	 * @param d1
	 *            binned distribution with int datastructures
	 * @param d2
	 *            binned distribution with int datastructures to compare
	 *            equality
	 * @return true if both distributions have the same binsize, denominator,
	 *         amount of values and all values are equal
	 */
	public static boolean equals(BinnedDistributionInt d1,
			BinnedDistributionInt d2) {
		if (d1.getBinSize() != d2.getBinSize())
			return false;
		return DistributionInt.equals(d1, d2);
	}
}
