package Factories;

import DataStructures.GraphDataStructure;
import Utils.parameters.Parameter;

public abstract class DirectedGraphGenerator extends GraphGenerator {

	public DirectedGraphGenerator(String name, Parameter[] params, GraphDataStructure gds, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, gds, timestampInit, nodesInit, edgesInit);
	}
}
