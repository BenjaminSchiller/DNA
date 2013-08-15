package DataStructures;

import Graph.Nodes.Node;

public interface INodeListDatastructure extends IDataStructure {
	public Node get(int element);

	public int getMaxNodeIndex();
	
	public boolean removeNode(Node element);
}
