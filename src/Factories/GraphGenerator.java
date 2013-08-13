package Factories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import DataStructures.IEdgeListDatastructure;
import DataStructures.INodeListDatastructure;
import Graph.Edge;
import Graph.Graph;
import Graph.Node;
import Utils.parameters.Parameter;
import Utils.parameters.ParameterList;

public abstract class GraphGenerator extends ParameterList {
	protected long timestampInit;

	protected int nodesInit;
	protected int edgesInit;

	protected Class<? extends INodeListDatastructure> nodeListType;
	protected Class<? extends IEdgeListDatastructure> graphEdgeListType;
	protected Class<? extends IEdgeListDatastructure> nodeEdgeListType;
	protected Class<? extends Node> nodeType;
	protected Class<? extends Edge> edgeType = null;
	
	public GraphGenerator(String name, Parameter[] params,
			Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType,
			long timestampInit,
			int nodesInit, int edgesInit) {
		super(name, params);
		
		this.nodeListType = nodeListType;
		this.graphEdgeListType = graphEdgeListType;
		this.nodeEdgeListType = nodeEdgeListType;
		this.nodeType = nodeType;
		
		this.timestampInit = timestampInit;
		this.nodesInit = nodesInit;
		this.edgesInit = edgesInit;
	}
	
	public abstract Graph generate();
	
	public Graph newGraphInstance() {
		return this.newGraphInstance(this.getName(), this.timestampInit, this.nodesInit, this.edgesInit);
	}
	
	public Graph newGraphInstance(String name, long timestamp, int nodes,
			int edges) {
		return new Graph(name, timestamp, nodeListType, graphEdgeListType, nodeEdgeListType, nodeType, nodes, edges);
	}

	/*
	public BatchReader<G, N, E> getBatchReader() {
		return new BatchReader<G, N, E>(this);
	}

	public BatchWriter<G, N, E> getBatchWriter() {
		return new BatchWriter<G, N, E>();
	} */
	
	protected Node newNodeInstance(int index) {
		Constructor<? extends Node> c;
		try {
			c = nodeType.getConstructor(int.class, Class.class, Class.class);
			return c.newInstance(index, this.nodeEdgeListType, this.nodeListType);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new node instance");
	}
	
	public Node newNodeInstance(int index, double weight) {
		Node res = this.newNodeInstance(index);
		res.setWeight(weight);
		return res;
	}
	
	public Node newNodeInstance(String str) {
		Constructor<? extends Node> c;
		try {
			c = nodeType.getConstructor(String.class, Class.class, Class.class);
			return c.newInstance(str, this.nodeEdgeListType, this.nodeListType);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new node instance");
	}
	
	protected Edge newEdgeInstance(Node src, Node dst) {
		Constructor<? extends Edge> c;
		try {
			c = edgeType.getConstructor(Node.class, Node.class);
			return c.newInstance(src, dst);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new edge instance");
	}
	
	public Edge newEdgeInstance(Node src, Node dst, double weight) {
		Edge res = this.newEdgeInstance(src, dst);
		res.setWeight(weight);
		return res;
	}
	
	public Edge newEdgeInstance(String str, Graph graph) {
		Constructor<? extends Edge> c;
		try {
			c = edgeType.getConstructor(String.class, Graph.class);
			return c.newInstance(str, graph);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new RuntimeException("Could not generate new edge instance");		
	}
}
