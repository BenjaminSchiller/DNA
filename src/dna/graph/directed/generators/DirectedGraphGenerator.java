package dna.graph.directed.generators;

import dna.graph.GraphGenerator;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedGraphDatastructures;
import dna.graph.directed.DirectedNode;
import dna.util.parameters.Parameter;

/**
 * 
 * abstract class for a directed graph generator
 * 
 * @author benni
 * 
 */
public abstract class DirectedGraphGenerator extends
		GraphGenerator<DirectedGraph, DirectedNode, DirectedEdge> {

	public DirectedGraphGenerator(String name, Parameter p1,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1 }, datastructures, timestampInit,
				nodesInit, edgesInit);
	}

	public DirectedGraphGenerator(String name,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, new Parameter[] {}, datastructures, timestampInit,
				nodesInit, edgesInit);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2 }, datastructures, timestampInit,
				nodesInit, edgesInit);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, DirectedGraphDatastructures datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, Parameter p4,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3, p4 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, Parameter p4, Parameter p5,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, Parameter p4, Parameter p5, Parameter p6,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5, p6 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public DirectedGraphGenerator(String name, Parameter[] params,
			DirectedGraphDatastructures datastructures, long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params, datastructures, timestampInit, nodesInit, edgesInit);
	}

}
