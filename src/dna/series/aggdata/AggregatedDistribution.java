package dna.series.aggdata;

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.Writer;
import dna.util.Config;

/**
 * AggregatedDistribution is a class containing the values of an aggregated
 * Distribution. It contains an array of AggregatedValue objects.
 * AggregatedValue object array structure: { x (diff number), avg, min, max,
 * median, variance, variance-low, variance-up, confidence-low, confidence-up }
=======
=======
>>>>>>> Codeupdate 13-06-10.
import dna.util.Log;

/**
 * AggregatedDistribution is a class containing the values of an aggregated distribution.
<<<<<<< HEAD
>>>>>>> Codeupdate 13-06-10.
 * 
=======
import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.etc.Keywords;

/**
<<<<<<< HEAD
 * AggregatedDistribution is a class containing the values of an aggregated distribution.
 * Array structure as follows: values = { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 *
>>>>>>> Codeupdate 13-06-18
=======
 * AggregatedDistribution is a class containing the values of an aggregated Distribution.
 * It contains an array of AggregatedValue objects.
 * AggregatedValue object array structure: { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 * 
>>>>>>> Codeupdate 13-06-24
=======
 * 
>>>>>>> Codeupdate 13-06-10.
=======
import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.etc.Keywords;

/**
<<<<<<< HEAD
 * AggregatedDistribution is a class containing the values of an aggregated distribution.
 * Array structure as follows: values = { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 *
>>>>>>> Codeupdate 13-06-18
=======
 * AggregatedDistribution is a class containing the values of an aggregated Distribution.
 * It contains an array of AggregatedValue objects.
 * AggregatedValue object array structure: { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 * 
>>>>>>> Codeupdate 13-06-24
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedDistribution extends AggregatedData {
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD

<<<<<<< HEAD
<<<<<<< HEAD
	// member variables
	private AggregatedValue[] values;

=======
	
	// member variables
	private AggregatedValue[] values;
	
>>>>>>> Codeupdate 13-06-24
	// constructors
	public AggregatedDistribution(String name) {
		super(name);
	}

	public AggregatedDistribution(String name, AggregatedValue[] values) {
		super(name);
		this.values = values;
	}

	// get methods
	public AggregatedValue[] getValues() {
		return this.values;
	}

	// IO methods
	/**
	 * @param dir
	 *            String which contains the path to the directory the
	 *            AggregatedDistribution will be read from.
	 * 
	 * @param filename
	 *            String representing the filename the Distribution will be read
	 *            from.
	 * 
	 * @param readValues
	 *            Boolean. True: values from the file will be read. False: empty
	 *            Distribution will be created.
	 */
	public static AggregatedDistribution read(String dir, String filename,
			String name, boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedDistribution(name);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<AggregatedValue> list = new ArrayList<AggregatedValue>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Config.get("AGGREGATED_DATA_DELIMITER"));
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			double[] tempDouble = new double[temp.length];
			for (int i = 0; i < tempDouble.length; i++) {
				tempDouble[i] = Double.parseDouble(temp[i]);
			}

			AggregatedValue tempV = new AggregatedValue(name + temp[0],
					tempDouble);
			list.add(tempV);
			index++;
		}
		AggregatedValue[] values = new AggregatedValue[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new AggregatedDistribution(name, values);
	}

<<<<<<< HEAD
	public void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		AggregatedValue[] tempData = this.getValues();

		for (AggregatedValue aggData : tempData) {
			String temp = "" + (int) aggData.getValues()[0]
					+ Config.get("AGGREGATED_DATA_DELIMITER");
			for (int i = 1; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Config.get("AGGREGATED_DATA_DELIMITER");
			}
			w.writeln(temp);
		}
		w.close();
	}

	public static void write(String dir, String filename, double[][] values)
			throws IOException {
		Writer w = new Writer(dir, filename);

		for (int i = 0; i < values.length; i++) {
			String temp = "";
			for (int j = 0; j < values[i].length; j++) {
				if (j == 0)
					temp += (int) values[i][j]
							+ Config.get("AGGREGATED_DATA_DELIMITER");
				else {
					if (j == values[i].length - 1)
						temp += values[i][j];
					else
						temp += values[i][j]
								+ Config.get("AGGREGATED_DATA_DELIMITER");
				}
			}
			w.writeln(temp);
		}
		w.close();
	}

