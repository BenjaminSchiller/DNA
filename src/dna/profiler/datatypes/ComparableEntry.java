package dna.profiler.datatypes;

import dna.profiler.datatypes.complexity.ComplexityType;

public abstract class ComparableEntry implements Cloneable {
	public abstract ComparableEntry clone();

	public abstract void setValues(int numberOfCalls, double meanListSize, ComplexityType.Base base);
	
	public abstract String getData();

	public abstract int getCounter();
	
	public abstract void setCounter(int counter);

	public abstract ComparableEntryMap getMap();
}
