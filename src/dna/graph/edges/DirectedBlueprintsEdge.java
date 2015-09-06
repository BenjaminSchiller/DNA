package dna.graph.edges;

import java.util.HashMap;

import com.tinkerpop.blueprints.Edge;

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
	
	/** The edge. */
	protected Edge edge;

	/**
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param src the src
	 * @param dst the dst
	 */
	public DirectedBlueprintsEdge(DirectedBlueprintsNode src, DirectedBlueprintsNode dst) {
		super(src, dst);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.edges.IGDBEdge#setEdge(com.tinkerpop.blueprints.Edge)
	 */
	@Override
	public void setGDBEdge(Edge edge) {
		this.edge = edge;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dna.graph.edges.IGDBEdge#getEdge()
	 */
	@Override
	public Edge getGDBEdge() {
		return this.edge;
	}

	/**
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param s the s
	 * @param g the g
	 */
	public DirectedBlueprintsEdge(String s, IGraph g) {
		super((DirectedNode) getNodeFromStr(0, s, g),
				(DirectedNode) getNodeFromStr(1, s, g));		
	}
	
	/**
	 * Gets the node from str.
	 *
	 * @param index the index
	 * @param s the s
	 * @param g the g
	 * @return the node from str
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
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param s the s
	 * @param g the g
	 * @param addedNodes the added nodes
	 */
	public DirectedBlueprintsEdge(String s, IGraph g, HashMap<Integer, Node> addedNodes) {
		super((DirectedNode) getNodeFromStr(0, s, g, addedNodes)
				, (DirectedNode) getNodeFromStr(1, s, g, addedNodes));
	}

	/**
	 * Gets the node from str.
	 *
	 * @param index the index
	 * @param s the s
	 * @param g the g
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

	/**
	 * Instantiates a new directed blueprints edge.
	 *
	 * @param src the src
	 * @param dst the dst
	 * @param e the e
	 */
	public DirectedBlueprintsEdge(DirectedBlueprintsNode src, DirectedBlueprintsNode dst,
			com.tinkerpop.blueprints.Edge e) {
		super(src, dst);
		this.setGDBEdge(e);
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#getSrc()
	 */
	//
	@Override
	public DirectedBlueprintsNode getSrc() {
		return (DirectedBlueprintsNode) getN1();
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#getDst()
	 */
	@Override
	public DirectedBlueprintsNode getDst() {
		return (DirectedBlueprintsNode) getN2();
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#connectToNodes()
	 */
	@Override
	public boolean connectToNodes() {
		if (this.getSrc().hasEdge(this) && this.getDst().hasEdge(this))
			return true;
		
//		boolean added = this.getSrc().addEdge(this);
//		return added;
		return false;
	}

	/* (non-Javadoc)
	 * @see dna.graph.edges.DirectedEdge#disconnectFromNodes()
	 */
	@Override
	public boolean disconnectFromNodes() {
		if (!this.getSrc().hasEdge(this) && !this.getDst().hasEdge(this))
			return true;

//		boolean removed = this.getSrc().removeEdge(this);
//		return removed;
		return false;
	}
	
	@Override
	public String toString()
	{
		return this.getN1().getIndex() + " " + separator + " " + this.getN2().getIndex();
	}
}
