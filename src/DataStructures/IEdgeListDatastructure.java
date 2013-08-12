package DataStructures;

import Graph.Edge;

public interface IEdgeListDatastructure extends IDataStructure {
	public Edge get(Edge element);
	public boolean removeEdge(Edge element);
}
