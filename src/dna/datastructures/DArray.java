package dna.datastructures;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * Data structure to store IElements in an array
 * 
 * @author Nico
 * 
 */
public class DArray extends DataStructureReadable implements INodeListDatastructureReadable {
	private IElement[] list;
	private int count;
	private int maxNodeIndex;

	public DArray(Class<? extends IElement> dT) {
		this.init(dT, defaultSize);
	}

	public void init(Class<? extends IElement> dT, int initialSize) {
		this.dataType = dT;
		this.list = new IElement[initialSize];
		this.maxNodeIndex = -1;
	}

	public boolean add(IElement element) {
		if (element instanceof Node)
			return this.add((Node) element);
		throw new RuntimeException("Can't handle element of type " + element.getClass() + " here");
	}

	/**
	 * Adding a node needs some more treatment than just writing it, as the
	 * array might need to grow to the proper size
	 */
	public boolean add(Node element) {
		super.canAdd(element);

		if (this.list.length == 0) {
			this.list = new IElement[element.getIndex() + 1];
		}

		while (element.getIndex() >= this.list.length) {
			IElement[] newList = new IElement[this.list.length * 2];
			System.arraycopy(this.list, 0, newList, 0, this.list.length);
			this.list = newList;
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
	public boolean contains(IElement element) {
		if (element instanceof Node)
			return this.contains((Node) element);
		throw new RuntimeException("Can't handle element of type " + element.getClass() + " here");
	}

	public boolean contains(Node element) {
		return this.list.length > element.getIndex() && this.list[element.getIndex()] != null;
	}

	public boolean remove(IElement element) {
		if (element instanceof Node)
			return this.remove((Node) element);
		else
			throw new RuntimeException("Cannot remove a non-node from a node list");
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
	public int size() {
		return count;
	}

	@Override
	public IElement getRandom() {
		int index = Rand.rand.nextInt(this.list.length);
		while (this.list[index] == null) {
			index = Rand.rand.nextInt(this.list.length);
		}
		return this.list[index];
	}

	@Override
	public Collection<IElement> getElements() {
		return Arrays.asList(this.list);
	}

	@Override
	public Iterator<IElement> iterator() {
		return Arrays.asList(this.list).iterator();
	}

	@Override
	public Node get(int index) {
		return (Node) this.list[index];
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}
}
