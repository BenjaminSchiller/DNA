package dna.series.lists;

import java.util.Collection;
import java.util.HashMap;

// ODO remove name
public abstract class List<T extends ListItem> {
	public List() {
		this.map = new HashMap<String, T>();
	}

	public List(int size) {
		this.map = new HashMap<String, T>(size);
	}

	protected HashMap<String, T> map;

	public Collection<String> getNames() {
		return this.map.keySet();
	}

	public Collection<T> getList() {
		return this.map.values();
	}

	public T get(String name) {
		return this.map.get(name);
	}

	public void add(T item) {
		this.map.put(item.getName(), item);
	}

	public int size() {
		return this.map.size();
	}
}
