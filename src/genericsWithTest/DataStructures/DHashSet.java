package genericsWithTest.DataStructures;

import genericsWithTest.Edge;
import genericsWithTest.IElement;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import Utils.Rand;

public class DHashSet extends DataStructure implements IEdgeListDatastructure {
	private HashSet<IElement> list;

	public DHashSet(Class<? extends IElement> dT) {
		this.list = new HashSet<>();
		this.dataType = dT;
	}

	@Override
	public boolean add(IElement element) {
		super.add(element);
		return element != null && this.list.add(element);
	}

	@Override
	public boolean contains(IElement element) {
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
	public IElement getRandom() {
		int index = Rand.rand.nextInt(this.list.size());
		int counter = 0;
		Iterator<IElement> iter = this.list.iterator();
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
	public Collection<IElement> getElements() {
		return this.list;
	}

	@Override
	public boolean removeEdge(Edge element) {
		// TODO Auto-generated method stub
		return false;
	}

}
