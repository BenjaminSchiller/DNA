package dna.graph.datastructures;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * Data structure to store IElements in a linked list
 * 
 * @author Nico
 * 
 */
public class DLinkedList extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {
	private LinkedList<IElement> list;
	private int maxNodeIndex;

	public DLinkedList(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	public void init(Class<? extends IElement> dT, int initialSize,
			boolean firstTime) {
		this.list = new LinkedList<IElement>();
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

	public boolean add(Node element) {
		super.canAdd(element);
		if (this.list.contains(element) || !this.list.add(element)) {
			return false;
		}
		this.maxNodeIndex = Math.max(this.maxNodeIndex, element.getIndex());
		return true;
	}

	public boolean add(Edge element) {
		super.canAdd(element);
		return !this.list.contains(element) && this.list.add(element);
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

	@Override
	public boolean contains(Node element) {
		return list.contains(element);
	}

	@Override
	public boolean contains(Edge element) {
		return list.contains(element);
	}

	@Override
	public boolean remove(IElement element) {
		if (element instanceof Node)
			return this.remove((Node) element);
		if (element instanceof Edge)
			return this.remove((Edge) element);
		else
			throw new RuntimeException(
					"Cannot remove a non-node from a node list");
	}

	@Override
	public boolean remove(Node element) {
		if (!this.list.remove(element)) {
			return false;
		}
		if (this.maxNodeIndex == element.getIndex()) {
			int max = -1;
			for (IElement n : this.getElements()) {
				max = Math.max(((Node) n).getIndex(), max);
			}
			this.maxNodeIndex = max;
		}
		return true;
	}

	public boolean remove(Edge e) {
		return this.list.remove(e);
	}

	@Override
	public int size() {
		return list.size();
	}

	/**
	 * In a linked list, a node with index i must not be stored at position i,
	 * so search deeper for it
	 */
	public Node get(int index) {
		Node n = null;

		// check node at $index
		if (this.list.size() > index) {
			n = (Node) this.list.get(index);
			if (n != null && n.getIndex() == index) {
				return n;
			}
		}

		// check nodes around $index in both directions
		Node n2;
		int i;
		for (i = index; i < this.list.size(); i++) {
			n2 = (Node) this.list.get(i);
			if (n2 != null && n2.getIndex() == index) {
				return n2;
			}
		}

		for (i = Math.min(this.list.size() - 1, index); i >= 0; i--) {
			n2 = (Node) this.list.get(i);
			if (n2 != null && n2.getIndex() == index) {
				return n2;
			}
		}

		return null;
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

	@Override
	public IElement getRandom() {
		return this.list.get(Rand.rand.nextInt(this.list.size()));
	}

	@Override
	public Collection<IElement> getElements() {
		return this.list;
	}

	@Override
	protected Iterator<IElement> iterator_() {
		return this.list.iterator();
	}
}
