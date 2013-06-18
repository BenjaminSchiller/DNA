package dna.series.aggdata;

<<<<<<< HEAD
import java.io.IOException;

import dna.io.Reader;
import dna.io.etc.Keywords;

/**
 * An AggregatedValue object contains aggregated values.
=======
/**
 * AggregatedValue is a class containing the aggregated values of a list of values.
 * Array structure as follows: values = { avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedValue extends AggregatedData {

<<<<<<< HEAD
	// member variables
	private double[] values;
	
	// constructors
	public AggregatedValue(String name) {
		super(name);
	}
	
	public AggregatedValue(String name, double[] values) {
		super(name);
		this.values = values;
	}
	
	// get methods
=======
	// class variables
	private String name;
	private String type = "AggregatedValue";
	private double value;
	private double[] values; // AggregatedValue array structure:  { 0, 0, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
	
	// constructors
	public AggregatedValue(String name) {
		super(name);
	}
	
	public AggregatedValue(String name, double[] values) {
		super(name, values);
	}
	
	// class methods
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return this.value;
	}
	
>>>>>>> Codeupdate 13-06-10.
	public double[] getValues() {
		return this.values;
	}
	
<<<<<<< HEAD
	// IO methods
	/**
	 * @param dir String which contains the path to the directory the AggregatedValue will be read from.
	 * 
	 * @param filename String representing the filename the Distribution will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty AggregatedValue will be created.	
	 */
	public static AggregatedValue read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedValue(name, null);
		}
		Reader r = new Reader(dir, filename);

		String line = null;
		
		line = r.readString();
		String[] temp = line.split(Keywords.aggregatedDataDelimiter);

		double[] tempDouble = new double[temp.length];
		for(int i = 0; i < tempDouble.length; i++) {
			tempDouble[i] = Double.parseDouble(temp[i]);
		}

		r.close();
		return new AggregatedValue(name, tempDouble);
=======
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return this.type;
>>>>>>> Codeupdate 13-06-10.
	}

}
