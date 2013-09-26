package dna.graph.datastructures;

import dna.graph.nodes.Node;

public interface INodeListDatastructureReadable extends INodeListDatastructure, IReadable {
	/**
	 * Retrieve a node from the data structure by its index
	 * 
	 * @param index
	 * @return
	 */
	public Node get(int index);
}
