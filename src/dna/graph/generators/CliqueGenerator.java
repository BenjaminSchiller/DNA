package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.parameters.Parameter;

/**
 * 
 * graph generator for a directed clique, i.e., a directed graph that contains
 * all possible (n * (n-1)) edges
 * 
 * @author benni, Nico
 * 
 */

public class CliqueGenerator extends GraphGenerator {

	public CliqueGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit, int nodesInit,
			int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
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

	@Override
	public boolean canGenerateNodeType(Class<? extends Node> nodeType) {
		return true;
	}

	@Override
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType) {
		return true;
	}

}
