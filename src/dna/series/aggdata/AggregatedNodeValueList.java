package dna.series.aggdata;

import java.io.IOException;
import java.util.ArrayList;

import com.sun.media.sound.InvalidFormatException;

import dna.io.Reader;
import dna.io.etc.Keywords;

/**
 * AggregatedNodeValueList is a class containing the values of an aggregated NodeValueList.
 * It contains an array of AggregatedValue objects.
 * AggregatedValue object array structure: { x (diff number), avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedNodeValueList extends AggregatedData {

	// member variables
	private AggregatedValue[] values;
	
	// constructors
	public AggregatedNodeValueList(String name) {
		super(name);
	}
	
	public AggregatedNodeValueList(String name, AggregatedValue[] values) {
		super(name);
		this.values = values;
	}

	// get methods
	public AggregatedValue[] getValues() {
		return this.values;
	}
	
	// IO methods
	/**
	 * @param dir String which contains the path to the directory the AggregatedNodeValueList will be read from.
	 * 
	 * @param filename String representing the filename the Distribution will be read from.
	 * 
	 * @param readValues Boolean. True:  values from the file will be read.
	 * 							  False: empty AggregatedNodeValueList will be created.	
	 */
	public static AggregatedNodeValueList read(String dir, String filename, String name,
			boolean readValues) throws IOException {
		if (!readValues) {
			return new AggregatedNodeValueList(name, null);
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
		return new AggregatedNodeValueList(name, values);
	}
}
