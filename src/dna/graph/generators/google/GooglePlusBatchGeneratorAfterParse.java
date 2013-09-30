package dna.graph.generators.google;

import java.util.HashMap;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.datastructures.GraphDataStructure;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.directed.DirectedBatchGenerator;

public class GooglePlusBatchGeneratorAfterParse extends DirectedBatchGenerator
		implements IDtoForDatabase {

	private HashMap<String, Integer> mapping;
	private HashMap<DirectedNode, Long> lastSeen;
	private HashMap<String, DirectedNode> newNodes;
	private HashSet<DirectedEdge> newEdges;
	private HashMap<String, DirectedNode> oldNodes;
	private HashSet<DirectedEdge> oldEdges;
	private GraphNodeDeletionType deletionType;
	private GraphNodeAdditionType additionType;
	private HashMap<DirectedNode, Integer> count;
	private int n;
	private int nodeLabelCounter;

	public GooglePlusBatchGeneratorAfterParse(String name,
			GraphDataStructure datastructures,
			GraphNodeAdditionType additionType,
			GraphNodeDeletionType deletionType, Dto dto1, Dto dto2, int n) {
		super(name, datastructures);
		this.newNodes = dto2.nodes;
		this.newEdges = dto2.edges;
		this.oldNodes = dto1.nodes;
		this.oldEdges = dto1.edges;

		this.mapping = dto1.mapping;
		this.mapping.putAll(dto2.mapping);
		this.lastSeen = dto1.lastSeen;
		this.lastSeen.putAll(dto2.lastSeen);

		this.deletionType = deletionType;
		this.additionType = additionType;
		this.count = dto1.count;
		this.count.putAll(dto2.count);
		this.n = n;
		this.nodeLabelCounter = dto2.nodeLabelCounter;
	}

	@Override
	public Batch<DirectedEdge> generate(Graph graph) {
		Graph g = graph;
		Batch<DirectedEdge> b = new Batch<DirectedEdge>(this.ds,
				graph.getTimestamp(), graph.getTimestamp() + 1);

		// case 1.1
		if (additionType == GraphNodeAdditionType.EverySeenNode
				&& deletionType == GraphNodeDeletionType.NoDeletions) {
			for (DirectedNode node : newNodes.values()) {
				if (!oldNodes.containsValue(node)) {
					b.add(new NodeAddition<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		// case 1.2
		if (additionType == GraphNodeAdditionType.EverySeenNode
				&& deletionType == GraphNodeDeletionType.AfterNTimes) {
			for (DirectedNode n : newNodes.values()) {
				if (!oldNodes.containsValue(n)) {
					b.add(new NodeAddition<DirectedEdge>(n));
				}
			}
			for (DirectedNode node : oldNodes.values()) {
				if (lastSeen.get(node) + n < b.getTo()) {
					b.add(new NodeRemoval<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		// case 1.3
		if (additionType == GraphNodeAdditionType.EverySeenNode
				&& deletionType == GraphNodeDeletionType.NotSeenInBatch) {
			for (DirectedNode n : newNodes.values()) {
				if (!oldNodes.containsValue(n)) {
					b.add(new NodeAddition<DirectedEdge>(n));
				}
			}
			for (DirectedNode node : oldNodes.values()) {
				if (!newNodes.containsValue(node)) {
					b.add(new NodeRemoval<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		// case 1.4
		if (additionType == GraphNodeAdditionType.EverySeenNode
				&& deletionType == GraphNodeDeletionType.EmptyNodes) {
			for (DirectedNode node : newNodes.values()) {
				if (!oldNodes.containsValue(node) && node.getDegree() > 0) {
					b.add(new NodeAddition<DirectedEdge>(node));
				}
			}
			for (DirectedNode node : oldNodes.values()) {
				if (node.getDegree() > 0) {
					b.add(new NodeRemoval<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		// case 2.1
		if (additionType == GraphNodeAdditionType.AfterNTimes
				&& deletionType == GraphNodeDeletionType.NoDeletions) {
			for (DirectedNode node : newNodes.values()) {
				if (!oldNodes.containsValue(node) && count.get(node) == n) {
					b.add(new NodeAddition<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		// case 2.2
		if (additionType == GraphNodeAdditionType.AfterNTimes
				&& deletionType == GraphNodeDeletionType.AfterNTimes) {
			for (DirectedNode node : newNodes.values()) {
				if (!oldNodes.containsValue(node) && count.get(node) == n) {
					b.add(new NodeAddition<DirectedEdge>(node));
				}
			}
			for (DirectedNode node : oldNodes.values()) {
				if (lastSeen.get(node) + n < b.getTo()) {
					b.add(new NodeRemoval<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		// case 2.3
		if (additionType == GraphNodeAdditionType.AfterNTimes
				&& deletionType == GraphNodeDeletionType.NotSeenInBatch) {
			for (DirectedNode node : newNodes.values()) {
				if (!oldNodes.containsValue(node) && count.get(node) == n) {
					b.add(new NodeAddition<DirectedEdge>(node));
				}
			}
			for (DirectedNode node : oldNodes.values()) {
				if (!newNodes.containsValue(node)) {
					b.add(new NodeRemoval<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		// case 2.4
		if (additionType == GraphNodeAdditionType.AfterNTimes
				&& deletionType == GraphNodeDeletionType.EmptyNodes) {
			for (DirectedNode node : newNodes.values()) {
				if (!oldNodes.containsValue(node) && node.getDegree() > 0
						&& count.get(node) == n) {
					b.add(new NodeAddition<DirectedEdge>(node));
				}
			}
			for (DirectedNode node : oldNodes.values()) {
				if (node.getDegree() > 0) {
					b.add(new NodeRemoval<DirectedEdge>(node));
				}
			}
			for (DirectedEdge e : newEdges) {
				if (!oldEdges.contains(e)) {
					b.add(new EdgeAddition<DirectedEdge>(e));
					continue;
				}
			}
			for (DirectedEdge e : oldEdges) {
				if (newEdges.contains(e)) {
					b.add(new EdgeRemoval<DirectedEdge>(e));
				}
			}
		}

		return b;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	@Override
	public Dto getDto() {

		return new Dto(newNodes, newEdges, mapping, count, lastSeen,
				nodeLabelCounter, this.getName());
	}

}
