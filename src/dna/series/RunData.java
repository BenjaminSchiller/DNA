package dna.series;

import java.io.IOException;
import java.util.ArrayList;

import dna.io.Dir;
import dna.util.Log;

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

	public DiffData getDiff(int index) {
		return this.diffs.get(index);
	}

	public void addDiff(DiffData diff) {
		this.diffs.add(diff);
	}

	public void write(String seriesDir) throws IOException {
		Log.debug("writing RunData " + this.run + " in " + seriesDir);
		for (DiffData d : this.diffs) {
			d.write(Dir.getDiffDataDir(seriesDir, this, d));
		}
	}
}
