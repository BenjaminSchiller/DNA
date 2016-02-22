package dna.parallel.partition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.parallel.auxData.OverlappingAuxData;

public class OverlappingPartition extends Partition {

	public OverlappingPartition(Graph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	public static AllPartitions<OverlappingPartition, OverlappingAuxData> partition(
			String name, PartitionType partitionType, Graph g,
			List<Node>[] nodess) {
		Graph[] graphs = getInitialGraphs(g, nodess);
		OverlappingPartition[] partitions = new OverlappingPartition[nodess.length];
		HashMap<Node, Integer> mapping = new HashMap<Node, Integer>();
		for (int i = 0; i < graphs.length; i++) {
			partitions[i] = new OverlappingPartition(graphs[i]);
			for (Node n : nodess[i]) {
				mapping.put(n, i);
			}
		}

		Set<Edge> edges = new HashSet<Edge>();
		Set<Node> nodes = new HashSet<Node>();

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			int pIndex = mapping.get(n);
			OverlappingPartition p = partitions[pIndex];
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				Node n2 = e.getDifferingNode(n);
				if (mapping.get(n2) != pIndex) {
					edges.add(e);
				}
				if (!p.g.containsNode(n2)) {
					p.g.addNode(p.g.getGraphDatastructures().newNodeInstance(
							n.asString()));
					nodes.add(n2);
				}
				p.g.addEdge(p.g.getGraphDatastructures().newEdgeInstance(
						e.asString(), p.g));
			}
		}

		OverlappingAuxData auxData = new OverlappingAuxData(nodes, edges);

		return new AllPartitions<OverlappingPartition, OverlappingAuxData>(
				name, partitionType, g, partitions, auxData, mapping);
	}
}
