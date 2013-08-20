package dna.metrics.connectedComponents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import dna.graph.directed.DirectedEdge;

public class ComponentVertex {

	public ComponentVertex(int index) {
		this.index = index;
<<<<<<< HEAD
=======
		this.ed = new HashMap<>();
>>>>>>> some stuff
	}

	private int index = 0;
	private LinkedList<DAGEdge> edges = new LinkedList<DAGEdge>();
<<<<<<< HEAD
=======
	public Map<Integer, Set<DirectedEdge>> ed;
>>>>>>> some stuff

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
