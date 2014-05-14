package dna.graph.datastructures;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedHashMultimap;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Rand;

public class DLinkedHashMultimap extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {

	private LinkedHashMultimap<Integer, IElement> list;
	private int maxNodeIndex;

	public DLinkedHashMultimap(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}
	
	@Override
	public void init(Class<? extends IElement> dT, int initialSize,
			boolean firstTime) {
		this.list = LinkedHashMultimap.create(initialSize, 1);
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

	protected boolean add_(Node element) {
		this.list.put(element.getIndex(), element);
		if (element.getIndex() > this.maxNodeIndex) {
			this.maxNodeIndex = element.getIndex();
		}
		return true;
	}

	@Override
	protected boolean add_(Edge element) {
		this.list.put(element.hashCode(), element);
		return true;
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
		return list.containsKey(element.getIndex());
	}

	@Override
	public boolean contains(Edge element) {
		return list.containsKey(element.hashCode());
	}
	
	@Override
	public boolean remove(IElement element) {
		if (element instanceof Node)
			return this.remove((Node) element);
		if (element instanceof Edge)
			return this.remove((Edge) element);
		throw new RuntimeException("Can't handle element of type "
				+ element.getClass() + " here");
	}

	@Override
	public boolean remove(Node element) {
		if (!this.list.remove(element.getIndex(), element)) {
			return false;
		}
		if (element.getIndex() == this.maxNodeIndex) {
			int max = this.maxNodeIndex - 1;
			while (!this.list.containsKey(max) && max >= 0) {
				max--;
			}
			this.maxNodeIndex = max;
		}
		return true;
	}

	@Override
	public boolean remove(Edge element) {
		return this.list.remove(element.hashCode(), element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public IElement getRandom() {
		int index = Rand.rand.nextInt(this.list.size());
		int counter = 0;
		for (IElement element : this.list.values()) {
			if (counter == index) {
				return element;
			}
			counter++;
		}
		return null;
	}

	@Override
	public Collection<IElement> getElements() {
		return this.list.values();
	}

	@Override
	protected Iterator<IElement> iterator_() {
		return this.list.values().iterator();
	}

	@Override
	public Node get(int index) {
		return (Node) Iterables.getFirst(this.list.get(index), null);
	}

	@Override
	public Edge get(Node n1, Node n2) {
		return (Edge) Iterables.getFirst(this.list.get(Edge.getHashcode(n1, n2)),null);
	}

	@Override
	public Edge get(Edge element) {
		return get(element.getN1(), element.getN2());
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}

	public void prepareForGC() {
		this.list = null;
	}	
}
