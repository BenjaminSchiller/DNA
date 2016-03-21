package dna.graph.nodes;

import java.util.ArrayList;
import java.util.Collection;
import com.google.common.collect.Iterables;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;

import dna.graph.BlueprintsGraph;
import dna.graph.IElement;
import dna.graph.IGraph;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedBlueprintsEdge;
import dna.util.Log;
import dna.util.MathHelper;

/**
 * The Class UndirectedBlueprintsNode.
 * 
 * @author Matthias
 */
public class UndirectedBlueprintsNode extends UndirectedNode implements
		IGDBNode<Vertex> {
	
	private Object gdbNodeId;
	
	/** The gds. */
	protected GraphDataStructure gds;

	/** The graph. */
	protected BlueprintsGraph graph;

	/**
	 * Instantiates a new undirected blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 */
	public UndirectedBlueprintsNode(int i, GraphDataStructure gds) {
		super(i, gds);		
		this.setIndex(i);
	}

	/**
	 * Instantiates a new undirected blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 * @param graph the graph
	 */
	public UndirectedBlueprintsNode(int i, GraphDataStructure gds, IGraph graph) {
		super(i, gds);
		this.setIndex(i);
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new undirected blueprints node.
	 *
	 * @param i the i
	 * @param gds the gds
	 * @param getGDBNode() the getGDBNode()
	 * @param graph the graph
	 */
	public UndirectedBlueprintsNode(Integer i, GraphDataStructure gds,
			Object gdbNodeId, IGraph graph) {
		super(i, gds);
		this.setGDBNodeId(gdbNodeId);
		this.setIndex(i);
		this.setGraph(graph);
	}

	/**
	 * Instantiates a new undirected blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 */
	public UndirectedBlueprintsNode(String str, GraphDataStructure gds) {
		this(MathHelper.parseInt(str), gds);
	}

	/**
	 * Instantiates a new undirected blueprints node.
	 *
	 * @param str the str
	 * @param gds the gds
	 * @param graph the graph
	 */
	public UndirectedBlueprintsNode(String str, GraphDataStructure gds,
			IGraph graph) {
		this(MathHelper.parseInt(str), gds, graph);
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#addEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean addEdge(Edge edge) {
		if (!(edge instanceof UndirectedBlueprintsEdge))
			return false;
		UndirectedBlueprintsEdge e = (UndirectedBlueprintsEdge) edge;

		if (e.getGDBEdge() != null && this.graph.containsEdge(edge)) {
			return false;
		}

		return this.graph.addEdge(e);				
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#getDegree()
	 */
	@Override
	public int getDegree() {
		return Iterables.size(this.getEdges());
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#getEdges()
	 */
	@Override
	public Iterable<IElement> getEdges() {
		Collection<Edge> result = new ArrayList<Edge>();
		for (com.tinkerpop.blueprints.Edge e : getGDBNode().getEdges(Direction.BOTH,
				"IGDBEdge")) {
			Edge edge = this.graph.getEdge(e);

			if (!result.contains((Edge) edge))
				result.add((Edge) edge);
		}

		return new ArrayList<IElement>(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.nodes.IGDBNode#getVertex()
	 */
	@Override
	public Vertex getGDBNode() {
		if (this.graph == null || this.gdbNodeId == null)
			return null;
		else
			return this.graph.getGDBNode(this.gdbNodeId);
	}

	@Override
	public Object getGDBNodeId() {
		return this.gdbNodeId;
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
	 * @see dna.graph.nodes.Node#getIndex()
	 */
	@Override
	public int getIndex() {
		if (this.getGDBNode() == null)
			return this.index;
		Integer idx = (Integer) this.getGDBNode().getProperty("index");
		if (idx == null)
			this.setIndex(this.index);
		return this.index;
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#hasEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean hasEdge(Edge edge) {
		if (edge == null || !(edge instanceof UndirectedBlueprintsEdge))
			return false;
		UndirectedBlueprintsEdge e = (UndirectedBlueprintsEdge) edge;

		return ((ArrayList<IElement>) this.getEdges()).contains(e);
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#init(dna.graph.datastructures.GraphDataStructure)
	 */
	@Override
	public void init(GraphDataStructure gds) {
		this.gds = gds;
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#removeEdge(dna.graph.edges.Edge)
	 */
	@Override
	public boolean removeEdge(Edge edge) {
		if (edge == null || !(edge instanceof UndirectedBlueprintsEdge))
			return false;
		UndirectedBlueprintsEdge e = (UndirectedBlueprintsEdge) edge;

		return this.graph.removeEdge(e);
	}

	@Override
	public void setGDBNodeId(Object gdbNodeId) {
		this.gdbNodeId = gdbNodeId;		
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
			throw new RuntimeException(
					"The parameter 'graph' must be an instance of "
							+ BlueprintsGraph.class + "but was "
							+ graph.getClass());
	}
	
	/* (non-Javadoc)
	 * @see dna.graph.nodes.Node#setIndex(int)
	 */
	@Override
	public void setIndex(int index) {
		this.index = index;
		if (this.getGDBNode() == null)
			return;
		this.getGDBNode().setProperty("index", index);
	}

	/* (non-Javadoc)
	 * @see dna.graph.nodes.UndirectedNode#switchDataStructure(dna.graph.datastructures.DataStructure.ListType, dna.graph.datastructures.IDataStructure)
	 */
	@Override
	public void switchDataStructure(ListType type,
			IDataStructure newDatastructure) {
		Log.info("Switch datastructure is not available for " + UndirectedBlueprintsNode.class);
	}

	public String toString() {
		return "" + this.getIndex() + " (" + this.getDegree() + ")";
	}
}
