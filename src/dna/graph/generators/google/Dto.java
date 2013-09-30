package dna.graph.generators.google;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;

public class Dto {
	public final HashMap<String, DirectedNode> nodes;
	public final HashSet<DirectedEdge> edges;
	public final HashMap<String, Integer> mapping;
	public final HashMap<DirectedNode, Integer> count;
	public final HashMap<DirectedNode, Long> lastSeen;
	public final String name;
	public final int nodeLabelCounter;

	public Dto(HashMap<String, DirectedNode> nodes,
			HashSet<DirectedEdge> edges, HashMap<String, Integer> mapping,
			HashMap<DirectedNode, Integer> count,
			HashMap<DirectedNode, Long> lastSeen, int nodeLabelCounter,
			String name) {
		this.nodes = nodes;
		this.edges = edges;
		this.mapping = mapping;
		this.count = count;
		this.lastSeen = lastSeen;
		this.nodeLabelCounter = nodeLabelCounter;
		this.name = name;
	}
}
