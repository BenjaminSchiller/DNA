package dna.graph.edges;

import java.util.HashMap;

import com.tinkerpop.blueprints.Edge;

import dna.graph.BlueprintsGraph;
import dna.graph.IGraph;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.DirectedBlueprintsNode;
import dna.graph.nodes.Node;
import dna.util.MathHelper;

/**
 * The Class DirectedBlueprintsEdge.
 * 
 * @author Matthias
 */
public class DirectedBlueprintsEdge extends DirectedEdge implements IGDBEdge<Edge> {
	
	/** The edge identifier. */
	protected Object gdbEdgeId;
	
	/** The graph. */
	protected BlueprintsGraph graph;

	/**
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param src the source node
	 * @param dst the destination node
	 */
	public DirectedBlueprintsEdge(DirectedBlueprintsNode src, DirectedBlueprintsNode dst) {
		super(src, dst);
	}

	/**
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param src the source node
	 * @param dst the destination node
	 * @param e the graph database edge
	 */
	public DirectedBlueprintsEdge(DirectedBlueprintsNode src, DirectedBlueprintsNode dst,
			Object gdbEdgeId) {
		super(src, dst);
		this.setGDBEdgeId(this.gdbEdgeId);
	}

	/**
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param s the string
	 * @param g the graph
	 */
	public DirectedBlueprintsEdge(String s, IGraph g) {
		super((DirectedNode) getNodeFromStr(0, s, g),
				(DirectedNode) getNodeFromStr(1, s, g));
		this.setGraph(g);
	}
	
	/**
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param s the string
	 * @param g the graph
	 * @param addedNodes the added nodes
	 */
	public DirectedBlueprintsEdge(String s, IGraph g, HashMap<Integer, Node> addedNodes) {
		super((DirectedNode) getNodeFromStr(0, s, g, addedNodes)
				, (DirectedNode) getNodeFromStr(1, s, g, addedNodes));
		this.setGraph(g);
	}	

	/**
	 * Gets the node from the string.
	 *
	 * @param index the index
	 * @param s the string
	 * @param g the graph
	 * @return the node from the string
	 */
	private static Node getNodeFromStr(int index, String s, IGraph g) {
		if (index < 0 || index > 1) {
			throw new IndexOutOfBoundsException(
					"The index must be 0 or 1, but was " + index + ".");
		}
		
		String[] temp = s.split(DirectedEdge.separator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into a directed edge");
		}		
		return (DirectedBlueprintsNode) g.getNode(MathHelper
				.parseInt(temp[index]));		
	}

	/**
	 * Gets the node from str.
	 *
	 * @param index the index
	 * @param s the string
	 * @param g the graph
	 * @param addedNodes the added nodes
	 * @return the node from str
	 */
	private static Node getNodeFromStr(int index, String s, IGraph g,
			HashMap<Integer, Node> addedNodes) {
		if (index < 0 || index > 1) {
			throw new IndexOutOfBoundsException(
					"The index must be 0 or 1, but was " + index + ".");
		}

		String[] temp = s.split(DirectedBlueprintsEdge.separator);
		if (temp.length != 2) {
			throw new IllegalArgumentException("Cannot parse " + s
					+ " into a directed edge");
		}
		
		int idx = MathHelper.parseInt(temp[index]);
		if (addedNodes.containsKey(idx)) {
			return (DirectedBlueprintsNode) addedNodes.get(idx);
		} else {
			return (DirectedBlueprintsNode) g.getNode(idx);
		}
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#connectToNodes()
	 */
	@Override
	public boolean connectToNodes() {
		if (this.getSrc().hasEdge(this) && this.getDst().hasEdge(this))
			return true;
		
		return false;
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#disconnectFromNodes()
	 */
	@Override
	public boolean disconnectFromNodes() {
		if (!this.getSrc().hasEdge(this) && !this.getDst().hasEdge(this))
			return true;

		return false;
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#getDst()
	 */
	@Override
	public DirectedBlueprintsNode getDst() {
		return (DirectedBlueprintsNode) getN2();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.edges.IGDBEdge#getEdge()
	 */
	@Override
	public Edge getGDBEdge() {
		if (this.graph == null || this.gdbEdgeId == null)
			return null;
		else
			return this.graph.getGDBEdge(this.gdbEdgeId);
	}

	@Override
	public Object getGDBEdgeId() {
		return this.gdbEdgeId;
	}
	
	@Override
	public IGraph getGraph() {
		return this.graph;
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#getSrc()
	 */
	//
	@Override
	public DirectedBlueprintsNode getSrc() {
		return (DirectedBlueprintsNode) getN1();
	}

	@Override
	public void setGDBEdgeId(Object gdbEdgeId) {
		this.gdbEdgeId = gdbEdgeId;
	}

	@Override
	public void setGraph(IGraph graph) {
		if (graph instanceof BlueprintsGraph)
			this.graph = (BlueprintsGraph) graph;
		else
			throw new RuntimeException("The parameter 'graph' must be an instance of " + BlueprintsGraph.class 
					+ "but was " + graph.getClass());
	}

	@Override
	public String toString()
	{
		return this.getN1().getIndex() + " " + separator + " " + this.getN2().getIndex();
	}
}
