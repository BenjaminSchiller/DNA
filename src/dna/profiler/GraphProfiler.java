package dna.profiler;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import dna.graph.datastructures.GraphDataStructure;
import dna.util.Log;

public class GraphProfiler {
	public static Map<ProfilerType, Integer> calls;
	
	public static enum ProfilerType {
		AddNodeGlobal, AddEdgeGlobal, AddEdgeLocal,
		RemoveNodeGlobal, RemoveEdgeGlobal, RemoveEdgeLocal
	}

	public static void init(GraphDataStructure gds) {
		// This is initialization of profiles
		
		Log.debug("Created new graph with gds" + gds.getDataStructures());		
		
		calls = Collections.synchronizedMap(new EnumMap<ProfilerType, Integer>(ProfilerType.class));
		for ( ProfilerType p: ProfilerType.values()) {
			calls.put(p, 0);
		}
	}

	public static void finish() {
		// Actions to be done after generation of stats, eg. writing them to
		// disk, printing them,...
		
		for (ProfilerType p: ProfilerType.values()) {
			System.out.println("Calls of type " + p.toString() + ": " + calls.get(p));
		}
	}

	public static void count(ProfilerType p) {
		calls.put(p, calls.get(p) + 1);
	}
}
