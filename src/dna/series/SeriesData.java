package dna.series;

import java.util.ArrayList;

public class SeriesData {

	public SeriesData(String dir) {
		this.dir = dir;
		this.runs = new ArrayList<RunData>();
	}

	public SeriesData(String dir, int size) {
		this.dir = dir;
		this.runs = new ArrayList<RunData>(size);
	}

	public SeriesData(String dir, RunData[] runs) {
		this.dir = dir;
		this.runs = new ArrayList<RunData>(runs.length);
		for (RunData run : runs) {
			this.runs.add(run);
		}
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
}
