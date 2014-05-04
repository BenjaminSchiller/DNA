package dna.profiler;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.DataStructure;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.tests.GlobalTestParameters;
import dna.io.Writer;
import dna.io.ZipWriter;
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.profiler.ProfilerGranularity.Options;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;
import dna.profiler.datatypes.ComparableEntryMap;
import dna.profiler.datatypes.benchmarkresults.BenchmarkingResultsMap;
import dna.series.Series;
import dna.series.SeriesGeneration;
import dna.updates.update.Update.UpdateType;
import dna.util.Config;
import dna.util.Log;

public class Profiler {
	private static Map<String, ProfileEntry> singleBatchCalls = new HashMap<>();
	private static Map<String, ProfileEntry> singleRunCalls = new HashMap<>();
	private static Map<String, ProfileEntry> singleSeriesCalls = new HashMap<>();
	private static Map<String, ProfileEntry> globalCalls = new HashMap<>();
	private static Map<String, ProfileEntry> pointerForMemoryAggregation = null;

	private static EnumMap<DataStructure.ListType, Integer> generatedListsCounter;
	private static EnumMap<DataStructure.ListType, Integer> listSizeCounter;

	private static boolean active = false;
	private static boolean inInitialBatch = false;
	private static Graph graph;
	private static GraphDataStructure gds;

	private static String seriesDir;
	private static String graphGeneratorName;
	private static int run;
	private static int totalNumberOfBatches;
	private static HashSet<String> batchGeneratorNames = new HashSet<>();
	private static HashSet<String> metricNames = new HashSet<>();

	private static FileSystem currentFileSystem;

	final static String separator = System.getProperty("line.separator");
	final static String aggregatedPrefix = "aggregated";

	private static Map<ProfilerMeasurementData.ProfilerDataType, RecommenderEntry> lastRecommendations = new EnumMap<>(
			ProfilerDataType.class);
	private static Map<ProfilerMeasurementData.ProfilerDataType, RecommenderEntry> lastCosts = new EnumMap<>(
			ProfilerDataType.class);
	private static ProfileEntry lastAccesses;

	public static void activate() {
		active = true;
	}

	public static void reset() {
		singleBatchCalls = new HashMap<>();
		singleRunCalls = new HashMap<>();
		singleSeriesCalls = new HashMap<>();
		globalCalls = new HashMap<>();
	}

	public static void setInInitialBatch(boolean newInInitialBatch) {
		inInitialBatch = newInInitialBatch;
	}

	public static boolean isActive() {
		return active;
	}

	public static void init(Graph g, GraphDataStructure newGds) {
		if (!active)
			return;

		Log.debug("Created new graph with gds" + newGds.getDataStructures());
		graph = g;
		gds = newGds;
	}

	public static void setSeriesData(Series s, int numberOfBatches) {
		seriesDir = s.getDir();
		totalNumberOfBatches = numberOfBatches;
		HotSwap.setTotalNumberOfBatches(totalNumberOfBatches);
	}

	public static void startRun(int newRun) {
		run = newRun;
		singleRunCalls = new HashMap<>();

		generatedListsCounter = new EnumMap<DataStructure.ListType, Integer>(
				DataStructure.ListType.class);
		listSizeCounter = new EnumMap<DataStructure.ListType, Integer>(
				DataStructure.ListType.class);

		for (ListType lt : ListType.values()) {
			generatedListsCounter.put(lt, 0);
			listSizeCounter.put(lt, 0);
		}
	}

	public static void startBatch() {
		singleBatchCalls = new HashMap<>();
	}

	public static void setGraphGeneratorName(String name) {
		graphGeneratorName = name;
	}

	public static void addBatchGeneratorName(String name) {
		batchGeneratorNames.add(name);
	}

	public static void addMetricName(String name) {
		metricNames.add(name);
	}

	public static void finish() {
		// Actions to be done after generation of stats, eg. writing them to
		// disk, printing them,...

		if (!active || globalCalls.isEmpty())
			return;

		System.out.println(getGlobalComplexity(globalCalls));
	}

