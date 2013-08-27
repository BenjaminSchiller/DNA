package dna.factories;

import dna.datastructures.GraphDataStructure;
import dna.graph.Graph;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.Node;
import dna.util.Rand;
import dna.util.parameters.Parameter;

public class RandomDirectedGraphGenerator extends DirectedGraphGenerator implements IRandomGenerator {
	public RandomDirectedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}

	@Override
	public Graph generate() {
		Graph graph = this.newGraphInstance();

		for (int i = 0; i < this.nodesInit; i++) {
			Node node = this.gds.newNodeInstance(i);
			graph.addNode(node);
		}

		while (graph.getEdgeCount() < this.edgesInit) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				DirectedEdge edge = (DirectedEdge) this.gds.newEdgeInstance(graph.getNode(src), graph.getNode(dst));
				graph.addEdge(edge);
				edge.getSrc().addEdge(edge);
				edge.getDst().addEdge(edge);
			}
		}

		return graph;
	}

}
