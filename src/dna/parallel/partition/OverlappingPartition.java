package dna.parallel.partition;

import java.util.ArrayList;
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
import dna.updates.update.Update;

public class OverlappingPartition extends Partition {

	public OverlappingPartition(Graph g) {
		super(g);
		// TODO Auto-generated constructor stub
	}

	/*
	 * PARTITIONING
	 */

	public static AllPartitions<OverlappingPartition, OverlappingAuxData> partition(
			String name, Graph g, List<Node>[] nodess) {
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
		Set<Node>[] nodes = new HashSet[nodess.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new HashSet<Node>(nodess[i]);
		}
		@SuppressWarnings("unchecked")
		Set<Node>[] neighbors = new HashSet[nodess.length];
		for (int i = 0; i < neighbors.length; i++) {
			neighbors[i] = new HashSet<Node>();
		}

		for (IElement n_ : g.getNodes()) {
			Node n = (Node) n_;
			int pIndex = mapping.get(n);
			OverlappingPartition p = partitions[pIndex];
			for (IElement e_ : n.getEdges()) {
				Edge e = (Edge) e_;
				Node n2 = e.getDifferingNode(n);
				if (mapping.get(n2) != pIndex) {
					neighbors[pIndex].add(n2);
				}
				if (!p.g.containsNode(n2)) {
					p.g.addNode(p.g.getGraphDatastructures().newNodeInstance(
							n2.asString()));
				}
			}
		}

		for (IElement e_ : g.getEdges()) {
			Edge e = (Edge) e_;
			Node n1 = e.getN1();
			Node n2 = e.getN2();
			int p1 = mapping.get(n1);
			int p2 = mapping.get(n2);

			addEdge(partitions[p1], e);

			if (p1 != p2) {
				addEdge(partitions[p2], e);
			}

			for (int i : getNeighboringPartitions(neighbors, n1, n2)) {
				addEdge(partitions[i], e);
			}
		}

		OverlappingAuxData auxData = new OverlappingAuxData(
				g.getGraphDatastructures(), nodes, neighbors);

		return new AllPartitions<OverlappingPartition, OverlappingAuxData>(
				name, PartitionType.Overlapping, g, partitions, auxData,
				mapping);
	}

	/**
	 * adds the edge to the graphs of the given partition
	 * 
	 * @param partition
	 * @param e
	 */
	protected static void addEdge(OverlappingPartition partition, Edge e) {
		Edge newEdge = partition.g.getGraphDatastructures().newEdgeInstance(
				e.asString(), partition.g);
		partition.g.addEdge(newEdge);
		newEdge.connectToNodes();
	}

	/**
	 * generates a list of the indexes of partitions which contain the two given
	 * nodes as neighbors
	 * 
	 * @param neighbors
	 * @param n1
	 * @param n2
	 * @return list of partition indexes that have the given nodes as neighbors
	 */
	protected static ArrayList<Integer> getNeighboringPartitions(
			Set<Node>[] neighbors, Node n1, Node n2) {
		ArrayList<Integer> neighboring = new ArrayList<Integer>(
				neighbors.length);
		for (int i = 0; i < neighbors.length; i++) {
			if (neighbors[i].contains(n1) && neighbors[i].contains(n2)) {
				neighboring.add(i);
			}
		}
		return neighboring;
	}

	/*
	 * SPLITTING
	 */

	public static AllChanges split(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, NodeAssignment nodeAssignment) {
		Batch[] batches = getEmptyBatches(b, all.partitions.length);
		OverlappingAuxData auxAdd = new OverlappingAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());
		OverlappingAuxData auxRemove = new OverlappingAuxData(
				b.getGraphDatastructures(), all.getPartitionCount());

		@SuppressWarnings("unchecked")
		HashMap<Node, Integer>[] degree = new HashMap[all.getPartitionCount()];
		for (int i = 0; i < degree.length; i++) {
			degree[i] = new HashMap<Node, Integer>();
		}

		// NR
		for (NodeRemoval nr : b.getNodeRemovals()) {
			process(all, b, batches, auxAdd, auxRemove, degree, nr);
		}
		// NA
		for (NodeAddition na : b.getNodeAdditions()) {
			process(all, b, batches, auxAdd, auxRemove, degree, na,
					nodeAssignment);
		}
		// EA
		for (EdgeAddition ea : b.getEdgeAdditions()) {
			processV(all, b, batches, auxAdd, auxRemove, degree, ea);
		}
		for (EdgeAddition ea : b.getEdgeAdditions()) {
			processE(all, b, batches, auxAdd, auxRemove, degree, ea);
		}
		// ER
		for (EdgeRemoval er : b.getEdgeRemovals()) {
			processE(all, b, batches, auxAdd, auxRemove, degree, er);
		}
		for (EdgeRemoval er : b.getEdgeRemovals()) {
			processV(all, b, batches, auxAdd, auxRemove, degree, er);
		}

