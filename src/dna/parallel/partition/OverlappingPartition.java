package dna.parallel.partition;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.IEdge;
import dna.graph.nodes.Node;
import dna.parallel.auxData.OverlappingAuxData;
import dna.parallel.nodeAssignment.NodeAssignment;
import dna.updates.batch.Batch;
import dna.updates.batch.BatchSanitization;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class OverlappingPartition extends Partition {

	public OverlappingPartition(Graph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	public static AllPartitions<OverlappingPartition, OverlappingAuxData> partition(
			String name, PartitionType partitionType, Graph g,
			List<Node>[] nodess) {
		Graph[] graphs = getInitialGraphs(g, nodess);
		OverlappingPartition[] partitions = new OverlappingPartition[nodess.length];
		HashMap<Node, Integer> mapping = new HashMap<Node, Integer>();
		for (int i = 0; i < graphs.length; i++) {
			partitions[i] = new OverlappingPartition(graphs[i]);
			for (Node n : nodess[i]) {
				mapping.put(n, i);
			}
		}

		@SuppressWarnings("unchecked")
		Set<Node>[] nodesOfPartitions = new HashSet[nodess.length];
		for (int i = 0; i < nodesOfPartitions.length; i++) {
			nodesOfPartitions[i] = new HashSet<Node>(nodess[i]);
		}
		@SuppressWarnings("unchecked")
		Set<Node>[] neighborsOfPartitions = new HashSet[nodess.length];
		for (int i = 0; i < neighborsOfPartitions.length; i++) {
			neighborsOfPartitions[i] = new HashSet<Node>();
		}

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			int pIndex = mapping.get(n);
			OverlappingPartition p = partitions[pIndex];
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				Node n2 = e.getDifferingNode(n);
				if (mapping.get(n2) != pIndex) {
					neighborsOfPartitions[pIndex].add(n2);
				}
				if (!p.g.containsNode(n2)) {
					p.g.addNode(p.g.getGraphDatastructures().newNodeInstance(
							n2.asString()));
				}
				Edge newEdge = p.g.getGraphDatastructures().newEdgeInstance(
						e.asString(), p.g);
				p.g.addEdge(newEdge);
				newEdge.connectToNodes();
			}
		}

		OverlappingAuxData auxData = new OverlappingAuxData(
				g.getGraphDatastructures(), nodesOfPartitions,
				neighborsOfPartitions);

		return new AllPartitions<OverlappingPartition, OverlappingAuxData>(
				name, partitionType, g, partitions, auxData, mapping);
	}

	public static AllChanges split(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, NodeAssignment nodeAssignment) {
		Batch[] batches = getEmptyBatches(b, all.partitions.length);
		OverlappingAuxData auxAdd = new OverlappingAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());
		OverlappingAuxData auxRemove = new OverlappingAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());

		HashMap<Node, Integer>[] degree = new HashMap[all.getPartitionCount()];
		for (int i = 0; i < degree.length; i++) {
			degree[i] = new HashMap<Node, Integer>();
		}

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
			Node n1 = ea.getEdge().getN1();
			Node n2 = ea.getEdge().getN2();
			int p1 = all.auxData.getPartitionIndex(n1);
			int p2 = all.auxData.getPartitionIndex(n2);
			if (p1 == p2) {
				/*
				 * assigned to same partition
				 */
				batches[p1].add(ea);
				incr(degree[p1], ea.getEdge());
			} else {
				/*
				 * process n1 @ p1
				 */
				if (all.auxData.neighborsOfPartitions[p1].contains(n2)
						|| auxAdd.neighborsOfPartitions[p1].contains(n2)) {
					// n2 is already a neighbor in p1
					batches[p1].add(ea);
					incr(degree[p1], ea.getEdge());
				} else {
					// n2 is not yet a neighbor in p1
					auxAdd.neighborsOfPartitions[p1].add(n2);
					batches[p1].add(new NodeAddition(n2));
					batches[p1].add(ea);
					incr(degree[p1], ea.getEdge());
					for (IElement e_ : n2.getEdges()) {
						Edge e = (Edge) e_;
						Node n3 = e.getDifferingNode(n2);
						if (all.auxData.neighborsOfPartitions[p1].contains(n3)
								|| auxAdd.neighborsOfPartitions[p1]
										.contains(n3)) {
							batches[p1].add(new EdgeAddition(e));
							incr(degree[p1], e);
						}
					}
				}

				/*
				 * process n2 @ p2
				 */
				if (all.auxData.neighborsOfPartitions[p2].contains(n1)
						|| auxAdd.neighborsOfPartitions[p2].contains(n1)) {
					// n1 is already a neighbor in p2
					batches[p2].add(ea);
					incr(degree[p2], ea.getEdge());
				} else {
					// n1 is not yet a neighbor in p2
					auxAdd.neighborsOfPartitions[p2].add(n1);
					batches[p2].add(new NodeAddition(n1));
					batches[p2].add(ea);
					incr(degree[p2], ea.getEdge());
					for (IElement e_ : n1.getEdges()) {
						Edge e = (Edge) e_;
						Node n3 = e.getDifferingNode(n1);
						if (all.auxData.neighborsOfPartitions[p2].contains(n3)
								|| auxAdd.neighborsOfPartitions[p2]
										.contains(n3)) {
							batches[p2].add(new EdgeAddition(e));
							incr(degree[p1], e);
						}
					}
				}
			}
		}

		/**
		 * ER
		 */
		for (EdgeRemoval er : b.getEdgeRemovals()) {
			Node n1 = er.getEdge().getN1();
			Node n2 = er.getEdge().getN2();
			int p1 = all.auxData.getPartitionIndex(n1);
			int p2 = all.auxData.getPartitionIndex(n2);
			if (p1 == p2) {
				/*
				 * assigned to same partition
				 */
				batches[p1].add(er);
			} else {
				if (decr(degree[p1], n2) == 0) {
					batches[p1].add(new NodeRemoval(n2));
					auxRemove.neighborsOfPartitions[p1].add(n2);
				} else {
					batches[p1].add(er);
				}
				if (decr(degree[p2], n1) == 0) {
					batches[p2].add(new NodeRemoval(n1));
					auxRemove.neighborsOfPartitions[p2].add(n1);
				} else {
					batches[p2].add(er);
				}
			}
			// for (int i = 0; i < all.partitions.length; i++) {
			// if (all.auxData.neighborsOfPartitions[i].contains(n1)
			// && all.auxData.neighborsOfPartitions[i].contains(n2)) {
			// batches[i].add(er);
			// }
			// }
		}

		/**
		 * NR
		 */
		for (NodeRemoval nr : b.getNodeRemovals()) {
			int p = all.auxData.getPartitionIndex((Node) nr.getNode());
			batches[p].add(nr);
			auxRemove.nodesOfPartitions[p].add((Node) nr.getNode());
			for (int i = 0; i < all.getPartitionCount(); i++) {
				if (all.auxData.neighborsOfPartitions[i].contains(nr.getNode())) {
					batches[i].add(nr);
					auxRemove.neighborsOfPartitions[i].add((Node) nr.getNode());
				}
			}
		}

		for (int i = 0; i < batches.length; i++) {
			BatchSanitization.sanitize(batches[i]);
		}

		return new AllChanges(b, batches, auxAdd, auxRemove);
	}

	protected static int incr(HashMap<Node, Integer> degree, Node n) {
		if (degree.containsKey(n)) {
			degree.put(n, degree.get(n) + 1);
		} else {
			degree.put(n, n.getDegree() + 1);
		}
		return degree.get(n);
	}

	protected static void incr(HashMap<Node, Integer> degree, Node n1, Node n2) {
		incr(degree, n1);
		incr(degree, n2);
	}

	protected static void incr(HashMap<Node, Integer> degree, IEdge e) {
		incr(degree, e.getN1(), e.getN2());
	}

	protected static int decr(HashMap<Node, Integer> degree, Node n) {
		if (degree.containsKey(n)) {
			degree.put(n, degree.get(n) - 1);
		} else {
			degree.put(n, n.getDegree() - 1);
		}
		return degree.get(n);
	}
}
