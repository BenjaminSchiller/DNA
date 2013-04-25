package dna.graph.directed.generators;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.GraphGenerator;
import dna.graph.Node;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.util.parameters.Parameter;

public abstract class DirectedGraphGenerator extends
		GraphGenerator<DirectedEdge> {

	public DirectedGraphGenerator(String name, Parameter p1,
			GraphDatastructures<DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1 }, datastructures);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			GraphDatastructures<DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2 }, datastructures);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, GraphDatastructures<DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3 }, datastructures);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, Parameter p4,
			GraphDatastructures<DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4 }, datastructures);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, Parameter p4, Parameter p5,
			GraphDatastructures<DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5 }, datastructures);
	}

	public DirectedGraphGenerator(String name, Parameter p1, Parameter p2,
			Parameter p3, Parameter p4, Parameter p5, Parameter p6,
			GraphDatastructures<DirectedEdge> datastructures) {
		super(name, new Parameter[] { p1, p2, p3, p4, p5, p6 }, datastructures);
	}

	public DirectedGraphGenerator(String name, Parameter[] params,
			GraphDatastructures<DirectedEdge> datastructures) {
		super(name, params, datastructures);
	}

	protected DirectedGraph getGraphInstance(String name, long timestamp,
			int nodes, int edges) {
		try {
			Constructor<? extends Graph<? extends Node<DirectedEdge>, DirectedEdge>> constr = this.datastructures
					.getGraphType().getConstructor(String.class, long.class,
							int.class, int.class);
			return (DirectedGraph) constr.newInstance(name, timestamp, nodes,
					edges);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected DirectedNode getNodeInstance(int index) {
		try {
			Constructor<? extends Node<DirectedEdge>> constr = this.datastructures
					.getNodeType().getConstructor(int.class);
			return (DirectedNode) constr.newInstance(index);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected DirectedEdge getEdgeInstance(DirectedNode src, DirectedNode dst) {
		try {
			Constructor<DirectedEdge> constr = this.datastructures
					.getEdgeType().getConstructor(DirectedNode.class,
							DirectedNode.class);
			return constr.newInstance(src, dst);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

}
