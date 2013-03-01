package dna.series.data;

import dna.series.lists.ListItem;

public class RunTime implements ListItem {

	public RunTime(String name, double time) {
		this.name = name;
		this.runtime = time;
	}

	public String toString() {
		return "runtime(" + this.name + "): " + this.runtime + " msec";
	}

	private String name;

	public String getName() {
		return this.name;
	}

	private double runtime;

	public double getRuntime() {
		return this.runtime;
	}

	public double getSec() {
		return this.runtime / 1000;
	}

	public double getMSec() {
		return this.runtime;
	}

}
