package dna.graph.generators.undirected;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

/**
 * 
 * graph generator for an undirected clique, i.e., an undirected graph that
 * contains all possible (n * (n-1) / 2) edges (no loops)
 * 
 * @author benni
 * 
 */
public class UndirectedCliqueGraphGenerator extends UndirectedGraphGenerator {

	public UndirectedCliqueGraphGenerator(GraphDataStructure gds, int nodes) {
		super("UndirectedClique", null, gds, 0, nodes, nodes * (nodes - 1) / 2);
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
			for (int j = i + 1; j < this.nodesInit; j++) {
				Edge edge = this.gds
						.newEdgeInstance(g.getNode(i), g.getNode(j));
				g.addEdge(edge);
				edge.connectToNodes();
			}
		}

		return g;

	}

}
