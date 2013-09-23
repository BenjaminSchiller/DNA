package dna.profiler;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.GraphProfiler.ProfilerType;
import dna.profiler.complexity.Complexity;

public class ProfileEntry {
	private Map<ProfilerType, Complexity> list;

	public ProfileEntry( GraphDataStructure gds) {
		list = Collections.synchronizedMap(new EnumMap<ProfilerType, Complexity>(ProfilerType.class));
		for ( ProfilerType p: ProfilerType.values()) {
			list.put(p, gds.getComplexityClass(p));
		}
	}	
	
	public Complexity get(ProfilerType p) {
		return list.get(p);
	}
	
	public void increase(ProfilerType p, int i) {
		list.get(p).increaseBy(i);
	}
	
	public void increase(ProfilerType p) {
		increase(p, 1);
	}

	public String callsAsString(String prefix) {
		StringBuilder s = new StringBuilder();
		for (ProfilerType p : ProfilerType.values()) {
			s.append(prefix + "." + p.toString() + "=" + get(p).getComplexityCounter() + "\n");
		}
		return s.toString();
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (ProfilerType p : ProfilerType.values()) {
			s.append("  Calls of type " + p.toString() + ": " + get(p) + "\n");
		}
		return s.toString();
	}
}
