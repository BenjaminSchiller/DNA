package Factories;

import Graph.Graph;
import Graph.Nodes.Node;

public interface IGraphGenerator {
	public Graph generate();

	public boolean canGenerateNodeType(Class<? extends Node> nodeType);
}
