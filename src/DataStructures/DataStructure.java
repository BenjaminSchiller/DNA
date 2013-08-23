package DataStructures;

import Graph.IElement;

public abstract class DataStructure implements IDataStructure {
	protected Class<? extends IElement> dataType;
	protected int size;
	protected final int defaultSize = 10;
	
	public abstract boolean add(IElement element);
	
	public boolean canAdd(IElement element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: " + dataType.getName()
					+ ", datatype tried to be stored: " + element.getClass().getName());
		return true;
	}
	
	public abstract void init(Class<? extends IElement> dT, int initialSize);

	public void reinitializeWithSize(int size) {
		this.init(this.dataType, size);
	}
	
	public boolean equals(Object that) {
		if (that == null) return false;
		if (! (that instanceof DataStructure)) {
			return false;
		}
		
		DataStructure thatCasted = (DataStructure)that;
		if (this.size() != thatCasted.size()) return false;
		if ( this.size() == 0 ) return true;		
		
		return this.dataEquals(thatCasted);
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
}
