package DataStructures;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import Graph.Edge;
import Graph.Graph;
import Graph.Node;

public class GraphDataStructure {
	private Class<? extends INodeListDatastructure> nodeListType;
	private Class<? extends IEdgeListDatastructure> graphEdgeListType;
	private Class<? extends IEdgeListDatastructure> nodeEdgeListType;
	private Class<? extends Node> nodeType;
	private Class<? extends Edge> edgeType;


	public GraphDataStructure(Class<? extends INodeListDatastructure> nodeListType,
			Class<? extends IEdgeListDatastructure> graphEdgeListType,
			Class<? extends IEdgeListDatastructure> nodeEdgeListType,
			Class<? extends Node> nodeType) {
		this.nodeListType = nodeListType;
		this.graphEdgeListType = graphEdgeListType;
		this.nodeEdgeListType = nodeEdgeListType;
		this.nodeType = nodeType;
		
		System.out.println(this.nodeType);
		
		try {
			this.edgeType = (Class<? extends Edge>) nodeType.getField("edgeType").get(null);
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Class<? extends INodeListDatastructure> getNodeListType() {
		return nodeListType;
	}

	public Class<? extends IEdgeListDatastructure> getGraphEdgeListType() {
		return graphEdgeListType;
	}

	public Class<? extends IEdgeListDatastructure> getNodeEdgeListType() {
		return nodeEdgeListType;
	}

	public Class<? extends Node> getNodeType() {
		return nodeType;
	}
	
	public Graph newGraphInstance(String name, long timestamp, int nodes, int edges) {
		return new Graph(name, timestamp, this, nodes, edges);
	}
	
	public INodeListDatastructure newNodeList() {
		INodeListDatastructure res = null;
		try {
			res = (INodeListDatastructure) nodeListType.getConstructor(nodeType.getClass())
			.newInstance(nodeType);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public IEdgeListDatastructure newEdgeList() {
		IEdgeListDatastructure res = null;
		try {
			res = graphEdgeListType.getConstructor(edgeType.getClass()).newInstance(
					edgeType);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public Node newNodeInstance(int index) {
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
	
	public Edge newEdgeInstance(Node src, Node dst) {
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
