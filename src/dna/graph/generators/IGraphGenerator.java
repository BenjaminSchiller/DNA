package dna.graph.generators;

import dna.graph.Graph;

public interface IGraphGenerator {
	/**
	 * Receive a new graph generated through this generator
	 * 
	 * @return
	 */
	public Graph generate();
	
	public String getName();

}
