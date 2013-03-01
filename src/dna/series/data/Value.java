package dna.series.data;

import dna.series.lists.ListItem;

public class Value implements ListItem {

	public Value(String name, double value) {
		this.name = name;
		this.value = value;
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

}
