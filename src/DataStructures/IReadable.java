package DataStructures;

import java.util.Collection;

import Graph.IElement;

public interface IReadable extends IDataStructure {
	public IElement getRandom();
	public Collection<IElement> getElements();	
}
