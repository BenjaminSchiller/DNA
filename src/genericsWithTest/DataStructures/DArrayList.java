package genericsWithTest.DataStructures;

import genericsWithTest.Node;

import java.util.ArrayList;

public class DArrayList extends DataStructure implements INodeListDatastructure {
	private ArrayList<Object> list;

	public DArrayList(Class<?> dT) {
		this.list = new ArrayList<>();
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
	
	public Node get(int i) {
		return (Node)list.get(i);
	}

}
