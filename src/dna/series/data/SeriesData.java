package dna.series.data;

import java.util.ArrayList;

import dna.series.aggdata.AggregatedSeries;

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

	public SeriesData(String dir, RunData[] runs, AggregatedSeries aggregation) {
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

	private AggregatedSeries aggregation;

	public AggregatedSeries getAggregation() {
		return this.aggregation;
	}

	public void setAggregation(AggregatedSeries aggregation) {
		this.aggregation = aggregation;
	}
}
