package dna.series.data;

import dna.series.lists.ListItem;

public class RunTime implements ListItem {

	public RunTime(String name, double time) {
		this.name = name;
		this.runtime = time;
	}

	public String toString() {
		return "runtime(" + this.name + "): " + this.getMilliSec() + " msec";
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
		return this.getMilliSec() / 1000.0;
	}

	public double getMilliSec() {
		return this.getNanoSec() / 1000.0 / 1000.0;
	}

	public double getNanoSec() {
		return this.runtime;
	}

}
