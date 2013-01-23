package dynamicGraphs.util;

import dynamicGraphs.diff.Diff;

public class DiffStats extends Stats {
	public DiffStats(Diff d) {
		super(d.toString());
		this.d = d;
	}

	private Diff d;

	public Diff getDiff() {
		return this.d;
	}
}
