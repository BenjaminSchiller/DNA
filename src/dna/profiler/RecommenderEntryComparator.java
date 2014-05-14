package dna.profiler;

import java.util.Comparator;

import dna.profiler.ProfilerMeasurementData.ProfilerDataType;

public class RecommenderEntryComparator implements Comparator<RecommenderEntry> {
	private ProfilerDataType e;

	public RecommenderEntryComparator(ProfilerDataType entryType) {
		this.e = entryType;
	}

	@Override
	public int compare(RecommenderEntry o1, RecommenderEntry o2) {
		return o1.compareToOther(o2, e);
	}

}
