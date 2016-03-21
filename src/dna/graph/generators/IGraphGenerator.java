package dna.graph.generators;

import dna.graph.IGraph;

public interface IGraphGenerator {
	/**
	 * Receive a new graph generated through this generator
	 * 
	 * @return
	 */
	public IGraph generate();
	
	public String getName();

}
