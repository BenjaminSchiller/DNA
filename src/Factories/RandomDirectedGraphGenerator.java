package Factories;

import Utils.Rand;
import Utils.parameters.Parameter;
import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;
import Graph.DirectedEdge;
import Graph.DirectedNode;
import Graph.Graph;
import Graph.Node;

public class RandomDirectedGraphGenerator extends DirectedGraphGenerator {

	public RandomDirectedGraphGenerator(String name, long timestampInit, Parameter[] params,
			Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType, 
			int nodesInit, int edgesInit) {
		super(name, params, nodeListType, graphEdgeListType, nodeEdgeListType, DirectedNode.class, timestampInit, nodesInit, edgesInit);
	}

	@Override
	public Graph generate() {
		Graph graph = this.newGraphInstance();

		for (int i = 0; i < this.nodesInit; i++) {
			Node node = this.newNodeInstance(i);
			graph.addNode(node);
		}

		while (graph.getEdgeCount() < this.edgesInit) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				DirectedEdge edge = (DirectedEdge) this.newEdgeInstance(graph.getNode(src), graph.getNode(dst));
				graph.addEdge(edge);
				edge.getSrc().addEdge(edge);
				edge.getDst().addEdge(edge);
			}
		}

		return graph;
	}

}
