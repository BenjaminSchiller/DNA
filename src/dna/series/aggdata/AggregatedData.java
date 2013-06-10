package dna.series.aggdata;

<<<<<<< HEAD
import java.io.IOException;

import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.series.lists.ListItem;

/**
 * AggregatedData is the super-class for all provided aggregation data-structures.
=======
import dna.series.lists.ListItem;

/**
 * AggregatedData is the super-class for all provided aggregated data-structures.
>>>>>>> Codeupdate 13-06-10.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedData implements ListItem {
	
<<<<<<< HEAD
	// member variables
	private String name;
	
	// constructors
=======
	// class variables
	private String name;
	private double value;
	private double[] values;
	
	// class methods
>>>>>>> Codeupdate 13-06-10.
	public AggregatedData() { }
	
	public AggregatedData(String name) {
		this.name = name;
	}
	
<<<<<<< HEAD
	// get methods
	public String getName() {
		return this.name;
	}
	
	// IO methods
	/**
	 * Method to write the context of an Array of AggregatedValue objects to a specified location.
	 * 
	 * @param inputData The Aggregated Data that is to be written on the filesystem.
	 * @param dir String which contains the path / directory the Data will be written to.
	 * @param filename String representing the desired filename for the Data.
	 */
	public static void write(AggregatedValue[] inputData, String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		
		for(AggregatedValue aggData : inputData) {			
			String temp = "" + (int) aggData.getValues()[0] + Keywords.aggregatedDataDelimiter;
			for (int i = 1; i < aggData.getValues().length; i++) {
				if(i == aggData.getValues().length-1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i] + Keywords.aggregatedDataDelimiter;
			}
			w.writeln(temp);
		}
		w.close();
	}
	
	public static void write(AggregatedNodeValueList inputData, String dir, String filename) throws IOException {
		write(inputData.getValues(), dir, filename);
	}
	
	public static void write(AggregatedDistribution inputData, String dir, String filename) throws IOException {
		write(inputData.getValues(), dir, filename);
=======
	public AggregatedData(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	public AggregatedData(String name, double[] values) {
		this.name = name;
		this.values = values;
	}
	
	public String getName(){
		return this.name;
	}
	
	public double getValue() {
		return this.value;
	}
	
	public double getValue(int index) {
		return this.values[index];
	}
	
	public double[] getValues() {
		return this.values;
>>>>>>> Codeupdate 13-06-10.
	}
}
