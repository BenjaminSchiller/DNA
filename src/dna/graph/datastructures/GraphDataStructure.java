package dna.graph.datastructures;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.tests.GlobalTestParameters;
import dna.graph.weights.IWeighted;
import dna.graph.weights.Weight;
import dna.graph.weights.Weight.WeightSelection;
import dna.profiler.ProfilerMeasurementData;
import dna.profiler.ProfilerMeasurementData.ProfilerDataType;
import dna.profiler.datatypes.ComparableEntry;
import dna.profiler.datatypes.complexity.ComplexityType.Base;
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
	private Class<? extends Weight> nodeWeightType;
	private Class<? extends Weight> edgeWeightType;
	private WeightSelection nodeWeightSelection;
	private WeightSelection edgeWeightSelection;
	private Constructor<?> lastWeightedEdgeConstructor = null;
	private Constructor<?> lastEdgeConstructor = null;

	private IEdgeListDatastructure emptyList = new DEmpty(null);

	private EnumMap<ListType, Class<? extends IDataStructure>> listTypes;
	private EnumMap<ListType, Integer> defaultListSizes;
	private EnumMap<ListType, Integer> overrideDefaultListSizes;

	private static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> allListCombinations = null;
	private static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> simpleListCombinations = null;

	private int defaultListSize = 10;

	public GraphDataStructure(
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType) {
		this(listTypes, nodeType, edgeType, null, null, null, null);
	}

	public GraphDataStructure(
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType,
			Class<? extends Weight> nodeWeightType,
			WeightSelection nodeWeightSelection,
			Class<? extends Weight> edgeWeightType,
			WeightSelection edgeWeightSelection) {

		this.listTypes = listTypes;

		this.nodeType = nodeType;
		this.edgeType = edgeType;

		this.nodeWeightType = nodeWeightType;
		this.edgeWeightType = edgeWeightType;

		this.nodeWeightSelection = nodeWeightSelection;
		this.edgeWeightSelection = edgeWeightSelection;

		init();
	}

	@SuppressWarnings("unchecked")
	public GraphDataStructure(String gdsString) {
		String splitted[] = gdsString.split(Config
				.get("DATASTRUCTURES_CLASS_DELIMITER"));
		listTypes = new EnumMap<ListType, Class<? extends IDataStructure>>(
				ListType.class);

		int legacyParsePosition = 0;
		try {
			for (String singleClassDef : splitted) {
				String innerSplitted[] = singleClassDef.split("=");
				if (innerSplitted[0].equals("edge")) {
					this.edgeType = (Class<? extends Edge>) Class
							.forName(innerSplitted[1]);
				} else if (innerSplitted[0].equals("node")) {
					this.nodeType = (Class<? extends Node>) Class
							.forName(innerSplitted[1]);
				} else if (innerSplitted[0].equals("edgeWeight")) {
					this.edgeWeightType = (Class<? extends Weight>) Class
							.forName(innerSplitted[1]);
				} else if (innerSplitted[0].equals("nodeWeight")) {
					this.nodeWeightType = (Class<? extends Weight>) Class
							.forName(innerSplitted[1]);
				} else if (innerSplitted[0].equals("edgeWeightSelection")) {
					this.edgeWeightSelection = WeightSelection
							.valueOf(innerSplitted[1]);
				} else if (innerSplitted[0].equals("nodeWeightSelection")) {
					this.nodeWeightSelection = WeightSelection
							.valueOf(innerSplitted[1]);
				} else if (ListType.hasValue(innerSplitted[0])) {
					ListType l = ListType.valueOf(innerSplitted[0]);
					listTypes.put(l, (Class<? extends IDataStructure>) Class
							.forName(innerSplitted[1]));
				} else {
					// Legacy parsing?
					legacyParse(legacyParsePosition, singleClassDef);
					legacyParsePosition++;
				}
			}
		} catch (ClassNotFoundException | ClassCastException e) {
			e.printStackTrace();
		}
		init();
	}

	@SuppressWarnings("unchecked")
	private void legacyParse(int position, String input)
			throws ClassNotFoundException {
		switch (position) {
		case 0:
			listTypes.put(ListType.GlobalNodeList,
					(Class<? extends INodeListDatastructure>) Class
							.forName(input));
			break;
		case 1:
			listTypes.put(ListType.GlobalEdgeList,
					(Class<? extends IEdgeListDatastructure>) Class
							.forName(input));
			break;
		case 2:
			listTypes.put(ListType.LocalEdgeList,
					(Class<? extends IEdgeListDatastructure>) Class
							.forName(input));
			break;
		case 3:
			this.nodeType = (Class<? extends Node>) Class.forName(input);
			break;
		case 4:
			this.edgeType = (Class<? extends Edge>) Class.forName(input);
			break;
		default:
			throw new RuntimeException("Cannot handle input " + input
					+ " at legacy position " + position);
		}
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

		if (getListClass(ListType.GlobalEdgeList, list) == DEmpty.class
				&& getListClass(ListType.LocalEdgeList, list) == DEmpty.class
				&& getListClass(ListType.LocalInEdgeList, list) == DEmpty.class
				&& getListClass(ListType.LocalOutEdgeList, list) == DEmpty.class)
			return false;

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
		this.defaultListSizes.put(ListType.GlobalEdgeList, defaultListSize);
		this.defaultListSizes.put(ListType.GlobalNodeList, defaultListSize);

		this.overrideDefaultListSizes = new EnumMap<DataStructure.ListType, Integer>(
				DataStructure.ListType.class);
	}

	public void overrideDefaultListSize(ListType listType, int defaultSize) {
		this.overrideDefaultListSizes.put(listType, defaultSize);
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

	public Class<? extends Weight> getNodeWeightType() {
		return nodeWeightType;
	}

	public Class<? extends Weight> getEdgeWeightType() {
		return edgeWeightType;
	}

	public WeightSelection getNodeWeightSelection() {
		return nodeWeightSelection;
	}

	public WeightSelection getEdgeWeightSelection() {
		return edgeWeightSelection;
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

		if (nodes > 0) {
			int estimatedMeanSize = (int) ((edges / nodes) * 1.1d);
			if (estimatedMeanSize < 1) {
				estimatedMeanSize = 1;
			}
			this.defaultListSizes
					.put(ListType.LocalEdgeList, estimatedMeanSize);
			this.defaultListSizes
					.put(ListType.LocalNodeList, estimatedMeanSize);
		}

		return new Graph(name, timestamp, this, nodes, edges);
	}

	private int getStartingSize(ListType lt) {
		if (overrideDefaultListSizes.containsKey(lt)) {
			return overrideDefaultListSizes.get(lt);
		} else if (defaultListSizes.containsKey(lt)) {
			return defaultListSizes.get(lt);
		}
		if (lt.getFallback() != null) {
			return getStartingSize(lt.getFallback());
		}
		return defaultListSize;
	}

	public static IDataStructure constructList(ListType lt,
			Class<? extends IDataStructure> sourceClass,
			Class<? extends IElement> storedDataType) {
		IDataStructure res = null;
		try {
			res = sourceClass.getConstructor(ListType.class,
					storedDataType.getClass()).newInstance(lt, storedDataType);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return res;
	}

	public IDataStructure newList(ListType listType) {
		if (Config.getBoolean("GRAPHDATASTRUCTURE_OVERRIDE_CHECKS") != true)
			this.canGDSCreateProperLists();

		Class<? extends IDataStructure> sourceClass = getListClass(listType,
				listTypes);
		Class<? extends IElement> storedDataType = listType.getStoredClass();

		if (sourceClass == DEmpty.class) {
			return emptyList;
		}
		IDataStructure res = constructList(listType, sourceClass,
				storedDataType);
		res.reinitializeWithSize(this.getStartingSize(listType));
		return res;
	}

	public Class<? extends IDataStructure> getListClass(ListType singleListType) {
		return getListClass(singleListType, listTypes);
	}

	public static Class<? extends IDataStructure> getListClass(
			ListType singleListType,
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes) {
		Class<? extends IDataStructure> sourceClass = listTypes
				.get(singleListType);
		while (sourceClass == null && singleListType.getFallback() != null) {
			singleListType = singleListType.getFallback();
			sourceClass = listTypes.get(singleListType);
		}
		return sourceClass;
	}

	public Node newNodeInstance(int index) {
		Constructor<? extends Node> c;

		if (this.createsWeightedNodes()) {
			return newWeightedNode(index);
		}

		try {
			c = nodeType.getConstructor(int.class, GraphDataStructure.class);
			return c.newInstance(index, this);
		} catch (InvocationTargetException ite) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new node instance: "
							+ ite.getTargetException().getMessage());
			rt.setStackTrace(ite.getTargetException().getStackTrace());
			throw rt;
		} catch (Exception e) {
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
		} catch (InvocationTargetException ite) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new node instance: "
							+ ite.getTargetException().getMessage());
			rt.setStackTrace(ite.getTargetException().getStackTrace());
			throw rt;
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new node instance: " + e.getMessage());
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	public Node newWeightedNode(int index) {
		Weight w = this.newNodeWeight(nodeWeightSelection);
		return this.newWeightedNode(index, w);
	}

	public Node newWeightedNode(int index, Weight weight) {
		Constructor<? extends Node> c;
		try {
			c = (Constructor<? extends Node>) nodeType.getConstructor(
					int.class, Weight.class, GraphDataStructure.class);
			return c.newInstance(index, weight, this);
		} catch (InvocationTargetException ite) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new weighted node instance: "
							+ ite.getTargetException().getMessage());
			rt.setStackTrace(ite.getTargetException().getStackTrace());
			throw rt;
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new weighted node instance: "
							+ e.getMessage());
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
		if (this.createsWeightedEdges()) {
			return this.newWeightedEdge(src, dst);
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
		} catch (InvocationTargetException ite) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance: "
							+ ite.getTargetException().getMessage());
			rt.setStackTrace(ite.getTargetException().getStackTrace());
			throw rt;
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance: " + e.getMessage());
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
		} catch (InvocationTargetException ite) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance: "
							+ ite.getTargetException().getMessage());
			rt.setStackTrace(ite.getTargetException().getStackTrace());
			throw rt;
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance: " + e.getMessage());
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	public Edge newEdgeInstance(String str, Graph graph) {
		Constructor<? extends Edge> c;
		try {
			c = edgeType.getConstructor(String.class, Graph.class);
			return c.newInstance(str, graph);
		} catch (InvocationTargetException ite) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance: "
							+ ite.getTargetException().getMessage());
			rt.setStackTrace(ite.getTargetException().getStackTrace());
			throw rt;
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new edge instance: " + e.getMessage());
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

	public Edge newWeightedEdge(Node src, Node dst) {
		Weight w = this.newEdgeWeight(edgeWeightSelection);
		return this.newWeightedEdge(src, dst, w);
	}

	public Edge newWeightedEdge(Node src, Node dst, Weight weight) {
		if (src.getClass() != dst.getClass()) {
			throw new RuntimeException(
					"Could not generate new edge instance for non-equal node classes "
							+ src.getClass() + " and " + dst.getClass());
		}

		if (this.lastWeightedEdgeConstructor != null) {
			// Try to use cached constructor, but throw it away if it is not the
			// correct one
			try {
				return (Edge) edgeType.cast(this.lastWeightedEdgeConstructor
						.newInstance(src, dst, weight));
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
				Weight.class };
		cNeeded = getConstructor(cList, cRequired);

		// Okay, check for super types if needed
		if (cNeeded == null) {
			Class<?> superType;
			superType = src.getClass().getSuperclass();
			while (cNeeded == null && Node.class.isAssignableFrom(superType)) {
				cRequired = new Class[] { superType, superType, Weight.class };
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
			return (Edge) edgeType.cast(cNeeded.newInstance(src, dst, weight));
		} catch (InvocationTargetException ite) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new weighted edge instance: "
							+ ite.getTargetException().getMessage());
			rt.setStackTrace(ite.getTargetException().getStackTrace());
			throw rt;
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new weighted edge instance: "
							+ e.getMessage());
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}

	private Weight newWeight(Class<? extends Weight> weightClass,
			WeightSelection ws) {
		Constructor<?> c;
		Weight w = null;

		if (weightClass == null) {
			throw new RuntimeException(
					"Can not generate new weight instance as weightClass is NULL");
		}

		try {
			c = weightClass.getConstructor(WeightSelection.class);
			w = (Weight) c.newInstance(ws);
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new weight instance: " + e.getMessage());
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
		return w;
	}

	private Weight newWeight(Class<? extends Weight> weightClass, String s) {
		Constructor<?> c;
		Weight w = null;

		if (weightClass == null) {
			throw new RuntimeException(
					"Can not generate new weight instance as weightClass is NULL");
		}

		/**
		 * Legacy parsing of "old" weights
		 */
		if (s.startsWith("(W)")) {
			s = s.substring(4);
		}

		try {
			c = weightClass.getConstructor(String.class);
			w = (Weight) c.newInstance(s);
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new weight instance: " + e.getMessage());
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
		return w;
	}

	public Weight newNodeWeight(WeightSelection ws) {
		return newWeight(nodeWeightType, ws);
	}

	public Weight newEdgeWeight(WeightSelection ws) {
		return newWeight(edgeWeightType, ws);
	}

	public Weight newNodeWeight(String s) {
		return newWeight(nodeWeightType, s);
	}

	public Weight newEdgeWeight(String s) {
		return newWeight(edgeWeightType, s);
	}

	public boolean createsWeightedNodes() {
		return IWeighted.class.isAssignableFrom(nodeType);
	}

	public boolean createsWeightedEdges() {
		return IWeighted.class.isAssignableFrom(edgeType);
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
		String res = getStorageDataStructures(false)
				+ Config.get("DATASTRUCTURES_CLASS_DELIMITER") + "node="
				+ nodeType.getName()
				+ Config.get("DATASTRUCTURES_CLASS_DELIMITER") + "edge="
				+ edgeType.getName();

		if (createsWeightedEdges()) {
			res += Config.get("DATASTRUCTURES_CLASS_DELIMITER") + "edgeWeight="
					+ edgeWeightType.getName();
			res += Config.get("DATASTRUCTURES_CLASS_DELIMITER")
					+ "edgeWeightSelection=" + edgeWeightSelection.name();
		}

		if (createsWeightedNodes()) {
			res += Config.get("DATASTRUCTURES_CLASS_DELIMITER") + "nodeWeight="
					+ nodeWeightType.getName();
			res += Config.get("DATASTRUCTURES_CLASS_DELIMITER")
					+ "nodeWeightSelection=" + nodeWeightSelection.name();
		}

		return res;
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

	private ComparableEntry getComplexityClass(
			Class<? extends IDataStructure> ds, Class<? extends IElement> dt,
			ProfilerDataType complexityType, AccessType at, Base b) {
		return ProfilerMeasurementData.get(complexityType, ds.getSimpleName(),
				at, dt.getSimpleName(), b);
	}

	public ComparableEntry getComplexityClass(ListType lt, AccessType at,
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

	public static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> getSimpleDatastructureCombinations() {
		if (simpleListCombinations == null)
			simpleListCombinations = combineWith(
					new EnumMap<ListType, Class<? extends IDataStructure>>(
							ListType.class), 0, 3);
		return simpleListCombinations;
	}

	public static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> getAllDatastructureCombinations() {
		if (allListCombinations == null)
			allListCombinations = combineWith(
					new EnumMap<ListType, Class<? extends IDataStructure>>(
							ListType.class), 0, ListType.values().length);
		return allListCombinations;
	}

	@SuppressWarnings("unchecked")
	private static ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> combineWith(
			EnumMap<ListType, Class<? extends IDataStructure>> inList, int i,
			int maxI) {
		ListType lt = ListType.values()[i];
		ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>> resAggregator = new ArrayList<EnumMap<ListType, Class<? extends IDataStructure>>>();
		EnumMap<ListType, Class<? extends IDataStructure>> tempInList;
		for (Class<? extends IDataStructure> clazz : GlobalTestParameters.dataStructures) {
			if (lt.getRequiredType().isAssignableFrom(clazz)) {
				tempInList = inList.clone();
				tempInList.put(lt, clazz);
				if (i == (maxI - 1)) {
					if (GraphDataStructure.validListTypesSet(tempInList))
						resAggregator.add(tempInList);
				} else {
					resAggregator.addAll(combineWith(tempInList, i + 1, maxI));
				}
			}
		}
		return resAggregator;
	}
}
