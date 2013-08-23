package DataStructures;

import java.util.Collection;

import Graph.IElement;

public interface IReadable extends IDataStructure {
	public boolean contains(IElement element);
	public IElement getRandom();
	public Collection<IElement> getElements();	
}