	/**
	 * Get a string representation of the monitored calls
	 * 
	 * @param listOfEntries
	 *            List of monitored entries
	 * @param prefixFilter
	 *            Filter -- if set, output only profiling entries that start
	 *            with the given prefix
	 * @param dismissPrefix
	 *            If set to true, the prefix is not written out before any
	 *            individual call (making it possible to have one central list,
	 *            but output only calls for specific subparts and not printing
	 *            out the common prefix)
	 * 
	 *            If set to false, the prefix is kept (making it possible to
	 *            filter the list by a prefix, but keep it in front of each
	 *            entry)
	 * @return
	 */
	private static String getCallList(Map<String, ProfileEntry> listOfEntries,
			String prefixFilter, boolean showRecommendations,
			boolean isCombinedOutputForAllAccessTypes) {

		boolean forceAllRecommendations = ProfilerGranularity.all();

		StringBuilder res = new StringBuilder();
		String prefix;
		ProfileEntry aggregated = new ProfileEntry();

		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			prefix = entry.getKey();

			if (prefixFilter != null) {
				if (!entry.getKey().equals(prefixFilter)) {
					continue;
				}
			} else {
				aggregated = aggregated.add(entry.getValue());
			}

			res.append(getOutputDataForProfileEntry(entry.getValue(), prefix,
					true, (showRecommendations && prefixFilter != null)
							|| forceAllRecommendations,
					isCombinedOutputForAllAccessTypes));
		}

		if (prefixFilter == null) {
			res.append(getOutputDataForProfileEntry(aggregated,
					aggregatedPrefix, true, showRecommendations,
					isCombinedOutputForAllAccessTypes));
		}

