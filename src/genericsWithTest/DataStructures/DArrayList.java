package genericsWithTest.DataStructures;

import genericsWithTest.Element;
import genericsWithTest.Node;

import java.util.ArrayList;

public class DArrayList extends DataStructure implements INodeListDatastructure {
	private ArrayList<Element> list;

	public DArrayList(Class<?> dT) {
		this.list = new ArrayList<>();
		this.dataType = dT;
	}

	@Override
	public void add(Element element) {
		super.add(element);
		list.add(element);
	}

	@Override
	public boolean contains(Element element) {
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
