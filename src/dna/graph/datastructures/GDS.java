package dna.graph.datastructures;

import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedBlueprintsEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.DirectedWeightedBlueprintsEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedBlueprintsEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.edges.UndirectedWeightedBlueprintsEdge;
import dna.graph.nodes.DirectedBlueprintsNode;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedWeightedBlueprintsNode;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedBlueprintsNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.nodes.UndirectedWeightedBlueprintsNode;
import dna.graph.nodes.UndirectedWeightedNode;
import dna.graph.weights.Weight;
import dna.graph.weights.Weight.WeightSelection;

public class GDS {
	public static Class<? extends IDataStructure> nodes = DArrayList.class;
	public static Class<? extends IDataStructure> edges = DHashMap.class;
	public static Class<? extends IDataStructure> local = DArrayList.class;

	public static GraphDataStructure directed() {
		return gds(DirectedNode.class, DirectedEdge.class);
	}

	public static GraphDataStructure directed(
			Class<? extends IDataStructure> listTypes) {
		return new GraphDataStructure(GraphDataStructure.getList(
				ListType.GlobalNodeList, listTypes, ListType.GlobalEdgeList,
				listTypes, ListType.LocalEdgeList, listTypes),
				DirectedNode.class, DirectedEdge.class);
	}

	public static GraphDataStructure undirected() {
		return gds(UndirectedNode.class, UndirectedEdge.class);
	}

	public static GraphDataStructure undirected(
			Class<? extends IDataStructure> listTypes) {
		return new GraphDataStructure(GraphDataStructure.getList(
				ListType.GlobalNodeList, listTypes, ListType.GlobalEdgeList,
				listTypes, ListType.LocalEdgeList, listTypes),
				UndirectedNode.class, UndirectedEdge.class);
	}

	public static GraphDataStructure directedV(Class<? extends Weight> nw,
			WeightSelection nws) {
		return gds(DirectedWeightedNode.class, DirectedEdge.class, nw, nws,
				null, null);
	}

	public static GraphDataStructure undirectedV(Class<? extends Weight> nw,
			WeightSelection nws) {
		return gds(UndirectedWeightedNode.class, UndirectedEdge.class, nw, nws,
				null, null);
	}

	public static GraphDataStructure directedE(Class<? extends Weight> ew,
			WeightSelection ews) {
		return gds(DirectedNode.class, DirectedWeightedEdge.class, null, null,
				ew, ews);
	}

	public static GraphDataStructure undirectedE(Class<? extends Weight> ew,
			WeightSelection ews) {
		return gds(UndirectedNode.class, UndirectedWeightedEdge.class, null,
				null, ew, ews);
	}

	public static GraphDataStructure directedVE(Class<? extends Weight> nw,
			WeightSelection nws, Class<? extends Weight> ew, WeightSelection ews) {
		return gds(DirectedWeightedNode.class, DirectedWeightedEdge.class, nw,
				nws, ew, ews);
	}

	public static GraphDataStructure undirectedVE(Class<? extends Weight> nw,
			WeightSelection nws, Class<? extends Weight> ew, WeightSelection ews) {
		return gds(UndirectedWeightedNode.class, UndirectedWeightedEdge.class,
				nw, nws, ew, ews);
	}

	public static GraphDataStructure gds(Class<? extends Node> node,
			Class<? extends Edge> edge) {
		return gds(node, edge, null, null, null, null);
	}

	public static GraphDataStructure gds(Class<? extends Node> node,
			Class<? extends Edge> edge, Class<? extends Weight> nw,
			WeightSelection nws, Class<? extends Weight> ew, WeightSelection ews) {
		return new GraphDataStructure(GraphDataStructure.getList(
				ListType.GlobalNodeList, nodes, ListType.GlobalEdgeList, edges,
				ListType.LocalEdgeList, local), node, edge, nw, nws, ew, ews);
	}

	public static GraphDataStructure directedGDB() {
		return gds(DirectedBlueprintsNode.class, DirectedBlueprintsEdge.class);
	}

	public static GraphDataStructure directedVGDB(Class<? extends Weight> nw,
			WeightSelection nws) {
		return gds(DirectedWeightedBlueprintsNode.class, DirectedBlueprintsEdge.class, nw,
				nws, null, null);
	}

	public static GraphDataStructure directedEGDB(Class<? extends Weight> ew,
			WeightSelection ews) {
		return gds(DirectedBlueprintsNode.class, DirectedWeightedBlueprintsEdge.class, null, null, ew, ews);
	}

	public static GraphDataStructure directedVEGDB(Class<? extends Weight> nw,
			WeightSelection nws, Class<? extends Weight> ew, WeightSelection ews) {
		return gds(DirectedWeightedBlueprintsNode.class, DirectedWeightedBlueprintsEdge.class,
				nw, nws, ew, ews);
	}

	public static GraphDataStructure undirectedGDB() {
		return gds(UndirectedBlueprintsNode.class, UndirectedBlueprintsEdge.class);
	}

	public static GraphDataStructure undirectedVGDB(Class<? extends Weight> nw,
			WeightSelection nws) {
		return gds(UndirectedWeightedBlueprintsNode.class, UndirectedBlueprintsEdge.class,
				nw, nws, null, null);
	}

	public static GraphDataStructure undirectedEGDB(Class<? extends Weight> ew,
			WeightSelection ews) {
		return gds(UndirectedBlueprintsNode.class, UndirectedWeightedBlueprintsEdge.class,
				null, null, ew, ews);
	}

	public static GraphDataStructure undirectedVEGDB(
			Class<? extends Weight> nw, WeightSelection nws,
			Class<? extends Weight> ew, WeightSelection ews) {
		return gds(UndirectedWeightedBlueprintsNode.class,
				UndirectedWeightedBlueprintsEdge.class, nw, nws, ew, ews);
	}

}
