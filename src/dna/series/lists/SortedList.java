package dna.series.lists;

import java.io.IOException;
import java.util.ArrayList;

public abstract class SortedList<T> {
	public SortedList() {
		this.list = new ArrayList<T>();
	}

	public SortedList(int size) {
		this.list = new ArrayList<T>(size);
	}

	public ArrayList<T> list;

	public ArrayList<T> getList() {
		return this.list;
	}

	public T get(int index) {
		return this.list.get(index);
	}

	public void add(T item) {
		this.list.add(item);
	}

	public int size() {
		return this.list.size();
	}

	public abstract void write(String dir) throws IOException;

}
