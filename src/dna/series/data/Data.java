package dna.series.data;

import dna.series.lists.ListItem;


/**
 * Data is the super-class for all provided data-structures.
 * 
 * @author Rwilmes
 * @date 06.06.2013
 */
public class Data implements ListItem {

	// class variables
	private String name;
	private double value;
	private double[] values;
	
	// constructors
	public Data() {}
	
	public Data(String name) {
		this.name = name;
	}
	
	public Data(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	public Data(String name, double[] values) {
		this.name = name;
		this.values = values;
	}
	
	// get methods
	public String getName() {
		return this.name;
	}
	
	public String getType() {
		return "Data";
	}
	
	public double getValue() {
		return this.value;
	}
	
	public double[] getValues() {
		return this.values;
	}
}
