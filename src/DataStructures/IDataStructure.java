package DataStructures;

import Graph.IElement;

@SuppressWarnings("rawtypes")
public interface IDataStructure extends Iterable {
	public boolean add(IElement element);
	public boolean contains(IElement element);
	public boolean remove(IElement element);

	public int size();
		
	public void reinitializeWithSize(int size);
	
	public boolean dataEquals(IDataStructure that);
	
	public boolean canStore(Class<? extends IElement> o);
}
