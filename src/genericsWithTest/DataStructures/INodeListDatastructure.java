package genericsWithTest.DataStructures;

import genericsWithTest.Node;

public interface INodeListDatastructure extends IDataStructure {
	public Node get(int element);

	public int getMaxNodeIndex();
}
