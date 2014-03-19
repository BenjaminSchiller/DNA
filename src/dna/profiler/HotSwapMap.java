package dna.profiler;

import java.util.HashMap;
import java.util.Map.Entry;

import dna.util.Config;

public class HotSwapMap {
	private RecommenderEntry[] innerMap;
	private int windowSize;
	private int currIndex = 0;

	public HotSwapMap() {
		windowSize = Config.getInt("HOTSWAP_WINDOWSIZE");
		innerMap = new RecommenderEntry[windowSize];
	}

	public void put(RecommenderEntry entry) {
		innerMap[currIndex] = entry;
		currIndex = (currIndex + 1) % windowSize;
	}

	public RecommenderEntry getRecommendation() {
		double lowerSwappingBound = Config.getDouble("HOTSWAP_LOWER_BOUND");

		HashMap<RecommenderEntry, Integer> entrySet = new HashMap<>();
		for (int i = windowSize - 1; i >= 0; i--) {
			int index = (currIndex + i) % windowSize;
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
