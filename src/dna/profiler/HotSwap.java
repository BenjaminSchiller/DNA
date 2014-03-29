package dna.profiler;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;

public class HotSwap {
	private static Map<ProfilerMeasurementData.ProfilerDataType, HotSwapMap> slidingWindow = null;
	private static long lastFinishedBatch;
	private static int totalNumberOfBatches;
	private static Map<Long, EnumMap<ListType, Class<? extends IDataStructure>>> manualSwitching = null;

	public static void reset() {
		slidingWindow = new EnumMap<ProfilerMeasurementData.ProfilerDataType, HotSwapMap>(
				ProfilerDataType.class);
		for (ProfilerDataType dt : ProfilerDataType.values()) {
			slidingWindow.put(dt, new HotSwapMap());
		}
	}

	public static void addNewResults() {
		if (slidingWindow == null) {
			reset();
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

	public static void addForManualSwitching(long batchCount,
			EnumMap<ListType, Class<? extends IDataStructure>> newDatastructures) {
		if (manualSwitching == null) {
			manualSwitching = new TreeMap<>();
		}
		manualSwitching.put(batchCount, newDatastructures);
	}

	private static void doSwap(Graph g, GraphDataStructure newGDS) {
		GraphDataStructure gds = g.getGraphDatastructures();
		System.out.println("  Old DS: " + gds.getStorageDataStructures(true));
		System.out
				.println("  New DS: " + newGDS.getStorageDataStructures(true));
		gds.switchDatastructures(newGDS, g);
	}

	public static void trySwap(Graph g) {
		if (manualSwitching != null) {
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes = manualSwitching
					.get(lastFinishedBatch);
			if (listTypes != null) {
				System.out
						.println("Should swap here according to manualSwitchingMap");
				GraphDataStructure newGDS = new GraphDataStructure(listTypes,
						null, null);
				doSwap(g, newGDS);
			}
			return;
		}

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
					GraphDataStructure newGDS = entry.getGraphDataStructure();
					doSwap(g, newGDS);
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
