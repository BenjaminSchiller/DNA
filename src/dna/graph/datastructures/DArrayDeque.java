package dna.graph.datastructures;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Rand;

public class DArrayDeque extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {
	private ArrayDeque<IElement> list;
	private int maxNodeIndex;

	public DArrayDeque(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	public void init(Class<? extends IElement> dT, int initialSize,
			boolean firstTime) {
		this.list = new ArrayDeque<>(initialSize);
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

	public Node get(int index) {
		for (IElement node : this.list) {
			Node n = (Node) node;
			if (n.getIndex() == index)
				return n;
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
		int listIndex = Rand.rand.nextInt(this.list.size());
		int count = 0;

		for (IElement el : this.list) {
			if (count == listIndex)
				return el;
			count++;
		}
		return list.getLast();
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
