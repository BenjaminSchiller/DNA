package dna.profiler;

import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.Profiler.ProfilerType;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityMap;

public class ProfileEntry {
	private int[] list;

	public ProfileEntry() {
		this.list = new int[ProfilerType.values().length];
		for (int i = 0; i < list.length; i++) {
			list[i] = 0;
		}
	}

	public int get(ProfilerType p) {
		return list[p.ordinal()];
	}

	public void increase(ProfilerType p, int i) {
		list[p.ordinal()] += i;
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
			if (other != null)
				res.increase(p, other.get(p));
		}

		return res;
	}
}