=======
=======

>>>>>>> Codeupdate 13-06-10.
	// class variables
	private String name;
	private String type = "AggregatedDistribution";
	private double[] values; // array containing the aggregated values for this object. Structure as follows: values = { x, Aggregated-y, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }

<<<<<<< HEAD
=======
>>>>>>> Codeupdate 13-06-18
=======
	
	// member variables
	private AggregatedValue[] values;
	
>>>>>>> Codeupdate 13-06-24
	// constructors
	public AggregatedDistribution(String name) {
		super(name);
	}
	
	public AggregatedDistribution(String name, AggregatedValue[] values) {
		super(name);
		this.values = values;
	}
	
	// get methods
	public AggregatedValue[] getValues() {
		return this.values;
	}
<<<<<<< HEAD
<<<<<<< HEAD
=======
=======
>>>>>>> Codeupdate 13-06-18
	// constructors
	public AggregatedDistribution(String name) {
		super(name);
	}
	
	public AggregatedDistribution(String name, AggregatedValue[] values) {
		super(name);
		this.values = values;
	}
	
	// get methods
	public AggregatedValue[] getValues() {
		return this.values;
	}
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> Codeupdate 13-06-10.
	
	public double[] getValues() {
		return this.values;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> Codeupdate 13-06-10.
=======

>>>>>>> Codeupdate 13-06-18
=======
	

	// IO methods
	/**
	 * @param dir String which contains the path to the directory the AggregatedDistribution will be read from.
	 * 
	 * @param filename String representing the filename the Distribution will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty Distribution will be created.	
	 */
	public static AggregatedDistribution read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedDistribution(name, null);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<AggregatedValue> list = new ArrayList<AggregatedValue>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.aggregatedDataDelimiter);
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			double[] tempDouble = new double[temp.length];
			for(int i = 0; i < tempDouble.length; i++) {
				tempDouble[i] = Double.parseDouble(temp[i]);
			}

			AggregatedValue tempV = new AggregatedValue(name + temp[0], tempDouble);
			list.add(tempV);
			index++;
		}
		AggregatedValue[] values = new AggregatedValue[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new AggregatedDistribution(name, values);
	}
>>>>>>> Codeupdate 13-06-24
=======
>>>>>>> Codeupdate 13-06-10.
=======

>>>>>>> Codeupdate 13-06-18
=======
	
=======
>>>>>>> Nachbesserung Merge 28.06.2013
	// IO methods
	/**
	 * @param dir String which contains the path to the directory the AggregatedDistribution will be read from.
	 * 
	 * @param filename String representing the filename the Distribution will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty Distribution will be created.	
	 */
	public static AggregatedDistribution read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedDistribution(name, null);
		}
		Reader r = new Reader(dir, filename);
		ArrayList<AggregatedValue> list = new ArrayList<AggregatedValue>();
		String line = null;
		int index = 0;
		while ((line = r.readString()) != null) {
			String[] temp = line.split(Keywords.aggregatedDataDelimiter);
			if (Integer.parseInt(temp[0]) != index) {
				throw new InvalidFormatException("expected index " + index
						+ " but found " + temp[0] + " @ \"" + line + "\"");
			}
			double[] tempDouble = new double[temp.length];
			for(int i = 0; i < tempDouble.length; i++) {
				tempDouble[i] = Double.parseDouble(temp[i]);
			}

			AggregatedValue tempV = new AggregatedValue(name + temp[0], tempDouble);
			list.add(tempV);
			index++;
		}
		AggregatedValue[] values = new AggregatedValue[list.size()];
		for (int i = 0; i < list.size(); i++) {
			values[i] = list.get(i);
		}
		r.close();
		return new AggregatedDistribution(name, values);
	}
>>>>>>> Codeupdate 13-06-24
}
