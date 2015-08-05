package dna.visualization.graph.rules;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;

/** Abstract class for all graph style rules. **/
public abstract class GraphStyleRule {

	protected String name;

	// used for rule application order
	protected int index;

	public abstract void onNodeAddition(Node n);

	public abstract void onNodeRemoval(Node n);

	public abstract void onNodeWeightChange(Node n);

	public abstract void onEdgeAddition(Edge e, Node n1, Node n2);

	public abstract void onEdgeRemoval(Edge e, Node n1, Node n2);

	public abstract void onEdgeWeightChange(Edge e);

	public String getName() {
		return this.name;
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public abstract String toString();

}
