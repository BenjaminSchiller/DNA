package dna.graph.generators.google;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.util.parameters.Parameter;

public class GooglePlusGraphGeneratorAfterParse {

	private HashMap<String, Integer> mapping;
	private HashMap<Integer, Long> lastSeen;
	private HashMap<Integer, Integer> count;
	private int nodeLabelCounter;
	private GraphNodeAdditionType type;
	private int n;
	private HashMap<String, DirectedNode> nodes;
	private HashSet<DirectedEdge> edges;
	private GraphDataStructure ds;

	public GooglePlusGraphGeneratorAfterParse(GraphDataStructure d, int n,
			GraphNodeAdditionType type, ParseDto dto, Parameter[] p) {
		this.ds = d;
		this.nodes = dto.nodes;
		this.edges = dto.edges;
		this.mapping = dto.mapping;
		this.lastSeen = new HashMap<>();
		this.count = new HashMap<>();
		this.nodeLabelCounter = dto.nodeLabelCounter;
		this.type = type;
		this.n = n;
	}

	public Graph generate() {
		Graph g = ds.newGraphInstance(" ", 0, 0, 0);

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

	public MappingDto getDto() {
		return new MappingDto(mapping, count, lastSeen, nodeLabelCounter, "");
	}
}
