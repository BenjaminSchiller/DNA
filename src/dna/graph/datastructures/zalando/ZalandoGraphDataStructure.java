package dna.graph.datastructures.zalando;

import java.util.EnumMap;

import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.nodes.Node;
import dna.graph.nodes.zalando.DirectedZalandoNode;
import dna.graph.nodes.zalando.UndirectedZalandoNode;
import dna.graph.weights.Weight;
import dna.graph.weights.Weight.WeightSelection;

public class ZalandoGraphDataStructure extends GraphDataStructure {

	public ZalandoGraphDataStructure(
			EnumMap<ListType, Class<? extends IDataStructure>> listTypes,
			Class<? extends Node> nodeType, Class<? extends Edge> edgeType,
			Class<? extends Weight> nodeWeightType,
			WeightSelection nodeWeightSelection,
			Class<? extends Weight> edgeWeightType,
			WeightSelection edgeWeightSelection) {
		super(listTypes, nodeType, edgeType, nodeWeightType,
				nodeWeightSelection, edgeWeightType, edgeWeightSelection);
	}

	public Node newNodeInstance(int index, EventColumn[] type) {
		try {
			if (this.getNodeType() == UndirectedZalandoNode.class)
				return new UndirectedZalandoNode(index, this, type);
			else if (this.getNodeType() == DirectedZalandoNode.class)
				return new DirectedZalandoNode(index, this, type);
			else
				throw new RuntimeException(
						"Could not generate new node instance. NodeType must be UndirectedZalandoNode or DirectedZalandoNode.");
		} catch (Exception e) {
			RuntimeException rt = new RuntimeException(
					"Could not generate new node instance: " + e.getMessage());
			rt.setStackTrace(e.getStackTrace());
			throw rt;
		}
	}
}
