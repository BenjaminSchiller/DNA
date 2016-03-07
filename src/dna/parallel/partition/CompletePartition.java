package dna.parallel.partition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.parallel.auxData.CompleteAuxData;
import dna.parallel.nodeAssignment.NodeAssignment;
import dna.updates.batch.Batch;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class CompletePartition extends Partition {

	public CompletePartition(Graph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	public static AllPartitions<CompletePartition, CompleteAuxData> partition(
			String name, Graph g, List<Node>[] nodess) {
		Graph[] graphs = getInitialGraphs(g, nodess);
		for (int i = 0; i < graphs.length; i++) {
			for (int j = 0; j < graphs.length; j++) {
				if (j == i) {
					continue;
				}
				for (Node n : nodess[j]) {
					graphs[i].addNode(n);
				}
			}
			for (IElement e_ : g.getEdges()) {
				graphs[i].addEdge((Edge) e_);
			}
		}

		CompletePartition[] partitions = new CompletePartition[nodess.length];
		HashMap<Node, Integer> mapping = new HashMap<Node, Integer>();

		for (int i = 0; i < nodess.length; i++) {
			partitions[i] = new CompletePartition(graphs[i]);
			for (Node n : nodess[i]) {
				mapping.put(n, i);
			}

		}

		@SuppressWarnings("unchecked")
		Set<Node>[] nodes = new HashSet[nodess.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new HashSet<Node>(nodess[i]);
		}

		CompleteAuxData auxData = new CompleteAuxData(
				g.getGraphDatastructures(), nodes);

		return new AllPartitions<CompletePartition, CompleteAuxData>(name,
				PartitionType.Complete, g, partitions, auxData, mapping);
	}

	public static AllChanges split(
			AllPartitions<CompletePartition, CompleteAuxData> all, Batch b,
			NodeAssignment nodeAssignment) {
		// TODO split node additions!

		Batch[] batches = new Batch[all.getPartitionCount()];
		for (int i = 0; i < batches.length; i++) {
			batches[i] = new Batch(b.getGraphDatastructures(), b.getFrom(),
					b.getTo());
			batches[i].addAll(b.getNodeRemovals());
			batches[i].addAll(b.getNodeWeights());
			batches[i].addAll(b.getEdgeAdditions());
			batches[i].addAll(b.getEdgeRemovals());
			batches[i].addAll(b.getEdgeWeights());
		}

		CompleteAuxData auxAdd = new CompleteAuxData(
				all.g.getGraphDatastructures(), all.getPartitionCount());
		for (NodeAddition na : b.getNodeAdditions()) {
			Node n = (Node) na.getNode();
			int p = nodeAssignment.assignNode(all, b, na);
			auxAdd.addNode(p, n);
			for (int i = 0; i < batches.length; i++) {
				if (i == p) {
					Node newNode = all.partitions[i].g.getGraphDatastructures()
							.newNodeInstance(
									na.getNode().getIndex() + "@"
											+ Partition.mainNodeType);
					batches[i].add(new NodeAddition(newNode));
				} else {
					batches[i].add(na);
				}
			}
		}

		CompleteAuxData auxRemove = new CompleteAuxData(
				all.g.getGraphDatastructures(), all.getPartitionCount());
		for (NodeRemoval nr : b.getNodeRemovals()) {
			Node n = (Node) nr.getNode();
			int p = all.auxData.getPartitionIndex(n);
			if (p == -1)
				p = auxAdd.getPartitionIndex(n);
			auxRemove.addNode(p, n);
		}

		AllChanges changes = new AllChanges(b, batches, auxAdd, auxRemove);
		System.out.println("changes: " + changes);
		return changes;
	}
}
