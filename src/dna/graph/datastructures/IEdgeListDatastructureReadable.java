package dna.graph.datastructures;

import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public interface IEdgeListDatastructureReadable extends IEdgeListDatastructure,
		IReadable {
	/**
	 * Retrieve an edge from this data structure (use case: we know source and
	 * destination node, but want to get additional data too like weights,
	 * flows,...)
	 * 
	 * @param element
	 * @return
	 */
	public Edge get(Edge element);
	
	/**
	 * Retrieve an edge from this data structure (use case: we know source and
	 * destination node, but want to get additional data too like weights,
	 * flows,...)
	 * 
	 * @param int n1, n2
	 * @return
	 */
	public Edge get(int n1, int n2);
}
