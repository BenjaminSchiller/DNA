package dna.profiler;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.GraphProfiler.ProfilerType;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType;

public class ProfileEntry {
	private Map<ProfilerType, Integer> list;
	private GraphDataStructure gds;

	public ProfileEntry(GraphDataStructure gds) {
		this.list = Collections
				.synchronizedMap(new EnumMap<ProfilerType, Integer>(
						ProfilerType.class));
		this.gds = gds;
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
		StringBuilder s = new StringBuilder();
		for (ProfilerType p : ProfilerType.values()) {
			s.append(prefix + "." + p.toString() + "="
					+ get(p) + "\n");
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

	public String combinedComplexity() {
		Complexity aggregated = new Complexity();
		for (ProfilerType p : ProfilerType.values()) {
			Complexity c = gds.getComplexityClass(p);
			c.setCounter(get(p));
			aggregated = new AddedComplexity(aggregated, c);
		}
		HashMap<ComplexityType, Integer> weightedComplexityMap = aggregated
				.getWeightedComplexityMap();

		StringBuilder s = new StringBuilder();
		for (Entry<ComplexityType, Integer> elem : weightedComplexityMap
				.entrySet()) {
			s.append(elem + " -- ");
		}

		return s.toString();
	}
}
