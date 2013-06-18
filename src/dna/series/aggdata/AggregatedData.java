package dna.series.aggdata;

import java.io.IOException;

import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.series.lists.ListItem;
import dna.util.Log;

/**
 * AggregatedData is the super-class for all provided aggregated data-structures.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedData implements ListItem {
	
	// class variables
	private String name;
	private double[] values;
	
	// class methods
	public AggregatedData() { }
	
	public AggregatedData(String name) {
		this.name = name;
	}
	
	public AggregatedData(String name, double[] values) {
		this.name = name;
		this.values = values;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double[] getValues() {
		return this.values;
	}
	
	public double getValue(int index) {
		try{
			return this.values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.error("AggregatedNodeValueList IndexOutOfBoundsException");
		}
		return 0;
	}
	
	// IO methods
	/**
	 * Method to write the context of a single AggregatedData object to a specified location.
	 * 
	 * @param dir String which contains the path / directory the Data will be written to.
	 * 
	 * @param filename String representing the desired filename for the Data.
	 */
	public void write(String dir, String filename) throws IOException {
		if (this.values == null) {
			throw new NullPointerException("no values for aggregateddata \""
					+ this.name + "\" set to be written to " + dir);
		}
		Writer w = new Writer(dir, filename);
		String temp = "";
		for (int i = 0; i < this.values.length; i++) {
			if(i == this.values.length-1)
				temp += this.values[i];
			else
				temp += this.values[i] + "\t";
		}
		w.writeln(temp);
		w.close();
	}
	
	/**
	 * Method to write the context of a single AggregatedData object to a specified location.
	 * 
	 * @param inputData The Aggregated Data that is to be written on the filesystem.
	 * @param dir String which contains the path / directory the Data will be written to.
	 * @param filename String representing the desired filename for the Data.
	 */
	public static void write(AggregatedData[] inputData, String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		
		for(AggregatedData aggData : inputData) {
			String temp = "";
			for (int i = 0; i < aggData.getValues().length; i++) {
				if(i == aggData.getValues().length-1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i] + "\t";
			}
			w.writeln(temp);
		}
		w.close();
	}
}
