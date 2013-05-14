package dna.updates.directed;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.util.parameters.IntParameter;

public class RandomDirectedEdgeAdditions extends DirectedBatchGenerator {

	private int additions;

	public RandomDirectedEdgeAdditions(int additions,
			DirectedGraphDatastructures datastructures) {
		super("randomDirectedEdgeAdditions", new IntParameter("additions",
				additions), datastructures);
		this.additions = additions;
	}

	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		DirectedGraph g = (DirectedGraph) graph;
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(0, 0, 0,
				this.additions, 0, 0);
		HashSet<DirectedEdge> added = new HashSet<DirectedEdge>(this.additions);
		while (batch.getSize() < this.additions) {
			DirectedNode n1 = (DirectedNode) g.getRandomNode();
			DirectedNode n2 = (DirectedNode) g.getRandomNode();
			if (n1.equals(n2)) {
				continue;
			}
			DirectedEdge e = this.datastructures.newEdgeInstance(n1, n2);
			if (g.containsEdge(e) || added.contains(e)) {
				continue;
			}
			added.add(e);
			batch.add(new EdgeAddition<DirectedEdge>(e));
		}

		return batch;
	}

}
