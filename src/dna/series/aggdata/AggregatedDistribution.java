package dna.series.aggdata;

import dna.util.Log;

/**
 * AggregatedDistribution is a class containing the values of an aggregated distribution.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedDistribution extends AggregatedData {

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
	
}
