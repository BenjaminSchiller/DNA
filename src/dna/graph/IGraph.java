package dna.graph;

import java.math.BigInteger;
import java.util.Collection;

import org.apache.commons.lang.NotImplementedException;

import dna.graph.DNAGraphFactory.DNAGraphType;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.datastructures.IDataStructure;
import dna.graph.datastructures.DataStructure.ListType;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

/**
 * The Interface IGraph.
 * 
 * @author Matthias
 */
public interface IGraph{

	/**
	 * Adds the node.
	 *
	 * @param n the n
	 * @return true, if successful
	 */
	public abstract boolean addNode(Node n);

	/**
	 * Contains node.
	 *
	 * @param n the n
	 * @return true, if successful
	 */
	public abstract boolean containsNode(Node n);

	/**
	 * Retrieve a node by its index.
	 *
	 * @param index the index
	 * @return the node
	 */
	public abstract Node getNode(int index);

	/**
	 * Retrieve a random node.
	 *
	 * @return the random node
	 */
	public abstract Node getRandomNode();

	/**
	 * Retrieve a collection of all nodes within this graph.
	 *
	 * @return the nodes
	 */
	public abstract Iterable<IElement> getNodes();

	/**
	 * Removes the node.
	 *
	 * @param n the node to be removed
	 * @return true, if successful
	 */
	public abstract boolean removeNode(Node n);

	/**
	 * Retrieve the highest node index within this graph.
	 *
	 * @return the max node index
	 */
	public abstract int getMaxNodeIndex();

	/**
	 * Retrieve the number of nodes within this graph.
	 *
	 * @return the node count
	 */
	public abstract int getNodeCount();

	/**
	 * Adds the edge.
	 *
	 * @param e the edge that should be added
	 * @return true, if successful
	 */
	public abstract boolean addEdge(Edge e);

	/**
	 * Contains edge.
	 *
	 * @param n1 the n1
	 * @param n2 the n2
	 * @return true, if successful
	 */
	public abstract boolean containsEdge(Node n1, Node n2);

	/**
	 * Contains edge.
	 *
	 * @param n1 the n1
	 * @param n2 the n2
	 * @return true, if successful
	 */
	public abstract boolean containsEdge(int n1, int n2);

	/**
	 * Contains edge.
	 *
	 * @param e the e
	 * @return true, if successful
	 */
	public abstract boolean containsEdge(Edge e);

	/**
	 * Contains nodes.
	 *
	 * @param e the e
	 * @return true, if successful
	 */
	public abstract boolean containsNodes(Edge e);

	/**
	 * Get an edge by its attached nodes.
	 *
	 * @param n1 the n1
	 * @param n2 the n2
	 * @return the edge
	 */
	public abstract Edge getEdge(Node n1, Node n2);

	/**
	 * Retrieve a random edge.
	 *
	 * @return the random edge
	 */
	public abstract Edge getRandomEdge();

	/**
	 * Retrieve a collection of all edges within this graph.
	 *
	 * @return the edges
	 */
	public abstract Iterable<IElement> getEdges();

	/**
	 * Removes the edge.
	 *
	 * @param e the e
	 * @return true, if successful
	 */
	public abstract boolean removeEdge(Edge e);

	/**
	 * Retrieve the number of edges within this graph.
	 *
	 * @return the edge count
	 */
	public abstract int getEdgeCount();

	/**
	 * Check whether this is a directed graph or not.
	 *
	 * @return true, if the graph is directed; false otherwise
	 */
	public abstract boolean isDirected();

	/**
	 * 
	 * i.e., V*(V-1) in case of a directed graph, V*(V-1)/2 in case of an
	 * undirected graph
	 * 
	 * @return maximum number of edges the graph could have with the current
	 *         number of nodes
	 */
	public abstract BigInteger getMaxEdgeCount();

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public abstract void setName(String name);

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Sets the timestamp.
	 *
	 * @param timestamp the new timestamp
	 */
	public abstract void setTimestamp(long timestamp);

	/**
	 * Gets the timestamp.
	 *
	 * @return the timestamp
	 */
	public abstract long getTimestamp();

	/**
	 * Gets the graph datastructures.
	 *
	 * @return the graph datastructures
	 */
	public abstract GraphDataStructure getGraphDatastructures();

	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	public abstract boolean equals(Object obj);

	/**
	 * To string.
	 *
	 * @return the string
	 */
	public abstract String toString();

	/**
	 * Prints the.
	 */
	public abstract void print();

	/**
	 * Prints the all.
	 */
	public abstract void printAll();

	/**
	 * Prints the v.
	 */
	public abstract void printV();

	/**
	 * Prints the e.
	 */
	public abstract void printE();

	/**
	 * Switch data structure.
	 *
	 * @param type the type
	 * @param newDatastructureType the new datastructure type
	 */
	public abstract void switchDataStructure(ListType type,
			Class<? extends IDataStructure> newDatastructureType);
	
	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public abstract DNAGraphType getInstanceType();
	
	/**
	 * release all resources.
	 */
	public abstract void close();
	
}