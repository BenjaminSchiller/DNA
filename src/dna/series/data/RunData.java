package dna.series.data;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.lists.BatchDataList;
import dna.util.Log;

public class RunData {

	public RunData(int run) {
		this.run = run;
		this.batches = new BatchDataList();
	}

	public RunData(int run, int size) {
		this.run = run;
		this.batches = new BatchDataList(size);
	}

	public RunData(int run, BatchData[] batches) {
		this.run = run;
		this.batches = new BatchDataList(batches.length);
		for (BatchData batch : batches) {
			this.batches.add(batch);
		}
	}

	private int run;

	public int getRun() {
		return this.run;
	}

	private BatchDataList batches;

	public BatchDataList getBatches() {
		return this.batches;
	}

	public void write(String dir) throws IOException {
		Log.debug("writing RunData " + this.run + " in " + dir);
		for (BatchData d : this.batches.getList()) {
			d.write(Dir.getBatchDataDir(dir, d.getTimestamp()));
		}
	}

	public static RunData read(String dir, int run,
			boolean readDistributionValues) throws NumberFormatException,
			IOException {
		String[] batches = Dir.getBatches(dir);
		RunData runData = new RunData(run, batches.length);
		for (String batch : batches) {
			runData.getBatches().add(
					BatchData.read(dir, Dir.getTimestamp(batch),
							readDistributionValues));
		}
		return runData;
	}

}
