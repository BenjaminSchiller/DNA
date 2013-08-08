package genericsWithTest.DataStructures;

import genericsWithTest.Edge;

import java.util.HashSet;

public class DHashSet extends DataStructure implements IEdgeListDatastructure {
	private HashSet<Object> list;

	public DHashSet(Class<?> dT) {
		this.list = new HashSet<>();
		this.dataType = dT;
	}

	@Override
	public void add(Object element) {
		super.add(element);
		list.add(element);
	}

	@Override
	public boolean contains(Object element) {
		return list.contains(element);
	}

	@Override
	public int size() {
		return list.size();
	}

	public Edge get(Edge e) {
		for (Object o : list) {
			if (o.equals(e))
				return (Edge) o;
		}
		return null;
	}

}
