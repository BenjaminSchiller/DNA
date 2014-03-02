package dna.profiler.datatypes;

public abstract class ComparableEntryMap  {
	public abstract void add(ComparableEntryMap resSecond);

	public abstract int hashCode();

	public abstract boolean equals(Object obj);
}
