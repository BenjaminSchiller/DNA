package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.ArrayUtils;
import dna.util.Rand;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public class RandomGraphGenerator extends GraphGenerator implements
		IRandomGenerator {
	public RandomGraphGenerator(GraphDataStructure gds, int nodes, int edges) {
		this("RandomGraph", null, gds, nodes, edges);
	}

	protected RandomGraphGenerator(String name, Parameter[] parameters,
			GraphDataStructure gds, int nodes, int edges) {
		super(buildName(name, gds), ArrayUtils.append(new Parameter[] {
				new IntParameter("N", nodes), new IntParameter("E", edges) },
				parameters), gds, 0, nodes, edges);
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
				Edge edge = this.gds.newEdgeInstance(graph.getNode(src),
						graph.getNode(dst));
				graph.addEdge(edge);
				edge.connectToNodes();
			}
		}

		return graph;
	}

}
