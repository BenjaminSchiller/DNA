package dna.series.aggdata;

import java.io.IOException;

import dna.io.Reader;
import dna.io.etc.Keywords;

/**
 * An AggregatedValue object contains aggregated values.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedValue extends AggregatedData {

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
	public double[] getValues() {
		return this.values;
	}
	
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
	}

}
