//package dna.graph.generators.google;
//
//import java.util.HashMap;
//import java.util.HashSet;
//
//import dna.graph.Graph;
//import dna.graph.datastructures.GraphDataStructure;
//import dna.graph.edges.DirectedEdge;
//import dna.graph.nodes.DirectedNode;
//import dna.updates.batch.Batch;
//import dna.updates.generators.BatchGenerator;
//import dna.updates.update.EdgeAddition;
//import dna.updates.update.EdgeRemoval;
//import dna.updates.update.NodeAddition;
//import dna.updates.update.NodeRemoval;
//import dna.util.parameters.Parameter;
//
//public class GooglePlusBatchGeneratorAfterParse extends BatchGenerator
//		implements IDtoForDatabase {
//
//	private HashMap<String, Integer> mapping;
//	private HashMap<DirectedNode, Long> lastSeen;
//	private HashMap<String, DirectedNode> newNodes;
//	private HashSet<DirectedEdge> newEdges;
//	private HashMap<String, DirectedNode> oldNodes;
//	private HashSet<DirectedEdge> oldEdges;
//	private GraphNodeDeletionType deletionType;
//	private GraphNodeAdditionType additionType;
//	private HashMap<DirectedNode, Integer> count;
//	private int n;
//	private int nodeLabelCounter;
//	private GraphDataStructure ds;
//
//	public GooglePlusBatchGeneratorAfterParse(String name,
//			GraphNodeAdditionType additionType,
//			GraphNodeDeletionType deletionType, MappingDto dto1, MappingDto dto2, int n) {
//		super(name, new Parameter[0]);
//		this.newNodes = dto2.nodes;
//		this.newEdges = dto2.edges;
//		this.oldNodes = dto1.nodes;
//		this.oldEdges = dto1.edges;
//
//		this.mapping = dto1.mapping;
//		this.mapping.putAll(dto2.mapping);
//		this.lastSeen = dto1.lastSeen;
//		this.lastSeen.putAll(dto2.lastSeen);
//
//		this.deletionType = deletionType;
//		this.additionType = additionType;
//		this.count = dto1.count;
//		this.count.putAll(dto2.count);
//		this.n = n;
//		this.nodeLabelCounter = dto2.nodeLabelCounter;
//	}
//
//	@Override
//	public Batch generate(Graph graph) {
//		Graph g = graph;
//		this.ds = g.getGraphDatastructures();
//		Batch b = new Batch(this.ds, graph.getTimestamp(),
//				graph.getTimestamp() + 1);
//
//		// case 1.1
//		if (additionType == GraphNodeAdditionType.EverySeenNode
//				&& deletionType == GraphNodeDeletionType.NoDeletions) {
//			for (DirectedNode node : newNodes.values()) {
//				if (!oldNodes.containsValue(node)) {
//					b.add(new NodeAddition(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		// case 1.2
//		if (additionType == GraphNodeAdditionType.EverySeenNode
//				&& deletionType == GraphNodeDeletionType.AfterNTimes) {
//			for (DirectedNode n : newNodes.values()) {
//				if (!oldNodes.containsValue(n)) {
//					b.add(new NodeAddition(n));
//				}
//			}
//			for (DirectedNode node : oldNodes.values()) {
//				if (lastSeen.get(node) + n < b.getTo()) {
//					b.add(new NodeRemoval(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		// case 1.3
//		if (additionType == GraphNodeAdditionType.EverySeenNode
//				&& deletionType == GraphNodeDeletionType.NotSeenInBatch) {
//			for (DirectedNode n : newNodes.values()) {
//				if (!oldNodes.containsValue(n)) {
//					b.add(new NodeAddition(n));
//				}
//			}
//			for (DirectedNode node : oldNodes.values()) {
//				if (!newNodes.containsValue(node)) {
//					b.add(new NodeRemoval(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		// case 1.4
//		if (additionType == GraphNodeAdditionType.EverySeenNode
//				&& deletionType == GraphNodeDeletionType.EmptyNodes) {
//			for (DirectedNode node : newNodes.values()) {
//				if (!oldNodes.containsValue(node) && node.getDegree() > 0) {
//					b.add(new NodeAddition(node));
//				}
//			}
//			for (DirectedNode node : oldNodes.values()) {
//				if (node.getDegree() > 0) {
//					b.add(new NodeRemoval(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		// case 2.1
//		if (additionType == GraphNodeAdditionType.AfterNTimes
//				&& deletionType == GraphNodeDeletionType.NoDeletions) {
//			for (DirectedNode node : newNodes.values()) {
//				if (!oldNodes.containsValue(node) && count.get(node) == n) {
//					b.add(new NodeAddition(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		// case 2.2
//		if (additionType == GraphNodeAdditionType.AfterNTimes
//				&& deletionType == GraphNodeDeletionType.AfterNTimes) {
//			for (DirectedNode node : newNodes.values()) {
//				if (!oldNodes.containsValue(node) && count.get(node) == n) {
//					b.add(new NodeAddition(node));
//				}
//			}
//			for (DirectedNode node : oldNodes.values()) {
//				if (lastSeen.get(node) + n < b.getTo()) {
//					b.add(new NodeRemoval(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		// case 2.3
//		if (additionType == GraphNodeAdditionType.AfterNTimes
//				&& deletionType == GraphNodeDeletionType.NotSeenInBatch) {
//			for (DirectedNode node : newNodes.values()) {
//				if (!oldNodes.containsValue(node) && count.get(node) == n) {
//					b.add(new NodeAddition(node));
//				}
//			}
//			for (DirectedNode node : oldNodes.values()) {
//				if (!newNodes.containsValue(node)) {
//					b.add(new NodeRemoval(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		// case 2.4
//		if (additionType == GraphNodeAdditionType.AfterNTimes
//				&& deletionType == GraphNodeDeletionType.EmptyNodes) {
//			for (DirectedNode node : newNodes.values()) {
//				if (!oldNodes.containsValue(node) && node.getDegree() > 0
//						&& count.get(node) == n) {
//					b.add(new NodeAddition(node));
//				}
//			}
//			for (DirectedNode node : oldNodes.values()) {
//				if (node.getDegree() > 0) {
//					b.add(new NodeRemoval(node));
//				}
//			}
//			for (DirectedEdge e : newEdges) {
//				if (!oldEdges.contains(e)) {
//					b.add(new EdgeAddition(e));
//					continue;
//				}
//			}
//			for (DirectedEdge e : oldEdges) {
//				if (newEdges.contains(e)) {
//					b.add(new EdgeRemoval(e));
//				}
//			}
//		}
//
//		return b;
//	}
//
//	@Override
//	public void reset() {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public MappingDto getDto() {
//
//		return new MappingDto(newNodes, newEdges, mapping, count, lastSeen,
//				nodeLabelCounter, this.getName());
//	}
//
// }
