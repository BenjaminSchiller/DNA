package DataStructures;

import Graph.Edges.Edge;

public interface IEdgeListDatastructureReadable extends IEdgeListDatastructure, IReadable {
	/**
	 * Retrieve an edge from this data structure (use case: we know source and
	 * destination node, but want to get additional data too like weights,
	 * flows,...)
	 * 
	 * @param element
	 * @return
	 */
	public Edge get(Edge element);
}
