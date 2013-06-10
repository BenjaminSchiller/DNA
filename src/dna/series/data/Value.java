package dna.series.data;

import dna.util.Log;

public class Value extends Data {

	// class variables
	private double value;
	
	// constructors
	public Value(String name, double value) {
		super(name);
		this.value = value;
	}
	
	public Value(String name, double[] values) {
		Log.warn("Value object initialized with to much arguments");
	}

	// class methods
	public String toString() {
		return "value(" + super.getName() + ") = " + this.value;
	}

	public double getValue() {
		return this.value;
	}
	
	public String getType() {
		return "Value";
	}

}
