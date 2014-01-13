package dna.profiler;

import dna.graph.datastructures.GraphDataStructure;
import dna.profiler.ProfilerConstants.ProfilerType;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityMap;

public class ProfileEntry {
	private int[] list;

	public ProfileEntry() {
		this.list = new int[ProfilerConstants.ProfilerType.values().length];
		for (int i = 0; i < list.length; i++) {
			list[i] = 0;
		}
	}

	public int getCombined() {
		int res = 0;
		for (int i = 0; i < list.length; i++) {
			res += list[i];
		}
		return res;
	}

	public boolean hasAccessesOfType(ProfilerConstants.ProfilerType[] list) {
		for (ProfilerConstants.ProfilerType p : list) {
			if (get(p) != 0)
				return true;
		}
		return false;
	}

	public int get(ProfilerConstants.ProfilerType p) {
		return list[p.ordinal()];
	}

	public void increase(ProfilerConstants.ProfilerType p, int i) {
		list[p.ordinal()] += i;
	}

	public String callsAsString(String prefix) {
		StringBuilder res = new StringBuilder();

		if (prefix.length() > 0)
			prefix += ".";

		for (ProfilerConstants.ProfilerType p : ProfilerConstants.ProfilerType
				.values()) {
			res.append(prefix + p.toString() + "=" + get(p)
					+ Profiler.separator);
		}
		return res.toString();
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		for (ProfilerConstants.ProfilerType p : ProfilerConstants.ProfilerType
				.values()) {
			s.append("  Calls of type " + p.toString() + ": " + get(p)
					+ Profiler.separator);
		}
		return s.toString();
	}

	public ComplexityMap combinedComplexity(GraphDataStructure gds,
			ProfilerType[] allowedAccesses) {
		Complexity aggregated = new Complexity();
		for (ProfilerConstants.ProfilerType p : allowedAccesses) {
			Complexity c = gds.getComplexityClass(p,
					ProfilerMeasurementData.ProfilerDataType.RuntimeComplexity);
			c.setCounter(get(p));
			aggregated = new AddedComplexity(aggregated, c);
		}
		return aggregated.getWeightedComplexityMap();
	}

	public ProfileEntry add(ProfileEntry other) {
		ProfileEntry res = new ProfileEntry();

		for (ProfilerConstants.ProfilerType p : ProfilerConstants.ProfilerType
				.values()) {
			res.increase(p, this.get(p));
			if (other != null)
				res.increase(p, other.get(p));
		}

		return res;
	}
}
