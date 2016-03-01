package dna.parallel.partition;

import java.util.HashMap;
import java.util.List;

import dna.graph.Graph;
import dna.graph.nodes.Node;
import dna.parallel.auxData.NodeCutAuxData;

public class NodeCutPartition extends Partition {

	public NodeCutPartition(Graph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	public static AllPartitions<NodeCutPartition, NodeCutAuxData> partition(
			String name, PartitionType partitionType, Graph g,
			List<Node>[] nodess) {
		Graph[] graphs = getInitialGraphs(g, nodess);
		NodeCutPartition[] partitions = new NodeCutPartition[nodess.length];
		HashMap<Node, Integer> mapping = new HashMap<Node, Integer>();
		for (int i = 0; i < graphs.length; i++) {
			partitions[i] = new NodeCutPartition(graphs[i]);
			for (Node n : nodess[i]) {
				mapping.put(n, i);
			}
		}

		// TODO edges

		// Set<Edge> externalEdges = new HashSet<Edge>();
		//
		// for (IElement e_ : g.getEdges()) {
		// Edge e = (Edge) e_;
		// NodeCutPartition p1 = mapping.get(e.getN1());
		// NodeCutPartition p2 = mapping.get(e.getN2());
		// if (p1 == p2) {
		// p1.g.addEdge(p1.g.getGraphDatastructures().newEdgeInstance(
		// e.asString(), p1.g));
		// } else {
		// externalEdges.add(e);
		// }
		// }

		NodeCutAuxData auxData = new NodeCutAuxData(g.getGraphDatastructures());

		return new AllPartitions<NodeCutPartition, NodeCutAuxData>(name,
				partitionType, g, partitions, auxData, mapping);
	}

}
