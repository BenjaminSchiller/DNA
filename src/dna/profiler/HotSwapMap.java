package dna.profiler;

import java.util.HashMap;
import java.util.Map.Entry;

import dna.util.Config;

public class HotSwapMap {
	/**
	 * Three variables for the sliding window of the hot swap map
	 */
	private final int windowSize = Config.getInt("HOTSWAP_WINDOWSIZE");
	private RecommenderEntry[] innerMap;
	private int currInnerMapIndex = 0;
	
	public HotSwapMap() {
		innerMap = new RecommenderEntry[windowSize];
	}

	public void put(RecommenderEntry entry) {
		innerMap[currInnerMapIndex] = entry;
		currInnerMapIndex = (currInnerMapIndex + 1) % windowSize;
	}

	public RecommenderEntry getRecommendation() {
		double lowerSwappingBound = Config.getDouble("HOTSWAP_LOWER_BOUND");

		HashMap<RecommenderEntry, Integer> entrySet = new HashMap<>();
		for (int i = windowSize - 1; i >= 0; i--) {
			int index = (currInnerMapIndex + i) % windowSize;
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
		for (Entry<RecommenderEntry, Integer> e : entrySet.entrySet()) {
			if (e.getValue() > maxCounter) {
				maxCounter = e.getValue();
				maxEntry = e.getKey();
			}
		}

		if (maxCounter > lowerSwappingBound * windowSize) {
			return maxEntry;
		} else {
			return null;
		}
	}
}
