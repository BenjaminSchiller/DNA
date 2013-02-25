package dna.series;

import java.util.ArrayList;

public class RunData {

	public RunData(int run) {
		this.run = run;
		this.diffs = new ArrayList<DiffData>();
	}

	public RunData(int run, int size) {
		this.run = run;
		this.diffs = new ArrayList<DiffData>(size);
	}

	public RunData(int run, DiffData[] diffs) {
		this.run = run;
		this.diffs = new ArrayList<DiffData>(diffs.length);
		for (DiffData diff : diffs) {
			this.diffs.add(diff);
		}
	}

	private int run;

	public int getRun() {
		return this.run;
	}

	private ArrayList<DiffData> diffs;

	public ArrayList<DiffData> getDiffs() {
		return this.diffs;
	}

	public void addDiff(DiffData diff) {
		this.diffs.add(diff);
	}
}
