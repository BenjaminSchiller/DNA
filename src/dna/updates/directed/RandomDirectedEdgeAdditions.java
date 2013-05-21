package dna.updates.directed;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.util.parameters.IntParameter;

/**
 * 
 * batch generator for random edge additions. edges are added uniformly at
 * random from all possible (non-existing) edges in the graph. no edge is added
 * twice.
 * 
 * @author benni
 * 
 */
public class RandomDirectedEdgeAdditions extends DirectedBatchGenerator {

	private int additions;

	/**
	 * 
	 * @param additions
	 *            number of random edges to add per batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomDirectedEdgeAdditions(int additions,
			DirectedGraphDatastructures datastructures) {
		super("randomDirectedEdgeAdditions", new IntParameter("additions",
				additions), datastructures);
		this.additions = additions;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		DirectedGraph g = (DirectedGraph) graph;
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(
				(GraphDatastructures) this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1, 0, 0, 0, this.additions, 0, 0);
		HashSet<DirectedEdge> added = new HashSet<DirectedEdge>(this.additions);
		while (batch.getSize() < this.additions) {
			DirectedNode n1 = (DirectedNode) g.getRandomNode();
			DirectedNode n2 = (DirectedNode) g.getRandomNode();
			if (n1.equals(n2)) {
				continue;
			}
			DirectedEdge e = this.ds.newEdgeInstance(n1, n2);
			if (g.containsEdge(e) || added.contains(e)) {
				continue;
			}
			added.add(e);
			batch.add(new EdgeAddition<DirectedEdge>(e));
		}

		return batch;
	}

}
