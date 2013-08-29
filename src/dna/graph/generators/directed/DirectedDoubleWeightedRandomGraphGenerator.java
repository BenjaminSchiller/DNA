package dna.graph.generators.directed;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.generators.IRandomGenerator;
import dna.graph.nodes.DirectedDoubleWeightedNode;
import dna.util.Rand;
import dna.util.parameters.Parameter;

public class DirectedDoubleWeightedRandomGraphGenerator extends DirectedDoubleWeightedGraphGenerator implements
		IRandomGenerator {
	public DirectedDoubleWeightedRandomGraphGenerator(String name, Parameter[] params, GraphDataStructure gds,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}

	public Graph generate() {
		Graph graph = this.newGraphInstance();

		for (int i = 0; i < this.nodesInit; i++) {
			DirectedDoubleWeightedNode node = (DirectedDoubleWeightedNode) this.gds.newNodeInstance(i);
			node.setWeight(Rand.rand.nextDouble());
			graph.addNode(node);
		}

		while (graph.getEdgeCount() < this.edgesInit) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				DirectedDoubleWeightedEdge edge = (DirectedDoubleWeightedEdge) this.gds.newEdgeInstance(
						graph.getNode(src), graph.getNode(dst));
				edge.setWeight(Rand.rand.nextDouble());

				graph.addEdge(edge);
				edge.getSrc().addEdge(edge);
				edge.getDst().addEdge(edge);
			}
		}

		return graph;
	}

}
