package dna.graph.generators.directed;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

/**
 * 
 * graph generator for a directed clique, i.e., a directed graph that contains
 * all possible (n * (n-1)) edges (no loops)
 * 
 * @author benni
 * 
 */
public class DirectedCliqueGraphGenerator extends DirectedGraphGenerator {

	public DirectedCliqueGraphGenerator(GraphDataStructure gds, int nodes) {
		super("DirectedClique", null, gds, 0, nodes, nodes * (nodes - 1));
	}

	@Override
	public Graph generate() {

		Graph g = gds.newGraphInstance(this.getName(), this.timestampInit,
				nodesInit, edgesInit);

		for (int i = 0; i < this.nodesInit; i++) {
			Node node = this.gds.newNodeInstance(i);
			g.addNode(node);
		}

		for (int i = 0; i < this.nodesInit; i++) {
			for (int j = 0; j < this.nodesInit; j++) {
				if (i == j) {
					continue;
				}
				Edge edge = this.gds
						.newEdgeInstance(g.getNode(i), g.getNode(j));
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}

		return g;

	}

}
