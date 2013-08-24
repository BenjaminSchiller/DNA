package DataStructures;

import java.util.Collection;

import Graph.IElement;

public interface IReadable extends IDataStructure {
	/**
	 * Check for deep data equality with another data structure
	 * 
	 * @param that
	 * @return true, if stored data is equal
	 */
	public boolean dataEquals(IReadable that);	
	
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
