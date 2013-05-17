package dna.graph.directed;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dna.graph.GraphDatastructures;

public class DirectedGraphDatastructures extends
		GraphDatastructures<DirectedGraph, DirectedNode, DirectedEdge> {

	@SuppressWarnings("unchecked")
	public DirectedGraphDatastructures(
			Class<? extends DirectedGraph> graphType,
			Class<? extends DirectedNode> nodeType,
			Class<? extends DirectedEdge> edgeType) {
		super((Class<DirectedGraph>) graphType, (Class<DirectedNode>) nodeType,
				(Class<DirectedEdge>) edgeType);
	}

	@Override
	public DirectedGraph newGraphInstance(String name, long timestamp,
			int nodes, int edges) {
		try {
			Constructor<DirectedGraph> constr = this.graphType.getConstructor(
					String.class, long.class, GraphDatastructures.class,
					int.class, int.class);
			return constr.newInstance(name, timestamp, this, nodes, edges);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DirectedNode newNodeInstance(int index) {
		try {
			Constructor<DirectedNode> constr = this.nodeType
					.getConstructor(int.class);
			return constr.newInstance(index);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DirectedNode newNodeInstance(int index, double weight) {
		try {
			Constructor<DirectedNode> constr = this.nodeType.getConstructor(
					int.class, double.class);
			return constr.newInstance(index, weight);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DirectedNode newNodeInstance(String str) {
		try {
			Constructor<DirectedNode> constr = this.nodeType
					.getConstructor(String.class);
			return constr.newInstance(str);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DirectedEdge newEdgeInstance(DirectedNode src, DirectedNode dst) {
		try {
			Constructor<DirectedEdge> constr = this.edgeType.getConstructor(
					DirectedNode.class, DirectedNode.class);
			return constr.newInstance(src, dst);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DirectedEdge newEdgeInstance(DirectedNode src, DirectedNode dst,
			double weight) {
		try {
			Constructor<DirectedEdge> constr = this.edgeType.getConstructor(
					DirectedNode.class, DirectedNode.class, double.class);
			return constr.newInstance(src, dst, weight);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public DirectedEdge newEdgeInstance(String str, DirectedGraph graph) {
		try {
			Constructor<DirectedEdge> constr = this.edgeType.getConstructor(
					String.class, DirectedGraph.class);
			return constr.newInstance(str, graph);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

}
