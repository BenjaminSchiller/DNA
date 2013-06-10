package dna.series.data;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
<<<<<<< HEAD
import dna.util.Config;

/**
 * Distribution is a class for representing a distribution. Values are stored in
 * a private double array.
 * 
 * @date 24.06.2013
 */
public class Distribution extends Data {

	// member variables
	private double[] values;

	// constructors
=======
import dna.io.etc.Keywords;
import dna.util.Log;

public class Distribution extends Data {
//public class Distribution implements ListItem {
>>>>>>> Codeupdate 13-06-10.
	public Distribution(String name, double[] values) {
		super(name);
		this.values = values;
	}

<<<<<<< HEAD
	public Distribution(String name) {
		super(name);
=======
	public Distribution(String name, double value) {
		Log.warn("Distribution initialized with a single value");
		double[] temp = { value };
		this.values = temp;
	}
	
	public String toString() {
		return "distribution(" + this.name + ")";
>>>>>>> Codeupdate 13-06-10.
	}

	// class methods
	public String toString() {
		return "distribution(" + super.getName() + ")";
	}

	public double[] getValues() {
		return this.values;
	}

<<<<<<< HEAD
	// IO Methods
	/**
	 * @param dir
	 *            String which contains the path / directory the Distribution
	 *            will be written to.
	 * 
	 * @param filename
	 *            String representing the desired filename for the Distribution.
	 */
=======
	public String getType() {
		return "Distribution";
	}
	
>>>>>>> Codeupdate 13-06-10.
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for distribution \""
					+ this.getName() + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);
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
	public static Distribution read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new Distribution(name, null);
		}
		Reader r = new Reader(dir, filename);
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
		return new Distribution(name, values);
	}

}
