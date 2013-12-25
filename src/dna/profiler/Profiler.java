package dna.profiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dna.graph.datastructures.DEmpty;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.tests.GlobalTestParameters;
import dna.io.Writer;
import dna.io.filesystem.Dir;
import dna.io.filesystem.Files;
import dna.profiler.complexity.ComplexityMap;
import dna.updates.update.Update.UpdateType;
import dna.util.Config;
import dna.util.Log;

public class Profiler {
	private static Map<String, ProfileEntry> singleBatchCalls = new HashMap<>();
	private static Map<String, ProfileEntry> singleRunCalls = new HashMap<>();
	private static Map<String, ProfileEntry> singleSeriesCalls = new HashMap<>();
	private static Map<String, ProfileEntry> globalCalls = new HashMap<>();

	private static boolean active = false;
	private static boolean inInitialBatch = false;
	private static GraphDataStructure gds;

	private static String seriesDir, batchDir;
	private static String graphGeneratorName;
	private static int run;
	private static HashSet<String> batchGeneratorNames = new HashSet<>();
	private static HashSet<String> metricNames = new HashSet<>();

	final static String separator = System.getProperty("line.separator");
	private static boolean writeAllRecommendations;
	private static boolean disableAllRecommendations;

	public static void activate() {
		writeAllRecommendations = Config
				.getBoolean("PROFILER_WRITE_ALL_RECOMMENDATIONS");
		disableAllRecommendations = Config
				.getBoolean("PROFILER_DISABLE_ALL_RECOMMENDATIONS");
		active = true;
	}

	public static void setInInitialBatch(boolean newInInitialBatch) {
		inInitialBatch = newInInitialBatch;
	}

	public static boolean isActive() {
		return active;
	}

	public static void init(GraphDataStructure newGds) {
		// This is initialization of profiles

		if (!active)
			return;

		Log.debug("Created new graph with gds" + newGds.getDataStructures());
		gds = newGds;
	}

	public static void setSeriesDir(String dir) {
		seriesDir = dir;
	}

	public static void startRun(int newRun) {
		run = newRun;
		singleRunCalls = new HashMap<>();
	}

