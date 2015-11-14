package dna.graph.generators.util;

import java.util.HashMap;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.parallelization.partitioning.Partition;

public class CombiningGraph {
	public static Graph combine(GraphDataStructure gds, String name,
			Partition[] partitions) {
		Graph[] graphs = new Graph[partitions.length];
		for (int i = 0; i < partitions.length; i++) {
			graphs[i] = partitions[i].getGraph();
		}
		return combine(gds, name, graphs);
	}

	// public static Graph combine(GraphDataStructure gds, String name, Graph g,
	// Graph... graphs_) {
	// Graph[] graphs = new Graph[graphs_.length + 1];
	// graphs[0] = g;
	// System.arraycopy(graphs_, 0, graphs, 1, graphs_.length);
	// return combine(gds, name, graphs);
	// }

	public static Graph combine(GraphDataStructure gds, String name,
			Graph... graphs) {
		int nodes = 0;
		int edges = 0;
		for (Graph graph : graphs) {
			nodes += graph.getNodeCount();
			edges += graph.getEdgeCount();
		}

		Graph g = gds.newGraphInstance(name, 0, nodes, edges);

		int index = 0;
		HashMap<Node, Node>[] map = new HashMap[graphs.length];
		for (int i = 0; i < graphs.length; i++) {
			map[i] = new HashMap<Node, Node>();
			for (IElement n_ : graphs[i].getNodes()) {
				Node n = (Node) n_;
				Node copy = gds.newNodeInstance(n.asString());
				copy.setIndex(index);
				g.addNode(copy);
				map[i].put(n, copy);
				index++;
			}
			for (IElement e_ : graphs[i].getEdges()) {
				Edge e = (Edge) e_;
				Edge copy = gds.newEdgeInstance(map[i].get(e.getN1()),
						map[i].get(e.getN2()));
				g.addEdge(copy);
			}
		}

		return g;
	}
}
