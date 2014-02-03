package dna.graph.datastructures;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;

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
import dna.graph.tests.GlobalTestParameters;
import dna.graph.weights.IWeighted;
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
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;
	private Constructor<?> lastWeightedEdgeConstructor = null;
	private Constructor<?> lastEdgeConstructor = null;
	private IEdgeListDatastructure emptyList = new DEmpty(null);

	private EnumMap<ListType, Class<? extends IDataStructure>> listTypes;
	private EnumMap<ListType, Integer> defaultListSizes;

	private static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> allListCombinations = null;

	public GraphDataStructure(
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType) {

		this.listTypes = listTypes;

		this.nodeType = nodeType;
		this.edgeType = edgeType;
		init();
	}

	@SuppressWarnings("unchecked")
	public GraphDataStructure(String gdsString) {
		String splitted[] = gdsString.split(Config
				.get("DATASTRUCTURES_CLASS_DELIMITER"));
		listTypes = new EnumMap<ListType, Class<? extends IDataStructure>>(
				ListType.class);

		try {
			for (String singleClassDef : splitted) {
				String innerSplitted[] = singleClassDef.split("=");
				if (innerSplitted[0].equals("edge")) {
					this.edgeType = (Class<? extends Edge>) Class
							.forName(innerSplitted[1]);
				} else if (innerSplitted[0].equals("node")) {
					this.nodeType = (Class<? extends Node>) Class
							.forName(innerSplitted[1]);
				} else if (ListType.hasValue(innerSplitted[0])) {
					ListType l = ListType.valueOf(innerSplitted[0]);
					listTypes.put(l, (Class<? extends IDataStructure>) Class
							.forName(innerSplitted[1]));
				}
			}
		} catch (ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}
		init();
	}

	public static boolean validListTypesSet(
			EnumMap<ListType, Class<? extends IDataStructure>> list) {
		for (Entry<ListType, Class<? extends IDataStructure>> entry : list
				.entrySet()) {
			if (!entry.getKey().getRequiredType()
					.isAssignableFrom(entry.getValue())) {
				return false;
			}
		}
		return true;
	}

	private boolean canGDSCreateProperLists() {
		if (getListClass(ListType.GlobalEdgeList) == null
				&& getListClass(ListType.LocalEdgeList) == null) {
			throw new RuntimeException(
					"Either the global or local edge list must not be NULL");
		}

		if (getListClass(ListType.GlobalNodeList) == null) {
			throw new RuntimeException(
					"The GraphDataStructure cannot be initialized without a global node list");
		}

		if (getListClass(ListType.LocalNodeList) == null) {
			listTypes.put(ListType.LocalNodeList,
					(Class<? extends IDataStructure>) listTypes
							.get(ListType.GlobalNodeList));
		}

		if (!validListTypesSet(listTypes)) {
			throw new RuntimeException("Invalid set of list types");
		}
		return true;
	}

	private void init() {
		this.defaultListSizes = new EnumMap<DataStructure.ListType, Integer>(
				DataStructure.ListType.class);
		this.defaultListSizes.put(ListType.GlobalEdgeList, 10);
		this.defaultListSizes.put(ListType.GlobalNodeList, 10);
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
		if (getListClass(ListType.GlobalEdgeList) == null) {
			if (other.getListClass(ListType.GlobalEdgeList) != null)
				return false;
		} else if (!getListClass(ListType.GlobalEdgeList).equals(
				other.getListClass(ListType.GlobalEdgeList)))
			return false;
		if (getListClass(ListType.LocalEdgeList) == null) {
			if (other.getListClass(ListType.LocalEdgeList) != null)
				return false;
		} else if (!getListClass(ListType.LocalEdgeList).equals(
				other.getListClass(ListType.LocalEdgeList)))
			return false;
		if (getListClass(ListType.GlobalNodeList) == null) {
			if (other.getListClass(ListType.GlobalNodeList) != null)
				return false;
		} else if (!getListClass(ListType.GlobalNodeList).equals(
				other.getListClass(ListType.GlobalNodeList)))
			return false;
		if (nodeType == null) {
			if (other.nodeType != null)
				return false;
		} else if (!nodeType.equals(other.nodeType))
			return false;
		return true;
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
		this.canGDSCreateProperLists();
		this.defaultListSizes.put(ListType.GlobalNodeList, nodes);
		this.defaultListSizes.put(ListType.GlobalEdgeList, edges);
		return new Graph(name, timestamp, this, nodes, edges);
	}

	public IDataStructure newList(ListType listType) {
		this.canGDSCreateProperLists();

		Class<? extends IDataStructure> sourceClass = getListClass(listType);
		Class<? extends IElement> storedDataType = listType.getStoredClass();
				
		if (sourceClass == DEmpty.class) {
			return emptyList;
		}
		IDataStructure res = null;
		try {
			res = sourceClass.getConstructor(ListType.class,
					storedDataType.getClass()).newInstance(listType,
					storedDataType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		if (this.defaultListSizes.containsKey(listType)) {
			res.reinitializeWithSize(this.defaultListSizes.get(listType));
		}
		return res;
	}

	public Class<? extends IDataStructure> getListClass(ListType listType) {
		Class<? extends IDataStructure> sourceClass = listTypes.get(listType);
		if ( sourceClass == null && listType.getFallback() != null ) {
			sourceClass = listTypes.get(listType.getFallback());
		}
		return sourceClass;
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
		StringBuilder res = new StringBuilder();
		boolean first = true;
		Class<?> clazz = null;
		for (ListType lt : ListType.values()) {
			if (!first)
				res.append(Config.get("DATASTRUCTURES_CLASS_DELIMITER"));
			res.append(lt + "=");
			clazz = getListClass(lt);
			if (clazz == null)
				res.append("null");
			else
				res.append(getSimpleNames ? clazz.getSimpleName() : clazz
						.getName());
			first = false;
		}
		return res.toString();
	}

	public String getDataStructures() {
		return getStorageDataStructures(false)
				+ Config.get("DATASTRUCTURES_CLASS_DELIMITER") + "node="
				+ nodeType.getName()
				+ Config.get("DATASTRUCTURES_CLASS_DELIMITER") + "edge="
				+ edgeType.getName();
	}

	public boolean isReadable() {
		return IReadable.class
				.isAssignableFrom((Class<? extends IDataStructure>) listTypes
						.get(ListType.GlobalEdgeList))
				&& IReadable.class
						.isAssignableFrom((Class<? extends IDataStructure>) listTypes
								.get(ListType.GlobalNodeList));
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
		if (!this.isReadable(getListClass(ListType.GlobalEdgeList))) {
			System.err
					.println("Reject switching data structures, as graph edge list of type "
							+ this.getListClass(ListType.GlobalEdgeList)
							+ " cannot be converted");
			return;
		}
		if (!this.isReadable(getListClass(ListType.LocalEdgeList))) {
			System.err
					.println("Reject switching data structures, as node edge list of type "
							+ this.getListClass(ListType.LocalEdgeList)
							+ " cannot be converted");
			return;
		}
		if (!this.isReadable(getListClass(ListType.GlobalNodeList))) {
			System.err
					.println("Reject switching data structures, as node list of type "
							+ this.getListClass(ListType.GlobalNodeList)
							+ " cannot be converted");
			return;
		}

		for (ListType lt : ListType.values()) {
			if (this.getListClass(lt) != newGDS.getListClass(lt)) {
				this.listTypes.put(lt, newGDS.getListClass(lt));
				g.switchDataStructure(lt, this.newList(lt));
			}
		}
	}

	private Complexity getComplexityClass(Class<? extends IDataStructure> ds,
			Class<? extends IElement> dt, ProfilerDataType complexityType,
			AccessType at, Base b) {
		return ProfilerMeasurementData.get(complexityType, ds.getSimpleName(),
				at, dt.getSimpleName(), b);
	}

	public Complexity getComplexityClass(ListType lt, AccessType at,
			ProfilerDataType complexityType) {
		Class<? extends IDataStructure> listClass = getListClass(lt);
		Class<? extends IElement> storedElement = lt.getStoredClass();
		Base baseType = lt.getBase();
		return getComplexityClass(listClass, storedElement, complexityType, at,
				baseType);
	}

	public static EnumMap<ListType, Class<? extends IDataStructure>> getList(
			ListType l1, Class<? extends IDataStructure> c1) {
		EnumMap<ListType, Class<? extends IDataStructure>> res = new EnumMap<ListType, Class<? extends IDataStructure>>(
				ListType.class);
		res.put(l1, c1);
		return res;
	}

	public static EnumMap<ListType, Class<? extends IDataStructure>> getList(
			ListType l1, Class<? extends IDataStructure> c1, ListType l2,
			Class<? extends IDataStructure> c2) {
		EnumMap<ListType, Class<? extends IDataStructure>> res = getList(l1, c1);
		res.put(l2, c2);
		return res;
	}

	public static EnumMap<ListType, Class<? extends IDataStructure>> getList(
			ListType l1, Class<? extends IDataStructure> c1, ListType l2,
			Class<? extends IDataStructure> c2, ListType l3,
			Class<? extends IDataStructure> c3) {
		EnumMap<ListType, Class<? extends IDataStructure>> res = getList(l1,
				c1, l2, c2);
		res.put(l3, c3);
		return res;
	}

	public static EnumMap<ListType, Class<? extends IDataStructure>> getList(
			ListType l1, Class<? extends IDataStructure> c1, ListType l2,
			Class<? extends IDataStructure> c2, ListType l3,
			Class<? extends IDataStructure> c3, ListType l4,
			Class<? extends IDataStructure> c4) {
		EnumMap<ListType, Class<? extends IDataStructure>> res = getList(l1,
				c1, l2, c2, l3, c3);
		res.put(l4, c4);
		return res;
	}

	public static EnumMap<ListType, Class<? extends IDataStructure>> getList(
			ListType l1, Class<? extends IDataStructure> c1, ListType l2,
			Class<? extends IDataStructure> c2, ListType l3,
			Class<? extends IDataStructure> c3, ListType l4,
			Class<? extends IDataStructure> c4, ListType l5,
			Class<? extends IDataStructure> c5) {
		EnumMap<ListType, Class<? extends IDataStructure>> res = getList(l1,
				c1, l2, c2, l3, c3, l4, c4);
		res.put(l5, c5);
		return res;
	}

	public static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> getAllDatastructureCombinations() {
		if (allListCombinations == null)
			allListCombinations = combineWith(
					new EnumMap<ListType, Class<? extends IDataStructure>>(
							ListType.class), 0);
		return allListCombinations;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> combineWith(
			EnumMap<ListType, Class<? extends IDataStructure>> inList, int i) {
		ListType lt = ListType.values()[i];
		ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> resAggregator = new ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>>();
		EnumMap<ListType, Class<? extends IDataStructure>> tempInList;
		for (Class<? extends IDataStructure> clazz : GlobalTestParameters.dataStructures) {
			if (lt.getRequiredType().isAssignableFrom(clazz)) {
				tempInList = inList.clone();
				tempInList.put(lt, clazz);
				if (i == ListType.values().length - 1) {
					if (GraphDataStructure.validListTypesSet(tempInList))
						resAggregator.add(tempInList);
				} else {
					resAggregator.addAll(combineWith(tempInList, i + 1));
				}
			}
		}
		return resAggregator;
	}
}