	public static void startBatch(long batchCounter) {
		batchDir = Dir.getBatchDataDir(seriesDir, run, batchCounter);
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

		if (!active)
			return;

		System.out.println(getOutput(globalCalls, true));
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
	public static String getCallList(Map<String, ProfileEntry> listOfEntries,
			String prefixFilter, boolean showRecommendations) {

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
							|| writeAllRecommendations));
		}

		if (prefixFilter == null) {
			res.append(getOutputDataForProfileEntry(aggregated, "aggregated",
					true, showRecommendations));
		}

		return res.toString();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String getOtherComplexitiesForEntry(ProfileEntry entry,
			boolean outputAsCommentWithPrefix) {
		GraphDataStructure tempGDS;
		TreeMap<ComplexityMap, GraphDataStructure> listOfOtherComplexities = new TreeMap<>();
		StringBuilder res = new StringBuilder();

		String outputPrefix = outputAsCommentWithPrefix ? "# " : "";
		res.append(outputPrefix + "  Recommendations:");

		if (disableAllRecommendations) {
			res.append(" disabled using PROFILER_DISABLE_ALL_RECOMMENDATIONS"
					+ separator);
			return res.toString();
		}

		if (entry.getCombined() == 0) {
			res.append(" not available" + separator);
			return res.toString();
		}

		for (Class nodeListType : GlobalTestParameters.dataStructures) {
			for (Class edgeListType : GlobalTestParameters.dataStructures) {
				for (Class nodeEdgeListType : GlobalTestParameters.dataStructures) {
					if (!(INodeListDatastructure.class
							.isAssignableFrom(nodeListType)))
						continue;
					if (!(IEdgeListDatastructure.class
							.isAssignableFrom(edgeListType)))
						continue;
					if (!(IEdgeListDatastructure.class
							.isAssignableFrom(nodeEdgeListType)))
						continue;
					if (edgeListType == DEmpty.class
							&& nodeEdgeListType == DEmpty.class)
						continue;
					if ((edgeListType == DEmpty.class && hasGlobalEdgeListAccess(entry))
							|| (nodeEdgeListType == DEmpty.class && hasLocalEdgeListAccess(entry))) {
						continue;
					}
					tempGDS = new GraphDataStructure(nodeListType,
							edgeListType, nodeEdgeListType, gds.getNodeType(),
							gds.getEdgeType());
					final ComplexityMap combinedComplexityMap = entry
							.combinedComplexity(tempGDS);
					
					GraphDataStructure graphDataStructure = listOfOtherComplexities.get(combinedComplexityMap);
					if (graphDataStructure == null) {
						// Key not yet in list
						listOfOtherComplexities.put(combinedComplexityMap,
								tempGDS);
					} else if ((edgeListType == DEmpty.class && graphDataStructure
							.getGraphEdgeListType() != DEmpty.class)
							|| (nodeEdgeListType == DEmpty.class && graphDataStructure
									.getNodeEdgeListType() != DEmpty.class)) {
						// Key already in list, but with concrete types where we could also use DEmpty to save memory
						listOfOtherComplexities.put(combinedComplexityMap,
								tempGDS);						
					}
				}
			}
		}

		/**
		 * Recoomendations are picked from the front of the list, as they have
		 * the largest counter for the most important complexity class
		 */
		for (int i = 0; (i < Config.getInt("NUMBER_OF_RECOMMENDATIONS") && listOfOtherComplexities
				.size() > 0); i++) {
			Entry<ComplexityMap, GraphDataStructure> pollFirstEntry = listOfOtherComplexities
					.pollFirstEntry();
			String polledEntry = pollFirstEntry.getValue()
					.getStorageDataStructures(true)
					+ ": "
					+ pollFirstEntry.getKey();
			res.append(separator);
			res.append(outputPrefix + "   " + polledEntry);
		}

		// res.append(separator + "  Bottom list: ");
		// for (int i = 0; (i < NumberOfRecommendations &&
		// listOfOtherComplexities
		// .size() > 0); i++) {
		// Entry<ComplexityMap, GraphDataStructure> pollLastEntry =
		// listOfOtherComplexities
		// .pollLastEntry();
		// String polledEntry = pollLastEntry.getValue()
		// .getStorageDataStructures(true)
		// + ": "
		// + pollLastEntry.getKey();
		// res.append(separator);
		// res.append(outputPrefix + "   " + polledEntry);
		// }

		res.append(separator);
		return res.toString();
	}

	private static boolean hasLocalEdgeListAccess(ProfileEntry entry) {
		return entry.hasAccessesOfType(ProfilerConstants.localEdgeListAccesses);
	}

	private static boolean hasGlobalEdgeListAccess(ProfileEntry entry) {
		return entry.hasAccessesOfType(ProfilerConstants.globalEdgeListAccesses);
	}

	public static String getGlobalComplexity(
			Map<String, ProfileEntry> listOfEntries) {
		StringBuilder res = new StringBuilder();
		ProfileEntry resEntry = new ProfileEntry();
		res.append(separator + "Complexity analysis over all access types:"
				+ separator);
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			resEntry = resEntry.add(entry.getValue());
		}
		res.append(resEntry.toString());
		res.append(" Aggr: " + resEntry.combinedComplexity(gds) + separator);
		res.append(getOtherComplexitiesForEntry(resEntry, false));
		return res.toString();
	}

	public static String getOutput(Map<String, ProfileEntry> listOfEntries,
			boolean showRecommendations) {
		StringBuilder res = new StringBuilder();
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			if (res.length() > 0)
				res.append(separator);
			res.append("Count type: " + entry.getKey() + separator);
			res.append(entry.getValue().toString());
			res.append(" Aggr: " + entry.getValue().combinedComplexity(gds)
					+ separator);
			if (showRecommendations)
				res.append(getOtherComplexitiesForEntry(entry.getValue(), false));
		}
		return res.toString();
	}

	public static ProfileEntry entryForKey(Map<String, ProfileEntry> calls,
			String mapKey, boolean forceReset) {
		ProfileEntry innerMap = calls.get(mapKey);
		if (innerMap == null || forceReset) {
			innerMap = new ProfileEntry();
			calls.put(mapKey, innerMap);
		}
		return innerMap;
	}

	public static void count(String mapKey, ProfilerConstants.ProfilerType p) {
		if (!active)
			return;

		ProfileEntry innerMap = entryForKey(singleBatchCalls, mapKey, false);
		innerMap.increase(p, 1);
	}

	public static int getCount(String mapKey, ProfilerConstants.ProfilerType p) {
		return getCount(singleBatchCalls, mapKey, p);
	}

	public static int getCount(Map<String, ProfileEntry> calls, String mapKey,
			ProfilerConstants.ProfilerType p) {
		ProfileEntry innerMap = calls.get(mapKey);
		if (innerMap == null)
			return 0;
		return innerMap.get(p);
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
			boolean showRecommendations) {
		String outputPrefix = outputAsCommentWithPrefix ? "# " : "";

		StringBuilder res = new StringBuilder();
		res.append(aggregated.callsAsString(prefix));
		res.append(outputPrefix + " Aggr: "
				+ aggregated.combinedComplexity(gds) + separator);
		if (showRecommendations)
			res.append(getOtherComplexitiesForEntry(aggregated,
					outputAsCommentWithPrefix));
		return res.toString();
	}

	public static void write(Map<String, ProfileEntry> calls, String dir,
			String filename, boolean writeRecommendations) throws IOException {
		Writer w = new Writer(dir, filename);
		w.writeln(getCallList(calls, null, writeRecommendations));
		w.close();
	}

	public static void writeMetric(String metricKey, String dir)
			throws IOException {
		Profiler.writeSingle(singleBatchCalls, metricKey, dir,
				Files.getProfilerFilename(Config.get("METRIC_PROFILER")),
				writeAllRecommendations);
	}

	private static void writeUpdates(Map<String, ProfileEntry> calls,
			String dir, boolean writeRecommendations) throws IOException {
		Writer w = new Writer(dir, Files.getProfilerFilename(Config
				.get("UPDATES_PROFILER")));

		ProfileEntry aggregated = new ProfileEntry();

		for (UpdateType u : UpdateType.values()) {
			// Ensure that the update type is in the needed list
			entryForKey(calls, u.toString(), false);
			w.writeln(getCallList(calls, u.toString(), false));
			aggregated = aggregated.add(calls.get(u.toString()));
		}

		w.writeln(getOutputDataForProfileEntry(aggregated, "aggregated", true,
				writeRecommendations || writeAllRecommendations));
		w.close();
	}

	public static void writeSingle(Map<String, ProfileEntry> callList,
			String metricName, String dir, String filename,
			boolean writeRecommendations) throws IOException {
		writeMultiple(callList, new String[] { metricName }, dir, filename,
				writeRecommendations);
	}

	public static void writeMultiple(Map<String, ProfileEntry> calls,
			String[] keys, String dir, String filename,
			boolean writeRecommendations) throws IOException {
		Writer w = new Writer(dir, filename);

		ProfileEntry aggregated = new ProfileEntry();

		for (String singleKey : keys) {
			// Are we still in the initial batch? Then add the specific key to
			// the
			// end of the metric name
			if (inInitialBatch)
				singleKey += Config.get("PROFILER_INITIALBATCH_KEYADDITION");
			w.writeln(getCallList(calls, singleKey,
					(keys.length == 1 && writeRecommendations)
							|| writeAllRecommendations));
			aggregated = aggregated.add(calls.get(singleKey));
		}

		if (keys.length > 1) {
			w.writeln(getOutputDataForProfileEntry(aggregated, "aggregated",
					true, writeAllRecommendations || writeRecommendations));
		}
		w.close();
	}

	public static void finishSeries() {
		if (!active)
			return;

		globalCalls = merge(globalCalls, singleSeriesCalls);

		try {
			Profiler.write(singleSeriesCalls, seriesDir, Files
					.getProfilerFilename(Config.get("AGGREGATED_PROFILER")),
					true);

			Profiler.writeMultiple(singleSeriesCalls,
					batchGeneratorNames.toArray(new String[0]), seriesDir,
					Files.getProfilerFilename(Config.get("BATCH_PROFILER")),
					true);

			Profiler.writeMultiple(singleSeriesCalls,
					metricNames.toArray(new String[0]), seriesDir,
					Files.getProfilerFilename(Config.get("METRIC_PROFILER")),
					true);

			Profiler.writeUpdates(singleSeriesCalls, seriesDir, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		singleSeriesCalls = new HashMap<>();
	}

	public static void finishRun() {
		if (!active)
			return;

		singleSeriesCalls = merge(singleSeriesCalls, singleRunCalls);

		try {
			String runDataDir = Dir.getRunDataDir(seriesDir, run);
			Profiler.writeSingle(singleRunCalls, graphGeneratorName,
					runDataDir, Files.getProfilerFilename(Config
							.get("GRAPHGENERATOR_PROFILER")), true);

			Profiler.write(singleRunCalls, runDataDir, Files
					.getProfilerFilename(Config.get("AGGREGATED_PROFILER")),
					true);

			Profiler.writeMultiple(singleRunCalls,
					batchGeneratorNames.toArray(new String[0]), runDataDir,
					Files.getProfilerFilename(Config.get("BATCH_PROFILER")),
					true);

			Profiler.writeMultiple(singleRunCalls,
					metricNames.toArray(new String[0]), runDataDir,
					Files.getProfilerFilename(Config.get("METRIC_PROFILER")),
					true);

			Profiler.writeUpdates(singleRunCalls, runDataDir, true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void finishBatch(long batchTimestamp) {
		if (!active)
			return;

		singleRunCalls = merge(singleRunCalls, singleBatchCalls);

		try {
			Profiler.write(singleBatchCalls, batchDir, Files
					.getProfilerFilename(Config.get("AGGREGATED_PROFILER")),
					false);
			Profiler.writeMultiple(singleBatchCalls,
					metricNames.toArray(new String[0]), batchDir,
					Files.getProfilerFilename(Config.get("METRIC_PROFILER")),
					false);
			writeUpdates(singleBatchCalls, batchDir, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (batchTimestamp == 1) {
			/**
			 * First batch is finished, so ensure that we write a dummy file for
			 * the batch profiler into batch.0 for this run. This helps with the
			 * visualization of this data
			 */
			for (String bGenName : batchGeneratorNames) {
				Profiler.entryForKey(singleBatchCalls, bGenName, true);
			}
			try {
				Profiler.writeMultiple(
						singleBatchCalls,
						batchGeneratorNames.toArray(new String[0]),
						Dir.getBatchDataDir(seriesDir, run, 0),
						Files.getProfilerFilename(Config.get("BATCH_PROFILER")),
						false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (batchTimestamp > 0) {
			try {
				Profiler.writeMultiple(
						singleBatchCalls,
						batchGeneratorNames.toArray(new String[0]),
						Dir.getBatchDataDir(seriesDir, run, batchTimestamp),
						Files.getProfilerFilename(Config.get("BATCH_PROFILER")),
						false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
