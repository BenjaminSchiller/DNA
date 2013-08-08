package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.util.ArrayUtils;

/**
 * DistributionInt is an object which represents an distribution by whole
 * numbers and its denominator. Integer data-structures are used. For larger
 * numbers see DistributionLong.
 * 
 * @author Rwilmes
 * @date 17.06.2013
 */
public class DistributionInt extends Distribution {

	// class variables
	private int[] values;
	private int denominator;

	// constructors
	public DistributionInt(String name, int[] values) {
		super(name);
		this.values = values;
		this.denominator = ArrayUtils.sum(values);
	}

	public DistributionInt(String name, int[] values, int denominator) {
		super(name);
		this.values = values;
		this.denominator = denominator;
	}

	// get methods
	public int[] getIntValues() {
		return this.values;
	}

	public int getDenominator() {
		return this.denominator;
	}

	public void setDenominator(int denominator) {
		this.denominator = denominator;
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
	 * value array.
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
		Writer w = new Writer(dir, filename);

		w.writeln(this.denominator); // write denominator in first line

		for (int i = 0; i < this.values.length; i++) {
			w.writeln(i + Keywords.distributionDelimiter + this.values[i]);
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
		Reader r = new Reader(dir, filename);
		ArrayList<Integer> list = new ArrayList<Integer>();
		String line = null;
		int index = 0;

		line = r.readString();
		int denominator = Integer.parseInt(line);

		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.distributionDelimiter);
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

}
