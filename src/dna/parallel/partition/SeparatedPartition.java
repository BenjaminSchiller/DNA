package dna.parallel.partition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.parallel.auxData.SeparatedAuxData;
import dna.parallel.nodeAssignment.NodeAssignment;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class SeparatedPartition extends Partition {

	public SeparatedPartition(Graph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	public static AllPartitions<SeparatedPartition, SeparatedAuxData> partition(
			String name, PartitionType partitionType, Graph g,
			List<Node>[] nodess) {
		Graph[] graphs = getInitialGraphs(g, nodess);
		SeparatedPartition[] partitions = new SeparatedPartition[nodess.length];
		HashMap<Node, Integer> mapping = new HashMap<Node, Integer>();
		for (int i = 0; i < graphs.length; i++) {
			partitions[i] = new SeparatedPartition(graphs[i]);
			for (Node n : nodess[i]) {
				mapping.put(n, i);
			}
		}

		Set<Edge> edges = new HashSet<Edge>();

		for (IElement e_ : g.getEdges()) {
			Edge e = (Edge) e_;
			SeparatedPartition p1 = partitions[mapping.get(e.getN1())];
			SeparatedPartition p2 = partitions[mapping.get(e.getN2())];
			if (p1 == p2) {
				Edge newEdge = p1.g.getGraphDatastructures().newEdgeInstance(
						e.asString(), p1.g);
				p1.g.addEdge(newEdge);
				newEdge.connectToNodes();
			} else {
				edges.add(e);
			}
		}

		@SuppressWarnings("unchecked")
		Set<Node>[] nodesOfPartitions = new HashSet[nodess.length];
		for (int i = 0; i < nodesOfPartitions.length; i++) {
			nodesOfPartitions[i] = new HashSet<Node>(nodess[i]);
		}

		SeparatedAuxData auxData = new SeparatedAuxData(
				g.getGraphDatastructures(), nodesOfPartitions,
				edges);

		return new AllPartitions<SeparatedPartition, SeparatedAuxData>(
				name, partitionType, g, partitions, auxData, mapping);
	}

	public static AllChanges split(
			AllPartitions<SeparatedPartition, SeparatedAuxData> all,
			Batch b, NodeAssignment nodeAssignment) {
		Batch[] batches = getEmptyBatches(b, all.partitions.length);
		SeparatedAuxData auxAdd = new SeparatedAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());
		SeparatedAuxData auxRemove = new SeparatedAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());

		/**
		 * NA
		 */
		for (NodeAddition na : b.getNodeAdditions()) {
			int p = nodeAssignment.assignNode(all, b, na);
			batches[p].add(na);
			auxAdd.nodesOfPartitions[p].add((Node) na.getNode());
			// TODO handle node addition for other updates also!!!
		}

		/**
		 * EA
		 */
		for (EdgeAddition ea : b.getEdgeAdditions()) {
			int p1 = all.auxData.getPartitionIndex(ea.getEdge().getN1());
			int p2 = all.auxData.getPartitionIndex(ea.getEdge().getN2());
			if (p1 == p2) {
				batches[p1].add(ea);
			} else {
				auxAdd.bridges.add((Edge) ea.getEdge());
			}
		}

		/**
		 * ER
		 */
		for (EdgeRemoval er : b.getEdgeRemovals()) {
			int p1 = all.auxData.getPartitionIndex(er.getEdge().getN1());
			int p2 = all.auxData.getPartitionIndex(er.getEdge().getN2());
			if (p1 == p2) {
				batches[p1].add(er);
			} else {
				auxRemove.bridges.add((Edge) er.getEdge());
			}
		}

		/**
		 * NR
		 */
		for (NodeRemoval nr : b.getNodeRemovals()) {
			int p = all.auxData.getPartitionIndex((Node) nr.getNode());
			batches[p].add(nr);
			// TODO also remove external edges
			auxRemove.nodesOfPartitions[p].add((Node) nr.getNode());
			for (IElement e : nr.getNode().getEdges()) {
				auxRemove.bridges.add((Edge) e);
			}
		}

		for (int i = 0; i < batches.length; i++) {
			BatchSanitization.sanitize(batches[i]);
		}

		return new AllChanges(b, batches, auxAdd, auxRemove);
	}

}
