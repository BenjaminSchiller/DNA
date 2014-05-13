package dna.profiler;

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;
import dna.profiler.datatypes.ComparableEntry;
import dna.profiler.datatypes.ComparableEntryMap;
import dna.util.Config;

public class HotSwap {
	private static HotSwapMap slidingWindow = null;
	private static long lastFinishedBatch;
	private static int totalNumberOfBatches;
	private static boolean inFirstBatch = true;
	private static Map<Long, EnumMap<ListType, Class<? extends IDataStructure>>> manualSwitching = null;
	private static EnumMap<ListType, Class<? extends IDataStructure>> firstSwitch = null;

	private static double hotswapLowerBound = Config
			.getDouble("HOTSWAP_LOWER_BOUND");
	private static int hotswapWindowSize = Config.getInt("HOTSWAP_WINDOWSIZE");

	/**
	 * Three variables for storing the accesses onto underlying lists
	 */
	private static final int maxAccessListSize = Config
			.getInt("HOTSWAP_AMORTIZATION_COUNTER");
	private static ProfileEntry[] accessList;
	private static int currAccessListIndex = 0;

	public static void reset() {
		slidingWindow = new HotSwapMap();
		accessList = new ProfileEntry[maxAccessListSize];
		firstSwitch = null;
		inFirstBatch = true;
	}

	public static void addNewResults() {
		if (slidingWindow == null) {
			reset();
		}

		if (!inFirstBatch
				|| Config
						.getBoolean("HOTSWAP_INCLUDE_FIRSTBATCH_FOR_EFFICIENCY_CHECK")) {
			ProfileEntry accesses = Profiler.getLastAccesses();
			accessList[currAccessListIndex] = accesses;
		}
		currAccessListIndex = (currAccessListIndex + 1) % maxAccessListSize;

		TreeSet<RecommenderEntry> latestRecommendations = Profiler
				.getRecommendations(Profiler.profilerDataTypeForHotSwap);
		if (latestRecommendations != null) {
			slidingWindow.put(latestRecommendations);
		}
		inFirstBatch = false;
	}

	private static ProfileEntry getAccumulatedAccesses(int amortizationCounter) {
		ProfileEntry res = new ProfileEntry();

		for (int i = maxAccessListSize - 1; (i >= 0 && amortizationCounter > 0); i--) {
			int index = (currAccessListIndex + i) % maxAccessListSize;
			if (accessList[index] != null) {
				res = res.add(accessList[index]);
			}
			amortizationCounter--;
		}

		return res;
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

		if (firstSwitch == null) {
			firstSwitch = newGDS.getStorageDataStructures();
		}
	}

	public static int getAmortizationCounter() {
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
		return amortizationCounterToUse;
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

		if (lastFinishedBatch < Math.floor(hotswapLowerBound
				* hotswapWindowSize)) {
			return;
		}

		int amortizationCounter = getAmortizationCounter();
		ProfileEntry accumulatedAccesses = getAccumulatedAccesses(amortizationCounter);

		TreeSet<RecommenderEntry> entrySet = slidingWindow.getRecommendations();
		for (RecommenderEntry entry : entrySet) {
			ComparableEntryMap lastOwnCosts = Profiler
					.getLastCosts(Profiler.profilerDataTypeForHotSwap);
			ComparableEntryMap recCosts = entry
					.getCosts(Profiler.profilerDataTypeForHotSwap);

			if (!entry.getDatastructures().equals(
					g.getGraphDatastructures().getStorageDataStructures())) {
				System.out.println("Recommendation based on "
						+ Profiler.profilerDataTypeForHotSwap
						+ " could swap to "
						+ entry.getGraphDataStructure()
								.getStorageDataStructures(true));
				System.out.println("  " + Profiler.profilerDataTypeForHotSwap
						+ " costs in last batch with current combination: "
						+ lastOwnCosts + ", with recommended entry: "
						+ recCosts);

				GraphDataStructure currentGDS = g.getGraphDatastructures();
				GraphDataStructure newGDS = entry.getGraphDataStructure();

				if (isSwapEfficient(accumulatedAccesses, currentGDS, newGDS)) {
					System.out
							.println("  Swapping looks efficient, so do it now");
					doSwap(g, newGDS);
					return;
				} else {
					System.out.println("  Skip the swap, it is inefficient");
				}
			}
		}
	}

	private static boolean isSwapEfficient(ProfileEntry accesses,
			GraphDataStructure currentGDS, GraphDataStructure recGDS) {
		int amortizationCounterToUse = getAmortizationCounter();
		System.out
				.println("   Check whether the swap will be amortized within "
						+ amortizationCounterToUse
						+ " batches by runtime costs");

		/**
		 * Generate the costs for the current state
		 */
		ComparableEntryMap currentStateCosts = accesses.combinedComplexity(
				ProfilerDataType.RuntimeBenchmark, currentGDS, null);

		/**
		 * Generate the costs for the recommended state
		 */
		ComparableEntryMap recStateCosts = accesses.combinedComplexity(
				ProfilerDataType.RuntimeBenchmark, recGDS, null);

		/**
		 * Generate the costs for swapping, which is: for each changed list type
		 * the number of lists * (init + meanlistSize * add)
		 */

		ComparableEntryMap swappingCosts = ProfilerMeasurementData
				.getMap(ProfilerDataType.RuntimeBenchmark);
		for (ListType lt : ListType.values()) {
			if (recGDS.getListClass(lt) == currentGDS.getListClass(lt)) {
				continue;
			}

			int numberOfLists = Profiler.getNumberOfGeneratedLists(lt);
			double meanListSize = Profiler.getMeanSize(lt);
			int totalNumberOfElements = (int) (numberOfLists * meanListSize);

			ComparableEntry initCosts = recGDS.getCostData(lt, AccessType.Init,
					ProfilerDataType.RuntimeBenchmark);
			initCosts.setValues(numberOfLists, meanListSize, null);
			swappingCosts.add(initCosts.getMap());

			ComparableEntry addCosts = recGDS.getCostData(lt, AccessType.Add,
					ProfilerDataType.RuntimeBenchmark);
			addCosts.setValues(totalNumberOfElements, meanListSize, null);
			swappingCosts.add(addCosts.getMap());
		}

		System.out.println("   Total costs with current GDS: "
				+ currentStateCosts + ", total swapping costs: "
				+ swappingCosts + ", total costs with recommended GDS: "
				+ recStateCosts);
		recStateCosts.add(swappingCosts);
		System.out.println("   Total costs with NEW GDS, incl swap: "
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

	public static EnumMap<ListType, Class<? extends IDataStructure>> getFirstSwitch() {
		return firstSwitch;
	}

}
