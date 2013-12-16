package dna.graph.datastructures;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType.Base;

public class DEmpty extends DataStructureReadable implements
		IEdgeListDatastructureReadable {

	public DEmpty(Class<? extends IElement> dataType) {
		super(ListType.GlobalEdgeList, Edge.class);
	}

	@Override
	public void init(Class<? extends IElement> dataType, int initialSize) {
	}

	@Override
	public boolean add(Edge element) {
		return true;
	}

	@Override
	public boolean add(IElement element) {
		return true;
	}

	@Override
	public boolean contains(IElement element) {
		// TODO Define a DEFAULT return value
		return false;
	}

	@Override
	public boolean contains(Edge element) {
		// TODO Define a DEFAULT return value
		return false;
	}

	@Override
	public boolean remove(IElement element) {
		return true;
	}

	@Override
	public boolean remove(Edge element) {
		return true;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public IElement getRandom() {
		return null;
	}

	@Override
	public Collection<IElement> getElements() {
		return Collections.<IElement>emptyList();
	}

	@Override
	public Edge get(Edge element) {
		return null;
	}

	@Override
	protected Iterator<IElement> iterator_() {
		return Collections.<IElement>emptyList().iterator();
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
		return new Complexity();
	}

}
