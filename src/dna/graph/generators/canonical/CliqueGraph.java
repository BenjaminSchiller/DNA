package dna.graph.generators.canonical;

import dna.graph.IGraph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.generators.GraphGenerator;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

/**
 * 
 * graph generator for a directed / undirected clique, i.e., a directed /
 * undirected graph that contains all possible (n * (n-1)) edges
 * 
 * @author benni, Nico
 * 
 */
public class CliqueGraph extends GraphGenerator {

	public CliqueGraph(GraphDataStructure gds, int nodes) {
		super(buildName("CliqueGraph", gds),
				new Parameter[] { new IntParameter("N", nodes) }, gds, 0,
				nodes, gds.createsDirected() ? nodes * (nodes - 1) : nodes
						* (nodes - 1) / 2);
	}

	@Override
	public IGraph generate() {
		IGraph g = gds.newGraphInstance(this.getName(), this.timestampInit,
				nodesInit, edgesInit);

		for (int i = 0; i < this.nodesInit; i++) {
			Node node = this.gds.newNodeInstance(i);
			g.addNode(node);
		}

		int startJ;

		for (int i = 0; i < this.nodesInit; i++) {
			/**
			 * In the case of undirected edges, we can skip some edges that have
			 * already been added to the graph. This makes the generation a bit
			 * faster
			 */
			if (g.isDirected())
				startJ = 0;
			else
				startJ = i;

			for (int j = startJ; j < this.nodesInit; j++) {
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
