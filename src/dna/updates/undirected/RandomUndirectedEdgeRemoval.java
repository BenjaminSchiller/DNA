package dna.updates.undirected;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedGraphDatastructures;
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
public class RandomUndirectedEdgeRemoval extends UndirectedBatchGenerator {

	private int removals;

	/**
	 * 
	 * @param removals
	 *            number of egdes to remove with each batch
	 * @param datastructures
	 *            datastructures
	 */
	public RandomUndirectedEdgeRemoval(int removals,
			UndirectedGraphDatastructures datastructures) {
		super("randomUndirectedEdgeRemoval", new IntParameter("removals",
				removals), datastructures);
		this.removals = removals;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Batch<UndirectedEdge> generate(
			Graph<? extends Node<UndirectedEdge>, UndirectedEdge> graph) {
		UndirectedGraph g = (UndirectedGraph) graph;
		Batch<UndirectedEdge> batch = new Batch<UndirectedEdge>(
				(GraphDatastructures) this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1, 0, 0, 0, 0, this.removals, 0);
		HashSet<UndirectedEdge> removed = new HashSet<UndirectedEdge>(
				this.removals);
		while (batch.getSize() < this.removals) {
			UndirectedEdge e = g.getRandomEdge();
			if (removed.contains(e)) {
				continue;
			}
			removed.add(e);
			batch.add(new EdgeRemoval<UndirectedEdge>(e));
		}
		return batch;
	}

	@Override
	public void reset() {
	}
}
