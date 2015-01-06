package dna.series.data;

import java.io.IOException;

import dna.io.ZipReader;
import dna.io.ZipWriter;
import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
import dna.series.lists.BatchDataList;
import dna.util.Config;
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

	/** Reads the whole run from a single zip file. **/
	public static RunData readFromSingleFile(String fsDir, String dir, int run,
			BatchReadMode batchReadMode) throws IOException {
		ZipReader.readFileSystem = ZipWriter.createRunFileSystem(fsDir, run);
		RunData tempRunData = read(dir, run, batchReadMode);
		ZipReader.readFileSystem.close();
		ZipReader.readFileSystem = null;
		return tempRunData;
	}

	public static RunData read(String dir, int run, BatchReadMode batchReadMode)
			throws NumberFormatException, IOException {
		String[] batches = Dir.getBatches(dir);
		RunData runData = new RunData(run, batches.length);
		boolean singleFile = Config.getBoolean("GENERATION_BATCHES_AS_ZIP");

		for (String batch : batches) {
			if (singleFile)
				runData.getBatches().add(
						BatchData.readFromSingleFile(dir,
								Dir.getTimestamp(batch), Dir.delimiter,
								batchReadMode));
			else
				runData.getBatches().add(
						BatchData.read(
								Dir.getBatchDataDir(dir,
										Dir.getTimestamp(batch)),
								Dir.getTimestamp(batch), batchReadMode));
		}
		return runData;
	}

	/**
	 * This method tests if two different RunData objects can be aggregated.
	 * Checks: - same amount of batches - same batches (uses
	 * BatchData.sameType())
	 * 
	 * @author Rwilmes
	 * @date 25.06.2013
	 */
	public static boolean isSameType(RunData r1, RunData r2) {
		BatchDataList list1 = r1.getBatches();
		BatchDataList list2 = r2.getBatches();

		if (list1.size() != list2.size()) {
			Log.warn("different amount of batches on run " + r1.getRun()
					+ " and run " + r2.getRun());
			return false;
		}

		for (int i = 0; i < list1.size(); i++) {
			if (!BatchData.isSameType(list1.get(i), list2.get(i))) {
				Log.warn("different batches on run " + r1.getRun()
						+ " and run " + r2.getRun());
				return false;
			}
		}

		return true;
	}

}