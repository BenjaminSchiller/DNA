package dna.graph.generators.random;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.ArrayUtils;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public class RandomGraph extends GraphGenerator implements IRandomGenerator {
	public RandomGraph(GraphDataStructure gds, int nodes, int edges) {
		this("RandomGraph", null, gds, nodes, edges);
	}

	protected RandomGraph(String name, Parameter[] parameters,
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
			Node src = graph.getRandomNode();
			Node dst = graph.getRandomNode();
			if (!src.equals(dst)) {
				Edge edge = this.gds.newEdgeInstance(src, dst);
				if (graph.addEdge(edge)) {
					edge.connectToNodes();
				}
			}
		}

		return graph;
	}

}
