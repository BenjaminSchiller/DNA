package Factories;

import DataStructures.GraphDataStructure;
import Graph.Graph;
import Graph.ReadableGraph;
import Graph.Edges.DirectedEdge;
import Graph.Nodes.Node;
import Utils.Rand;
import Utils.parameters.Parameter;

public class RandomDirectedGraphGenerator extends DirectedGraphGenerator implements IRandomGenerator {
	public RandomDirectedGraphGenerator(String name, Parameter[] params,
			GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
		if ( !ReadableGraph.class.isAssignableFrom(gds.getGraphType())) {
			throw new RuntimeException("Can only generate graph with readable property");
		}
	}

	@Override
	public Graph generate() {
		Graph graphUnc = this.newGraphInstance();
		ReadableGraph graph = (ReadableGraph) graphUnc;

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
