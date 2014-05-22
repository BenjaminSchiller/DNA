package dna.graph.generators.zalando;

import dna.graph.datastructures.DHashTable;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.graph.weights.IntWeight;
import dna.graph.weights.Weight.WeightSelection;

public class DefaultGraphDataStructures {

	public static final GraphDataStructure CUSTOMERS = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			DoubleWeight.class, WeightSelection.One);

	public static final GraphDataStructure CUSTOMERS_ABSOLUTE = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

	public static final GraphDataStructure PRODUCTS = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			DoubleWeight.class, WeightSelection.One);

	public static final GraphDataStructure PRODUCTS_ABSOLUTE = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

	public static final GraphDataStructure CUSTOMERS_PRODUCTS = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

	public static final GraphDataStructure CUSTOMERS_BRANDS = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

	public static final GraphDataStructure CUSTOMERS_ACTIONS = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			DoubleWeight.class, WeightSelection.One);

	public static final GraphDataStructure PRODUCTS_ACTIONS = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			DoubleWeight.class, WeightSelection.One);

	public static final GraphDataStructure CUSTOMERS_CHRONOLOGY = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			DirectedNode.class, DirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

	public static final GraphDataStructure PRODUCTS_ACTIONS_CHRONOLOGY = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			DirectedNode.class, DirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

	public static final GraphDataStructure SESSIONS_CATEGORY4 = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

	public static final GraphDataStructure CUSTOMERS_CATEGORY4 = new GraphDataStructure(
			GraphDataStructure.getList(ListType.GlobalNodeList,
					DHashTable.class, ListType.GlobalEdgeList,
					DHashTable.class, ListType.LocalEdgeList, DHashTable.class),
			UndirectedNode.class, UndirectedWeightedEdge.class, null, null,
			IntWeight.class, WeightSelection.One);

}
