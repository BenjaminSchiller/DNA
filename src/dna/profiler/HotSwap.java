package dna.profiler;

import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;
import dna.profiler.datatypes.ComparableEntry;
import dna.profiler.datatypes.ComparableEntryMap;
import dna.util.Config;

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
		DataStructure.disableContainsOnAddition();
		gds.switchDatastructures(newGDS, g);
		DataStructure.enableContainsOnAddition();
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
					System.out
							.println("  Last own runtime costs: "
									+ lastCosts
											.getCosts(ProfilerDataType.RuntimeBenchmark)
									+ ", recommended entry runtime costs: "
									+ entry.getCosts(ProfilerDataType.RuntimeBenchmark));

					GraphDataStructure newGDS = entry.getGraphDataStructure();

					if (isSwapEfficient(lastCosts, entry)) {
						System.out
								.println("  Swapping looks efficient, so do it now");
						doSwap(g, newGDS);
					} else {
						System.out
								.println("  Skip the swap, it is inefficient");
					}
				}
			}
		}
	}

	private static boolean isSwapEfficient(RecommenderEntry currentState,
			RecommenderEntry recommendedState) {
		/**
		 * How many batches should we look into the future to see whether the
		 * currently used GDS performs worse than a new one, taking the costs of
		 * switching into account?
		 */
		long amortizationCounterInBatches = Config
				.getInt("HOTSWAP_AMORTIZATION_COUNTER");
		long maxNumberOfBatchesLeft = totalNumberOfBatches - lastFinishedBatch;
		int amortizationCounterToUse = (int) Math.min(
				amortizationCounterInBatches, maxNumberOfBatchesLeft);
		System.out.println("  Check whether the swap will be amortized within "
				+ amortizationCounterToUse + " batches");

		/**
		 * Generate the costs for the current state
		 */
		ComparableEntryMap currentStateCosts = currentState.getCosts(
				ProfilerDataType.RuntimeBenchmark).clone();
		currentStateCosts.multiplyBy(amortizationCounterToUse);

		/**
		 * Generate the costs for the recommended state
		 */
		ComparableEntryMap recStateCosts = recommendedState.getCosts(
				ProfilerDataType.RuntimeBenchmark).clone();
		recStateCosts.multiplyBy(amortizationCounterToUse);

		/**
		 * Generate the costs for swapping, which is: for each list type the
		 * number of lists * (init + meanlistSize * add)
		 */

		GraphDataStructure recGDS = recommendedState.getGraphDataStructure();
		ComparableEntryMap swappingCosts = ProfilerMeasurementData
				.getMap(ProfilerDataType.RuntimeBenchmark);
		for (ListType lt : ListType.values()) {
			int numberOfLists = Profiler.getNumberOfGeneratedLists(lt);
			double meanListSize = Profiler.getMeanSize(lt);
			int totalNumberOfElements = (int) (numberOfLists * meanListSize);

			ComparableEntry initCosts = recGDS.getComplexityClass(lt,
					AccessType.Init, ProfilerDataType.RuntimeBenchmark);
			initCosts.setValues(numberOfLists, meanListSize, null);
			swappingCosts.add(initCosts.getMap());

			ComparableEntry addCosts = recGDS.getComplexityClass(lt,
					AccessType.Add, ProfilerDataType.RuntimeBenchmark);
			addCosts.setValues(totalNumberOfElements, meanListSize, null);
			swappingCosts.add(addCosts.getMap());
		}

		System.out.println("  Total costs with current GDS: "
				+ currentStateCosts + ", total swapping costs: "
				+ swappingCosts + ", total costs with recommended GDS: "
				+ recStateCosts);
		recStateCosts.add(swappingCosts);
		System.out.println("  Total costs with NEW GDS, incl swap: "
				+ recStateCosts);

		boolean isEfficient = recStateCosts.compareTo(currentStateCosts) < 0;
		return isEfficient;
	}

	public static void setLastFinishedBatch(long batchTimestamp) {
		lastFinishedBatch = batchTimestamp;
	}

	public static void setTotalNumberOfBatches(int n) {
		totalNumberOfBatches = n;
	}

}
