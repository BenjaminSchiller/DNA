package dna.profiler;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.Profiler.ProfilerType;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityMap;

public class ProfileEntry {
	private Map<ProfilerType, Integer> list;

	public ProfileEntry() {
		this.list = Collections
				.synchronizedMap(new EnumMap<ProfilerType, Integer>(
						ProfilerType.class));
		for (ProfilerType p : ProfilerType.values()) {
			list.put(p, 0);
		}
	}

	public int get(ProfilerType p) {
		return list.get(p);
	}

	public void increase(ProfilerType p, int i) {
		int old = list.get(p);
		int newValue = old + i;
		list.put(p, newValue);
	}

	public void increase(ProfilerType p) {
		increase(p, 1);
	}

	public String callsAsString(String prefix) {
		StringBuilder res = new StringBuilder();

		if (prefix.length() > 0)
			prefix += ".";

		for (ProfilerType p : ProfilerType.values()) {
			res.append(prefix + p.toString() + "=" + get(p)
					+ Profiler.separator);
		}
		return res.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (ProfilerType p : ProfilerType.values()) {
			s.append("  Calls of type " + p.toString() + ": " + get(p)
					+ Profiler.separator);
		}
		return s.toString();
	}

	public ComplexityMap combinedComplexity(GraphDataStructure gds) {
		Complexity aggregated = new Complexity();
		for (ProfilerType p : ProfilerType.values()) {
			Complexity c = gds.getComplexityClass(p);
			c.setCounter(get(p));
			aggregated = new AddedComplexity(aggregated, c);
		}
		return aggregated.getWeightedComplexityMap();
	}

	public ProfileEntry add(ProfileEntry other) {
		ProfileEntry res = new ProfileEntry();

		for (ProfilerType p : ProfilerType.values()) {
			res.increase(p, this.get(p));
			res.increase(p, other.get(p));
		}

		return res;
	}
}
