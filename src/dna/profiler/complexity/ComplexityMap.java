package dna.profiler.complexity;

import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class ComplexityMap implements Comparable<ComplexityMap> {
	private TreeMap<ComplexityType, Integer> map = new TreeMap<>();
	
	public ComplexityMap() {
		for (ComplexityType t: ComplexityType.getAllComplexityTypes()) {
			map.put(t, 0);
		}
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		for (Entry<ComplexityType, Integer> elem : entrySet()) {
			if (elem.getValue() == null || elem.getValue() == 0)
				continue;
			if (s.length() > 0)
				s.append(" + ");
			s.append(elem.getValue() + "*" + elem.getKey());
		}
		if (s.length() == 0)
			s.append("0");

		return s.toString();
	}

	public void add(ComplexityMap resSecond) {
		for (Entry<ComplexityType, Integer> e : resSecond.entrySet()) {
			Integer tempCounter = this.get(e.getKey());
			if (tempCounter == null)
				tempCounter = 0;
			Integer addedValue = e.getValue();
			if ( addedValue == null )
				addedValue = 0;
			tempCounter += addedValue;
			this.put(e.getKey(), tempCounter);
		}
	}

	public void put(ComplexityType key, Integer tempCounter) {
		map.put(key, tempCounter);
	}

	public Integer get(ComplexityType key) {
		return map.get(key);
	}

	public Set<Entry<ComplexityType, Integer>> entrySet() {
		return map.entrySet();
	}

	/**
	 * This should compare different complexity maps based on their counted accesses.
	 * 
	 * Returning -1 iff this < o
	 * Returning 0 iff this == o
	 * Returning 1 iff this > o
	 */
	@Override
	public int compareTo(ComplexityMap o) {
		if (this.equals(o))
			return 0;
		
		ComplexityType t;
		final TreeSet<ComplexityType> listOfComplexityTypes = ComplexityType.getAllComplexityTypes();
		while ((t = listOfComplexityTypes.pollLast()) != null) {
			Integer thisCounter = this.get(t);
			Integer thatCounter = o.get(t);
			if (thisCounter == null && thatCounter == null) {
				continue;
			}
			if (thisCounter != null && thatCounter != null
					&& thisCounter.equals(thatCounter)) {
				continue;
			}
			if (thisCounter != null && thatCounter != null) {
				return thisCounter.compareTo(thatCounter);
			}
			
			if ( thisCounter == null && thatCounter != null) {
				return -1;
			}
			
			if ( thisCounter != null && thatCounter == null) {
				return +1;
			}			
		}
		return -1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ComplexityMap other = (ComplexityMap) obj;
		if (map == null) {
			if (other.map != null) {
				return false;
			}
		} else if (!map.equals(other.map)) {
			return false;
		}
		return true;
	}
}
