package dna.graph.datastructures;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import dna.graph.Graph;
import dna.graph.IWeighted;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.Node;
import dna.io.etc.Keywords;
import dna.profiler.GraphProfiler.ProfilerType;
import dna.profiler.complexity.ComplexityClass;

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
	private Constructor<?> lastWeightedEdgeConstructor = null;
	private Constructor<?> lastEdgeConstructor = null;
	
	private INodeListDatastructure dummyNodeList;
	private IEdgeListDatastructure dummyGraphEdgeList;
	private IEdgeListDatastructure dummyNodeEdgeList;

	public GraphDataStructure(
			Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType) {
		this.nodeListType = nodeListType;
		this.graphEdgeListType = graphEdgeListType;
		this.nodeEdgeListType = nodeEdgeListType;
		this.nodeType = nodeType;
		this.edgeType = edgeType;
	}

	@SuppressWarnings("unchecked")
	public GraphDataStructure(String gdsString) {
		String splitted[] = gdsString.split(Keywords.classDelimiter);
		try {
			this.nodeListType = (Class<? extends INodeListDatastructure>) Class
					.forName(splitted[0]);
			this.graphEdgeListType = (Class<? extends IEdgeListDatastructure>) Class
					.forName(splitted[1]);
			this.nodeEdgeListType = (Class<? extends IEdgeListDatastructure>) Class
					.forName(splitted[2]);
			this.nodeType = (Class<? extends Node>) Class.forName(splitted[3]);
			this.edgeType = (Class<? extends Edge>) Class.forName(splitted[4]);
		} catch (ClassNotFoundException | ClassCastException e) {
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

	public void setNodeType(Class<? extends Node> newNodeType) {
		this.nodeType = newNodeType;
	}

	public void setEdgeType(Class<? extends Edge> edgeType) {
		this.edgeType = edgeType;
	}

	public Graph newGraphInstance(String name, long timestamp, int nodes,
			int edges) {
		return new Graph(name, timestamp, this, nodes, edges);
	}

	public INodeListDatastructure newNodeList() {
		INodeListDatastructure res = null;
		try {
			res = (INodeListDatastructure) nodeListType.getConstructor(
					nodeType.getClass()).newInstance(nodeType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		dummyNodeList = res;
		return res;
	}

	public IEdgeListDatastructure newGraphEdgeList() {
		IEdgeListDatastructure res = null;
		try {
			res = graphEdgeListType.getConstructor(edgeType.getClass())
					.newInstance(edgeType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		dummyGraphEdgeList = res;
		return res;
	}

	public IEdgeListDatastructure newNodeEdgeList() {
		IEdgeListDatastructure res = null;
		try {
			res = nodeEdgeListType.getConstructor(edgeType.getClass())
					.newInstance(edgeType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		dummyNodeEdgeList = res;
		return res;
	}

	public Node newNodeInstance(int index) {
		Constructor<? extends Node> c;
		try {
			c = nodeType.getConstructor(int.class, GraphDataStructure.class);
			return c.newInstance(index, this);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new node instance");
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	public Node newNodeInstance(String str) {
		Constructor<? extends Node> c;
		try {
			c = nodeType.getConstructor(String.class, GraphDataStructure.class);
			return c.newInstance(str, this);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new node instance");
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public IWeightedNode<?> newWeightedNode(int index, Object weight) {
		Constructor<? extends IWeighted> c;
		try {
			c = (Constructor<? extends IWeightedNode>) nodeType.getConstructor(
					int.class, weight.getClass(), GraphDataStructure.class);
			return (IWeightedNode<?>) c.newInstance(index, weight, this);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new weighted node instance");
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	public Edge newEdgeInstance(Node src, Node dst) {
		if (src.getClass() != dst.getClass()) {
			throw new RuntimeException(
					"Could not generate new edge instance for non-equal node classes "
							+ src.getClass() + " and " + dst.getClass());
		}

		if (this.lastEdgeConstructor != null) {
			// Try to use cached constructor, but throw it away if it is not the
			// correct one
			try {
				return edgeType.cast(this.lastEdgeConstructor.newInstance(src,
						dst));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| ClassCastException e) {
				this.lastEdgeConstructor = null;
			}
		}

		Constructor<?>[] cList = edgeType.getConstructors();
		Constructor<?> cNeeded = null;

		// First: search matching constructor for src.getClass and dst.getClass
		Class<?>[] cRequired = new Class[] { src.getClass(), dst.getClass() };
		cNeeded = getConstructor(cList, cRequired);

		// Okay, check for super types if needed
		if (cNeeded == null) {
			Class<?> superType;
			superType = src.getClass().getSuperclass();
			while (cNeeded == null && Node.class.isAssignableFrom(superType)) {
				cRequired = new Class[] { superType, superType };
				for (Constructor<?> c : cList) {
					if (Arrays.equals(c.getParameterTypes(), cRequired)) {
						cNeeded = c;
					}
				}
				superType = superType.getSuperclass();
			}
		}

		if (cNeeded == null) {
			throw new RuntimeException("No edge constructor for nodetype "
					+ src.getClass() + " in edge type " + edgeType + " found");
		}

		try {
			this.lastEdgeConstructor = cNeeded;
			return edgeType.cast(cNeeded.newInstance(src, dst));
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance");
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	public Edge newEdgeInstance(String str, Graph graph) {
		Constructor<? extends Edge> c;
		try {
			c = edgeType.getConstructor(String.class, Graph.class);
			return c.newInstance(str, graph);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance");
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	public Constructor<?> getConstructor(Constructor<?>[] list,
			Class<?>[] required) {
		Constructor<?> cNeeded = null;

		for (Constructor<?> c : list) {
			Class<?>[] pt = c.getParameterTypes();
			if (pt.length != required.length)
				continue;

			for (int i = 0; i < required.length; i++) {
				if (pt[i] != required[i])
					break;
				if (i == (required.length - 1))
					return c;
			}
		}
		return cNeeded;
	}

	public IWeightedEdge<?> newWeightedEdge(Node src, Node dst, Object weight) {
		if (src.getClass() != dst.getClass()) {
			throw new RuntimeException(
					"Could not generate new edge instance for non-equal node classes "
							+ src.getClass() + " and " + dst.getClass());
		}

		if (this.lastWeightedEdgeConstructor != null) {
			// Try to use cached constructor, but throw it away if it is not the
			// correct one
			try {
				return (IWeightedEdge<?>) edgeType
						.cast(this.lastWeightedEdgeConstructor.newInstance(src,
								dst, weight));
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| ClassCastException e) {
				this.lastWeightedEdgeConstructor = null;
			}
		}

		Constructor<?>[] cList = edgeType.getConstructors();
		Constructor<?> cNeeded = null;

		// First: search matching constructor for src.getClass and dst.getClass
		Class<?>[] cRequired = new Class[] { src.getClass(), dst.getClass(),
				weight.getClass() };
		cNeeded = getConstructor(cList, cRequired);

		// Okay, check for super types if needed
		if (cNeeded == null) {
			Class<?> superType;
			superType = src.getClass().getSuperclass();
			while (cNeeded == null && Node.class.isAssignableFrom(superType)) {
				cRequired = new Class[] { superType, superType,
						weight.getClass() };
				cNeeded = getConstructor(cList, cRequired);
				superType = superType.getSuperclass();
			}
		}

		if (cNeeded == null) {
			throw new RuntimeException("No edge constructor for nodetype "
					+ src.getClass() + " in edge type " + edgeType + " found");
		}

		try {
			this.lastWeightedEdgeConstructor = cNeeded;
			return (IWeightedEdge<?>) edgeType.cast(cNeeded.newInstance(src, dst,
					weight));
		} catch (SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance");
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}
	
	private Class<?> getWeightType(Class<?> in, Class<?> superInterface) {
		Class<?> weightType = null;

		// Get the type of weight
		for (Type ptU : in.getGenericInterfaces()) {
			ParameterizedType pt = (ParameterizedType) ptU;
			if (pt.getRawType() != superInterface)
				continue;
			Type[] args = pt.getActualTypeArguments();
			weightType = (Class<?>) args[0];
		}
		return weightType;
	}
	
	public Class<?> getNodeWeightType() {
		return this.getWeightType(nodeType, IWeightedNode.class);
	}
	
	public Class<?> getEdgeWeightType() {
		return this.getWeightType(edgeType, IWeightedEdge.class);
	}

	public boolean createsDirected() {
		return DirectedEdge.class.isAssignableFrom(edgeType);
	}

	public String getDataStructures() {
		return nodeListType.getName() + Keywords.classDelimiter
				+ graphEdgeListType.getName() + Keywords.classDelimiter
				+ nodeEdgeListType.getName() + Keywords.classDelimiter
				+ nodeType.getName() + Keywords.classDelimiter
				+ edgeType.getName();
	}

	public boolean isReadable() {
		return IReadable.class.isAssignableFrom(graphEdgeListType)
				&& IReadable.class.isAssignableFrom(nodeListType);
	}

	public boolean isReadable(IDataStructure list) {
		return IReadable.class.isAssignableFrom(list.getClass());
	}

	public ComplexityClass getComplexityClass(ProfilerType p) {
		if (dummyGraphEdgeList == null) {
			newGraphEdgeList();
		}
		if (dummyNodeEdgeList == null) {
			newNodeEdgeList();
		}
		if (dummyNodeList == null) {
			newNodeList();
		}

		switch (p) {
		case AddEdgeGlobal:
			return dummyGraphEdgeList.getComplexity(AccessType.Add);
		case AddEdgeLocal:
			return dummyNodeEdgeList.getComplexity(AccessType.Add);
		case AddNodeGlobal:
		case AddNodeLocal:
			return dummyNodeList.getComplexity(AccessType.Add);
		case ContainsEdgeGlobal:
			return dummyGraphEdgeList.getComplexity(AccessType.Contains);
		case ContainsEdgeLocal:
			return dummyNodeEdgeList.getComplexity(AccessType.Contains);
		case ContainsNodeGlobal:
		case ContainsNodeLocal:
			return dummyNodeList.getComplexity(AccessType.Contains);
		case RandomEdgeGlobal:
			return dummyGraphEdgeList.getComplexity(AccessType.Random);
		case RandomNodeGlobal:
			return dummyNodeList.getComplexity(AccessType.Random);
		case RemoveEdgeGlobal:
			return dummyGraphEdgeList.getComplexity(AccessType.Remove);
		case RemoveEdgeLocal:
			return dummyNodeEdgeList.getComplexity(AccessType.Remove);
		case RemoveNodeGlobal:
		case RemoveNodeLocal:
			return dummyNodeList.getComplexity(AccessType.Remove);
		case SizeEdgeGlobal:
			return dummyGraphEdgeList.getComplexity(AccessType.Size);
		case SizeEdgeLocal:
			return dummyNodeEdgeList.getComplexity(AccessType.Size);
		case SizeNodeGlobal:
		case SizeNodeLocal:
			return dummyNodeList.getComplexity(AccessType.Size);
		}
		throw new RuntimeException("Access " + p + " missing here");
	}
}
