package dna.parallel.partition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.parallel.auxData.NonOverlappingAuxData;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;

public class NonOverlappingPartition extends Partition {

	public NonOverlappingPartition(Graph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	public static AllPartitions<NonOverlappingPartition, NonOverlappingAuxData> partition(
			String name, PartitionType partitionType, Graph g, List<Node>[] nodess) {
		Graph[] graphs = getInitialGraphs(g, nodess);
		NonOverlappingPartition[] partitions = new NonOverlappingPartition[nodess.length];
		HashMap<Node, Integer> mapping = new HashMap<Node, Integer>();
		for (int i = 0; i < graphs.length; i++) {
			partitions[i] = new NonOverlappingPartition(graphs[i]);
			for (Node n : nodess[i]) {
				mapping.put(n, i);
			}
		}

		Set<Edge> externalEdges = new HashSet<Edge>();

		for (IElement e_ : g.getEdges()) {
			Edge e = (Edge) e_;
			NonOverlappingPartition p1 = partitions[mapping.get(e.getN1())];
			NonOverlappingPartition p2 = partitions[mapping.get(e.getN2())];
			if (p1 == p2) {
				p1.g.addEdge(p1.g.getGraphDatastructures().newEdgeInstance(
						e.asString(), p1.g));
			} else {
				externalEdges.add(e);
			}
		}

		NonOverlappingAuxData auxData = new NonOverlappingAuxData(externalEdges);

		return new AllPartitions<NonOverlappingPartition, NonOverlappingAuxData>(
				name, partitionType, g, partitions, auxData, mapping);
	}

	public static AllChanges split(
			AllPartitions<NonOverlappingPartition, NonOverlappingAuxData> all,
			Batch b) {
		Batch[] batches = getEmptyBatches(b, all.partitions.length);

		// TODO add new nodes
		for (EdgeAddition ea : b.getEdgeAdditions()) {
			int p1 = all.getPartitionIndex(ea.getEdge().getN1());
			int p2 = all.getPartitionIndex(ea.getEdge().getN2());
			if (p1 == p2) {
				batches[p1].add(ea);
			} else {

			}
		}
		for (EdgeRemoval er : b.getEdgeRemovals()) {
			int p1 = all.getPartitionIndex(er.getEdge().getN1());
			int p2 = all.getPartitionIndex(er.getEdge().getN2());
			if (p1 == p2) {
				batches[p1].add(er);
			} else {

			}
		}
		for (NodeRemoval nr : b.getNodeRemovals()) {
			int p = all.getPartitionIndex((Node) nr.getNode());
			batches[p].add(nr);
			// TODO also remove external edges
		}

		return new AllChanges(b, batches);
	}

}
