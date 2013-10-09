package dna.graph.datastructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType;
import dna.profiler.complexity.ComplexityType.Base;
import dna.profiler.complexity.ComplexityType.Type;
import dna.util.Rand;

/**
 * Data structure to store IElements in a hashmap
 * 
 * @author Nico
 * 
 */
public class DHashMap extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {

	private HashMap<String, IElement> list;

	private int maxNodeIndex;

	public DHashMap(Class<? extends IElement> dT) {
		this.init(dT, defaultSize);
	}

	public void init(Class<? extends IElement> dT, int initialSize) {
		this.dataType = dT;
		this.list = new HashMap<String, IElement>(initialSize);
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

		if (!this.list.containsKey(Integer.toString(element.getIndex()))) {
			this.list.put(Integer.toString(element.getIndex()), element);
			if (element.getIndex() > this.maxNodeIndex) {
				this.maxNodeIndex = element.getIndex();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean add(Edge element) {
		super.canAdd(element);

		if (!this.list.containsKey(Integer.toString(element.hashCode()))) {
			this.list.put(Integer.toString(element.hashCode()), element);
			return true;
		}
		return false;
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
		return list.containsValue(element);
	}

	@Override
	public boolean contains(Edge element) {
		return list.containsKey(Integer.toString(element.hashCode()));
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
		if (this.list.remove(Integer.toString(element.getIndex())) == null) {
			return false;
		}
		if (element.getIndex() == this.maxNodeIndex) {
			int max = this.maxNodeIndex - 1;
			while (!this.list.containsKey(Integer.toString(max)) && max >= 0) {
				max--;
			}
			this.maxNodeIndex = max;
		}
		return true;
	}

	@Override
	public boolean remove(Edge element) {
		if (this.list.remove(Integer.toString(element.hashCode())) == null) {
			return false;
		}
		return true;
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
	public Iterator<IElement> iterator() {
		return this.list.values().iterator();
	}

	@Override
	public Node get(int index) {
		return (Node) this.list.get(Integer.toString(index));
	}

	@Override
	public Edge get(Edge element) {
		return (Edge) this.list.get(Integer.toString(element.hashCode()));
	}

	@Override
	public int getMaxNodeIndex() {
		return this.maxNodeIndex;
	}

	/**
	 * Get the complexity class for a specific access type
	 * 
	 * @param access
	 *            Access type
	 * @param base
	 *            Complexity base (NodeSize, EdgeSize,...)
	 * @return
	 */
	public static Complexity getComplexity(Class<? extends IElement> dt,
			AccessType access, Base base) {
		switch (access) {
		case Add:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			}
		case Contains:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Linear, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Linear, base));
			}
		case Random:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Linear, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Linear, base));
			}
		case Remove:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			}
		case Size:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			}
		case Iterator:
			return new Complexity(1, new ComplexityType(Type.Static, base));
		}
		return new Complexity(1, new ComplexityType(Type.Unknown, base));
	}
}
