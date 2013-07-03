package dna.updates.undirected;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.GraphDatastructures;
import dna.graph.Node;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.NodeAddition;
import dna.util.Rand;

public class UndirectedGrowingNetwork extends UndirectedBatchGenerator {

	public UndirectedGrowingNetwork(
			int nodes,
			GraphDatastructures<UndirectedGraph, UndirectedNode, UndirectedEdge> ds) {
		super("growingNwetwork", ds);
		this.nodes = nodes;
		this.links = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();
	}

	private int nodes;

	private HashMap<UndirectedNode, HashSet<UndirectedNode>> links;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Batch<UndirectedEdge> generate(
			Graph<? extends Node<UndirectedEdge>, UndirectedEdge> graph) {

		this.links = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();

		UndirectedGraph g = (UndirectedGraph) graph;
		Batch<UndirectedEdge> batch = new Batch<UndirectedEdge>(
				(GraphDatastructures) this.ds, graph.getTimestamp(),
				graph.getTimestamp() + 1, this.nodes, 0, 0, 0, 0, 0);

		int index = graph.getMaxNodeIndex() + 1;
		HashSet<UndirectedNode> nodeNodes = new HashSet<UndirectedNode>();
		while (nodeNodes.size() < this.nodes) {
			UndirectedNode n = (UndirectedNode) this.ds.newNodeInstance(index);
			if (!g.containsNode(n)) {
				batch.add(new NodeAddition<UndirectedEdge>(n));
				this.addEdges(n, batch, g, nodeNodes);
				nodeNodes.add(n);
			}
			index++;
		}

		this.links = null;

		return batch;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addEdges(UndirectedNode n, Batch<UndirectedEdge> batch,
			UndirectedGraph g, HashSet<UndirectedNode> newNodes) {
		UndirectedNode bootstrap = this.getRandomNode(g, newNodes);
		HashSet<UndirectedNode> links = new HashSet<UndirectedNode>();
		links.add(bootstrap);
		if (this.links.containsKey(bootstrap)) {
			for (UndirectedNode dest : this.links.get(bootstrap)) {
				links.add(dest);
			}
		}
		for (UndirectedEdge e : bootstrap.getEdges()) {
			UndirectedNode dest = e.getDifferingNode(bootstrap);
			links.add(dest);
		}
		for (UndirectedNode link : links) {
			batch.add(new EdgeAddition(this.ds.newEdgeInstance(n, link)));
			this.addLink(n, link);
			this.addLink(link, n);
		}
	}

	private void addLink(UndirectedNode node, UndirectedNode link) {
		if (this.links.containsKey(node)) {
			this.links.get(node).add(link);
		} else {
			HashSet<UndirectedNode> set = new HashSet<UndirectedNode>();
			set.add(link);
			this.links.put(node, set);
		}
	}

	private UndirectedNode getRandomNode(UndirectedGraph g,
			HashSet<UndirectedNode> newNodes) {
		int r = Rand.rand.nextInt(g.getNodeCount() + newNodes.size());
		if (r < g.getNodeCount()) {
			UndirectedNode node = (UndirectedNode) g.getRandomNode();
			return node;
		}
		r -= g.getNodeCount();
		int counter = 0;
		for (UndirectedNode node : newNodes) {
			if (counter == r) {
				return node;
			}
			counter++;
		}
		return null;
	}

	@Override
	public void reset() {
	}

}
