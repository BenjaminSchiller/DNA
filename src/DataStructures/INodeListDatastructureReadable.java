package DataStructures;

import Graph.Nodes.Node;

public interface INodeListDatastructureReadable extends INodeListDatastructure, IReadable {
	public Node get(int element);
}
