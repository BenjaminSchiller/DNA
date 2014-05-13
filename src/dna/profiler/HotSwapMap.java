package dna.profiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

import dna.profiler.datatypes.ComparableEntryMap;
import dna.util.Config;

public class HotSwapMap {
	private final int windowSize = Config.getInt("HOTSWAP_WINDOWSIZE");
	private final int recommendationsPerBatchToCheck = 5;
	private RecommenderEntry[] innerMap;
	private int currInnerMapIndex = 0;

	public HotSwapMap() {
		innerMap = new RecommenderEntry[windowSize
				* recommendationsPerBatchToCheck];
	}

	public void put(TreeSet<RecommenderEntry> latestRecommendations) {
		Iterator<RecommenderEntry> it = latestRecommendations.iterator();
		for (int i = 0; (i < recommendationsPerBatchToCheck && it.hasNext()); i++) {
			innerMap[currInnerMapIndex] = it.next();
			currInnerMapIndex = (currInnerMapIndex + 1) % innerMap.length;
		}
	}

	public RecommenderEntry getRecommendation() {
		double lowerSwappingBound = Config.getDouble("HOTSWAP_LOWER_BOUND");

		HashMap<RecommenderEntry, Integer> entrySet = new HashMap<>();
		for (int i = innerMap.length - 1; i >= 0; i--) {
			int index = (currInnerMapIndex + i) % innerMap.length;
			RecommenderEntry singleEntry = innerMap[index];

			if (singleEntry == null)
				continue;
			Integer formerCounter = entrySet.get(singleEntry);
			if (formerCounter == null) {
				formerCounter = 0;
			}
			formerCounter++;
			entrySet.put(singleEntry, formerCounter);
		}

		int maxCounter = -1;
		RecommenderEntry maxEntry = null;
		RecommenderEntry tempEntry;
		ComparableEntryMap maxEntryCosts, tempEntryCosts;

		for (Entry<RecommenderEntry, Integer> e : entrySet.entrySet()) {
			tempEntry = e.getKey();
			if (maxEntry == null) {
				maxCounter = e.getValue();
				maxEntry = tempEntry;
			} else if (e.getValue() >= maxCounter) {
				/**
				 * Okay, there might be a new number one. Look if it is also
				 * more efficient!
				 */
				maxEntryCosts = maxEntry
						.getCosts(Profiler.profilerDataTypeForHotSwap);
				tempEntryCosts = tempEntry
						.getCosts(Profiler.profilerDataTypeForHotSwap);
				if (maxEntryCosts.compareTo(tempEntryCosts) > 0) {
					maxCounter = e.getValue();
					maxEntry = e.getKey();
				}
			}
		}

		if (maxCounter > lowerSwappingBound * windowSize) {
			return maxEntry;
		} else {
			return null;
		}
	}
}
