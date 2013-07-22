package dna.updates.directed;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.updates.Batch;
import dna.updates.EdgeRemoval;
import dna.util.parameters.IntParameter;

/**
 * 
 * batch generator for random edge removals. the edges to be removed are chosen
 * uniformly at random from all existing edges. no edge is added for removal
 * twice.
 * 
 * @author benni
 * 
 */
public class RandomDirectedEdgeRemoval extends DirectedBatchGenerator {

	private int removals;

	/**
	 * 
	 * @param removals
	 *            number of egdes to remove with each batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomDirectedEdgeRemoval(int removals,
			DirectedGraphDatastructures datastructures) {
		super("randomDirectedEdgeRemoval", new IntParameter("removals",
				removals), datastructures);
		this.removals = removals;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		DirectedGraph g = (DirectedGraph) graph;
		Batch<DirectedEdge> batch = new Batch<DirectedEdge>(
				(GraphDatastructures) this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1, 0, 0, 0, 0, this.removals, 0);
		HashSet<DirectedEdge> removed = new HashSet<DirectedEdge>(this.removals);
		while (batch.getSize() < this.removals) {
			DirectedEdge e = g.getRandomEdge();
			if (removed.contains(e)) {
				continue;
			}
			removed.add(e);
			batch.add(new EdgeRemoval<DirectedEdge>(e));
		}
		return batch;
	}

	@Override
	public void reset() {
	}
}
