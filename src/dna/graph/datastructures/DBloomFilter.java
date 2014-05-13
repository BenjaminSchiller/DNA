package dna.graph.datastructures;

import java.util.Iterator;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class DBloomFilter extends DataStructure implements
		INodeListDatastructure, IEdgeListDatastructure {
	private BloomFilter<IElement> list;
	private int maxNodeIndex;

	public DBloomFilter(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	public void init(Class<? extends IElement> dT, int initialSize,
			boolean firstTime) {
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
	protected boolean add_(Edge element) {
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
	protected boolean add_(Node element) {
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
			into.putUnencodedChars(element.asString());
		}

	}

	public void prepareForGC() {
		this.list = null;
	}
}
