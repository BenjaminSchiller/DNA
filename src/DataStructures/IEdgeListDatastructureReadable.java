package DataStructures;

import Graph.Edges.Edge;

public interface IEdgeListDatastructureReadable extends IEdgeListDatastructure, IReadable {
	public Edge get(Edge element);	
}
