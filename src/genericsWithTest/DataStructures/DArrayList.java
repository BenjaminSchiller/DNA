package genericsWithTest.DataStructures;

import genericsWithTest.Element;
import genericsWithTest.Node;

import java.util.ArrayList;
import java.util.Collection;

import Utils.Rand;

public class DArrayList extends DataStructure implements INodeListDatastructure {
	private ArrayList<Element> list;
	private int maxNodeIndex;

	public DArrayList(Class<? extends Element> dT) {
		this.list = new ArrayList<>();
		this.dataType = dT;
		this.maxNodeIndex = -1;
	}

	@Override
	public boolean add(Element element) {
		super.add(element);
		if (this.list.contains(element) || !this.list.add(element)) {
			return false;
		}
		this.maxNodeIndex = Math.max(this.maxNodeIndex,element.getIndex());
		return true;
	}

	@Override
	public boolean contains(Element element) {
		return list.contains(element);
	}

	@Override
	public int size() {
		return list.size();
	}
	
	public Node get(int index) {
		Element n = null;

		// check node at $index
		if (this.list.size() > index) {
			n = this.list.get(index);
			if (n != null && n.getIndex() == index) {
				return (Node) n;
			}
		}

		// check nodes before $index
		if (n == null || n.getIndex() > index) {
			for (int i = Math.min(index - 1, this.list.size() - 1); i >= 0; i--) {
				Element n2 = this.list.get(i);
				if (n2 != null && n2.getIndex() == index) {
					return (Node) n2;
				}
			}
		}

		// check nodes after $index
		if (n == null || n.getIndex() < index) {
			for (int i = index + 1; i < this.list.size(); i++) {
				Element n2 = this.list.get(i);
				if (n2 != null && n2.getIndex() == index) {
					return (Node) n2;
				}
			}
		}

		return null;
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}

	@Override
	public boolean removeNode(Node element) {
		if (!this.list.remove(element)) {
			return false;
		}
		if (this.maxNodeIndex == element.getIndex()) {
			int max = -1;
			for (Element n : this.getElements()) {
				max = Math.max(n.getIndex(), max);
			}
			this.maxNodeIndex = max;
		}
		return true;
	}

	@Override
	public Element getRandom() {
		return this.list.get(Rand.rand.nextInt(this.list.size()));
	}

	@Override
	public Collection<Element> getElements() {
		return this.list;
	}

}
