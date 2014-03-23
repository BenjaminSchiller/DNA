package dna.profiler;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;

public class HotSwap {
	private static Map<ProfilerMeasurementData.ProfilerDataType, HotSwapMap> slidingWindow = null;
	private static long lastFinishedBatch;
	private static int totalNumberOfBatches;

	private static void init() {
		slidingWindow = new EnumMap<ProfilerMeasurementData.ProfilerDataType, HotSwapMap>(
				ProfilerDataType.class);
		for (ProfilerDataType dt : ProfilerDataType.values()) {
			slidingWindow.put(dt, new HotSwapMap());
		}
	}

	public static void addNewResults() {
		if (slidingWindow == null) {
			init();
		}
		for (ProfilerDataType dt : ProfilerDataType.values()) {
			RecommenderEntry latestRecommendation = Profiler
					.getRecommendation(dt);
			if (latestRecommendation != null) {
				HotSwapMap innerMap = slidingWindow.get(dt);
				innerMap.put(latestRecommendation);
			}
		}
	}

	public static void trySwap(Graph g) {
		for (Entry<ProfilerDataType, HotSwapMap> e : slidingWindow.entrySet()) {
			RecommenderEntry entry = e.getValue().getRecommendation();

			if (entry != null
					&& e.getKey().equals(ProfilerDataType.RuntimeBenchmark)) {

				RecommenderEntry lastCosts = Profiler.getLastCosts(e.getKey());
				if (!entry.getDatastructures().equals(
						g.getGraphDatastructures().getStorageDataStructures())) {
					System.out.println("Recommendation based on "
							+ e.getKey()
							+ " will swap to "
							+ entry.getGraphDataStructure()
									.getStorageDataStructures(true));
					System.out.println("  Last own costs: "
							+ lastCosts.getCosts()
							+ ", recommended entry costs: " + entry.getCosts());
					GraphDataStructure gds = g.getGraphDatastructures();
					GraphDataStructure newGDS = entry.getGraphDataStructure();
					System.out.println("  Old DS: "
							+ gds.getStorageDataStructures(true));
					System.out.println("  New DS: "
							+ newGDS.getStorageDataStructures(true));
					gds.switchDatastructures(newGDS, g);
				}
			}
		}
	}

	public static void setLastFinishedBatch(long batchTimestamp) {
		lastFinishedBatch = batchTimestamp;
	}

	public static void setTotalNumberOfBatches(int n) {
		totalNumberOfBatches = n;
	}

}
