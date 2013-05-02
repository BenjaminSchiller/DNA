package dna.graph.undirected.generators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.GraphGenerator;
import dna.graph.Node;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.util.parameters.Parameter;

public abstract class UndirectedGraphGenerator extends
		GraphGenerator<UndirectedGraph, UndirectedNode, UndirectedEdge> {

	public UndirectedGraphGenerator(
			String name,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] {}, datastructures, timestampInit,
				nodesInit, edgesInit);
	}

	public UndirectedGraphGenerator(
			String name,
			Parameter p1,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1 }, datastructures, timestampInit,
				nodesInit, edgesInit);
	}

	public UndirectedGraphGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2 }, datastructures, timestampInit,
				nodesInit, edgesInit);
	}

	public UndirectedGraphGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public UndirectedGraphGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3, p4 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public UndirectedGraphGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			Parameter p5,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public UndirectedGraphGenerator(
			String name,
			Parameter p1,
			Parameter p2,
			Parameter p3,
			Parameter p4,
			Parameter p5,
			Parameter p6,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5, p6 }, datastructures,
				timestampInit, nodesInit, edgesInit);
	}

	public UndirectedGraphGenerator(
			String name,
			Parameter[] params,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> datastructures,
			long timestampInit, int nodesInit, int edgesInit) {
		super(name, params, datastructures, timestampInit, nodesInit, edgesInit);
	}

	protected UndirectedGraph getGraphInstance(String name, long timestamp,
			int nodes, int edges) {
		try {
			Constructor<? extends Graph<? extends Node<UndirectedEdge>, UndirectedEdge>> constr = this.datastructures
					.getGraphType().getConstructor(String.class, long.class,
							int.class, int.class);
			return (UndirectedGraph) constr.newInstance(name, timestamp, nodes,
					edges);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected UndirectedNode getNodeInstance(int index) {
		try {
			Constructor<? extends Node<UndirectedEdge>> constr = this.datastructures
					.getNodeType().getConstructor(int.class);
			return (UndirectedNode) constr.newInstance(index);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected UndirectedEdge getEdgeInstance(UndirectedNode src,
			UndirectedNode dst) {
		try {
			Constructor<UndirectedEdge> constr = this.datastructures
					.getEdgeType().getConstructor(UndirectedNode.class,
							UndirectedNode.class);
			return constr.newInstance(src, dst);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

}
