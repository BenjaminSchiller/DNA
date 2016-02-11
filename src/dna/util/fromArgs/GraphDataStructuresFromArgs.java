package dna.util.fromArgs;

import java.util.EnumMap;

import dna.graph.datastructures.DArray;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedWeightedNode;
import dna.graph.nodes.UndirectedNode;
import dna.graph.nodes.UndirectedWeightedNode;
import dna.graph.weights.Weight.WeightSelection;

public class GraphDataStructuresFromArgs {
	public static enum GdsType {
		Directed, DirectedV, DirectedE, DirectedVE, Undirected, UndirectedV, UndirectedE, UndirectedVE
	}

	public static GraphDataStructure parse(GdsType gdsType, String... args) {
		Class<? extends IDataStructure> GlobalNodeList = DArray.class;
		Class<? extends IDataStructure> GlobalEdgeList = DArray.class;
		Class<? extends IDataStructure> LocalEdgeList = DArray.class;

		EnumMap<ListType, Class<? extends IDataStructure>> listTypes = GraphDataStructure
				.getList(ListType.GlobalNodeList, GlobalNodeList,
						ListType.GlobalEdgeList, GlobalEdgeList,
						ListType.LocalEdgeList, LocalEdgeList);

		switch (gdsType) {
		case Directed:
			return new GraphDataStructure(listTypes, DirectedNode.class,
					DirectedEdge.class);
		case DirectedE:
			return new GraphDataStructure(listTypes, DirectedNode.class,
					DirectedWeightedEdge.class, null, null,
					WeightFromArgs.parse(args[0]),
					WeightSelection.valueOf(args[1]));
		case DirectedV:
			return new GraphDataStructure(listTypes,
					DirectedWeightedNode.class, DirectedEdge.class,
					WeightFromArgs.parse(args[0]),
					WeightSelection.valueOf(args[1]), null, null);
		case DirectedVE:
			return new GraphDataStructure(listTypes,
					DirectedWeightedNode.class, DirectedWeightedEdge.class,
					WeightFromArgs.parse(args[0]),
					WeightSelection.valueOf(args[1]),
					WeightFromArgs.parse(args[2]),
					WeightSelection.valueOf(args[3]));
		case Undirected:
			return new GraphDataStructure(listTypes, UndirectedNode.class,
					UndirectedEdge.class);
		case UndirectedE:
			return new GraphDataStructure(listTypes, UndirectedNode.class,
					UndirectedWeightedEdge.class, null, null,
					WeightFromArgs.parse(args[0]),
					WeightSelection.valueOf(args[1]));
		case UndirectedV:
			return new GraphDataStructure(listTypes,
					UndirectedWeightedNode.class, UndirectedEdge.class,
					WeightFromArgs.parse(args[0]),
					WeightSelection.valueOf(args[1]), null, null);
		case UndirectedVE:
			return new GraphDataStructure(listTypes,
					UndirectedWeightedNode.class, UndirectedWeightedEdge.class,
					WeightFromArgs.parse(args[0]),
					WeightSelection.valueOf(args[1]),
					WeightFromArgs.parse(args[2]),
					WeightSelection.valueOf(args[3]));
		default:
			throw new IllegalArgumentException("unknown gds type: " + gdsType);
		}

	}
}
