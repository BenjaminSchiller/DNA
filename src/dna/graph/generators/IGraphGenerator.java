package dna.graph.generators;

import dna.graph.Graph;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;

public interface IGraphGenerator {
	/**
	 * Receive a new graph generated through this generator
	 * 
	 * @return
	 */
	public Graph generate();

	/**
	 * Check whether a specific node type can be generated using this generator
	 * 
	 * @param nodeType
	 * @return
	 */
	public boolean canGenerateNodeType(Class<? extends Node> nodeType);
	
	/**
	 * Check whether a specific edge type can be generated using this generator
	 * 
	 * @param nodeType
	 * @return
	 */
	public boolean canGenerateEdgeType(Class<? extends Edge> edgeType);	
}