		return res.toString();
	}

	private static boolean canDEmptyBeUsedForListInCurrentBatch(ListType lt) {
		int numberOfAccesses;
		boolean res = true;
		for (Entry<String, ProfileEntry> entry : singleBatchCalls.entrySet()) {
			ProfileEntry val = entry.getValue();
			for (AccessType at : AccessType.values()) {
				numberOfAccesses = val.get(lt, at);
				res = res && (numberOfAccesses == 0 || at.isAllowedOnEmpty());
				if (!res)
					return false;
			}
		}
		return true;
	}

	private static String getOtherRuntimeComplexitiesForEntry(
			ProfilerMeasurementData.ProfilerDataType entryType,
			HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> allCosts,
			boolean outputAsCommentWithPrefix) {

		StringBuilder res = new StringBuilder();

		String outputPrefix = outputAsCommentWithPrefix ? "# " : "";
		res.append(outputPrefix + "  Recommendations:");

		boolean disableAllRecommendations = ProfilerGranularity.disabled();

		if (disableAllRecommendations) {
			res.append(" disabled using ProfilerGranularity" + separator);
			return res.toString();
		}

		if (allCosts.size() == 0) {
			res.append(" no recommendations available" + separator);

			RecommenderEntry oldEl = lastRecommendations.get(entryType);
			if (oldEl == null) {
				lastRecommendations.put(entryType, null);
			} else {
				RecommenderEntry elementToNull = oldEl.clone();
				elementToNull.resetCosts();
				lastRecommendations.put(entryType, elementToNull);
			}
			return res.toString();
		}

		TreeSet<RecommenderEntry> recommendationQueue = new TreeSet<RecommenderEntry>(
				RecommenderEntry.getComparator(entryType));
		int numberOfRecommendations = Config
				.getInt("NUMBER_OF_RECOMMENDATIONS");

		RecommenderEntry aggregatedEntry;
		EnumMap<ListType, Class<? extends IDataStructure>> singleCombination;

		for (Entry<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> singleCostEntry : allCosts
				.entrySet()) {
			aggregatedEntry = singleCostEntry.getValue();
			singleCombination = singleCostEntry.getKey();

			RecommenderEntry lowerEntry = recommendationQueue
					.floor(aggregatedEntry);
			if (lowerEntry == null
					|| !lowerEntry.getCosts(entryType).equals(
							aggregatedEntry.getCosts(entryType))) {
				// Key not yet in list
				recommendationQueue.add(aggregatedEntry);
			} else if ((singleCombination.get(ListType.GlobalEdgeList) == DEmpty.class && lowerEntry
					.getDatastructure(ListType.GlobalEdgeList) != DEmpty.class)
					|| (singleCombination.get(ListType.LocalEdgeList) == DEmpty.class && lowerEntry
							.getDatastructure(ListType.LocalEdgeList) != DEmpty.class)) {
				// Key already in list, but with concrete types where we
				// could also use DEmpty to save memory
				recommendationQueue.add(aggregatedEntry);
			}
		}

		/**
		 * Recommendations are picked from the front of the list, as they have
		 * the largest counter for the most important complexity class
		 */
		for (int i = 0; (i < numberOfRecommendations && recommendationQueue
				.size() > 0); i++) {
			RecommenderEntry pollFirstEntry = recommendationQueue.pollFirst();

			String polledEntry = pollFirstEntry.getGraphDataStructure()
					.getStorageDataStructures(true)
					+ ": "
					+ pollFirstEntry.getCosts(entryType);
			res.append(separator);
			res.append(outputPrefix + "   " + polledEntry);

			if (i == 0) {
				lastRecommendations.put(entryType, pollFirstEntry);
			}
		}

		// res.append(separator + "  Bottom list: ");
		// for (int i = 0; (i < numberOfRecommendations && recommendationQueue
		// .size() > 0); i++) {
		// RecommenderEntry pollLastEntry = recommendationQueue.pollLast();
		// String polledEntry = pollLastEntry.getGraphDataStructure()
		// .getStorageDataStructures(true)
		// + ": "
		// + pollLastEntry.getCosts();
		// res.append(separator);
		// res.append(outputPrefix + "   " + polledEntry);
		// }

		res.append(separator);
		return res.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> calculateAllRecommendations(
			ProfileEntry globalMemoryEntry, ProfileEntry currentEntry,
			boolean isCombinedOutputForAllAccessTypes) {
		boolean disableAllRecommendations = ProfilerGranularity.disabled();

		HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> res = new HashMap<>();

		if (disableAllRecommendations || currentEntry.getCombined() == 0) {
			return res;
		}

		ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> allCombinationsRaw;

		if (Config.getBoolean("PROFILER_USE_SIMPLE_LIST_FOR_RECOMMENDATIONS"))
			allCombinationsRaw = GraphDataStructure
					.getSimpleDatastructureCombinations();
		else
			allCombinationsRaw = GraphDataStructure
					.getAllDatastructureCombinations();

		HashSet<EnumMap<ListType, Class<? extends IDataStructure>>> allCombinations = prefilter(
				allCombinationsRaw, currentEntry,
				isCombinedOutputForAllAccessTypes);

		for (ProfilerDataType entryType : ProfilerDataType.values()) {
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes;
			GraphDataStructure tempGDS;
			EnumMap<ListType, HashMap<Class, ComparableEntryMap>> listComplexities = new EnumMap<ListType, HashMap<Class, ComparableEntryMap>>(
					ListType.class);
			HashMap<Class, ComparableEntryMap> innerComplexities;

			for (ListType lt : ListType.values()) {
				innerComplexities = new HashMap<Class, ComparableEntryMap>();
				for (Class listClass : GlobalTestParameters.dataStructures) {
					if (!(lt.getRequiredType().isAssignableFrom(listClass)))
						continue;
					listTypes = GraphDataStructure.getList(lt, listClass);
					tempGDS = new GraphDataStructure(listTypes,
							gds.getNodeType(), gds.getEdgeType());

					ComparableEntryMap combinedComplexityMap = combineFiltered(
							entryType, tempGDS, currentEntry,
							globalMemoryEntry,
							entryType.getAccessTypesFromAggregation(), lt);
					innerComplexities.put(listClass, combinedComplexityMap);
				}
				listComplexities.put(lt, innerComplexities);
			}

			for (EnumMap<ListType, Class<? extends IDataStructure>> singleCombination : allCombinations) {
				ComparableEntryMap aggregated = ProfilerMeasurementData
						.getMap(entryType);

				Class<? extends IDataStructure> currClass;
				ListType recLT;

				for (ListType loopLT : ListType.values()) {
					currClass = null;
					recLT = loopLT;
					while (currClass == null) {
						currClass = singleCombination.get(recLT);
						recLT = recLT.getFallback();
					}
					aggregated.add(listComplexities.get(loopLT).get(currClass));
				}

				RecommenderEntry aggregatedEntry = res.get(singleCombination);
				if (aggregatedEntry == null) {
					aggregatedEntry = new RecommenderEntry(singleCombination);
					res.put(singleCombination, aggregatedEntry);
				}
				aggregatedEntry.setCosts(entryType, aggregated);
			}
		}

		res = postfilter(res);

		return res;
	}

	private static ComparableEntryMap combineFiltered(
			ProfilerDataType entryType, GraphDataStructure gds,
			ProfileEntry currentEntry, ProfileEntry globalMemoryEntry,
			ArrayList<AccessType> accessTypesFromAggregation,
			ListType listTypeLimitor) {
		if (accessTypesFromAggregation.size() == 0)
			return currentEntry.combinedComplexity(entryType, gds,
					listTypeLimitor);

		ComparableEntryMap res = ProfilerMeasurementData.getMap(entryType);

		for (AccessType at : AccessType.values()) {
			if (accessTypesFromAggregation.contains(at)) {
				res.add(globalMemoryEntry.combinedComplexity(entryType, gds,
						listTypeLimitor, at));
			} else {
				res.add(currentEntry.combinedComplexity(entryType, gds,
						listTypeLimitor, at));
			}
		}

		return res;
	}

	/**
	 * Do some pre-filtering for the list of data structures, for example to
	 * avoid using DEmpty when this is not suitable
	 * 
	 * @param oldList
	 *            Input list of data structure combinations
	 * @param entry
	 *            current ProfileEntry
	 * @param isCombinedOutputForAllAccessTypes
	 *            boolean flag to show whether we are in the aggregated case
	 *            over a whole batch or not
	 * @return
	 */
	private static HashSet<EnumMap<ListType, Class<? extends IDataStructure>>> prefilter(
			ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> oldList,
			ProfileEntry entry, boolean isCombinedOutputForAllAccessTypes) {

		HashSet<EnumMap<ListType, Class<? extends IDataStructure>>> res = new HashSet<>();
		EnumMap<ListType, Boolean> canDEmptyBeUsedForListInCurrentBatch = new EnumMap<>(
				ListType.class);

		for (ListType lt : ListType.values()) {
			canDEmptyBeUsedForListInCurrentBatch.put(lt,
					canDEmptyBeUsedForListInCurrentBatch(lt));
		}

		boolean skipThisEntry;
		for (EnumMap<ListType, Class<? extends IDataStructure>> el : oldList) {
			skipThisEntry = false;

			for (ListType lt : el.keySet()) {
				if (!skipThisEntry) {
					if (isCombinedOutputForAllAccessTypes) {
						/**
						 * Check here whether the current list type has any
						 * read-access during *metrics*. If the currently used
						 * metrics use no read-access to this list type, we can
						 * switch it to DEmpty
						 */
						if (canDEmptyBeUsedForListInCurrentBatch.get(lt)
								&& el.get(lt) != DEmpty.class) {
							el.put(lt, DEmpty.class);
						} else if (!canDEmptyBeUsedForListInCurrentBatch
								.get(lt) && el.get(lt) == DEmpty.class) {
							skipThisEntry = true;
						}
					} else if (entry.hasReadAccessesInList(lt)
							&& el.get(lt) == DEmpty.class) {
						skipThisEntry = true;
					}
				}
			}

			if (!skipThisEntry) {
				res.add(el);
			}
		}
		return res;
	}

	/**
	 * Do some post-filtering, based on the recommendations that were calculated
	 * 
	 * @param res
	 * @return
	 */
	private static HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> postfilter(
			HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> input) {
		HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> res = new HashMap<>();

		boolean isElementBlocked = false;
		double maxMemoryBound = Config
				.getDouble("RECOMMENDER_MAX_MEMORY_BOUND");

		for (Entry<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> element : input
				.entrySet()) {
			isElementBlocked = false;

			RecommenderEntry entry = element.getValue();
			BenchmarkingResultsMap memoryCosts = (BenchmarkingResultsMap) entry
					.getCosts(ProfilerDataType.MemoryBenchmark);
			double rawMemoryCosts = memoryCosts.getValue();
			if (maxMemoryBound > 0 && rawMemoryCosts > maxMemoryBound) {
				Log.debug("Will filter out " + element.getKey()
						+ " due to high memory costs of " + rawMemoryCosts
						+ " (maximum of " + maxMemoryBound + " is configured)");
				isElementBlocked = true;
			}

			if (!isElementBlocked) {
				res.put(element.getKey(), element.getValue());
			}
		}
		return res;
	}

	/**
	 * Method used to print the complexity analysis over all access types and
	 * matching recommendations
	 * 
	 * @param listOfEntries
	 * @return
	 */
	private static String getGlobalComplexity(
			Map<String, ProfileEntry> listOfEntries) {
		StringBuilder res = new StringBuilder();
		ProfileEntry resEntry = new ProfileEntry();
		res.append(separator + "Complexity analysis over all access types:"
				+ separator);
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			resEntry = resEntry.add(entry.getValue());
		}
		res.append(resEntry.toString());

		ProfileEntry globalMemoryEntry = new ProfileEntry();
		for (ProfileEntry other : pointerForMemoryAggregation.values()) {
			globalMemoryEntry = globalMemoryEntry.add(other);
		}

		HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> recEntryMap = calculateAllRecommendations(
				globalMemoryEntry, resEntry, true);
		for (ProfilerMeasurementData.ProfilerDataType entryType : ProfilerMeasurementData.ProfilerDataType
				.values()) {

			ComparableEntryMap aggregatedMap = combineFiltered(entryType, gds,
					resEntry, globalMemoryEntry,
					entryType.getAccessTypesFromAggregation(), null);

			RecommenderEntry aggregatedEntry = new RecommenderEntry(
					gds.getStorageDataStructures());
			aggregatedEntry.setCosts(entryType, aggregatedMap);

			res.append(" Aggr for " + entryType + ": " + aggregatedMap
					+ separator);
			res.append(getOtherRuntimeComplexitiesForEntry(entryType,
					recEntryMap, false));
		}
		return res.toString();
	}

	private static ProfileEntry entryForKey(Map<String, ProfileEntry> calls,
			String mapKey, boolean forceReset) {
		ProfileEntry innerMap = calls.get(mapKey);
		if (innerMap == null || forceReset) {
			innerMap = new ProfileEntry();
			calls.put(mapKey, innerMap);
		}
		return innerMap;
	}

	public static void count(String mapKey, ListType lt, AccessType a) {
		if (!active)
			return;

		ProfileEntry innerMap = entryForKey(singleBatchCalls, mapKey, false);
		innerMap.increase(lt, a, 1);

		switch (a) {
		case Init:
			generatedListsCounter.put(lt, generatedListsCounter.get(lt) + 1);
			break;
		case Add:
			listSizeCounter.put(lt, listSizeCounter.get(lt) + 1);
			break;
		case RemoveSuccess:
			listSizeCounter.put(lt, listSizeCounter.get(lt) - 1);
			break;
		case GetSuccess:
		case GetFailure:
		case RemoveFailure:
		case Iterator:
		case ContainsSuccess:
		case ContainsFailure:
		case Random:
		case Size:
			break;
		default:
			throw new RuntimeException("AccessType " + a
					+ " needs a case statement here");
		}
	}

	public static int getCount(String mapKey, ListType[] lt, AccessType at) {
		int res = 0;
		for (ListType inner : lt)
			res += getCount(mapKey, inner, at);
		return res;
	}

	public static int getCount(String mapKey, ListType lt, AccessType at) {
		return getCount(singleBatchCalls, mapKey, lt, at);
	}

	public static int getCount(Map<String, ProfileEntry> calls, String mapKey,
			ListType lt, AccessType at) {
		ProfileEntry innerMap = calls.get(mapKey);
		if (innerMap == null)
			return 0;
		return innerMap.get(lt, at);
	}

	private static HashMap<String, ProfileEntry> merge(
			Map<String, ProfileEntry> one, Map<String, ProfileEntry> two) {
		HashMap<String, ProfileEntry> res = new HashMap<>();
		for (Map.Entry<String, ProfileEntry> entry : one.entrySet()) {
			res.put(entry.getKey(), entry.getValue());
		}

		for (Map.Entry<String, ProfileEntry> entry : two.entrySet()) {
			ProfileEntry p = res.get(entry.getKey());
			if (p == null) {
				res.put(entry.getKey(), entry.getValue());
			} else {
				p = p.add(entry.getValue());
				res.put(entry.getKey(), p);
			}
		}
		return res;
	}

	private static String getOutputDataForProfileEntry(ProfileEntry aggregated,
			String prefix, boolean outputAsCommentWithPrefix,
			boolean showRecommendations,
			boolean isCombinedOutputForAllAccessTypes) {
		String outputPrefix = outputAsCommentWithPrefix ? "# " : "";

		StringBuilder res = new StringBuilder();
		res.append(aggregated.callsAsString(prefix));

		ProfileEntry globalMemoryEntry = aggregated;
		if (prefix == aggregatedPrefix) {
			globalMemoryEntry = new ProfileEntry();
			for (ProfileEntry other : pointerForMemoryAggregation.values()) {
				globalMemoryEntry = globalMemoryEntry.add(other);
			}
		}

		HashMap<EnumMap<ListType, Class<? extends IDataStructure>>, RecommenderEntry> recEntryMap = null;
		if (showRecommendations) {
			recEntryMap = calculateAllRecommendations(globalMemoryEntry,
					aggregated, isCombinedOutputForAllAccessTypes);
		}

		for (ProfilerMeasurementData.ProfilerDataType entryType : ProfilerMeasurementData.ProfilerDataType
				.values()) {

			ComparableEntryMap aggregatedMap = combineFiltered(entryType, gds,
					aggregated, globalMemoryEntry,
					entryType.getAccessTypesFromAggregation(), null);

			RecommenderEntry aggregatedEntry = new RecommenderEntry(
					gds.getStorageDataStructures());
			aggregatedEntry.setCosts(entryType, aggregatedMap);

			lastCosts.put(entryType, aggregatedEntry);
			lastAccesses = aggregated;

			res.append(outputPrefix + " Aggr for " + entryType + ": "
					+ aggregatedMap + separator);
			if (showRecommendations)
				res.append(getOtherRuntimeComplexitiesForEntry(entryType,
						recEntryMap, outputAsCommentWithPrefix));
		}
		return res.toString();
	}

	public static void writeAggregation(Map<String, ProfileEntry> calls,
			String dir, boolean additionalCond) throws IOException {
		boolean enabledHotswap = Config.getBoolean("HOTSWAP_ENABLED");
		Profiler.write(calls, dir,
				Files.getProfilerFilename(Config.get("AGGREGATED_PROFILER")),
				enabledHotswap || additionalCond, true);
		if (enabledHotswap) {
			HotSwap.addNewResults();
			HotSwap.trySwap(graph);
		}
	}

	public static void write(Map<String, ProfileEntry> calls, String dir,
			String filename, boolean writeRecommendations,
			boolean isCombinedOutputForAllAccessTypes) throws IOException {
		StringBuilder data = new StringBuilder();
		data.append(getCallList(calls, null, writeRecommendations,
				isCombinedOutputForAllAccessTypes));
		rawWrite(dir, filename, data.toString());
	}

	public static void writeMetric(String metricKey, String dir)
			throws IOException {
		boolean rec = ProfilerGranularity.isEnabled(Options.EACHMETRIC);
		Profiler.writeSingle(singleBatchCalls, metricKey, dir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")), rec,
				false);
	}

	private static void writeUpdates(Map<String, ProfileEntry> calls,
			String dir, boolean forceRecommendations) throws IOException {

		StringBuilder data = new StringBuilder();
		ProfileEntry aggregated = new ProfileEntry();

		boolean writeUpdateRecommendations = ProfilerGranularity
				.isEnabled(Options.EACHUPDATETYPE);

		for (UpdateType u : UpdateType.values()) {
			// Ensure that the update type is in the needed list
			entryForKey(calls, u.toString(), false);
			data.append(getCallList(calls, u.toString(),
					writeUpdateRecommendations, false));
			aggregated = aggregated.add(calls.get(u.toString()));
		}

		data.append(getOutputDataForProfileEntry(aggregated, "aggregated",
				true, forceRecommendations || writeUpdateRecommendations, false));
		rawWrite(dir,
				Files.getProfilerFilename(Config.get("UPDATES_PROFILER")),
				data.toString());
	}

	public static void writeSingle(Map<String, ProfileEntry> callList,
			String metricName, String dir, String filename,
			boolean writeRecommendations,
			boolean isCombinedOutputForAllAccessTypes) throws IOException {
		writeMultiple(callList, new String[] { metricName }, dir, filename,
				writeRecommendations, isCombinedOutputForAllAccessTypes);
	}

	public static void writeMultiple(Map<String, ProfileEntry> calls,
			String[] keys, String dir, String filename,
			boolean forceRecommendations,
			boolean isCombinedOutputForAllAccessTypes) throws IOException {

		ProfileEntry aggregated = new ProfileEntry();
		StringBuilder data = new StringBuilder();

		boolean forceAllRecommendations = ProfilerGranularity.all();

		for (String singleKey : keys) {
			// Are we still in the initial batch? Then add the specific key to
			// the
			// end of the metric name
			if (inInitialBatch)
				singleKey += Config.get("PROFILER_INITIALBATCH_KEYADDITION");
			data.append(getCallList(calls, singleKey,
					(keys.length == 1 && forceRecommendations)
							|| forceAllRecommendations, false));
			aggregated = aggregated.add(calls.get(singleKey));
		}

		if (keys.length > 1) {
			data.append(getOutputDataForProfileEntry(aggregated, "aggregated",
					true, forceAllRecommendations || forceRecommendations,
					isCombinedOutputForAllAccessTypes));
		}
		rawWrite(dir, filename, data.toString());
	}

	private static void rawWrite(String dir, String filename, String data)
			throws IOException {
		Writer w;

		if (SeriesGeneration.singleFile && currentFileSystem != null
				&& currentFileSystem.isOpen()) {
			w = new ZipWriter(currentFileSystem, "/", filename);
		} else {
			w = new Writer(dir, filename);
		}
		w.write(data);
		w.close();
	}

	public static void finishSeries() throws IOException {
		if (!active)
			return;

		globalCalls = merge(globalCalls, singleSeriesCalls);
		pointerForMemoryAggregation = globalCalls;

		boolean rec = ProfilerGranularity.isEnabled(Options.EACHSERIES);

		currentFileSystem = null;

		Profiler.writeMultiple(singleSeriesCalls,
				batchGeneratorNames.toArray(new String[0]), seriesDir,
				Files.getProfilerFilename(Config.get("BATCH_PROFILER")), rec,
				false);

		Profiler.writeMultiple(singleSeriesCalls,
				metricNames.toArray(new String[0]), seriesDir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")), rec,
				false);

		Profiler.writeUpdates(singleSeriesCalls, seriesDir, rec);

		Profiler.writeAggregation(singleSeriesCalls, seriesDir, true);

		singleSeriesCalls = new HashMap<>();
	}

	public static void finishRun() throws IOException {
		if (!active)
			return;

		boolean rec = ProfilerGranularity.isEnabled(Options.EACHRUN);

		singleSeriesCalls = merge(singleSeriesCalls, singleRunCalls);
		pointerForMemoryAggregation = singleSeriesCalls;

		String runDataDir = Dir.getRunDataDir(seriesDir, run);
		currentFileSystem = null;

		Profiler.writeSingle(
				singleRunCalls,
				graphGeneratorName,
				runDataDir,
				Files.getProfilerFilename(Config.get("GRAPHGENERATOR_PROFILER")),
				rec, false);

		Profiler.writeMultiple(singleRunCalls,
				batchGeneratorNames.toArray(new String[0]), runDataDir,
				Files.getProfilerFilename(Config.get("BATCH_PROFILER")), rec,
				false);

		Profiler.writeMultiple(singleRunCalls,
				metricNames.toArray(new String[0]), runDataDir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")), rec,
				false);

		Profiler.writeUpdates(singleRunCalls, runDataDir, rec);

		Profiler.writeAggregation(singleRunCalls, runDataDir, rec);
	}

	public static void finishBatch(long batchTimestamp) throws IOException {
		if (!active)
			return;

		String batchDir = Dir.getBatchDataDir(seriesDir, run, batchTimestamp);
		String runDataDir = Dir.getRunDataDir(seriesDir, run);

		singleRunCalls = merge(singleRunCalls, singleBatchCalls);
		pointerForMemoryAggregation = singleRunCalls;

		HotSwap.setLastFinishedBatch(batchTimestamp);

		if (SeriesGeneration.singleFile) {
			currentFileSystem = ZipWriter.createBatchFileSystem(runDataDir,
					Config.get("SUFFIX_ZIP_FILE"), batchTimestamp);
		}

		Profiler.writeMultiple(singleBatchCalls,
				metricNames.toArray(new String[0]), batchDir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")),
				false, false);
		writeUpdates(singleBatchCalls, batchDir,
				ProfilerGranularity.isEnabled(Options.EACHBATCH));

		if (batchTimestamp == 1) {
			/**
			 * First batch is finished, so ensure that we write a dummy file for
			 * the batch profiler into batch.0 for this run. This helps with the
			 * visualization of this data
			 */
			for (String bGenName : batchGeneratorNames) {
				Profiler.entryForKey(singleBatchCalls, bGenName, true);
			}
			Profiler.writeMultiple(singleBatchCalls,
					batchGeneratorNames.toArray(new String[0]),
					Dir.getBatchDataDir(seriesDir, run, 0),
					Files.getProfilerFilename(Config.get("BATCH_PROFILER")),
					false, false);
		}

		if (batchTimestamp > 0) {
			Profiler.writeMultiple(singleBatchCalls,
					batchGeneratorNames.toArray(new String[0]),
					Dir.getBatchDataDir(seriesDir, run, batchTimestamp),
					Files.getProfilerFilename(Config.get("BATCH_PROFILER")),
					false, false);
		}

		Profiler.writeAggregation(singleBatchCalls, batchDir,
				ProfilerGranularity.isEnabled(Options.EACHBATCH));

		if (SeriesGeneration.singleFile) {
			currentFileSystem.close();
		}
	}

	public static int getNumberOfGeneratedLists(ListType lt) {
		return generatedListsCounter.get(lt);
	}

	public static double getMeanSize(ListType lt) {
		double numberOfLists = getNumberOfGeneratedLists(lt);
		if (numberOfLists == 0)
			return 0;
		double accumulatedNumberOfElements = listSizeCounter.get(lt);
		return accumulatedNumberOfElements / numberOfLists;
	}

	public static RecommenderEntry getRecommendation(
			ProfilerMeasurementData.ProfilerDataType selector) {
		return lastRecommendations.get(selector);
	}

	public static RecommenderEntry getLastCosts(
			ProfilerMeasurementData.ProfilerDataType selector) {
		return lastCosts.get(selector);
	}

	public static ProfileEntry getLastAccesses() {
		return lastAccesses;
	}
}
