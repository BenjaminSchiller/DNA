package genericsWithTest.DataStructures;

import java.util.Collection;

import genericsWithTest.Edge;

public interface IEdgeListDatastructure extends IDataStructure {
	public Edge get(Edge element);
	public Collection<Edge> getEdges();
}
