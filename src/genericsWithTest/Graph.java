package genericsWithTest;

import java.lang.reflect.InvocationTargetException;

import genericsWithTest.DataStructures.DataStructure;
import genericsWithTest.DataStructures.IEdgeListDatastructure;
import genericsWithTest.DataStructures.INodeListDatastructure;

public class Graph {
	public DataStructure nodes;
	public DataStructure edges;
	public Class<?> nodeEdgeListType;

	/*
	 * First parameter: type of the list that stores all nodes (needs to be accessible by index)
	 * Second parameter: type of the *global* edge list (needs to be accessible by another edge)
	 * Third parameter: local per-node edge list
	 */
	public Graph(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType) {
		try {
			this.nodes = (DataStructure) nodeListType.getConstructor().newInstance();
			this.edges = (DataStructure) graphEdgeListType.getConstructor().newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.nodeEdgeListType = nodeEdgeListType;
	}
}
