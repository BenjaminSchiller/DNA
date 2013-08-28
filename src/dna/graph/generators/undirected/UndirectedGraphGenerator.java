package dna.graph.generators.undirected;

import dna.datastructures.GraphDataStructure;
import dna.graph.generators.GraphGenerator;
import dna.util.parameters.Parameter;

/**
 * Generator for graphs with undirected nodes
 * 
 * @author Nico
 * 
 */
public abstract class UndirectedGraphGenerator extends GraphGenerator {

	public UndirectedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}
}
