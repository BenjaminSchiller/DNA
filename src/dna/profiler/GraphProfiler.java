package dna.profiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.datastructures.GraphDataStructure;
import dna.io.Writer;
import dna.util.Log;

public class GraphProfiler {
	private static Map<String, ProfileEntry> calls = new HashMap<>();
	private static Map<String, ProfileEntry> globalCalls = new HashMap<>();
	private static boolean active = false;
	
	public static enum ProfilerType {
		AddNodeGlobal, AddNodeLocal, AddEdgeGlobal, AddEdgeLocal,
		RemoveNodeGlobal, RemoveNodeLocal, RemoveEdgeGlobal, RemoveEdgeLocal,
		ContainsNodeGlobal, ContainsNodeLocal, ContainsEdgeGlobal, ContainsEdgeLocal,
		SizeNodeGlobal, SizeNodeLocal, SizeEdgeGlobal, SizeEdgeLocal,
		RandomNodeGlobal, RandomEdgeGlobal
	}
	
	public static void activate() {
		active = true;
	}
	
	public static boolean isActive() {
		return active;
	}
		
	public static void init(GraphDataStructure gds) {
		// This is initialization of profiles
		
		if ( !active ) return;
		
		Log.debug("Created new graph with gds" + gds.getDataStructures());
	}

	public static void finish() {
		// Actions to be done after generation of stats, eg. writing them to
		// disk, printing them,...

		if (!active)
			return;

		System.out.println(getOutput(calls));
	}
	
	public static String getOutput(Map<String, ProfileEntry> listOfEntries) {
		final String separator = System.getProperty("line.separator");
		StringBuilder res = new StringBuilder();
		for (Entry<String, ProfileEntry> entry : listOfEntries.entrySet()) {
			if ( res.length() > 0 ) res.append(separator);
			res.append("Count type: " + entry.getKey() + separator);
			res.append(entry.getValue().toString());
		}
		return res.toString();
	}

	public static void count(String mapKey, ProfilerType p) {
		if ( !active ) return;
		
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
		integrateCallsToGlobal();
		calls = new HashMap<>();
	}

	private static void integrateCallsToGlobal() {
		for (Entry<String, ProfileEntry> entry : calls.entrySet()) {
			if ( !globalCalls.containsKey(entry.getKey())) {
				globalCalls.put(entry.getKey(), new ProfileEntry());
			}
			
			globalCalls.get(entry.getKey()).mergeWith(entry.getValue());
		}
	}

	public static void write(String dir, String filename) throws IOException {
		Writer w = new Writer(dir, filename);
		w.writeln(getOutput(calls));
		w.close();

	}

	public static void writeGlobal(String aggDir, String filename) throws IOException {
		Writer w = new Writer(aggDir, filename);
		w.writeln(getOutput(globalCalls));
		w.close();
	}
}
