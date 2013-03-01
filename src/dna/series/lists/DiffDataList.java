package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.data.DiffData;

public class DiffDataList extends SortedList<DiffData> {

	public DiffDataList() {
		super();
	}

	public DiffDataList(int size) {
		super(size);
	}

	@Override
	public void write(String dir) throws IOException {
		for (DiffData diffData : this.list) {
			diffData.write(dir);
		}
	}

	public static DiffDataList read(String dir, boolean readDistributionValues)
			throws IOException {
		String[] diffs = Dir.getDiffs(dir);
		DiffDataList list = new DiffDataList(diffs.length);
		for (String diff : diffs) {
			list.add(DiffData.read(dir, Dir.getTimestamp(diff),
					readDistributionValues));
		}
		return list;
	}

}
