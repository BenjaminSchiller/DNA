package dna.graph.datastructures;

import dna.graph.edges.Edge;

public interface IEdgeListDatastructure extends IDataStructure {
	/**
	 * Add an edge to this data structure
	 * 
	 * @param element
	 * @return true, if addition succeeded
	 */
	public boolean add(Edge element);

	/**
	 * Check whether an edge is contained in this data structure
	 * 
	 * @param element
	 * @return
	 */
	public boolean contains(Edge element);

	/**
	 * Remove an edge from this data structure
	 * 
	 * @param element
	 * @return true, if removal succeeded
	 */
	public boolean remove(Edge element);
}
