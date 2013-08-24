package Factories;

import DataStructures.GraphDataStructure;
import Utils.parameters.Parameter;

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
