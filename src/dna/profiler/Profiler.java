package dna.profiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.tests.GlobalTestParameters;
import dna.io.Writer;
import dna.io.filesystem.Files;
import dna.profiler.complexity.ComplexityMap;
import dna.updates.update.Update.UpdateType;
import dna.util.Config;
import dna.util.Log;

public class Profiler {
	private static Map<String, ProfileEntry> calls = new HashMap<>();
	private static boolean active = false;
	private static boolean inInitialBatch = false;
	private static GraphDataStructure gds;
	final static String separator = System.getProperty("line.separator");
	private static final int NumberOfRecommendations = 5;

	public static enum ProfilerType {
		AddNodeGlobal, AddNodeLocal, AddEdgeGlobal, AddEdgeLocal, RemoveNodeGlobal, RemoveNodeLocal, RemoveEdgeGlobal, RemoveEdgeLocal, ContainsNodeGlobal, ContainsNodeLocal, ContainsEdgeGlobal, ContainsEdgeLocal, GetNodeGlobal, GetNodeLocal, GetEdgeGlobal, GetEdgeLocal, SizeNodeGlobal, SizeNodeLocal, SizeEdgeGlobal, SizeEdgeLocal, RandomNodeGlobal, RandomEdgeGlobal, IteratorNodeGlobal, IteratorNodeLocal, IteratorEdgeGlobal, IteratorEdgeLocal
	}

	public static void activate() {
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

	public static void finish() {
		// Actions to be done after generation of stats, eg. writing them to
		// disk, printing them,...

		if (!active)
			return;

		System.out.println(getOutput(calls));
	}

	public static String getCallList(Map<String, ProfileEntry> listOfEntries) {
		return getCallList(listOfEntries, null, true);
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
	 *            If set tp false, the prefix is kept (making it possible to
	 *            filter the list by a prefix, but keep it in front of each
	 *            entry)
	 * @return
	 */
	public static String getCallList(Map<String, ProfileEntry> listOfEntries,
			String prefixFilter, boolean dismissPrefix) {
		StringBuilder res = new StringBuilder();
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			if (prefixFilter != null) {
				if (!entry.getKey().equals(prefixFilter)) {
					continue;
				} else {
					if (dismissPrefix)
						res.append(entry.getValue().callsAsString(""));
					else
						res.append(entry.getValue().callsAsString(
								entry.getKey()));

				}
			} else {
				res.append(entry.getValue().callsAsString(entry.getKey()));
			}
			res.append("# Aggr: " + entry.getValue().combinedComplexity(gds)
					+ separator);
		}
		return res.toString();
	}

	public static String getOtherComplexitiesForEntry(ProfileEntry entry) {
		GraphDataStructure tempGDS;
		TreeMap<ComplexityMap, GraphDataStructure> listOfOtherComplexities = new TreeMap<>();
		StringBuilder res = new StringBuilder();
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
					tempGDS = new GraphDataStructure(nodeListType,
							edgeListType, nodeEdgeListType, gds.getNodeType(),
							gds.getEdgeType());
					listOfOtherComplexities.put(
							entry.combinedComplexity(tempGDS), tempGDS);
				}
			}
		}

		res.append("  Recommendations: ");
		for (int i = 0; (i < NumberOfRecommendations && listOfOtherComplexities
				.size() > 0); i++) {
			Entry<ComplexityMap, GraphDataStructure> pollFirstEntry = listOfOtherComplexities
					.pollFirstEntry();
			String polledEntry = pollFirstEntry.getValue()
					.getStorageDataStructures(true)
					+ ": "
					+ pollFirstEntry.getKey();
			res.append(separator);
			res.append("   " + polledEntry);
		}

		// res.append(separator + "  Bottom list: ");
		// for (int i = 0; (i < NumberOfRecommendations &&
		// listOfOtherComplexities
		// .size() > 0); i++) {
		// Entry<ComplexityMap, GraphDataStructure> pollFirstEntry =
		// listOfOtherComplexities
		// .pollLastEntry();
		// String polledEntry = pollFirstEntry.getValue()
		// .getStorageDataStructures(true)
		// + ": "
		// + pollFirstEntry.getKey();
		// res.append(separator);
		// res.append("   " + polledEntry);
		// }

		return res.toString();
	}

	public static String getOutput(Map<String, ProfileEntry> listOfEntries) {
		StringBuilder res = new StringBuilder();
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			if (res.length() > 0)
				res.append(separator);
			res.append("Count type: " + entry.getKey() + separator);
			res.append(entry.getValue().toString());
			res.append(" Aggr: " + entry.getValue().combinedComplexity(gds)
					+ separator);
			res.append(getOtherComplexitiesForEntry(entry.getValue()));
		}
		return res.toString();
	}
	
	public static ProfileEntry entryForKey(String mapKey, boolean forceReset) {
		ProfileEntry innerMap = calls.get(mapKey);
		if (innerMap == null || forceReset) {
			innerMap = new ProfileEntry();
			calls.put(mapKey, innerMap);
		}
		return innerMap;
	}

	public static void count(String mapKey, ProfilerType p) {
		if (!active)
			return;

		ProfileEntry innerMap = entryForKey(mapKey, false);
		innerMap.increase(p);
	}

	public static int getCount(String mapKey, ProfilerType p) {
		ProfileEntry innerMap = calls.get(mapKey);
		if (innerMap == null)
			return 0;
		return innerMap.get(p);
	}

	public static void reset() {
		calls = new HashMap<>();
	}

	public static void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		w.writeln(getCallList(calls));
		w.close();

		writeUpdates(dir);
	}

	private static void writeUpdates(String dir) throws IOException {
		Writer w = new Writer(dir, Files.getProfilerFilename(Config
				.get("UPDATES_PROFILER")));

		for (UpdateType u : UpdateType.values()) {
			// Ensure that the update type is in the needed list
			entryForKey(u.toString(), false);
			w.writeln(getCallList(calls, u.toString(), false));
		}

		w.close();
	}

	public static void writeSingle(String metricName, String dir,
			String filename) throws IOException {
		// Are we still in the initial batch? Then add the specific key to the
		// end of the metric name
		if (inInitialBatch)
			metricName += Config.get("PROFILER_INITIALBATCH_KEYADDITION");

		Writer w = new Writer(dir, filename);
		w.writeln(getCallList(calls, metricName, true));
		w.close();
	}
	
	public static void writeMultiple(String[] keys, String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		for ( String singleKey: keys) {
			if (inInitialBatch)
				singleKey += Config.get("PROFILER_INITIALBATCH_KEYADDITION");
			w.writeln(getCallList(calls, singleKey, false));
		}
		w.close();
	}

	public static void aggregate(String seriesDir, String profilerFilename) {
		// TODO write an aggregator
	}

}
