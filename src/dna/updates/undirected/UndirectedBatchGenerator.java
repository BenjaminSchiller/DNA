package dna.updates.undirected;

import dna.datastructures.GraphDataStructure;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.BatchGenerator;
import dna.util.parameters.Parameter;

/**
 * 
 * implements the abstract class for an undirected batch generator
 * 
 * @author benni
 * 
 */
public abstract class UndirectedBatchGenerator extends BatchGenerator<UndirectedNode, UndirectedEdge> {

	public UndirectedBatchGenerator(String name, GraphDataStructure ds) {
		super(name, new Parameter[] {}, ds);
	}

	public UndirectedBatchGenerator(String name, Parameter p1, GraphDataStructure ds) {
		super(name, new Parameter[] { p1 }, ds);
	}

	public UndirectedBatchGenerator(String name, Parameter p1, Parameter p2, GraphDataStructure ds) {
		super(name, new Parameter[] { p1, p2 }, ds);
	}

	public UndirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3, GraphDataStructure ds) {
		super(name, new Parameter[] { p1, p2, p3 }, ds);
	}

	public UndirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3, Parameter p4,
			GraphDataStructure ds) {
		super(name, new Parameter[] { p1, p2, p3, p4 }, ds);
	}

	public UndirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3, Parameter p4, Parameter p5,
			GraphDataStructure ds) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5 }, ds);
	}

	public UndirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3, Parameter p4, Parameter p5,
			Parameter p6, GraphDataStructure ds) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5, p6 }, ds);
	}

	public UndirectedBatchGenerator(String name, Parameter[] params, GraphDataStructure ds) {
		super(name, params, ds);
	}

}
