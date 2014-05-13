package dna.profiler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeSet;

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

	public TreeSet<RecommenderEntry> getRecommendations() {
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

		int currCounter;
		RecommenderEntry tempEntry;

		TreeSet<RecommenderEntry> res = new TreeSet<RecommenderEntry>(
				RecommenderEntry
						.getComparator(Profiler.profilerDataTypeForHotSwap));

		for (Entry<RecommenderEntry, Integer> e : entrySet.entrySet()) {
			tempEntry = e.getKey();
			currCounter = e.getValue();
			if (currCounter > lowerSwappingBound * windowSize) {
				res.add(tempEntry);
			}
		}

		return res;
	}
}
