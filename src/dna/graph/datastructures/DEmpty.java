package dna.graph.datastructures;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public class DEmpty extends DataStructureReadable implements
		IEdgeListDatastructureReadable, INodeListDatastructureReadable {

	public DEmpty(ListType lt, Class<? extends IElement> dT) {
		super(lt, dT);
	}

	public DEmpty(Class<? extends IElement> dataType) {
		this(ListType.GlobalEdgeList, Edge.class);
	}

	@Override
	public void init(Class<? extends IElement> dataType, int initialSize,
			boolean firstTime) {
	}

	@Override
	public boolean add(IElement element) {
		return true;
	}

	@Override
	protected boolean add_(Node element) {
		return true;
	}

	@Override
	protected boolean add_(Edge element) {
		return true;
	}

	@Override
	public boolean contains(IElement element) {
		// TODO Define a DEFAULT return value
		return false;
	}

	@Override
	public boolean contains(Node element) {
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
	public boolean remove(Node element) {
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
		return Collections.<IElement> emptyList();
	}

	@Override
	public Edge get(int n1, int n2) {
		return null;
	}

	@Override
	public int getMaxNodeIndex() {
		return 0;
	}

	@Override
	public Node get(int index) {
		return null;
	}

	@Override
	public Edge get(Edge element) {
		return null;
	}

	@Override
	protected Iterator<IElement> iterator_() {
		return Collections.<IElement> emptyList().iterator();
	}

	@Override
	public boolean canAdd(IElement element) {
		return true;
	}

	public void prepareForGC() {
	}
}
