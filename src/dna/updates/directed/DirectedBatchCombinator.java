package dna.updates.directed;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.util.parameters.ParameterList;

/**
 * 
 * implements a directed batch generator that combines multiple batch generators
 * into a single one. the given batch generators are executed in the given order
 * and all updates combined to create a unified batch.
 * 
 * @author benni
 * 
 */
public class DirectedBatchCombinator extends DirectedBatchGenerator {

	private DirectedBatchGenerator[] generators;

	public DirectedBatchCombinator(String name, DirectedBatchGenerator g1,
			DirectedBatchGenerator g2,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds) {
		this(name, new DirectedBatchGenerator[] { g1, g2 }, ds);
	}

	public DirectedBatchCombinator(String name, DirectedBatchGenerator g1,
			DirectedBatchGenerator g2, DirectedBatchGenerator g3,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds) {
		this(name, new DirectedBatchGenerator[] { g1, g2, g3 }, ds);
	}

	public DirectedBatchCombinator(String name, DirectedBatchGenerator g1,
			DirectedBatchGenerator g2, DirectedBatchGenerator g3,
			DirectedBatchGenerator g4,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds) {
		this(name, new DirectedBatchGenerator[] { g1, g2, g3, g4 }, ds);
	}

	public DirectedBatchCombinator(String name, DirectedBatchGenerator g1,
			DirectedBatchGenerator g2, DirectedBatchGenerator g3,
			DirectedBatchGenerator g4, DirectedBatchGenerator g5,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds) {
		this(name, new DirectedBatchGenerator[] { g1, g2, g3, g4, g5 }, ds);
	}

	public DirectedBatchCombinator(String name, DirectedBatchGenerator g1,
			DirectedBatchGenerator g2, DirectedBatchGenerator g3,
			DirectedBatchGenerator g4, DirectedBatchGenerator g5,
			DirectedBatchGenerator g6,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds) {
		this(name, new DirectedBatchGenerator[] { g1, g2, g3, g4, g5, g6 }, ds);
	}

	public DirectedBatchCombinator(String name,
			DirectedBatchGenerator[] generators,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> ds) {
		super(name, ParameterList.getParameters(generators), ds);
		this.generators = generators;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Batch<DirectedEdge> generate(
			Graph<? extends Node<DirectedEdge>, DirectedEdge> graph) {
		Batch<DirectedEdge> b = new Batch<DirectedEdge>(
				(GraphDatastructures) this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1);
		for (DirectedBatchGenerator bg : this.generators) {
			b.addAll(bg.generate(graph).getAllUpdates());
		}
		return b;
	}

}
