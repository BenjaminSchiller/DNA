package dna.graph.datastructures;

import java.util.Iterator;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType.Base;

public class DBloomFilter extends DataStructure implements
		INodeListDatastructure, IEdgeListDatastructure {
	private BloomFilter<IElement> list;
	private int maxNodeIndex;

	public DBloomFilter(Class<? extends IElement> dT) {
		super(dT);
		this.init(dT, defaultSize);
	}

	public void init(Class<? extends IElement> dT, int initialSize) {
		this.list = BloomFilter.create(new IElementFunnel(), initialSize);
		this.maxNodeIndex = -1;
	}

	@Override
	public boolean contains(IElement element) {
		return this.list.mightContain(element);
	}

	@Override
	public boolean remove(IElement element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected Iterator<IElement> iterator_() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Edge element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Edge element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Edge element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean add(Node element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Node element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean remove(Node element) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getMaxNodeIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean add(IElement element) {
		// TODO Auto-generated method stub
		return false;
	}

	private class IElementFunnel implements Funnel<IElement> {

		@Override
		public void funnel(IElement element, PrimitiveSink into) {
			into.putString(element.getStringRepresentation());
		}

	}

	@Override
	public void printList() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return null;
	}
}
