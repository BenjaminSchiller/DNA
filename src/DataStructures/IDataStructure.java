package DataStructures;

import Graph.IElement;

/**
 * Interface to define common methods on data structures
 * 
 * @author Nico
 * 
 */

@SuppressWarnings("rawtypes")
public interface IDataStructure extends Iterable {
	/**
	 * Initialize a data structure with a default size
	 * 
	 * @param dataType
	 *            Data type to be stored here
	 * @param initialSize
	 */
	public void init(Class<? extends IElement> dataType, int initialSize);

	/**
	 * Reinitialize this data structure with a new size
	 * 
	 * @param size
	 */
	public void reinitializeWithSize(int size);

	/**
	 * Add an element to this data structure
	 * 
	 * @param element
	 * @return true, if addition succeeded
	 */
	public boolean add(IElement element);

	/**
	 * Check whether an element is contained in this data structure
	 * 
	 * @param element
	 * @return
	 */
	public boolean contains(IElement element);

	/**
	 * Remove an element from this data structure
	 * 
	 * @param element
	 * @return true, if removal succeeded
	 */
	public boolean remove(IElement element);

	/**
	 * Get the number of elements stored in this data structure
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Check for deep data equality with another data structure
	 * 
	 * @param that
	 * @return true, if stored data is equal
	 */
	public boolean dataEquals(IDataStructure that);

	/**
	 * Check whether an object with class elementClass can be stored within this
	 * data structure
	 * 
	 * @param elementClass
	 * @return
	 */
	public boolean canStore(Class<? extends IElement> elementClass);
}
