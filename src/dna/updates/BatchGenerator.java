package dna.updates;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class BatchGenerator<N extends Node, E extends Edge> extends ParameterList {

	protected GraphDataStructure ds;

	public BatchGenerator(String name, Parameter[] params, GraphDataStructure ds) {
		super(name, params);
		this.ds = ds;
	}

	public abstract Batch<E> generate(Graph graph);

	public abstract void reset();

}
