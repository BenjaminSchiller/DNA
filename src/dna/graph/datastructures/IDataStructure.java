package dna.graph.datastructures;

import dna.graph.IElement;

/**
 * Interface to define common methods on data structures
 * 
 * @author Nico
 * 
 */

public interface IDataStructure extends Iterable<IElement> {
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
	 * Check whether an object with class elementClass can be stored within this
	 * data structure
	 * 
	 * @param elementClass
	 * @return
	 */
	public boolean canStore(Class<? extends IElement> elementClass);
	
	/**
	 * Print a string representation of this list
	 */
	public void printList();
}
