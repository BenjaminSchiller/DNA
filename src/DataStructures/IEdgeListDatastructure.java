package DataStructures;

import Graph.Edges.Edge;

public interface IEdgeListDatastructure extends IDataStructure {
	public boolean add(Edge element);
	public boolean contains(Edge element);
	public boolean remove(Edge element);
}
