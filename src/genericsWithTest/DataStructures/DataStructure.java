package genericsWithTest.DataStructures;

import java.util.Collection;

import Graph.IElement;

public abstract class DataStructure implements IDataStructure {
	protected Class<? extends IElement> dataType;
	protected int size;

	public boolean add(IElement element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: " + dataType.getName()
					+ ", datatype tried to be stored: " + element.getClass().getName());
		return true;
	}
}
