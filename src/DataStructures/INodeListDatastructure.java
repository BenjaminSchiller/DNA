package DataStructures;

import Graph.Nodes.Node;

public interface INodeListDatastructure extends IDataStructure {
	public boolean add(Node element);
	public boolean contains(Node element);
	public boolean remove(Node element);
	public int getMaxNodeIndex();
}
