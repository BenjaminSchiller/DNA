package DataStructures;

import Graph.Nodes.Node;

public interface INodeListDatastructure extends IDataStructure {
	public Node get(int element);
	public boolean removeNode(Node element);
	public int getMaxNodeIndex();
}
