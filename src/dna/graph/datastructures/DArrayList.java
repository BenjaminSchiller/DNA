package dna.graph.datastructures;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.profiler.complexity.AddedComplexity;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType;
import dna.profiler.complexity.ComplexityType.Base;
import dna.profiler.complexity.ComplexityType.Type;
import dna.util.Rand;

/**
 * Data structure to store IElements in an arraylist
 * 
 * @author Nico
 * 
 */
public class DArrayList extends DataStructureReadable implements
		INodeListDatastructureReadable, IEdgeListDatastructureReadable {
	private ArrayList<IElement> list;
	private int maxNodeIndex;

	public DArrayList(Class<? extends IElement> dT) {
		super(dT);
		this.init(dT, defaultSize);
	}

	public void init(Class<? extends IElement> dT, int initialSize) {
		this.list = new ArrayList<>(initialSize);
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

		// check nodes before $index
		if (n == null || n.getIndex() > index) {
			for (int i = Math.min(index - 1, this.list.size() - 1); i >= 0; i--) {
				Node n2 = (Node) this.list.get(i);
				if (n2 != null && n2.getIndex() == index) {
					return n2;
				}
			}
		}

		// check nodes after $index
		if (n == null || n.getIndex() < index) {
			for (int i = index + 1; i < this.list.size(); i++) {
				Node n2 = (Node) this.list.get(i);
				if (n2 != null && n2.getIndex() == index) {
					return n2;
				}
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
				return new AddedComplexity(getComplexity(dt,
						AccessType.Contains, base), new Complexity(1,
						new ComplexityType(Type.Static, base)));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new AddedComplexity(getComplexity(dt,
						AccessType.Contains, base), new Complexity(1,
						new ComplexityType(Type.Static, base)));
			}
		case Contains:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Linear, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Linear, base));
			}
		case Get:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Linear, base));
			}
		case Random:
			if (Node.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
			} else if (Edge.class.isAssignableFrom(dt)) {
				return new Complexity(1, new ComplexityType(Type.Static, base));
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
