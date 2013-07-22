package dna.updates.undirected;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;

/**
 * 
 * implements an undirected batch generator that combines multiple batch
 * generators into a single one. the given batch generators are executed in the
 * given order and all updates combined to create a unified batch.
 * 
 * @author benni
 * 
 */
public class UndirectedBatchCombinator extends UndirectedBatchGenerator {

	private UndirectedBatchGenerator[] generators;

	public UndirectedBatchCombinator(
			UndirectedBatchGenerator g1,
			UndirectedBatchGenerator g2,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		this(new UndirectedBatchGenerator[] { g1, g2 }, ds);
	}

	public UndirectedBatchCombinator(
			UndirectedBatchGenerator g1,
			UndirectedBatchGenerator g2,
			UndirectedBatchGenerator g3,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		this(new UndirectedBatchGenerator[] { g1, g2, g3 }, ds);
	}

	public UndirectedBatchCombinator(
			UndirectedBatchGenerator g1,
			UndirectedBatchGenerator g2,
			UndirectedBatchGenerator g3,
			UndirectedBatchGenerator g4,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		this(new UndirectedBatchGenerator[] { g1, g2, g3, g4 }, ds);
	}

	public UndirectedBatchCombinator(
			UndirectedBatchGenerator[] generators,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super("undirectedBatchGenerator", ds);
		this.generators = generators;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Batch<UndirectedEdge> generate(
			Graph<? extends Node<UndirectedEdge>, UndirectedEdge> graph) {
		Batch<UndirectedEdge> b = new Batch<UndirectedEdge>(
				(GraphDatastructures) this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1);
		for (UndirectedBatchGenerator bg : this.generators) {
			b.addAll(bg.generate(graph).getAllUpdates());
		}
		return b;
	}

}
