package dna.updates.undirected;

import dna.graph.GraphDatastructures;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.updates.BatchGenerator;
import dna.util.parameters.Parameter;

/**
 * 
 * implements the abstract class for an undirected batch generator
 * 
 * @author benni
 * 
 */
public abstract class UndirectedBatchGenerator extends
		BatchGenerator<UndirectedGraph, UndirectedNode, UndirectedEdge> {

	public UndirectedBatchGenerator(
			String name,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, new Parameter[] {}, ds);
	}

	public UndirectedBatchGenerator(
			String name,
			Parameter p1,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, new Parameter[] { p1 }, ds);
	}

	public UndirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, new Parameter[] { p1, p2 }, ds);
	}

	public UndirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, new Parameter[] { p1, p2, p3 }, ds);
	}

	public UndirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, new Parameter[] { p1, p2, p3, p4 }, ds);
	}

	public UndirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			Parameter p5,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5 }, ds);
	}

	public UndirectedBatchGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			Parameter p5,
			Parameter p6,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5, p6 }, ds);
	}

	public UndirectedBatchGenerator(
			String name,
			Parameter[] params,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super(name, params, ds);
	}

}
