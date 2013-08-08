package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.data.BatchData;

public class DiffDataList extends SortedList<BatchData> {

	public DiffDataList() {
		super();
	}

	public DiffDataList(int size) {
		super(size);
	}

	@Override
	public void write(String dir) throws IOException {
		for (BatchData diffData : this.list) {
			diffData.write(dir);
		}
	}

	public static DiffDataList read(String dir, boolean readDistributionValues)
			throws IOException {
		String[] diffs = Dir.getDiffs(dir);
		DiffDataList list = new DiffDataList(diffs.length);
		for (String diff : diffs) {
			list.add(BatchData.read(dir, Dir.getTimestamp(diff),
					readDistributionValues));
		}
		return list;
	}

}
