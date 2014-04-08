package dna.graph.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Log;
import dna.util.Rand;

/**
 * Data structure to store IElements in an array
 * 
 * @author Nico
 * 
 */
public class DArray extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {
	private IElement[] list;
	private int count;
	private int maxNodeIndex;

	public DArray(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	public void init(Class<? extends IElement> dT, int initialSize,
			boolean firstTime) {
		this.list = new IElement[initialSize];
		this.maxNodeIndex = -1;
	}

	public boolean add(IElement element) {
		if (element instanceof Node)
			return this.add((Node) element);
		if (element instanceof Edge)
			return this.add((Edge) element);
		throw new RuntimeException("Can't handle element of type "
				+ element.getClass() + " here");
	}

	/**
	 * Adding a node needs some more treatment than just writing it, as the
	 * array might need to grow to the proper size
	 */
	protected boolean add_(Node element) {
		if (this.list.length == 0) {
			this.list = new IElement[element.getIndex() + 1];
		}

		while (element.getIndex() >= this.list.length) {
			this.resize();
		}
		if (this.list[element.getIndex()] != null) {
			return false;
		}
		this.list[element.getIndex()] = element;
		this.count++;
		if (element.getIndex() > this.maxNodeIndex) {
			this.maxNodeIndex = element.getIndex();
		}
		return true;
	}

	@Override
	protected boolean add_(Edge element) {
		int addPos = 0;

		// TODO these lists need shrinking!

		if (this.count == this.list.length) {
			addPos = this.list.length;
			this.resize();
		} else {
			// Find first free position
			while (addPos < this.list.length && this.list[addPos] != null)
				addPos++;
		}
		while (addPos > this.list.length) {
			this.resize();
		}
		if (this.list[addPos] != null)
			throw new RuntimeException("Won't overwrite");
		this.list[addPos] = element;
		this.count++;
		return true;
	}

	public void resize() {
		IElement[] newList = new IElement[this.list.length * 2];
		System.arraycopy(this.list, 0, newList, 0, this.list.length);
		this.list = newList;
	}

	@Override
	public boolean contains(IElement element) {
		if (element instanceof Node)
			return this.contains((Node) element);
		if (element instanceof Edge)
			return this.contains((Edge) element);
		throw new RuntimeException("Can't handle element of type "
				+ element.getClass() + " here");
	}

	public boolean contains(Node element) {
		return this.list.length > element.getIndex()
				&& this.list[element.getIndex()] != null;
	}

	@Override
	public boolean contains(Edge element) {
		for (IElement e : this.list) {
			if (e == null)
				continue;
			if (e.equals(element))
				return true;
		}
		return false;
	}

	public boolean remove(IElement element) {
		if (element instanceof Node)
			return this.remove((Node) element);
		if (element instanceof Edge)
			return this.remove((Edge) element);
		throw new RuntimeException("Can't handle element of type "
				+ element.getClass() + " here");
	}

	/**
	 * As for adding the node, removing a node needs special work as the array
	 * might also shrink again
	 */
	public boolean remove(Node element) {
		if (this.list.length <= element.getIndex()) {
			return false;
		}
		if (this.list[element.getIndex()] == null) {
			return false;
		}
		this.list[element.getIndex()] = null;

		this.count--;

		if (element.getIndex() == this.maxNodeIndex) {
			for (int i = this.maxNodeIndex; i >= 0; i--) {
				if (this.list[i] != null) {
					this.maxNodeIndex = ((Node) this.list[i]).getIndex();
					break;
				}
				if (i == 0 && this.maxNodeIndex == element.getIndex()) {
					this.maxNodeIndex = -1;
				}
			}
		}

		if (this.list[this.list.length - 1] != null) {
			return true;
		}

		if (this.maxNodeIndex >= this.list.length / 2) {
			return true;
		}

		IElement[] nodesNew = new IElement[this.maxNodeIndex + 1];
		System.arraycopy(this.list, 0, nodesNew, 0, this.maxNodeIndex + 1);
		this.list = nodesNew;

		return true;
	}

	@Override
	public boolean remove(Edge element) {
		for (int i = 0; i < this.list.length; i++) {
			if (this.list[i] == null)
				continue;
			if (this.list[i].equals(element)) {
				this.list[i] = null;
				this.count--;
				return true;
			}
		}
		Log.debug("Cannot remove element " + element.asString()
				+ " that is not in list of size " + this.size() + " / length "
				+ this.list.length);
		return false;
	}

	@Override
	public int size() {
		return count;
	}

	@Override
	public IElement getRandom() {
		if (this.size() == 0)
			return null;

		int index = Rand.rand.nextInt(this.list.length);
		while (this.list[index] == null) {
			index = Rand.rand.nextInt(this.list.length);
		}
		return this.list[index];
	}

	@Override
	public Collection<IElement> getElements() {
		Collection<IElement> res = new ArrayList<IElement>();
		Iterator<IElement> it = this.iterator();
		while (it.hasNext())
			res.add(it.next());
		return res;
	}

	@Override
	protected Iterator<IElement> iterator_() {
		return new DArrayIterator<IElement>(this.list);
	}

	@Override
	public Node get(int index) {
		if (index > maxNodeIndex)
			return null;
		return (Node) this.list[index];
	}

	@Override
	public Edge get(Node n1, Node n2) {
		for (IElement eU : list) {
			if (eU == null)
				continue;
			Edge e = (Edge) eU;
			if (e.getN1().equals(n1) && e.getN2().equals(n2))
				return e;
		}
		return null;
	}

	@Override
	public Edge get(Edge element) {
		return get(element.getN1(), element.getN2());
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}

	private class DArrayIterator<T> implements Iterator<T> {
		private T[] list;
		private int pos = 0;

		public DArrayIterator(T[] list) {
			this.list = list;
			while (pos < list.length && list[pos] == null)
				pos++;

		}

		@Override
		public boolean hasNext() {
			return pos < list.length;
		}

		@Override
		public T next() {
			T res = list[pos];
			pos++;
			while (pos < list.length && list[pos] == null)
				pos++;
			return res;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Not allowed");
		}

	}
}
