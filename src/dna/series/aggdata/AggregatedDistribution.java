package dna.series.aggdata;

<<<<<<< HEAD
<<<<<<< HEAD
import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.etc.Keywords;

/**
 * AggregatedDistribution is a class containing the values of an aggregated Distribution.
 * It contains an array of AggregatedValue objects.
 * AggregatedValue object array structure: { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
=======
import dna.util.Log;

import dna.io.Writer;
import dna.io.etc.Keywords;

/**
 * AggregatedDistribution is a class containing the values of an aggregated distribution.
 * Array structure as follows: values = { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 *
=======
import dna.util.Log;

/**
 * AggregatedDistribution is a class containing the values of an aggregated distribution.
 * 
>>>>>>> Codeupdate 13-06-10.
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedDistribution extends AggregatedData {
<<<<<<< HEAD
<<<<<<< HEAD
	
	// member variables
	private AggregatedValue[] values;
	
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
=======

	// class variables
	private String name;
	private String type = "AggregatedDistribution";
	private double[] values; // array containing the aggregated values for this object. Structure as follows: values = { x, Aggregated-y, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }

	// constructors
	public AggregatedDistribution(String name) {
		super(name);
	}
	
	public AggregatedDistribution(String name, double[] values) {
		super(name, values);
	}

>>>>>>> Codeupdate 13-06-10.
=======

	// class variables
	private String name;
	private String type = "AggregatedDistribution";
	private double[] values; // array containing the aggregated values for this object. Structure as follows: values = { x, Aggregated-y, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }

	// constructors
	public AggregatedDistribution(String name) {
		this.name = name;
	}
	
	public AggregatedDistribution(String name, double[] values) {
		this.name = name;
		this.values = values;
	}
	
	// class methods
	public void setValue(int index, double value) {
		try{
			this.values[index] = value;
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.error("AggregatedDistribution IndexOutOfBoundsException");
		}
	}
	
	public void setValues(double[] values) {
		this.values = values;
	}
	
	public double getValue(int index) {
		try{
			return this.values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.error("AggregatedDistribution IndexOutOfBoundsException");
		}
		return 0;
	}
	
	public double[] getValues() {
		return this.values;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
	}
	
>>>>>>> Codeupdate 13-06-10.
}
