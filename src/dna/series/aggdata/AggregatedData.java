package dna.series.aggdata;

import dna.series.lists.ListItem;

/**
 * AggregatedData is the super-class for all provided aggregated data-structures.
 * 
 * @author Rwilmes
 * @date 10.06.2013
 */
public class AggregatedData implements ListItem {
	
	// class variables
	private String name;
	private double value;
	private double[] values;
	
	// class methods
	public AggregatedData() { }
	
	public AggregatedData(String name) {
		this.name = name;
	}
	
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
	}
}
