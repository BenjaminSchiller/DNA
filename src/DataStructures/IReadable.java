package DataStructures;

import java.util.Collection;

import Graph.IElement;

public interface IReadable extends IDataStructure {
	/**
	 * Retrieve a random element from this data structure
	 * 
	 * @return
	 */
	public IElement getRandom();

	/**
	 * Retrieve a collection of all elements within this data structure
	 * 
	 * @return
	 */
	public Collection<IElement> getElements();
}
