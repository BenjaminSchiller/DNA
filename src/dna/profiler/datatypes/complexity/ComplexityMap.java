package dna.profiler.datatypes.complexity;

import java.text.DecimalFormat;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import dna.profiler.datatypes.ComparableEntryMap;

public class ComplexityMap extends ComparableEntryMap {
	private TreeMap<ComplexityType, Double> map = new TreeMap<>();

	public ComplexityMap() {
		for (ComplexityType t : ComplexityType.getAllComplexityTypes()) {
			map.put(t, 0d);
		}
	}

	public void add(ComparableEntryMap resSecondIn) {
		ComplexityMap resSecond = (ComplexityMap) resSecondIn;
		for (Entry<ComplexityType, Double> e : resSecond.entrySet()) {
			Double tempCounter = this.get(e.getKey());
			if (tempCounter == null)
				tempCounter = 0d;
			Double addedValue = e.getValue();
			if (addedValue == null)
				addedValue = 0d;
			tempCounter += addedValue;
			this.put(e.getKey(), tempCounter);
		}
	}

	public void put(ComplexityType key, int tempCounter) {
		put(key,(double)tempCounter);
	}
	
	public void put(ComplexityType key, Double tempCounter) {
		map.put(key, tempCounter);
	}

	public Double get(ComplexityType key) {
		return map.get(key);
	}

	public Set<Entry<ComplexityType, Double>> entrySet() {
		return map.entrySet();
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		DecimalFormat f = new DecimalFormat("###,##0");
		for (Entry<ComplexityType, Double> elem : entrySet()) {
			if (elem.getValue() == null || elem.getValue() == 0)
				continue;
			if (s.length() > 0)
				s.append(" + ");
			s.append(f.format(elem.getValue()) + "*" + elem.getKey());
		}
		if (s.length() == 0)
			s.append("0");

		return s.toString();
	}
	
	/**
	 * This should compare different complexity maps based on their counted
	 * accesses.
	 * 
	 * Returning -1 iff this < o
	 * Returning 0 iff this == o
	 * Returning 1 iff this > o
	 */
	@Override
	public int compareTo(ComparableEntryMap o) {
		if (this.equals(o))
			return 0;
		
		ComplexityType t;
		final TreeSet<ComplexityType> listOfComplexityTypes = ComplexityType
				.getAllComplexityTypes();
		while ((t = listOfComplexityTypes.pollLast()) != null) {
			Double thisCounter = this.get(t);
			Double thatCounter = ((ComplexityMap) o).get(t);
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

			if (thisCounter == null && thatCounter != null) {
				return -1;
			}

			if (thisCounter != null && thatCounter == null) {
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

	@Override
	public ComparableEntryMap clone() {
		ComplexityMap res = new ComplexityMap();
		for (ComplexityType ct : ComplexityType.getAllComplexityTypes()) {
			res.put(ct, this.get(ct));
		}
		return res;
	}

	@Override
	public void multiplyBy(double factor) {
		for (ComplexityType ct : ComplexityType.getAllComplexityTypes()) {
			put(ct, factor * get(ct));
		}
	}

}
