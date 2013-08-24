package Factories;

import DataStructures.GraphDataStructure;
import Graph.Graph;
import Graph.Edges.UndirectedDoubleWeightedEdge;
import Graph.Nodes.UndirectedDoubleWeightedNode;
import Utils.Rand;
import Utils.parameters.Parameter;

public class RandomUndirectedDoubleWeightedGraphGenerator extends UndirectedDoubleWeightedGraphGenerator implements IRandomGenerator {
	public RandomUndirectedDoubleWeightedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}
	
	public Graph generate() {
		Graph graph = this.newGraphInstance();
		
		for (int i = 0; i < this.nodesInit; i++) {
			UndirectedDoubleWeightedNode node = (UndirectedDoubleWeightedNode) this.gds.newNodeInstance(i);
			node.setWeight(Rand.rand.nextDouble());
			graph.addNode(node);
		}

		while (graph.getEdgeCount() < this.edgesInit) {
			int src = Rand.rand.nextInt(graph.getNodeCount());
			int dst = Rand.rand.nextInt(graph.getNodeCount());
			if (src != dst) {
				UndirectedDoubleWeightedEdge edge = (UndirectedDoubleWeightedEdge) this.gds.newEdgeInstance(
						graph.getNode(src), graph.getNode(dst));
				edge.setWeight(Rand.rand.nextDouble());
				
				graph.addEdge(edge);
				edge.getNode1().addEdge(edge);
				edge.getNode2().addEdge(edge);
			}
		}

		return graph;
	}

}
