package dna.series;

import java.util.ArrayList;

public class SeriesData {

	public SeriesData() {
		this.runs = new ArrayList<RunData>();
	}

	public SeriesData(int size) {
		this.runs = new ArrayList<RunData>(size);
	}

	public SeriesData(RunData[] runs) {
		this.runs = new ArrayList<RunData>(runs.length);
		for (RunData run : runs) {
			this.runs.add(run);
		}
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
}
