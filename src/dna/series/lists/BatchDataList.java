package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.aggdata.AggregatedBatch.BatchReadMode;
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
			batchData.writeIntelligent(Dir.getBatchDataDir(dir,
					batchData.getTimestamp()));
		}
	}

	public static BatchDataList read(String dir, BatchReadMode batchReadMode)
			throws IOException {
		String[] batches = Dir.getBatches(dir);
		BatchDataList list = new BatchDataList(batches.length);
		for (String batch : batches) {
			list.add(BatchData.read(
					Dir.getBatchDataDir(dir, Dir.getTimestamp(batch)),
					Dir.getTimestamp(batch), batchReadMode));
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
	 * @throws IOException
	 */
	public static BatchDataList readTimestamps(String dir) throws IOException {
		String[] batches = Dir.getBatches(dir);
		BatchDataList list = new BatchDataList(batches.length);
		for (String batch : batches) {
			list.add(new BatchData(Dir.getTimestamp(batch)));
		}
		return list;
	}
}
