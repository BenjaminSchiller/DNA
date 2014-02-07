package dna.graph.datastructures;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.Rand;

/**
 * Data structure to store IElements in a hashset
 * 
 * @author Nico
 * 
 */
public class DHashSet extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {

	private int maxNodeIndex;

	private HashSet<IElement> list;

	public DHashSet(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	@Override
	public void init(Class<? extends IElement> dT, int initialSize) {
		if (Node.class.isAssignableFrom(dT)) {
			// System.out.println("Warning: DHashSet is *incredibly* slow on "
			// + "removing nodes and recalculating the new maxNodeIndex!");
		}

		this.list = new HashSet<>(initialSize);
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

	@Override
	public boolean add(Node element) {
		super.canAdd(element);
		if (this.list.contains(element))
			return false;
		if (element != null && this.list.add(element)) {
			if (element.getIndex() > this.maxNodeIndex) {
				this.maxNodeIndex = element.getIndex();
			}
			return true;
		}
		return false;
	}

	public boolean add(Edge element) {
		super.canAdd(element);
		if (this.list.contains(element))
			return false;
		return element != null && this.list.add(element);
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
					"Cannot remove a non-edge from an edge list");
	}

	@Override
	public boolean remove(Node element) {
		if (this.list.remove(element)) {
			if (element.getIndex() == this.maxNodeIndex) {
				maxNodeIndex = -1;
				for (IElement n : getElements()) {
					maxNodeIndex = Math
							.max(maxNodeIndex, ((Node) n).getIndex());
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Edge element) {
		return this.list.remove(element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Node get(int index) {
		for (Object o : list) {
			Node oCasted = (Node) o;
			if (oCasted.getIndex() == index)
				return oCasted;
		}
		return null;
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
	protected Iterator<IElement> iterator_() {
		return this.list.iterator();
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}
}
