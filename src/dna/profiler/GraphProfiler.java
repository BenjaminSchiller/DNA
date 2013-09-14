package dna.profiler;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.datastructures.GraphDataStructure;
import dna.util.Log;

public class GraphProfiler {
	private static Map<String, Map<ProfilerType, Integer>> calls = new HashMap<>();
	private static boolean active = false;
	
	public static enum ProfilerType {
		AddNodeGlobal, AddNodeLocal, AddEdgeGlobal, AddEdgeLocal,
		RemoveNodeGlobal, RemoveNodeLocal, RemoveEdgeGlobal, RemoveEdgeLocal, ContainsNodeGlobal, ContainsNodeLocal, ContainsEdgeGlobal, ContainsEdgeLocal, SizeNodeGlobal, SizeNodeLocal, SizeEdgeGlobal, SizeEdgeLocal
	}
	
	public static void activate() {
		active = true;
	}
	
	public static boolean isActive() {
		return active;
	}
	
	public static Map<ProfilerType, Integer> initInnerMap() {
		Map res = Collections.synchronizedMap(new EnumMap<ProfilerType, Integer>(ProfilerType.class));
		for ( ProfilerType p: ProfilerType.values()) {
			res.put(p, 0);
		}
		return res;
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

		for (Entry<String, Map<ProfilerType, Integer>> entry : calls.entrySet()) {
			System.out.println("Count type: " + entry.getKey());
			Map<ProfilerType, Integer> innerMap = entry.getValue();
			for (ProfilerType p : ProfilerType.values()) {
				System.out.println("  Calls of type " + p.toString() + ": "
						+ innerMap.get(p));
			}
		}
	}

	public static void count(String mapKey, ProfilerType p) {
		if ( !active ) return;
		
		Map<ProfilerType, Integer> innerMap = calls.get(mapKey);
		if ( innerMap == null ) {
			innerMap = initInnerMap();
			calls.put(mapKey, innerMap);
		}
		innerMap.put(p, innerMap.get(p) + 1);
	}
}
