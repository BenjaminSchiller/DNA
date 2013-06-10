package dna.series.aggdata;

import dna.util.Log;

/**
 * AggregatedNodeValueList is a class containing the values of an aggregated NodeValueList.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedNodeValueList extends AggregatedData {

	// class variables
	private String name;
	private String type = "AggregatedNodeValueList";
	private double[] values; // array containing the aggregated values for this object. Structure as follows: values = { 0, 0, avg, min, max, median, variance, variance-low, variance-up, confidence-low, confidence-up }
	
	// constructors
	public AggregatedNodeValueList(String name) {
		this.name = name;
	}
	
	public AggregatedNodeValueList(String name, double[] values) {
		this.name = name;
		this.values = values;
	}
	
	// class methods
	public void setValue(int index, double value) {
		try{
			this.values[index] = value;
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.error("AggregatedNodeValueList IndexOutOfBoundsException");
		}
	}
	
	public void setValues(double[] values) {
		this.values = values;
	}
	
	public double getValue(int index) {
		try{
			return this.values[index];
		} catch (ArrayIndexOutOfBoundsException e) {
			Log.error("AggregatedNodeValueList IndexOutOfBoundsException");
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
	
}
