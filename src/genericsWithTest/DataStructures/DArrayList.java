package genericsWithTest.DataStructures;

import genericsWithTest.Element;
import genericsWithTest.Node;

import java.util.ArrayList;

public class DArrayList extends DataStructure implements INodeListDatastructure {
	private ArrayList<Element> list;
	private int maxNodeIndex;

	public DArrayList(Class<?> dT) {
		this.list = new ArrayList<>();
		this.dataType = dT;
		this.maxNodeIndex = -1;
	}

	@Override
	public void add(Element element) {
		super.add(element);
		list.add(element);
		this.maxNodeIndex = Math.max(this.maxNodeIndex,element.getIndex());
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

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}

}
