package dna.metrics.connectedComponents;

import java.util.LinkedList;

public class ComponentVertex {

	public ComponentVertex(int index) {
		this.index = index;
	}

	private int index = 0;
	private LinkedList<DAGEdge> edges = new LinkedList<DAGEdge>();

	public LinkedList<DAGEdge> getEdges() {
		return edges;
	}

	public void addEdge(DAGEdge edge) {
		edges.add(edge);
	}

	public int getIndex() {
		return index;
	}

}
