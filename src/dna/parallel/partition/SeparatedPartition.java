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
			String name, Graph g, List<Node>[] nodess) {
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
				g.getGraphDatastructures(), nodesOfPartitions, edges);

		return new AllPartitions<SeparatedPartition, SeparatedAuxData>(name,
				PartitionType.Separated, g, partitions, auxData, mapping);
	}

	public static AllChanges split(
			AllPartitions<SeparatedPartition, SeparatedAuxData> all, Batch b,
			NodeAssignment nodeAssignment) {
		Batch[] batches = getEmptyBatches(b, all.partitions.length);
		SeparatedAuxData auxAdd = new SeparatedAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());
		SeparatedAuxData auxRemove = new SeparatedAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());

		// NA
		for (NodeAddition na : b.getNodeAdditions()) {
			process(all, b, batches, auxAdd, auxRemove, na, nodeAssignment);
		}
		// EA
		for (EdgeAddition ea : b.getEdgeAdditions()) {
			process(all, b, batches, auxAdd, auxRemove, ea);
		}
		// ER
		for (EdgeRemoval er : b.getEdgeRemovals()) {
			process(all, b, batches, auxAdd, auxRemove, er);
		}
		// NR
		for (NodeRemoval nr : b.getNodeRemovals()) {
			process(all, b, batches, auxAdd, auxRemove, nr);
		}

		for (int i = 0; i < batches.length; i++) {
			BatchSanitization.sanitize(batches[i]);
		}

		return new AllChanges(b, batches, auxAdd, auxRemove);
	}

	protected static void process(
			AllPartitions<SeparatedPartition, SeparatedAuxData> all, Batch b,
			Batch[] batches, SeparatedAuxData auxAdd,
			SeparatedAuxData auxRemove, NodeAddition na,
			NodeAssignment nodeAssignment) {
		Node n = (Node) na.getNode();

		/**
		 * determine new partition to assign node to
		 */
		int p = nodeAssignment.assignNode(all, b, na);

		/**
		 * add node to batch and auxAdd for new partition
		 */
		Node newNode = all.partitions[p].g.getGraphDatastructures()
				.newNodeInstance(n.getIndex() + "@" + Partition.mainNodeType);
		batches[p].add(new NodeAddition(newNode));
		auxAdd.addNode(p, n);
	}

	protected static void process(
			AllPartitions<SeparatedPartition, SeparatedAuxData> all, Batch b,
			Batch[] batches, SeparatedAuxData auxAdd,
			SeparatedAuxData auxRemove, NodeRemoval nr) {
		Node n = (Node) nr.getNode();

		/**
		 * remove node from current partition
		 */
		int p = all.auxData.getPartitionIndex(n);
		if (p == -1)
			p = auxAdd.getPartitionIndex(n);
		batches[p].add(nr);
		auxRemove.addNode(p, n);

		/**
		 * remove all edges of node which are bridges
		 */
		for (IElement e_ : n.getEdges()) {
			Edge e = (Edge) e_;
			if (all.auxData.bridges.contains(e)) {
				auxRemove.bridges.add(e);
			}
		}
	}

	protected static void process(
			AllPartitions<SeparatedPartition, SeparatedAuxData> all, Batch b,
			Batch[] batches, SeparatedAuxData auxAdd,
			SeparatedAuxData auxRemove, EdgeAddition ea) {
		Edge e = (Edge) ea.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();
		int p1 = getPartitionIndex(all, auxAdd, n1);
		int p2 = getPartitionIndex(all, auxAdd, n2);

		if (p1 == p2) {
			/**
			 * add edge to partition in case it is internal
			 */
			batches[p1].add(ea);
		} else {
			/**
			 * remove edge from bridges in case it is external
			 */
			auxAdd.bridges.add((Edge) ea.getEdge());
		}
	}

	protected static void process(
			AllPartitions<SeparatedPartition, SeparatedAuxData> all, Batch b,
			Batch[] batches, SeparatedAuxData auxAdd,
			SeparatedAuxData auxRemove, EdgeRemoval er) {
		Edge e = (Edge) er.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();
		int p1 = getPartitionIndex(all, auxAdd, n1);
		int p2 = getPartitionIndex(all, auxAdd, n2);

		if (p1 == p2) {
			/**
			 * remove edge from partition in case it is internal
			 */
			batches[p1].add(er);
		} else {
			/**
			 * remove edge from bridges in case it is external
			 */
			auxRemove.bridges.add((Edge) er.getEdge());
		}
	}

	/*
	 * HELPERS
	 */

	protected static int getPartitionIndex(
			AllPartitions<SeparatedPartition, SeparatedAuxData> all,
			SeparatedAuxData auxAdd, Node n) {
		int p = all.auxData.getPartitionIndex(n);
		if (p != -1) {
			return p;
		} else {
			return auxAdd.getPartitionIndex(n);
		}
	}

}
