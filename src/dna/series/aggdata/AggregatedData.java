package dna.series.aggdata;

import java.io.IOException;
import java.util.ArrayList;

import dna.io.Writer;
import dna.io.etc.Keywords;
import dna.series.lists.ListItem;

/**
<<<<<<< HEAD
 * AggregatedData is the super-class for all provided aggregation data-structures.
=======
 * AggregatedData is the super-class for all provided aggregation
 * data-structures.
>>>>>>> remotes/beniMaster/master
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedData implements ListItem {
<<<<<<< HEAD
	
	// member variables
	private String name;
	
	// constructors
	public AggregatedData() { }
	
	public AggregatedData(String name) {
		this.name = name;
	}
	
=======

	// member variables
	private String name;

	// constructors
	public AggregatedData() {
	}

	public AggregatedData(String name) {
		this.name = name;
	}

>>>>>>> remotes/beniMaster/master
	// get methods
	public String getName() {
		return this.name;
	}
<<<<<<< HEAD
	
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
			String temp = "" + aggData.getName() + Keywords.aggregatedDataDelimiter;
			for (int i = 0; i < aggData.getValues().length; i++) {
				if(i == aggData.getValues().length-1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i] + Keywords.aggregatedDataDelimiter;
=======

	// IO methods
	/**
	 * Method to write the context of an Array of AggregatedValue objects to a
	 * specified location.
	 * 
	 * @param inputData
	 *            The Aggregated Data that is to be written on the filesystem.
	 * @param dir
	 *            String which contains the path / directory the Data will be
	 *            written to.
	 * @param filename
	 *            String representing the desired filename for the Data.
	 */
	public static void write(AggregatedValue[] inputData, String dir,
			String filename) throws IOException {
		Writer w = new Writer(dir, filename);

		for (AggregatedValue aggData : inputData) {
			String temp = "" + aggData.getName()
					+ Keywords.aggregatedDataDelimiter;
			for (int i = 0; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Keywords.aggregatedDataDelimiter;
>>>>>>> remotes/beniMaster/master
			}
			w.writeln(temp);
		}
		w.close();
	}
<<<<<<< HEAD
	
	public static void write(ArrayList<AggregatedValue> inputData, String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		
		for(AggregatedValue aggData : inputData) {			
			String temp = "" + aggData.getName() + Keywords.aggregatedDataDelimiter;
			for (int i = 0; i < aggData.getValues().length; i++) {
				if(i == aggData.getValues().length-1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i] + Keywords.aggregatedDataDelimiter;
=======

	public static void write(ArrayList<AggregatedValue> inputData, String dir,
			String filename) throws IOException {
		Writer w = new Writer(dir, filename);

		for (AggregatedValue aggData : inputData) {
			String temp = "" + aggData.getName()
					+ Keywords.aggregatedDataDelimiter;
			for (int i = 0; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Keywords.aggregatedDataDelimiter;
>>>>>>> remotes/beniMaster/master
			}
			w.writeln(temp);
		}
		w.close();
	}
<<<<<<< HEAD
	
	public static void write(AggregatedValue inputData, String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		
		String temp = "" + inputData.getName() + Keywords.aggregatedDataDelimiter;
		//String temp = "" + Keywords.aggregatedDataDelimiter;
		for (int i = 0; i < inputData.getValues().length; i++) {
			if(i == inputData.getValues().length-1)
				temp += inputData.getValues()[i];
			else
				temp += inputData.getValues()[i] + Keywords.aggregatedDataDelimiter;
=======

	public static void write(AggregatedValue inputData, String dir,
			String filename) throws IOException {
		Writer w = new Writer(dir, filename);

		String temp = "" + inputData.getName()
				+ Keywords.aggregatedDataDelimiter;
		// String temp = "" + Keywords.aggregatedDataDelimiter;
		for (int i = 0; i < inputData.getValues().length; i++) {
			if (i == inputData.getValues().length - 1)
				temp += inputData.getValues()[i];
			else
				temp += inputData.getValues()[i]
						+ Keywords.aggregatedDataDelimiter;
>>>>>>> remotes/beniMaster/master
		}
		w.writeln(temp);

		w.close();
	}
<<<<<<< HEAD
	
	public static void write(AggregatedNodeValueList inputData, String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		AggregatedValue[] tempData = inputData.getValues();
		
		for(AggregatedValue aggData : tempData) {			
			String temp = "" + (int) aggData.getValues()[0] + Keywords.aggregatedDataDelimiter;
			for (int i = 1; i < aggData.getValues().length; i++) {
				if(i == aggData.getValues().length-1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i] + Keywords.aggregatedDataDelimiter;
=======

	public static void write(AggregatedNodeValueList inputData, String dir,
			String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		AggregatedValue[] tempData = inputData.getValues();

		for (AggregatedValue aggData : tempData) {
			String temp = "" + (int) aggData.getValues()[0]
					+ Keywords.aggregatedDataDelimiter;
			for (int i = 1; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Keywords.aggregatedDataDelimiter;
>>>>>>> remotes/beniMaster/master
			}
			w.writeln(temp);
		}
		w.close();
	}
<<<<<<< HEAD
	
	public static void write(AggregatedDistribution inputData, String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		AggregatedValue[] tempData = inputData.getValues();
		
		for(AggregatedValue aggData : tempData) {			
			String temp = "" + (int) aggData.getValues()[0] + Keywords.aggregatedDataDelimiter;
			for (int i = 1; i < aggData.getValues().length; i++) {
				if(i == aggData.getValues().length-1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i] + Keywords.aggregatedDataDelimiter;
=======

	public static void write(AggregatedDistribution inputData, String dir,
			String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		AggregatedValue[] tempData = inputData.getValues();

		for (AggregatedValue aggData : tempData) {
			String temp = "" + (int) aggData.getValues()[0]
					+ Keywords.aggregatedDataDelimiter;
			for (int i = 1; i < aggData.getValues().length; i++) {
				if (i == aggData.getValues().length - 1)
					temp += aggData.getValues()[i];
				else
					temp += aggData.getValues()[i]
							+ Keywords.aggregatedDataDelimiter;
>>>>>>> remotes/beniMaster/master
			}
			w.writeln(temp);
		}
		w.close();
	}
}
