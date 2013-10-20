package dna.graph.datastructures;

import java.util.Iterator;

import dna.graph.IElement;

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

	protected Class<? extends IElement> dataType;
	protected final int defaultSize = 10;

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
