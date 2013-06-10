package dna.series.data;

import dna.util.Log;

public class Value extends Data {

	public Value(String name, double value) {
		this.name = name;
		this.value = value;
	}
	
	public Value(String name, double[] values) {
		Log.warn("Value object initialized with to much arguments");
	}

	public String toString() {
		return "value(" + this.name + ") = " + this.value;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	private double value;

	public double getValue() {
		return this.value;
	}
	
	public String getType() {
		return "Value";
	}

}
