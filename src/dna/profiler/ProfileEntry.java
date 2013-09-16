package dna.profiler;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import dna.profiler.GraphProfiler.ProfilerType;

public class ProfileEntry {
	private Map<ProfilerType, Integer> list;

	public ProfileEntry() {
		list = Collections.synchronizedMap(new EnumMap<ProfilerType, Integer>(ProfilerType.class));
		for ( ProfilerType p: ProfilerType.values()) {
			list.put(p, 0);
		}
	}	
	
	public int get(ProfilerType p) {
		return list.get(p);
	}
	
	public void increase(ProfilerType p, int i) {
		list.put(p, list.get(p) + i);
	}
	
	public void increase(ProfilerType p) {
		increase(p, 1);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (ProfilerType p : ProfilerType.values()) {
			s.append("  Calls of type " + p.toString() + ": " + get(p) + "\n");
		}
		return s.toString();
	}

	public void mergeWith(ProfileEntry value) {
		for (ProfilerType p: ProfilerType.values()) {
			increase(p, value.get(p));
		}
	}
}
