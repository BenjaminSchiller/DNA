package DataStructures;

import java.util.Collection;

import Graph.IElement;

@SuppressWarnings("rawtypes")
public interface IDataStructure extends Iterable {
	public boolean add(IElement element);

	public boolean contains(IElement element);

	public int size();
	
	public IElement getRandom();
	
	public Collection<IElement> getElements();
	
	public void reinitializeWithSize(int size);

}
