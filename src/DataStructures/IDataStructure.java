package DataStructures;

import java.util.Collection;

import Graph.IElement;

public interface IDataStructure extends Iterable {
	public boolean add(IElement element);

	public boolean contains(IElement element);

	public int size();
	
	public IElement getRandom();
	
	public Collection<IElement> getElements();

}
