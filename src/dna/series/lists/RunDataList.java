package dna.series.lists;

import java.io.IOException;

import dna.io.filesystem.Dir;
import dna.series.data.RunData;

public class RunDataList extends SortedList<RunData> {

	public RunDataList() {
		super();
	}

	public RunDataList(int size) {
		super(size);
	}

	@Override
	public void write(String dir) throws IOException {
		for (RunData runData : this.list) {
			runData.write(Dir.getRunDataDir(dir, runData.getRun()));
		}
	}

	public static RunDataList read(String dir, boolean readValues)
			throws IOException {
		String[] runs = Dir.getRuns(dir);
		RunDataList list = new RunDataList(runs.length);
		for (String run : runs) {
			list.add(RunData.read(run, Dir.getRun(run), readValues));
		}
		return list;
	}

}
