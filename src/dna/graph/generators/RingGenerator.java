package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

/**
 * Graph generator for a ring topology, i.e. 1 -> 2 -> 3 -> ... -> n -> 0
 * 
 * @author Nico
 * 
 */
public class RingGenerator extends GraphGenerator {

	public RingGenerator(GraphDataStructure gds, int nodes) {
		super(buildName("Ring", gds), new Parameter[]{new IntParameter("N", nodes)}, gds, 0, nodes, nodes);
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
			Edge edge = this.gds.newEdgeInstance(g.getNode(i),
					g.getNode((i + 1) % this.nodesInit));
			g.addEdge(edge);
			edge.connectToNodes();
		}

		return g;
	}

}
