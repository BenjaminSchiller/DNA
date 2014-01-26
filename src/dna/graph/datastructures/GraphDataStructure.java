package dna.graph.datastructures;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.DataStructure.AccessType;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.IWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.IWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.weights.IWeighted;
import dna.profiler.ProfilerConstants;
import dna.profiler.ProfilerMeasurementData;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;
import dna.profiler.complexity.Complexity;
import dna.profiler.complexity.ComplexityType.Base;
import dna.util.Config;

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
	private IEdgeListDatastructure emptyList = new DEmpty(null);
	private int defaultNodelistSize = 10;
	private int defaultGlobalEdgelistSize = 10;

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

		if (this.graphEdgeListType == null && this.nodeEdgeListType == null) {
			throw new RuntimeException(
					"Either the global or local edge list must not be NULL");
		}
	}

	@SuppressWarnings("unchecked")
	public GraphDataStructure(String gdsString) {
		String splitted[] = gdsString.split(Config
				.get("DATASTRUCTURES_CLASS_DELIMITER"));
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
		this.defaultNodelistSize = nodes;
		this.defaultGlobalEdgelistSize = edges;
		return new Graph(name, timestamp, this, nodes, edges);
	}

	public INodeListDatastructure newLocalNodeList() {
		return newNodeList(ListType.LocalNodeList);
	}

	public INodeListDatastructure newGlobalNodeList() {
		INodeListDatastructure res = newNodeList(ListType.GlobalNodeList);
		res.reinitializeWithSize(defaultNodelistSize);
		return res;
	}

	private INodeListDatastructure newNodeList(ListType listType) {
		INodeListDatastructure res = null;
		try {
			res = (INodeListDatastructure) nodeListType.getConstructor(
					ListType.class, nodeType.getClass()).newInstance(listType,
					nodeType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return res;
	}

	public IEdgeListDatastructure newGraphEdgeList() {
		if (graphEdgeListType == null) {
			return emptyList;
		}
		IEdgeListDatastructure res = null;
		try {
			res = graphEdgeListType.getConstructor(ListType.class,
					edgeType.getClass()).newInstance(ListType.GlobalEdgeList,
					edgeType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		res.reinitializeWithSize(defaultGlobalEdgelistSize);
		return res;
	}

	public IEdgeListDatastructure newNodeEdgeList() {
		if (nodeEdgeListType == null) {
			return emptyList;
		}
		Constructor<? extends IEdgeListDatastructure> c;
		try {
			c = nodeEdgeListType.getConstructor(ListType.class,
					edgeType.getClass());
			return c.newInstance(ListType.LocalEdgeList, edgeType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge list instance: "
							+ e.getMessage());
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
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
					"Could not generate new node instance: " + e.getMessage());
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

	public Edge newEdgeInstance(String str, Graph graph,
			HashMap<Integer, Node> addedNodes) {
		Constructor<? extends Edge> c;
		try {
			c = edgeType.getConstructor(String.class, Graph.class,
					HashMap.class);
			return c.newInstance(str, graph, addedNodes);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
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
			return (IWeightedEdge<?>) edgeType.cast(cNeeded.newInstance(src,
					dst, weight));
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
			return weightType;
		}
		return null;
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

	public boolean createsUndirected() {
		return UndirectedEdge.class.isAssignableFrom(edgeType);
	}

	public String getStorageDataStructures(boolean getSimpleNames) {
		if (getSimpleNames) {
			return nodeListType.getSimpleName()
					+ Config.get("DATASTRUCTURES_CLASS_DELIMITER")
					+ (graphEdgeListType == null ? "null" : graphEdgeListType
							.getSimpleName())
					+ Config.get("DATASTRUCTURES_CLASS_DELIMITER")
					+ (nodeEdgeListType == null ? "null" : nodeEdgeListType
							.getSimpleName());
		} else {
			return nodeListType.getName()
					+ Config.get("DATASTRUCTURES_CLASS_DELIMITER")
					+ (graphEdgeListType == null ? "null" : graphEdgeListType
							.getName())
					+ Config.get("DATASTRUCTURES_CLASS_DELIMITER")
					+ (nodeEdgeListType == null ? "null" : nodeEdgeListType
							.getName());
		}
	}

	public String getDataStructures() {
		return getStorageDataStructures(false)
				+ Config.get("DATASTRUCTURES_CLASS_DELIMITER")
				+ nodeType.getName()
				+ Config.get("DATASTRUCTURES_CLASS_DELIMITER")
				+ edgeType.getName();
	}

	public boolean isReadable() {
		return IReadable.class.isAssignableFrom(graphEdgeListType)
				&& IReadable.class.isAssignableFrom(nodeListType);
	}

	public boolean isReadable(IDataStructure list) {
		return isReadable(list.getClass());
	}

	public boolean isReadable(Class<? extends IDataStructure> list) {
		return IReadable.class.isAssignableFrom(list);
	}
	
	/**
	 * Switch data structures from the current setting stored here to another
	 * combination. Use the graph g as an entry point into the graph. We could
	 * also store a pointer to the graph within this object, but this currently
	 * looks more suitable.
	 * 
	 * @param newGDS
	 * @param g
	 */
	public void switchDatastructures(GraphDataStructure newGDS, Graph g) {
		if (!this.isReadable(graphEdgeListType)) {
			System.err
					.println("Reject switching data structures, as graph edge list of type "
							+ this.graphEdgeListType + " cannot be converted");
			return;
		}
		if (!this.isReadable(nodeEdgeListType)) {
			System.err
					.println("Reject switching data structures, as node edge list of type "
							+ this.nodeEdgeListType + " cannot be converted");
			return;
		}
		if (!this.isReadable(nodeListType)) {
			System.err
					.println("Reject switching data structures, as node list of type "
							+ this.nodeListType + " cannot be converted");
			return;
		}

		if (this.nodeEdgeListType != newGDS.getNodeEdgeListType()) {
			this.nodeEdgeListType = newGDS.getNodeEdgeListType();
			g.switchDataStructure(ListType.LocalEdgeList, this.newNodeEdgeList());
		}
		if (this.graphEdgeListType != newGDS.getGraphEdgeListType()) {
			this.graphEdgeListType = newGDS.getGraphEdgeListType();
			g.switchDataStructure(ListType.GlobalEdgeList, this.newGraphEdgeList());
		}
		if (this.nodeListType != newGDS.getNodeListType()) {
			this.nodeListType = newGDS.getNodeListType();
			g.switchDataStructure(ListType.LocalNodeList, this.newNodeList(ListType.LocalNodeList));
			g.switchDataStructure(ListType.GlobalNodeList, this.newNodeList(ListType.GlobalNodeList));
		}
	}

	private Complexity getComplexityClass(Class<? extends IDataStructure> ds,
			Class<? extends IElement> dt, ProfilerDataType complexityType,
			AccessType at, Base b) {
		return ProfilerMeasurementData.get(complexityType, ds.getSimpleName(),
				at, dt.getSimpleName(), b);
	}

	public Complexity getComplexityClass(ProfilerConstants.ProfilerType p,
			ProfilerDataType complexityType) {
		switch (p) {
		case InitEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Init, Base.EdgeSize);
		case InitEdgeLocal:
			return getComplexityClass(nodeEdgeListType, Edge.class,
					complexityType, AccessType.Init, Base.Degree);
		case InitNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Init, Base.NodeSize);
		case InitNodeLocal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Init, Base.Degree);		
		case AddEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Add, Base.EdgeSize);
		case AddEdgeLocal:
			return getComplexityClass(nodeEdgeListType, Edge.class,
					complexityType, AccessType.Add, Base.Degree);
		case AddNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Add, Base.NodeSize);
		case AddNodeLocal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Add, Base.Degree);
		case ContainsEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Contains, Base.EdgeSize);
		case ContainsEdgeLocal:
			return getComplexityClass(nodeEdgeListType, Edge.class,
					complexityType, AccessType.Contains, Base.Degree);
		case ContainsNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Contains, Base.NodeSize);
		case ContainsNodeLocal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Contains, Base.Degree);
		case GetNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Get, Base.NodeSize);
		case GetNodeLocal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Get, Base.Degree);
		case GetEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Get, Base.EdgeSize);
		case GetEdgeLocal:
			return getComplexityClass(nodeEdgeListType, Edge.class,
					complexityType, AccessType.Get, Base.Degree);
		case RandomEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Random, Base.EdgeSize);
		case RandomNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Random, Base.NodeSize);
		case RemoveEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Remove, Base.EdgeSize);
		case RemoveEdgeLocal:
			return getComplexityClass(nodeEdgeListType, Edge.class,
					complexityType, AccessType.Remove, Base.Degree);
		case RemoveNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Remove, Base.NodeSize);
		case RemoveNodeLocal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Remove, Base.Degree);
		case SizeEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Size, Base.EdgeSize);
		case SizeEdgeLocal:
			return getComplexityClass(nodeEdgeListType, Edge.class,
					complexityType, AccessType.Size, Base.Degree);
		case SizeNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Size, Base.NodeSize);
		case SizeNodeLocal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Size, Base.Degree);
		case IteratorNodeGlobal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Iterator, Base.NodeSize);
		case IteratorNodeLocal:
			return getComplexityClass(nodeListType, Node.class, complexityType,
					AccessType.Iterator, Base.Degree);
		case IteratorEdgeGlobal:
			return getComplexityClass(graphEdgeListType, Edge.class,
					complexityType, AccessType.Iterator, Base.EdgeSize);
		case IteratorEdgeLocal:
			return getComplexityClass(nodeEdgeListType, Edge.class,
					complexityType, AccessType.Iterator, Base.Degree);
		}
		throw new RuntimeException("Access " + p + " missing here");
	}
}
