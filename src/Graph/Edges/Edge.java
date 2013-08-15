package Graph.Edges;

import Graph.Element;
import Graph.IElement;

public abstract class Edge extends Element implements IElement {
	private int index;
	
	public int getIndex() {
		return index;
	}
}
