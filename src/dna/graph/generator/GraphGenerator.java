package dna.graph.generator;

import dna.graph.Graph;

/**
 * 
 * Abstract class for graph generator that allow to generate a graph object.
 * 
 * @author benni
 * 
 */
public abstract class GraphGenerator {

	public GraphGenerator(String name) {
		this.name = name;
	}

	private String name;

	/**
	 * 
	 * @return the name of the graph generator
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * generates a new graph object of the specified type
	 * 
	 * @return newly generated graph object
	 */
	public abstract Graph generate();
}
