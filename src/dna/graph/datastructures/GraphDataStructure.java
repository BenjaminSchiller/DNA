package dna.graph.datastructures;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import dna.graph.Graph;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.io.etc.Keywords;

/**
 * Container for different types of storages for everything: this holds the
 * graph type (eg. dna.graph, ReadableGraph), the storages within a graph for
 * edges and nodes, and the node type and the resulting edge type
 * 
 * @author Nico
 * 
 */
public class GraphDataStructure {
	private Class<? extends INodeListDatastructure> nodeListType;
	private Class<? extends IEdgeListDatastructure> graphEdgeListType;
	private Class<? extends IEdgeListDatastructure> nodeEdgeListType;
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;

	public GraphDataStructure(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, Class<? extends Node> nodeType) {
		this.nodeListType = nodeListType;
		this.graphEdgeListType = graphEdgeListType;
		this.nodeEdgeListType = nodeEdgeListType;
		this.setNodeType(nodeType);
	}

	@SuppressWarnings("unchecked")
	public GraphDataStructure(String gdsString) {
		String splitted[] = gdsString.split(Keywords.classDelimiter);
		try {
			this.nodeListType = (Class<? extends INodeListDatastructure>) Class.forName(splitted[0]);
			this.graphEdgeListType = (Class<? extends IEdgeListDatastructure>) Class.forName(splitted[1]);
			this.nodeEdgeListType = (Class<? extends IEdgeListDatastructure>) Class.forName(splitted[2]);
			this.nodeType = (Class<? extends Node>) Class.forName(splitted[3]);
		} catch (ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}

		try {
			this.edgeType = (Class<? extends Edge>) nodeType.getField("edgeType").get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphDataStructure other = (GraphDataStructure) obj;
		if (edgeType == null) {
			if (other.edgeType != null)
				return false;
		} else if (!edgeType.equals(other.edgeType))
			return false;
		if (graphEdgeListType == null) {
			if (other.graphEdgeListType != null)
				return false;
		} else if (!graphEdgeListType.equals(other.graphEdgeListType))
			return false;
		if (nodeEdgeListType == null) {
			if (other.nodeEdgeListType != null)
				return false;
		} else if (!nodeEdgeListType.equals(other.nodeEdgeListType))
			return false;
		if (nodeListType == null) {
			if (other.nodeListType != null)
				return false;
		} else if (!nodeListType.equals(other.nodeListType))
			return false;
		if (nodeType == null) {
			if (other.nodeType != null)
				return false;
		} else if (!nodeType.equals(other.nodeType))
			return false;
		return true;
	}

	public Class<? extends INodeListDatastructure> getNodeListType() {
		return nodeListType;
	}

	public Class<? extends IEdgeListDatastructure> getGraphEdgeListType() {
		return graphEdgeListType;
	}

	public Class<? extends IEdgeListDatastructure> getNodeEdgeListType() {
		return nodeEdgeListType;
	}

	public Class<? extends Node> getNodeType() {
		return nodeType;
	}

	public Class<? extends Edge> getEdgeType() {
		return edgeType;
	}

	@SuppressWarnings("unchecked")
	public void setNodeType(Class<? extends Node> newNodeType) {
		this.nodeType = newNodeType;

		try {
			Class<? extends Edge> eT = (Class<? extends Edge>) nodeType.getField("edgeType").get(null);
			this.setEdgeType(eT);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
		}
	}

	public void setEdgeType(Class<? extends Edge> edgeType) {
		this.edgeType = edgeType;
	}

	public Graph newGraphInstance(String name, long timestamp, int nodes, int edges) {
		return new Graph(name, timestamp, this, nodes, edges);
	}

	public INodeListDatastructure newNodeList() {
		INodeListDatastructure res = null;
		try {
			res = (INodeListDatastructure) nodeListType.getConstructor(nodeType.getClass()).newInstance(nodeType);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return res;
	}

	public IEdgeListDatastructure newGraphEdgeList() {
		IEdgeListDatastructure res = null;
		try {
			res = graphEdgeListType.getConstructor(edgeType.getClass()).newInstance(edgeType);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return res;
	}

	public IEdgeListDatastructure newNodeEdgeList() {
		IEdgeListDatastructure res = null;
		try {
			res = nodeEdgeListType.getConstructor(edgeType.getClass()).newInstance(edgeType);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return res;
	}

	public Node newNodeInstance(int index) {
		Constructor<? extends Node> c;
		try {
			c = nodeType.getConstructor(int.class, GraphDataStructure.class);
			return c.newInstance(index, this);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new node instance");
	}

	public Node newNodeInstance(String str) {
		Constructor<? extends Node> c;
		try {
			c = nodeType.getConstructor(String.class, GraphDataStructure.class);
			return c.newInstance(str, this);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new node instance");
	}

	public Edge newEdgeInstance(Node src, Node dst) {
		if (src.getClass() != dst.getClass()) {
			throw new RuntimeException("Could not generate new edge instance for non-equal node classes "
					+ src.getClass() + " and " + dst.getClass());
		}

		Constructor<?>[] cList = edgeType.getConstructors();
		Constructor<?> cNeeded = null;

		// First: search matching constructor for src.getClass and dst.getClass
		Class<?>[] cRequired = new Class[] { src.getClass(), dst.getClass() };

		for (Constructor<?> c : cList) {
			if (Arrays.equals(c.getParameterTypes(), cRequired)) {
				cNeeded = c;
			}
		}

		// Okay, check for super types
		Class<?> superType;
		superType = src.getClass().getSuperclass();
		while (Node.class.isAssignableFrom(superType) && cNeeded == null) {
			cRequired = new Class[] { superType, superType };
			for (Constructor<?> c : cList) {
				if (Arrays.equals(c.getParameterTypes(), cRequired)) {
					cNeeded = c;
				}
			}
			superType = superType.getSuperclass();
		}

		if (cNeeded == null) {
			throw new RuntimeException("No edge constructor for " + src.getClass() + " found");
		}

		try {
			return edgeType.cast(cNeeded.newInstance(src, dst));
		} catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new edge instance");
	}

	public Edge newEdgeInstance(String str, Graph graph) {
		Constructor<? extends Edge> c;
		try {
			c = edgeType.getConstructor(String.class, Graph.class);
			return c.newInstance(str, graph);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new edge instance");
	}

	public boolean createsDirected() {
		return DirectedEdge.class.isAssignableFrom(edgeType);
	}

	public String getDataStructures() {
		return nodeListType.getName() + Keywords.classDelimiter + graphEdgeListType.getName() + Keywords.classDelimiter
				+ nodeEdgeListType.getName() + Keywords.classDelimiter + nodeType.getName();
	}

	public boolean isReadable() {
		return IReadable.class.isAssignableFrom(graphEdgeListType) && IReadable.class.isAssignableFrom(nodeListType);
	}

	public boolean isReadable(IDataStructure list) {
		return IReadable.class.isAssignableFrom(list.getClass());
	}
}
