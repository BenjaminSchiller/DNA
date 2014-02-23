package dna.profiler.datatypes;

import dna.profiler.datatypes.complexity.ComplexityType.Base;

public class AddedComparableEntry extends ComparableEntry {

	private ComparableEntry first;
	private ComparableEntry second;

	public AddedComparableEntry(ComparableEntry first, ComparableEntry second) {
		this.first = first;
		this.second = second;
	}

	@Override
	public ComparableEntry clone() {
		return new AddedComparableEntry(first.clone(), second.clone());
	}

	@Override
	public void setValues(int numberOfCalls, double meanListSize, Base base) {
		first.setValues(numberOfCalls, meanListSize, base);
		second.setValues(numberOfCalls, meanListSize, base);
	}

	public String getData() {
		return this.first.getData() + " + " + this.second.getData();
	}

	@Override
	public void multiplyFactorBy(int factorMultiplyer) {
		this.first.multiplyFactorBy(factorMultiplyer);
		this.second.multiplyFactorBy(factorMultiplyer);
	}

	@Override
	public int getCounter() {
		return this.first.getCounter() + this.second.getCounter();
	}
	
	@Override
	public void setCounter(int counter) {
		this.first.setCounter(counter);
		this.second.setCounter(counter);
	}

	@Override
	public ComparableEntryMap getMap() {
		ComparableEntryMap res = first.getMap();
		ComparableEntryMap resSecond = second.getMap();
		res.add(resSecond);
		return res;
	}

	@Override
	public ComparableEntryMap getWeightedMap() {
		ComparableEntryMap res = first.getWeightedMap();
		ComparableEntryMap resSecond = second.getWeightedMap();
		res.add(resSecond);
		return res;
	}

}
