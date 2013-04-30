package dna.graph;

import dna.util.parameters.Parameter;
import dna.util.parameters.ParameterList;

public abstract class GraphGenerator<G extends Graph<N, E>, N extends Node<E>, E extends Edge>
		extends ParameterList {

	protected GraphDatastructures<G, N, E> datastructures;

	protected long timestampInit;

	protected int nodesInit;

	protected int edgesInit;

	/**
	 * 
	 * @param name
	 * @param params
	 * @param datastructures
	 */
	public GraphGenerator(String name, Parameter[] params,
			GraphDatastructures<G, N, E> datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params);
		this.datastructures = datastructures;
		this.timestampInit = timestampInit;
		this.nodesInit = nodesInit;
		this.edgesInit = edgesInit;
	}

	/**
	 * 
	 * @return
	 */
	public GraphDatastructures<G, N, E> getDatastructures() {
		return datastructures;
	}

	/**
	 * generated a graph of the specified type according to the generation
	 * principles defined for this graph generator
	 * 
	 * @return generated graph of the specified type
	 */
	public abstract G generate();

	protected G init() {
		return this.datastructures.newGraphInstance(this.getName(),
				this.timestampInit, this.nodesInit, this.edgesInit);
	}
}
