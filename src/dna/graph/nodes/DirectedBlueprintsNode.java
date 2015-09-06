package dna.graph.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import dna.graph.BlueprintsGraph;
import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.DirectedBlueprintsEdge;
import dna.graph.edges.Edge;
import dna.util.Log;
import dna.util.MathHelper;

/**
 * The Class DirectedBlueprintsNode.
 * 
 * @author Matthias
 */
public class DirectedBlueprintsNode extends DirectedNode implements IGDBNode<Vertex> {

	/** The gds. */
	protected GraphDataStructure gds;
	
	/** The graph. */
	protected BlueprintsGraph graph;
	
	/** The index. */
	private int index;

	/** The vertex. */
	protected Vertex vertex;

	/**
	 * Instantiates a new directed blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 */
	public DirectedBlueprintsNode(int i, GraphDataStructure gds) {
		super(i, gds);
		this.setIndex(i);
	}

	/**
	 * Instantiates a new directed blueprints node.
	 *
	 * @param i the index
	 * @param gds the gds
	 * @param graph the graph
	 */
	public DirectedBlueprintsNode(int i, GraphDataStructure gds, IGraph graph) {
		this(i, gds);
		this.setIndex(i);
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new directed blueprints node.
	 *
	 * @param i the index
	 * @param gds the gds
	 * @param vertex the vertex
	 * @param graph the graph
	 */
	public DirectedBlueprintsNode(int i, GraphDataStructure gds, Vertex vertex,
			IGraph graph) {
		super(i, gds);
		this.setGDBNode(vertex);
		this.setIndex(i);
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new directed blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 */
	public DirectedBlueprintsNode(String str, GraphDataStructure gds) {
		this(MathHelper.parseInt(str), gds);
	}

	/**
	 * Instantiates a new directed blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 * @param graph the graph
	 */
	public DirectedBlueprintsNode(String str, GraphDataStructure gds, IGraph graph) {
		this(MathHelper.parseInt(str), gds, graph);
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#addEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean addEdge(Edge edge) {
		if (edge == null || !(edge instanceof DirectedBlueprintsEdge))
			return false;
		DirectedBlueprintsEdge e = (DirectedBlueprintsEdge) edge;

		if (e.getGDBEdge() != null && this.graph.containsEdge(edge)) {
			return false;
		}

		return this.graph.addEdge(e);
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#getEdges()
	 */
	@Override
	public Iterable<IElement> getEdges() {		Collection<Edge> result = new ArrayList<Edge>();		Edge edge = null;		for	(com.tinkerpop.blueprints.Edge e : vertex.getEdges(Direction.BOTH, "IGDBEdge"))		{			edge = this.graph.getEdge(e);						if (!result.contains((Edge)edge))										result.add((Edge)edge);					}				return new ArrayList<IElement>(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.nodes.IGDBNode#getVertex()
	 */
	@Override
	public Vertex getGDBNode() {
		return this.vertex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.nodes.IGDBNode#getGraph()
	 */
	@Override
	public IGraph getGraph() {
		return this.graph;
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#getIncomingEdges()
	 */
	@Override
	public Iterable<IElement> getIncomingEdges() {		return this.in();
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#getInDegree()
	 */
	@Override
	public int getInDegree() {		return this.in().size();
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.Node#getIndex()
	 */
	@Override
	public int getIndex() {
		if (vertex == null)
			return this.index;
		Integer idx = (Integer) this.vertex.getProperty("index");
		if (idx == null)
			this.setIndex(this.index);
		return this.index;
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#getNeighborCount()
	 */
	@Override
	public int getNeighborCount() {
		return this.neighbors().size();
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#getNeighbors()
	 */
	@Override
	public Iterable<IElement> getNeighbors() {		return this.neighbors();
	}
	
	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#getOutDegree()
	 */
	@Override
	public int getOutDegree() {		return this.out().size();
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#getOutgoingEdges()
	 */
	@Override
	public Iterable<IElement> getOutgoingEdges() {		return this.out();
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#hasEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean hasEdge(Edge edge) {
		if (edge == null || !(edge instanceof DirectedBlueprintsEdge))
			return false;
		DirectedBlueprintsEdge e = (DirectedBlueprintsEdge) edge;
		return e.getSrcIndex() == this.index && this.out().contains(e)
				|| e.getDstIndex() == this.index && this.in().contains(e);
	}

	@Override
	public boolean hasEdge(Node n1, Node n2) {
		if (n1 == null || n2 == null 
				|| !(n1 instanceof DirectedBlueprintsNode)
				|| !(n2 instanceof DirectedBlueprintsNode))
			return false;
		
		return this.graph.getEdge(n1, n2) != null;
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#hasNeighbor(dna.graph.nodes.DirectedNode)
	 */
	@Override
	public boolean hasNeighbor(DirectedNode n) {
		return this.neighbors().contains(n);
	}

	/**
	 * Return the collection of incoming edges.
	 *
	 * @return the collection of incoming edges
	 */
	protected Collection<IElement> in() {
		Collection<IElement> result = new ArrayList<IElement>();
		Iterable<com.tinkerpop.blueprints.Edge> edges = vertex.getEdges(Direction.IN, "IGDBEdge");
		
		try{
		if (vertex != null) {
			for (com.tinkerpop.blueprints.Edge e : edges) {
				
				result.add((Edge) this.graph.getEdge(e));
			}
		}
		return result;
		}
		finally{
			result = null;
			edges = null;
		}
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#init(dna.graph.datastructures.GraphDataStructure)
	 */
	@Override
	public void init(GraphDataStructure gds) {
		this.gds = gds;
	}

	/**
	 * Neighbors of this node.
	 *
	 * @return the collection
	 */
	private Collection<IElement> neighbors() {
		Collection<IElement> result = new HashSet<IElement>();

		try{
		for (IElement e : this.out()) {
			if (this.in().contains(((DirectedBlueprintsEdge) e).invert())) {
				result.add(((DirectedBlueprintsEdge) e).getDst());
			}
		}
			return result;
		}		
		finally {
			result = null;
		}
	}

	/**
	 * Return the collection of outgoing edges.
	 *
	 * @return the collection of outgoing edges
	 */
	protected Collection<IElement> out() {
		Collection<IElement> result = new ArrayList<IElement>();
		Iterable<com.tinkerpop.blueprints.Edge> edges = vertex.getEdges(Direction.OUT, "IGDBEdge");
		
		try{
		if (vertex != null) {
			for (com.tinkerpop.blueprints.Edge e : edges) {
				
				result.add((Edge) this.graph.getEdge(e));
			}
		}
		return result;
		}
		finally{
			result = null;
			edges = null;
		}
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#removeEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean removeEdge(Edge edge) {
		if (edge == null || !(edge instanceof DirectedBlueprintsEdge))
			return false;
		DirectedBlueprintsEdge e = (DirectedBlueprintsEdge) edge;

		return this.graph.removeEdge(e);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.nodes.IGDBNode#setVertex(com.tinkerpop.blueprints.Vertex)
	 */
	@Override
	public void setGDBNode(Vertex vertex) {
		this.vertex = vertex;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.nodes.IGDBNode#setGraph(com.tinkerpop.blueprints.Graph)
	 */
	@Override
	public void setGraph(IGraph graph) {
		if (graph instanceof BlueprintsGraph)
			this.graph = (BlueprintsGraph) graph;
		else
			throw new RuntimeException("The parameter 'graph' must be an instance of " + BlueprintsGraph.class 
					+ "but was " + graph.getClass());
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.Node#setIndex(int)
	 */
	@Override
	public void setIndex(int index) {
		this.index = index;
		if (vertex == null)
			return;
		this.vertex.setProperty("index", index);
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#switchDataStructure(dna.graph.datastructures.DataStructure.ListType, dna.graph.datastructures.IDataStructure)
	 */
	@Override
	public void switchDataStructure(ListType type,
			IDataStructure newDatastructure) {
		Log.info("Switch datastructure is not available for " + DirectedBlueprintsNode.class);
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.DirectedNode#toString()
	 */
	@Override
	public String toString() {
		return "" + this.getIndex() + " (" + this.in().size() + "/"
				+ this.out().size() + ")";
	}
}
