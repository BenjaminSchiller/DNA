package dna.series;

import java.util.ArrayList;

public class SeriesData {

	public SeriesData(String dir) {
		this.dir = dir;
		this.runs = new ArrayList<RunData>();
		this.aggregation = null;
	}

	public SeriesData(String dir, int size) {
		this.dir = dir;
		this.runs = new ArrayList<RunData>(size);
		this.aggregation = null;
	}

	public SeriesData(String dir, RunData[] runs) {
		this(dir, runs.length);
		for (RunData run : runs) {
			this.runs.add(run);
		}
		this.aggregation = null;
	}

	public SeriesData(String dir, RunData[] runs, RunData aggregation) {
		this(dir, runs);
		this.aggregation = aggregation;
	}

	private String dir;

	public String getDir() {
		return this.dir;
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
