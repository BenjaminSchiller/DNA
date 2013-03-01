package dna.series.data;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.lists.DiffDataList;
import dna.util.Log;

public class RunData {

	public RunData(int run) {
		this.run = run;
		this.diffs = new DiffDataList();
	}

	public RunData(int run, int size) {
		this.run = run;
		this.diffs = new DiffDataList(size);
	}

	public RunData(int run, DiffData[] diffs) {
		this.run = run;
		this.diffs = new DiffDataList(diffs.length);
		for (DiffData diff : diffs) {
			this.diffs.add(diff);
		}
	}

	private int run;

	public int getRun() {
		return this.run;
	}

	private DiffDataList diffs;

	public DiffDataList getDiffs() {
		return this.diffs;
	}

	public void write(String dir) throws IOException {
		Log.debug("writing RunData " + this.run + " in " + dir);
		for (DiffData d : this.diffs.getList()) {
			d.write(Dir.getDiffDataDir(dir, d.getTimestamp()));
		}
	}

	public static RunData read(String dir, int run,
			boolean readDistributionValues) throws NumberFormatException,
			IOException {
		String[] diffs = Dir.getDiffs(dir);
		RunData runData = new RunData(run, diffs.length);
		for (String diff : diffs) {
			runData.getDiffs().add(
					DiffData.read(dir, Dir.getTimestamp(diff),
							readDistributionValues));
		}
		return runData;
	}

}
