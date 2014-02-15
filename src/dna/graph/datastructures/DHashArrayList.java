package dna.graph.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * Implementation of a HashArrayList, as proposed in Xu:
 * "CoCo: Sound and Adaptive Replacement of Java Collections", section 4.1
 * 
 * We use a HashSet internally, and not a HashMap. A HashSet is faster on
 * contains() operations, and this is the part that is to be fastened by this
 * data structure
 * 
 * @author Nico
 * 
 */
public class DHashArrayList extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {

	private ArrayList<IElement> list;
	private HashSet<IElement> set;

	private int maxNodeIndex;

	public DHashArrayList(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	public void init(Class<? extends IElement> dT, int initialSize) {
		this.list = new ArrayList<>(initialSize);
		this.set = new HashSet<>(initialSize);
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
		if (this.list.contains(element) || !this.list.add(element) || !this.set.add(element)) {
			return false;
		}
		this.maxNodeIndex = Math.max(this.maxNodeIndex, element.getIndex());
		return true;
	}

	public boolean add(Edge element) {
		super.canAdd(element);
		return !this.list.contains(element) && this.list.add(element) && this.set.add(element);
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
		return set.contains(element);
	}

	@Override
	public boolean contains(Edge element) {
		return set.contains(element);
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
		if (!this.list.remove(element) || !this.set.remove(element)) {
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
		return this.list.remove(e) && this.set.remove(e);
	}

	@Override
	public int size() {
		return list.size();
	}

	/**
	 * An array list automatically shrinks if elements are removed, so a node
	 * with index i must not be stored at position i
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

	public Edge get(Edge e) {
		for (IElement edge : this.list) {
			if (edge.equals(e)) {
				return (Edge) edge;
			}
		}
		return null;
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
