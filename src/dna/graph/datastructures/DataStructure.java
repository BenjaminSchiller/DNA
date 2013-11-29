package dna.graph.datastructures;

import java.util.Iterator;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

/**
 * Base class for storing IElements
 * 
 * @author Nico
 * 
 */
public abstract class DataStructure implements IDataStructure {
	public enum AccessType {
		Add, Contains, Get, Random, Remove, Size, Iterator
	}

	public final Class<? extends IElement> baseDataType;
	protected final Class<? extends IElement> dataType;
	protected final int defaultSize = 10;
	
	public DataStructure(Class<? extends IElement> inputDataType) {
		dataType = inputDataType;
		if ( Node.class.isAssignableFrom(dataType)) {
			baseDataType = Node.class;
		} else if ( Edge.class.isAssignableFrom(dataType)) {
			baseDataType = Edge.class;
		} else {
			throw new RuntimeException("Can't handle element of type "
					+ dataType + " here");
		}
	}

	public void reinitializeWithSize(int reinitSize) {
		this.init(this.dataType, reinitSize);
	}

	public boolean canAdd(IElement element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: "
					+ dataType.getName() + ", datatype tried to be stored: "
					+ element.getClass().getName());
		return true;
	}

	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (!(that instanceof DataStructure)) {
			return false;
		}

		DataStructure thatCasted = (DataStructure) that;
		if (this.size() != thatCasted.size())
			return false;
		if (this.size() == 0)
			return true;

		if (this instanceof IReadable) {
			return ((IReadable) this).dataEquals((IReadable) thatCasted);
		} else {
			return true;
		}
	}

	public int getDefaultSize() {
		return this.defaultSize;
	}

	public Class<? extends IElement> getDataType() {
		return this.dataType;
	}

	@Override
	public boolean canStore(Class<? extends IElement> o) {
		return dataType.isAssignableFrom(o);
	}
	
	protected abstract Iterator<IElement> iterator_();
	
	public Iterator<IElement> iterator() {
		return this.iterator_();
	}
}
