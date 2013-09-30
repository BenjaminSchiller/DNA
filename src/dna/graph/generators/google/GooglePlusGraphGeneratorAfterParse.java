package dna.graph.generators.google;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.generators.directed.DirectedGraphGenerator;
import dna.graph.nodes.DirectedNode;
import dna.util.parameters.Parameter;

public class GooglePlusGraphGeneratorAfterParse extends DirectedGraphGenerator
		implements IDtoForDatabase {

	private HashMap<String, Integer> mapping;
	private HashMap<DirectedNode, Long> lastSeen;
	private HashMap<DirectedNode, Integer> count;
	private int nodeLabelCounter;
	private GraphNodeAdditionType type;
	private int n;
	private HashMap<String, DirectedNode> nodes;
	private HashSet<DirectedEdge> edges;

	public GooglePlusGraphGeneratorAfterParse(GraphDataStructure d, int n,
			GraphNodeAdditionType type, Dto dto, Parameter[] p) {
		super(dto.name, p, d, 0L, 0, 0);
		this.nodes = dto.nodes;
		this.edges = dto.edges;
		this.mapping = dto.mapping;
		this.lastSeen = dto.lastSeen;
		this.count = dto.count;
		this.nodeLabelCounter = dto.nodeLabelCounter;
		this.type = type;
		this.n = n;
	}

	@Override
	public Graph generate() {
		Graph g = this.newGraphInstance();

		if (type == GraphNodeAdditionType.AfterNTimes) {
			if (n == 1) {
				g = buildGraph(g);
			}
		}

		if (type == GraphNodeAdditionType.EverySeenNode) {
			g = buildGraph(g);
		}

		return g;
	}

	private Graph buildGraph(Graph g) {
		for (DirectedNode n : nodes.values()) {
			g.addNode(n);
		}
		for (DirectedEdge e : edges) {
			g.addEdge(e);
		}
		return g;
	}

	@Override
	public Dto getDto() {
		return new Dto(nodes, edges, mapping, count, lastSeen,
				nodeLabelCounter, this.getName());
	}
}
