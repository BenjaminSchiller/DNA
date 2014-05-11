package dna.profiler;

import java.util.EnumMap;
import java.util.TreeSet;

import dna.profiler.ProfilerMeasurementData.ProfilerDataType;

public class CompleteRecommendationsHolder {
	private EnumMap<ProfilerDataType, TreeSet<RecommenderEntry>> innerData = new EnumMap<>(
			ProfilerDataType.class);
	private EnumMap<ProfilerDataType, RecommenderEntry> ownCosts = new EnumMap<>(
			ProfilerDataType.class);

	public boolean containsKey(ProfilerDataType pdt) {
		return innerData.containsKey(pdt);
	}

	public void put(ProfilerDataType pdt,
			TreeSet<RecommenderEntry> sortedCostsMap) {
		innerData.put(pdt, sortedCostsMap);
	}

	public TreeSet<RecommenderEntry> get(ProfilerDataType pdt) {
		return innerData.get(pdt);
	}

	public void setOwnCosts(ProfilerDataType pdt,
			RecommenderEntry aggregatedEntry) {
		ownCosts.put(pdt, aggregatedEntry);
	}

	public RecommenderEntry getOwnCosts(ProfilerDataType pdt) {
		return ownCosts.get(pdt);
	}
}
