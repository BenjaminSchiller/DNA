package genericsWithTest.DataStructures;

public abstract class DataStructure<E> {
	public abstract void add(E element);
	public abstract boolean contains(E element);
	public abstract int size();
}
