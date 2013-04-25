package dna.graph;

import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class GraphGenerator<EdgeType extends Edge> extends
		ParameterList {
	protected GraphDatastructures<EdgeType> datastructures;

	/**
	 * 
	 * @param name
	 * @param params
	 * @param datastructures
	 */
	public GraphGenerator(String name, Parameter[] params,
			GraphDatastructures<EdgeType> datastructures) {
		super(name, params);
		this.datastructures = datastructures;
	}

	/**
	 * 
	 * @return
	 */
	public GraphDatastructures<EdgeType> getDatastructures() {
		return datastructures;
	}

	/**
	 * generated a graph of the specified type according to the generation
	 * principles defined for this graph generator
	 * 
	 * @return generated graph of the specified type
	 */
	public abstract Graph<? extends Node<EdgeType>, EdgeType> generate();
}
