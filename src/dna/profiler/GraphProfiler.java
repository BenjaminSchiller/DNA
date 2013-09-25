package dna.profiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IEdgeListDatastructure;
import dna.graph.datastructures.INodeListDatastructure;
import dna.graph.tests.GlobalTestParameters;
import dna.io.Writer;
import dna.util.Log;

public class GraphProfiler {
	private static Map<String, ProfileEntry> calls = new HashMap<>();
	private static boolean active = false;
	private static GraphDataStructure gds;
	final static String separator = System.getProperty("line.separator");	

	public static enum ProfilerType {
		AddNodeGlobal, AddNodeLocal, AddEdgeGlobal, AddEdgeLocal, RemoveNodeGlobal, RemoveNodeLocal, RemoveEdgeGlobal, RemoveEdgeLocal, ContainsNodeGlobal, ContainsNodeLocal, ContainsEdgeGlobal, ContainsEdgeLocal, SizeNodeGlobal, SizeNodeLocal, SizeEdgeGlobal, SizeEdgeLocal, RandomNodeGlobal, RandomEdgeGlobal
	}

	public static void activate() {
		active = true;
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
		StringBuilder res = new StringBuilder();
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			if (res.length() > 0)
				res.append(separator);
			res.append(entry.getValue().callsAsString(entry.getKey()));
			res.append("# Aggr: " + entry.getValue().combinedComplexity(gds));
		}
		return res.toString();
	}
	
	public static String getOtherComplexitiesForEntry(ProfileEntry entry) {
		GraphDataStructure tempGDS;
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
					tempGDS = new GraphDataStructure(nodeListType, edgeListType, nodeEdgeListType, gds.getNodeType(), gds.getEdgeType());
					if (res.length() > 0)
						res.append(separator);
					res.append( "  Complexity for other gds: " + entry.combinedComplexity(tempGDS));
				}
			}
		}
		return res.toString();
	}

	public static String getOutput(Map<String, ProfileEntry> listOfEntries) {
		StringBuilder res = new StringBuilder();
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			if (res.length() > 0)
				res.append(separator);
			res.append("Count type: " + entry.getKey() + separator);
			res.append(entry.getValue().toString());
			res.append(" Aggr: " + entry.getValue().combinedComplexity(gds) + separator);
			res.append(getOtherComplexitiesForEntry(entry.getValue()));
		}
		return res.toString();
	}

	public static void count(String mapKey, ProfilerType p) {
		if (!active)
			return;

		ProfileEntry innerMap = calls.get(mapKey);
		if (innerMap == null) {
			innerMap = new ProfileEntry();
			calls.put(mapKey, innerMap);
		}
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

	}

	public static void aggregate(String seriesDir, String profilerFilename) {
		// TODO write an aggregator
	}

}
