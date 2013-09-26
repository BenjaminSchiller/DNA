package dna.graph.datastructures;

import dna.graph.nodes.Node;

public interface INodeListDatastructure extends IDataStructure {
	/**
	 * Add a node to this data structure
	 * 
	 * @param element
	 * @return true, if addition succeeded
	 */
	public boolean add(Node element);

	/**
	 * Check whether a node is contained in this data structure
	 * 
	 * @param element
	 * @return
	 */
	public boolean contains(Node element);

	/**
	 * Remove a node from this data structure
	 * 
	 * @param element
	 * @return true, if removal succeeded
	 */
	public boolean remove(Node element);

	/**
	 * Retrieve the highest node index within this data structure
	 * 
	 * @return
	 */
	public int getMaxNodeIndex();
}
