package dna.series.data;

import java.util.ArrayList;

import dna.series.aggdata.AggregatedSeries;

public class SeriesData {

	public SeriesData(String dir, String name) {
		this.dir = dir;
		this.name = name;
		this.runs = new ArrayList<RunData>();
		this.aggregation = null;
	}

	public SeriesData(String dir, String name, int size) {
		this.dir = dir;
		this.name = name;
		this.runs = new ArrayList<RunData>(size);
		this.aggregation = null;
	}

	public SeriesData(String dir, String name, RunData[] runs) {
		this(dir, name, runs.length);
		for (RunData run : runs) {
			this.runs.add(run);
		}
		this.aggregation = null;
	}
	
	public SeriesData(String dir, RunData[] runs, String name, AggregatedSeries aggregation) {
		this(dir, name, runs);
		//this.aggregation = aggregation;
	}

	private String dir;

	public String getDir() {
		return this.dir;
	}

	private String name;

	public String getName() {
		return this.name;
	}

	private ArrayList<RunData> runs;

	public ArrayList<RunData> getRuns() {
		return this.runs;
	}

	public RunData getRun(int index) {
		return this.runs.get(index);
	}

	public void addRun(RunData run) {
		this.runs.add(run);
	}

	private RunData aggregation;

	public RunData getAggregation() {
		return this.aggregation;
	}

	public void setAggregation(RunData aggregation) {
		this.aggregation = aggregation;
	}
}
