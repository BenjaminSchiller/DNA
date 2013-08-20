package dna.metrics.connectedComponents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import dna.graph.directed.DirectedEdge;

public class ComponentVertex {

	public ComponentVertex(int index) {
		this.index = index;
		this.ed = new HashMap<>();
	}

	private int index = 0;
	private LinkedList<DAGEdge> edges = new LinkedList<DAGEdge>();
	public Map<Integer, Set<DirectedEdge>> ed;

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
