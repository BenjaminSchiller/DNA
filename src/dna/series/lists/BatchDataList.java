package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.data.BatchData;

public class BatchDataList extends SortedList<BatchData> {

	public BatchDataList() {
		super();
	}

	public BatchDataList(int size) {
		super(size);
	}

	@Override
	public void write(String dir) throws IOException {
		for (BatchData batchData : this.list) {
			batchData.write(dir);
		}
	}

	public static BatchDataList read(String dir, boolean readValues)
			throws IOException {
		String[] batches = Dir.getBatches(dir);
		BatchDataList list = new BatchDataList(batches.length);
		for (String batch : batches) {
			list.add(BatchData.read(dir + batch + "/", Dir.getTimestamp(batch),
					readValues));
		}
		return list;
	}

	/**
	 * Reads only the timestamps of available batches and returns a list full of
	 * empty batches with only the corresponding timestamp.
	 * 
	 * @param dir
	 *            Directory to be read in
	 * @return BatchDataList filled with empty batches
	 */
	public static BatchDataList readTimestamps(String dir) {
		String[] batches = Dir.getBatches(dir);
		BatchDataList list = new BatchDataList(batches.length);
		for (String batch : batches) {
			list.add(new BatchData(Dir.getTimestamp(batch)));
		}
		return list;
	}
}
