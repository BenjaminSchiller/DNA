package dna.graph.datastructures;

import dna.graph.datastructures.DHashMap;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.Weight;
import dna.graph.weights.Weight.WeightSelection;

public class GDS {
	public static Class<? extends IDataStructure> nodes = DHashMap.class;
	public static Class<? extends IDataStructure> edges = DHashMap.class;
	public static Class<? extends IDataStructure> local = DHashMap.class;

	public static GraphDataStructure directed() {
		return gds(DirectedNode.class, DirectedEdge.class);
	}

	public static GraphDataStructure undirected() {
		return gds(UndirectedNode.class, UndirectedEdge.class);
	}

	public static GraphDataStructure directedN(Class<? extends Weight> nw,
			WeightSelection nws) {
		return gds(DirectedNode.class, DirectedWeightedEdge.class, nw, nws,
				null, null);
	}

	public static GraphDataStructure undirectedV(Class<? extends Weight> nw,
			WeightSelection nws) {
		return gds(UndirectedNode.class, UndirectedWeightedEdge.class, nw, nws,
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

}