		for (int i = 0; i < batches.length; i++) {
			BatchSanitization.sanitize(batches[i]);
		}

		return new AllChanges(b, batches, auxAdd, auxRemove);
	}

	/*
	 * NR
	 */

	protected static void process(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, Batch[] batches, OverlappingAuxData auxAdd,
			OverlappingAuxData auxRemove, HashMap<Node, Integer>[] degree,
			NodeRemoval nr) {
		Node n = (Node) nr.getNode();

		/**
		 * remove node from the partition the node is assigned to
		 */
		int p = all.auxData.getPartitionIndex((Node) nr.getNode());
		add(all.g, batches, p, nr, "NR1", nr);
		auxRemove.addNode(p, n);

		/**
		 * remove node from the partition where it is a neighbor
		 */
		for (int i : getNeighboringPartitions(all, auxAdd, auxRemove, n)) {
			add(all.g, batches, i, nr, "NR2", nr);
			auxRemove.addNeighbor(i, n);
		}
	}

	/*
	 * NA
	 */

	protected static void process(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, Batch[] batches, OverlappingAuxData auxAdd,
			OverlappingAuxData auxRemove, HashMap<Node, Integer>[] degree,
			NodeAddition na, NodeAssignment nodeAssignment) {
		Node n = (Node) na.getNode();

		/**
		 * determine new partition to assign node to
		 */
		int p = nodeAssignment.assignNode(all, b, na);

		/**
		 * add node to batch and auxAdd for new partition
		 */
		add(all.g, batches, p, na, "NA1", na);
		auxAdd.addNode(p, n);
	}

	/*
	 * EA - V
	 */

	protected static void processV(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, Batch[] batches, OverlappingAuxData auxAdd,
			OverlappingAuxData auxRemove, HashMap<Node, Integer>[] degree,
			EdgeAddition ea) {
		Edge e = (Edge) ea.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();
		int p1 = getPartitionIndex(all, auxAdd, n1);
		int p2 = getPartitionIndex(all, auxAdd, n2);

		/**
		 * add new neighbors in case p1 != p2
		 */
		if (p1 != p2) {
			/**
			 * add n2 as neighbor to p1
			 */
			if (!hasNeighbor(all, auxAdd, p1, n2)) {
				add(all.g, batches, p1, new NodeAddition(n2), "EA1", ea);
				auxAdd.addNeighbor(p1, n2);
			}
			/**
			 * add n1 as neighbor to p2
			 */
			if (!hasNeighbor(all, auxAdd, p2, n1)) {
				add(all.g, batches, p2, new NodeAddition(n1), "EA2", ea);
				auxAdd.addNeighbor(p2, n1);
			}
		}
	}

	/*
	 * EA - E
	 */

	protected static void processE(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, Batch[] batches, OverlappingAuxData auxAdd,
			OverlappingAuxData auxRemove, HashMap<Node, Integer>[] degree,
			EdgeAddition ea) {
		Edge e = (Edge) ea.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();
		int p1 = getPartitionIndex(all, auxAdd, n1);
		int p2 = getPartitionIndex(all, auxAdd, n2);

		/**
		 * add edge to partition if both nodes are contained
		 */
		if (p1 == p2) {
			add(all.g, batches, p1, ea, "EA1", ea);
			incr(degree[p1], n1, n2);
		}

		/**
		 * add edges in case nodes are neighboring
		 */
		if (p1 != p2) {
			/**
			 * add edge to p1 and p2
			 */
			add(all.g, batches, p1, ea, "EA2", ea);
			incr(degree[p1], n1, n2);
			add(all.g, batches, p2, ea, "EA3", ea);
			incr(degree[p2], n1, n2);

			/**
			 * treat n2 as new neighbor of p1
			 */
			if (auxAdd.hasNeighbor(p1, n2)) {
				for (IElement e_ : n2.getEdges()) {
					Edge e2 = (Edge) e_;
					Node n3 = e2.getDifferingNode(n2);
					if (hasNodeOrNeighbor(all, auxAdd, p1, n3)) {
						add(all.g, batches, p1, new EdgeAddition(e2), "EA4", ea);
						incr(degree[p1], n2, n3);
					}
				}
			}

			/**
			 * treat n1 as new neighbor of p2
			 */
			if (auxAdd.hasNeighbor(p2, n1)) {
				for (IElement e_ : n1.getEdges()) {
					Edge e2 = (Edge) e_;
					Node n3 = e2.getDifferingNode(n1);
					if (hasNodeOrNeighbor(all, auxAdd, p2, n3)) {
						add(all.g, batches, p2, new EdgeAddition(e2), "EA5", ea);
						incr(degree[p2], n1, n2);
					}
				}
			}
		}

		for (int p : getNeighboringPartitions(all, auxAdd, auxRemove, n1, n2)) {
			add(all.g, batches, p, new EdgeAddition(e), "EA6", ea);
			incr(degree[p], n1, n2);
		}
	}

	/*
	 * ER - E
	 */

	protected static void processE(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, Batch[] batches, OverlappingAuxData auxAdd,
			OverlappingAuxData auxRemove, HashMap<Node, Integer>[] degree,
			EdgeRemoval er) {
		Edge e = (Edge) er.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();
		int p1 = getPartitionIndex(all, auxAdd, n1);
		int p2 = getPartitionIndex(all, auxAdd, n2);

		/**
		 * remove edge from partition where both are contained
		 */
		if (p1 == p2) {
			add(all.g, batches, p1, er, "ER1", er);
			decr(degree[p1], n1, n2);
		}
		/**
		 * remove edge in case nodes are in different partitions
		 */
		if (p1 != p2) {
			add(all.g, batches, p1, er, "ER2", er);
			decr(degree[p1], n1, n2);
			add(all.g, batches, p2, er, "ER3", er);
			decr(degree[p2], n1, n2);
		}

		/**
		 * remove edge from partitions where both are neighbors
		 */
		for (int p : getNeighboringPartitions(all, auxAdd, auxRemove, n1, n2)) {
			add(all.g, batches, p, er, "ER4", er);
			decr(degree[p], n1, n2);
			if (degree[p].get(n1) <= 0 && degree[p].get(n2) <= 0) {
				throw new IllegalStateException(
						"cannot have only a single edge between neighbors");
			}
		}
	}

	/*
	 * ER - V
	 */

	protected static void processV(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			Batch b, Batch[] batches, OverlappingAuxData auxAdd,
			OverlappingAuxData auxRemove, HashMap<Node, Integer>[] degree,
			EdgeRemoval er) {
		Edge e = (Edge) er.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();
		int p1 = getPartitionIndex(all, auxAdd, n1);
		int p2 = getPartitionIndex(all, auxAdd, n2);

		if (p1 != p2) {
			// TODO: degree might be > 0 only because of other neighbor
			if (degree[p1].get(n2) <= 0) {
				add(all.g, batches, p1, new NodeRemoval(n2), "ER5", er);
				auxRemove.addNeighbor(p1, n2);
			}

			// TODO: degree might be > 0 only because of other neighbor
			if (degree[p2].get(n1) <= 0) {
				add(all.g, batches, p2, new NodeRemoval(n1), "ER6", er);
				auxRemove.addNeighbor(p2, n1);
			}
		}
	}

	// protected static void process(
	// AllPartitions<OverlappingPartition, OverlappingAuxData> all,
	// Batch b, Batch[] batches, OverlappingAuxData auxAdd,
	// OverlappingAuxData auxRemove, HashMap<Node, Integer>[] degree,
	// EdgeRemoval er) {
	// Edge e = (Edge) er.getEdge();
	// Node n1 = e.getN1();
	// Node n2 = e.getN2();
	// int p1 = getPartitionIndex(all, auxAdd, n1);
	// int p2 = getPartitionIndex(all, auxAdd, n2);
	//
	// if (p1 == p2) {
	// /**
	// * remove edge from partition where both are contained
	// */
	// add(all.g, batches, p1, er, "ER1", er);
	// } else {
	// /**
	// * remove edge from p1, remove neighbor n2 in case degree[p1]=0
	// */
	// add(all.g, batches, p1, er, "ER2", er);
	// // TODO: degree might be > 0 only because of other neighbor
	// if (decr(degree[p1], n2) == 0) {
	// add(all.g, batches, p1, new NodeRemoval(n2), "ER3", er);
	// }
	//
	// /**
	// * remove edge from p2, remove neighbor n1 in case degree[p2]=0
	// */
	// add(all.g, batches, p2, er, "ER4", er);
	// // TODO: degree might be > 0 only because of other neighbor
	// if (decr(degree[p2], n1) <= 0) {
	// add(all.g, batches, p2, new NodeRemoval(n1), "ER5", er);
	// }
	// }
	//
	// /**
	// * remove edge from partitions where both are neighbors
	// */
	// for (int p : getNeighboringPartitions(all, auxAdd, auxRemove, n1, n2)) {
	// add(all.g, batches, p, er, "ER6", er);
	// if (decr(degree[p], n1) <= 0) {
	// throw new IllegalStateException(
	// "cannot have only a single edge between neighbors");
	// }
	// if (decr(degree[p], n2) <= 0) {
	// throw new IllegalStateException(
	// "cannot have only a single edge between neighbors");
	// }
	// }
	// }

	/*
	 * HELPBERS
	 */

	protected static int getPartitionIndex(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			OverlappingAuxData auxAdd, Node n) {
		int p = all.auxData.getPartitionIndex(n);
		if (p != -1) {
			return p;
		} else {
			return auxAdd.getPartitionIndex(n);
		}
	}

	protected static boolean hasNeighbor(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			OverlappingAuxData auxAdd, int p, Node n) {
		return all.auxData.neighbors[p].contains(n)
				|| auxAdd.neighbors[p].contains(n);
	}

	protected static boolean hasNode(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			OverlappingAuxData auxAdd, int p, Node n) {
		return all.auxData.nodes[p].contains(n) || auxAdd.nodes[p].contains(n);
	}

	protected static boolean hasNodeOrNeighbor(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			OverlappingAuxData auxAdd, int p, Node n) {
		return hasNode(all, auxAdd, p, n) || hasNeighbor(all, auxAdd, p, n);
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

	protected static void decr(HashMap<Node, Integer> degree, Node n1, Node n2) {
		decr(degree, n1);
		decr(degree, n2);
	}

	/**
	 * 
	 * generates a list of the indexes of partitions that have the given node as
	 * neighbor (it is contained either in all.auxData or auxAdd but NOT in
	 * auxRemove)
	 * 
	 * @param all
	 * @param auxAdd
	 * @param auxRemove
	 * @param n
	 * @return list of partition indexes that have the given node as neighbor
	 */
	protected static ArrayList<Integer> getNeighboringPartitions(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			OverlappingAuxData auxAdd, OverlappingAuxData auxRemove, Node n) {
		ArrayList<Integer> neighboring = new ArrayList<Integer>(
				all.getPartitionCount());
		for (int i = 0; i < all.partitions.length; i++) {
			if ((all.auxData.neighbors[i].contains(n) || auxAdd.neighbors[i]
					.contains(n)) && !auxRemove.neighbors[i].contains(n)) {
				neighboring.add(i);
			}
		}
		return neighboring;
	}

	/**
	 * 
	 * generates a list of the indexes of partitions that have both given nodes
	 * as neighbors (it is contained either in all.auxData or auxAdd but NOT in
	 * auxRemove)
	 * 
	 * @param all
	 * @param auxAdd
	 * @param auxRemove
	 * @param n
	 * @return list of partition indexes that have the given node as neighbor
	 */
	protected static ArrayList<Integer> getNeighboringPartitions(
			AllPartitions<OverlappingPartition, OverlappingAuxData> all,
			OverlappingAuxData auxAdd, OverlappingAuxData auxRemove, Node n1,
			Node n2) {
		ArrayList<Integer> neighboring = new ArrayList<Integer>(
				all.getPartitionCount());
		for (int i = 0; i < all.partitions.length; i++) {
			if (((all.auxData.neighbors[i].contains(n1) || auxAdd.neighbors[i]
					.contains(n1)) && !auxRemove.neighbors[i].contains(n1))
					&& ((all.auxData.neighbors[i].contains(n2) || auxAdd.neighbors[i]
							.contains(n2)) && !auxRemove.neighbors[i]
							.contains(n2))) {
				neighboring.add(i);
			}
		}
		return neighboring;
	}

	/**
	 * adds the specified update to the batch of the partition as specified by
	 * the index
	 * 
	 * @param g
	 *            current graph (only used to get the current timestamp for log
	 *            output)
	 * @param batches
	 *            array of all batches
	 * @param index
	 *            index of the partition
	 * @param u
	 *            update to add / process
	 * @param name
	 *            describes where this is executed from (log only)
	 * @param current
	 *            update which is currently processed and results in this
	 *            addition (log only)
	 */
	public static void add(Graph g, Batch[] batches, int index, Update u,
			String name, Update current) {
		if (u instanceof EdgeRemoval) {
			EdgeAddition addition = batches[index]
					.getEdgeAddition((Edge) ((EdgeRemoval) u).getEdge());
			if (addition != null) {
				batches[index].remove(addition);
				System.out.println("CANCEL OUT: removing " + addition
						+ " instead of adding " + u);
				return;
			}
		}
		System.out.println(name + " - " + index + ": " + u + " for " + current
				+ " @ " + g.getTimestamp());
		batches[index].add(u);
	}
}
