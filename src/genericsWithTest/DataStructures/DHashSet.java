package genericsWithTest.DataStructures;

import genericsWithTest.Edge;
import genericsWithTest.Element;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import Utils.Rand;

public class DHashSet extends DataStructure implements IEdgeListDatastructure {
	private HashSet<Element> list;

	public DHashSet(Class<? extends Element> dT) {
		this.list = new HashSet<>();
		this.dataType = dT;
	}

	@Override
	public boolean add(Element element) {
		super.add(element);
		return element != null && this.list.add(element);
	}

	@Override
	public boolean contains(Element element) {
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

	@Override
	public Element getRandom() {
		int index = Rand.rand.nextInt(this.list.size());
		int counter = 0;
		Iterator<Element> iter = this.list.iterator();
		while (iter.hasNext()) {
			if (counter == index) {
				return iter.next();
			}
			iter.next();
			counter++;
		}
		return null;
	}


	@Override
	public Collection<Element> getElements() {
		return this.list;
	}

	@Override
	public boolean removeEdge(Edge element) {
		// TODO Auto-generated method stub
		return false;
	}

}
