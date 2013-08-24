package Factories;

import Graph.Graph;
import Graph.Nodes.Node;

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
}
