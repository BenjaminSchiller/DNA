package dna.updates.directed;

import dna.graph.GraphDatastructures;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.updates.BatchGenerator;
import dna.util.parameters.Parameter;

public abstract class DirectedBatchGenerator extends
		BatchGenerator<DirectedGraph, DirectedNode, DirectedEdge> {

	public DirectedBatchGenerator(
			String name,
			Parameter p1,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1 }, datastructures);
	}

	public DirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2 }, datastructures);
	}

	public DirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3 }, datastructures);
	}

	public DirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4 }, datastructures);
	}

	public DirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			Parameter p5,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5 }, datastructures);
	}

	public DirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			Parameter p5,
			Parameter p6,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5, p6 }, datastructures);
	}

	public DirectedBatchGenerator(
			String name,
			Parameter[] params,
			GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> datastructures) {
		super(name, params, datastructures);
	}

}
