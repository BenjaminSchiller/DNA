package dna.profiler.datatypes;

import java.util.Arrays;

import dna.profiler.datatypes.complexity.ComplexityType.Base;

public class CombinedEntry extends ComparableEntry {

	private ComparableEntry[] innerEntries;
	private double[] weights;

	public CombinedEntry(ComparableEntry[] subEntries, double[] weights) {
		this.innerEntries = subEntries;
		this.weights = weights;
	}

	@Override
	public ComparableEntry clone() {
		ComparableEntry[] newEntries = Arrays
				.copyOf(innerEntries, innerEntries.length);
		double[] newWeights = Arrays.copyOf(weights, weights.length);
		return new CombinedEntry(newEntries, newWeights);
	}

	@Override
	public void setValues(int numberOfCalls, double meanListSize, Base base) {
		for (ComparableEntry e : innerEntries) {
			e.setValues(numberOfCalls, meanListSize, base);
		}
	}

	@Override
	public String getData() {
		StringBuffer res = new StringBuffer();
		for (ComparableEntry e : innerEntries) {
			res.append(e.getData() + "\n");
		}
		return res.toString();
	}

	@Override
	public int getCounter() {
		for (ComparableEntry e : innerEntries) {
			return e.getCounter();
		}
		return 0;
	}

	@Override
	public void setCounter(int counter) {
		for (ComparableEntry e : innerEntries) {
			e.setCounter(counter);
		}
	}

	@Override
	public ComparableEntryMap getMap() {
		ComparableEntryMap res = null;
		int countee = 0;
		for (ComparableEntry e : innerEntries) {
			if (res == null) {
				res = e.getMap();
				res.multiplyBy(weights[countee]);
			} else {
				ComparableEntryMap innerMap = e.getMap();
				innerMap.multiplyBy(weights[countee]);
				res.add(innerMap);
			}
			countee++;
		}
		return res;
	}

}
