package dna.profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.datastructures.GraphDataStructure;
import dna.util.Log;

public class GraphProfiler {
	private static Map<String, ProfileEntry> calls = new HashMap<>();
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

		for (Entry<String, ProfileEntry> entry : calls.entrySet()) {
			System.out.println("Count type: " + entry.getKey());
			System.out.println(entry.getValue().toString());

		}
	}

	public static void count(String mapKey, ProfilerType p) {
		if ( !active ) return;
		
		ProfileEntry innerMap = calls.get(mapKey);
		if ( innerMap == null ) {
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
}
