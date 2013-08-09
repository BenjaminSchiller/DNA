package genericsWithTest.DataStructures;

import genericsWithTest.Element;

public abstract class DataStructure {
	protected Class dataType;

	public void add(Element element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: " + dataType.getName()
					+ ", datatype tried to be stored: " + element.getClass().getName());
	}

	public abstract boolean contains(Element element);

	public abstract int size();
}
