package genericsWithTest.DataStructures;

public abstract class DataStructure {
	protected Class dataType;

	public void add(Object element) {
		if (!dataType.isInstance(element))
			throw new RuntimeException("Datatype to be stored here: " + dataType.getName()
					+ ", datatype tried to be stored: " + element.getClass().getName());
	}

	public abstract boolean contains(Object element);

	public abstract int size();
}
