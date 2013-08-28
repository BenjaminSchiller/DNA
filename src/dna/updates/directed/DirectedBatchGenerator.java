package dna.updates.directed;

import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.BatchGenerator;
import dna.util.parameters.Parameter;

/**
 * 
 * implements the abstract class for a directed batch generator
 * 
 * @author benni
 * 
 */
public abstract class DirectedBatchGenerator extends BatchGenerator<DirectedNode, DirectedEdge> {

	public DirectedBatchGenerator(String name, GraphDataStructure datastructures) {
		super(name, new Parameter[] {}, datastructures);
	}

	public DirectedBatchGenerator(String name, Parameter p1, GraphDataStructure datastructures) {
		super(name, new Parameter[] { p1 }, datastructures);
	}

	public DirectedBatchGenerator(String name, Parameter p1, Parameter p2, GraphDataStructure datastructures) {
		super(name, new Parameter[] { p1, p2 }, datastructures);
	}

	public DirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3,
			GraphDataStructure datastructures) {
		super(name, new Parameter[] { p1, p2, p3 }, datastructures);
	}

	public DirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3, Parameter p4,
			GraphDataStructure datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4 }, datastructures);
	}

	public DirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3, Parameter p4, Parameter p5,
			GraphDataStructure datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5 }, datastructures);
	}

	public DirectedBatchGenerator(String name, Parameter p1, Parameter p2, Parameter p3, Parameter p4, Parameter p5,
			Parameter p6, GraphDataStructure datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5, p6 }, datastructures);
	}

	public DirectedBatchGenerator(String name, Parameter[] params, GraphDataStructure datastructures) {
		super(name, params, datastructures);
	}

}
