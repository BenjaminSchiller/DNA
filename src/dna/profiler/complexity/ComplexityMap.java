package dna.profiler.complexity;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class ComplexityMap implements Comparable<ComplexityMap> {
	private HashMap<ComplexityType, Integer> map = new HashMap<>();

	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Entry<ComplexityType, Integer> elem : entrySet()) {
			if (s.length() > 0)
				s.append(" -- ");
			s.append(elem);
		}
		return s.toString();
	}

	public void add(ComplexityMap resSecond) {
		for (Entry<ComplexityType, Integer> e : resSecond.entrySet()) {
			Integer tempCounter = this.get(e.getKey());
			if (tempCounter == null)
				tempCounter = 0;
			tempCounter += e.getValue();
			this.put(e.getKey(), tempCounter);
		}
	}

	public void put(ComplexityType key, Integer tempCounter) {
		map.put(key, tempCounter);
	}

	public Integer get(ComplexityType key) {
		Integer res = map.get(key);
		if (res == null)
			res = 0;
		return res;
	}

	public Set<Entry<ComplexityType, Integer>> entrySet() {
		return map.entrySet();
	}

	@Override
	public int compareTo(ComplexityMap o) {
		for (Entry<ComplexityType, Integer> entry : o.entrySet()) {
			if (this.get(entry.getKey()) > entry.getValue())
				return 1;
		}

		return -1;
	}
}
