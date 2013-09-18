package dna.profiler;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.GraphProfiler.ProfilerType;
import dna.profiler.complexity.Complexity;

public class ProfileEntry {
	private Map<ProfilerType, Complexity> list;
	private GraphDataStructure gds;

	public ProfileEntry( GraphDataStructure gds) {
		list = Collections.synchronizedMap(new EnumMap<ProfilerType, Complexity>(ProfilerType.class));
		this.gds = gds;
		for ( ProfilerType p: ProfilerType.values()) {
			list.put(p, new Complexity(0, gds.getComplexityClass(p)));
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
			increase(p, value.get(p).getComplexityCounter());
		}
	}

	public int summedComplexity() {
		int res = 0;
		for ( ProfilerType p: ProfilerType.values()) {
			res += list.get(p).getComplexityCounter();
		}
		return res;
	}
}
