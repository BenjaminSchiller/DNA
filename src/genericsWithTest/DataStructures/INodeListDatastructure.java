package genericsWithTest.DataStructures;

import java.util.Collection;

import genericsWithTest.Node;

public interface INodeListDatastructure extends IDataStructure {
	public Node get(int element);

	public int getMaxNodeIndex();
	
	public Collection<Node> getNodes();
}
