package dynamicGraphs.series;

import java.util.Collection;
import java.util.HashMap;

public class RunData {

	public RunData(int run) {
		this.run = run;
		this.diffs = new HashMap<Long, DiffData>();
	}

	public RunData(int run, int size) {
		this.run = run;
		this.diffs = new HashMap<Long, DiffData>(size);
	}

	public RunData(int run, DiffData[] diffs) {
		this.run = run;
		this.diffs = new HashMap<Long, DiffData>(diffs.length);
		for (DiffData diff : diffs) {
			this.diffs.put(diff.getTimestamp(), diff);
		}
	}

	private int run;

	public int getRun() {
		return this.run;
	}

	private HashMap<Long, DiffData> diffs;

	public Collection<DiffData> getDiffs() {
		return this.diffs.values();
	}

	public DiffData getDiff(long timestamp) {
		return this.diffs.get(timestamp);
	}

	public void addDiff(DiffData diff) {
		this.diffs.put(diff.getTimestamp(), diff);
	}
}
