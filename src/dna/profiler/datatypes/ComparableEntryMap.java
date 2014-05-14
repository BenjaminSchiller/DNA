package dna.profiler.datatypes;

public abstract class ComparableEntryMap implements
		Comparable<ComparableEntryMap> {
	public abstract void add(ComparableEntryMap resSecond);

	public abstract void multiplyBy(double factor);

	public abstract int hashCode();

	public abstract boolean equals(Object obj);

	public abstract ComparableEntryMap clone();
}
