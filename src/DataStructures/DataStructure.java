package DataStructures;

import Graph.IElement;

public abstract class DataStructure implements IDataStructure {
	protected Class<? extends IElement> dataType;
	protected int size;
	protected final int defaultSize = 10;

	public boolean add(IElement element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: " + dataType.getName()
					+ ", datatype tried to be stored: " + element.getClass().getName());
		return true;
	}
	
	public abstract void init(Class<? extends IElement> dT, int initialSize);

	public void reinitializeWithSize(int size) {
		this.init(this.dataType, size);
	}
}
