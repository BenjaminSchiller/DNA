package genericsWithTest.DataStructures;

import genericsWithTest.Element;

public abstract class DataStructure implements IDataStructure {
	protected Class<? extends Element> dataType;
	protected int size;

	public void add(Element element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: " + dataType.getName()
					+ ", datatype tried to be stored: " + element.getClass().getName());
	}
}
