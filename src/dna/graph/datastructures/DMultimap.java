package dna.graph.datastructures;

import java.util.Collection;
import java.util.Iterator;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Rand;

public abstract class DMultimap extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {

	protected Multimap<Integer, IElement> list;
	protected int maxNodeIndex;

	public DMultimap(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}

	@Override
	public void init(Class<? extends IElement> dataType, int initialSize,
			boolean firstTime) {
		this.list = ArrayListMultimap.create();
		this.maxNodeIndex = -1;
	}

	protected abstract Multimap<Integer, IElement> getInitialList(
			int initialSize, boolean firstTime);

	@Override
	public boolean add(Node element) {
		if (this.maxNodeIndex < element.getIndex()) {
			this.maxNodeIndex = element.getIndex();
		}
		return this.list.put(element.hashCode(), element);
	}

	@Override
	public boolean contains(Node element) {
		return this.list.containsEntry(element.hashCode(), element);
	}

	@Override
	public boolean remove(Node element) {
		return this.list.remove(element.hashCode(), element);
	}

	@Override
	public boolean add(IElement element) {
		return this.list.put(element.hashCode(), element);
	}

	@Override
	public boolean contains(IElement element) {
		return this.list.containsEntry(element.hashCode(), element);
	}

	@Override
	public boolean remove(IElement element) {
		return this.list.remove(element.hashCode(), element);
	}

	@Override
	public int size() {
		return this.list.size();
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
	public boolean add(Edge element) {
		return this.list.put(element.hashCode(), element);
	}

	@Override
	public boolean contains(Edge element) {
		return this.list.containsEntry(element.hashCode(), element);
	}

	@Override
	public boolean remove(Edge element) {
		return this.list.remove(element.hashCode(), element);
	}

	@Override
	public Edge get(Edge element) {
		for (IElement e : this.list.get(element.hashCode())) {
			if (e.equals(element)) {
				return (Edge) e;
			}
		}
		return null;
	}

	@Override
	public Edge get(Node n1, Node n2) {
		for (IElement e : this.list.get(Edge.getHashcode(n1, n2))) {
			if (((Edge) e).isConnectedTo(n1, n2)) {
				return (Edge) e;
			}
		}
		return null;
	}

	@Override
	public Node get(int index) {
		for (IElement e : this.list.get(index)) {
			return (Node) e;
		}
		return null;
	}

	@Override
	protected Iterator<IElement> iterator_() {
		return this.list.values().iterator();
	}

}
