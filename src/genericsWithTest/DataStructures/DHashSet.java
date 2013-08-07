package genericsWithTest.DataStructures;

import java.util.HashSet;

public class DHashSet<E> extends DataStructure<E> {
	private HashSet<E> list;

	public DHashSet() {
		this.list = new HashSet<E>();
	}
	
	@Override
	public void add(E element) {
		list.add(element);
	}

	@Override
	public boolean contains(E element) {
		return list.contains(element);
	}

	@Override
	public int size() {
		return list.size();
	}

}
