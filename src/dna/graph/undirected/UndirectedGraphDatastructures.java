package dna.graph.undirected;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dna.graph.GraphDatastructures;

public class UndirectedGraphDatastructures extends
		GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> {

	@SuppressWarnings("unchecked")
	public UndirectedGraphDatastructures(
			Class<? extends UndirectedGraph> graphType,
			Class<? extends UndirectedNode> nodeType,
			Class<? extends UndirectedEdge> edgeType) {
		super((Class<UndirectedGraph>) graphType,
				(Class<UndirectedNode>) nodeType,
				(Class<UndirectedEdge>) edgeType);
	}

	@Override
	public UndirectedGraph newGraphInstance(String name, long timestamp,
			int nodes, int edges) {
		try {
			Constructor<UndirectedGraph> constr = this.graphType
					.getConstructor(String.class, long.class,
							GraphDatastructures.class, int.class, int.class);
			return constr.newInstance(name, timestamp, this, nodes, edges);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UndirectedNode newNodeInstance(int index) {
		try {
			Constructor<UndirectedNode> constr = this.nodeType
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
	public UndirectedNode newNodeInstance(int index, double weight) {
		try {
			Constructor<UndirectedNode> constr = this.nodeType.getConstructor(
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
	public UndirectedNode newNodeInstance(String str) {
		try {
			Constructor<UndirectedNode> constr = this.nodeType
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
	public UndirectedEdge newEdgeInstance(UndirectedNode src, UndirectedNode dst) {
		try {
			Constructor<UndirectedEdge> constr = this.edgeType.getConstructor(
					UndirectedNode.class, UndirectedNode.class);
			return constr.newInstance(src, dst);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UndirectedEdge newEdgeInstance(UndirectedNode src,
			UndirectedNode dst, double weight) {
		try {
			Constructor<UndirectedEdge> constr = this.edgeType.getConstructor(
					UndirectedNode.class, UndirectedNode.class, double.class);
			return constr.newInstance(src, dst, weight);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public UndirectedEdge newEdgeInstance(String str, UndirectedGraph graph) {
		try {
			Constructor<UndirectedEdge> constr = this.edgeType.getConstructor(
					String.class, UndirectedGraph.class);
			return constr.newInstance(str, graph);
		} catch (NoSuchMethodException | SecurityException
				| InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			return null;
		}
	}

}
