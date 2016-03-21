package dna.updates.batch;

import java.util.HashSet;

import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedBlueprintsEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedBlueprintsEdge;
import dna.graph.nodes.Node;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.util.Log;

public class BatchSanitization {

	/**
	 * performs a sanitization of the updates stored in this batch, i.e., (1)
	 * deleted all edge removals that point to a node which is removed anyways
	 * (2) delete all edge additions that point to a node that is removed but
	 * not added again (3) delete all edge weights for edges that are removed
	 * but not added again (4) delete all node weights for nodes that are
	 * removed but not added again
	 * 
	 * @return
	 */
	public static BatchSanitizationStats sanitize(Batch b) {
		BatchSanitizationStats stats = new BatchSanitizationStats();

		HashSet<Node> removedN = new HashSet<Node>(b.getNodeRemovalsCount());
		for (NodeRemoval u : b.getNodeRemovals()) {
			removedN.add((Node) u.getNode());
		}

		HashSet<Node> addedN = new HashSet<Node>(b.getNodeAdditionsCount());
		for (NodeAddition u : b.getNodeAdditions()) {
			addedN.add((Node) u.getNode());
		}

		HashSet<Edge> removedE = new HashSet<Edge>(b.getEdgeRemovalsCount());
		for (EdgeRemoval u : b.getEdgeRemovals()) {
			removedE.add((Edge) u.getEdge());
		}

		/**
		 * delete edge removals of nodes which are going to be deleted anyways
		 */
		HashSet<EdgeRemoval> edgeRemovalsToDelete = new HashSet<EdgeRemoval>();
		for (EdgeRemoval u : b.getEdgeRemovals()) {
			Edge e = (Edge) u.getEdge();
			Node[] n = BatchSanitization.getNodesFromEdge(e);
			if (removedN.contains(n[0]) || removedN.contains(n[1])) {
				edgeRemovalsToDelete.add(u);
			}
		}
		b.removeAll(edgeRemovalsToDelete);
		stats.setDeletedEdgeRemovals(edgeRemovalsToDelete.size());

		/**
		 * delete edge additions of nodes which are going to be deleted anyways
		 * (only keep them in case the node is afterwards added again)
		 */
		HashSet<EdgeAddition> edgeAdditionsToDelete = new HashSet<EdgeAddition>();
		for (EdgeAddition u : b.getEdgeAdditions()) {
			Edge e = (Edge) u.getEdge();
			Node[] n = BatchSanitization.getNodesFromEdge(e);
			if ((removedN.contains(n[0]) && !addedN.contains(n[0]))
					|| (removedN.contains(n[1]) && !addedN.contains(n[1]))) {
				edgeAdditionsToDelete.add(u);
			}
		}
		b.removeAll(edgeAdditionsToDelete);
		stats.setDeletedEdgeAdditions(edgeAdditionsToDelete.size());

		/**
		 * delete node weights for nodes that are removed anyways
		 */
		HashSet<NodeWeight> nodeWeightsToDelete = new HashSet<NodeWeight>();
		for (NodeWeight u : b.getNodeWeights()) {
			Node n = (Node) u.getNode();
			if (removedN.contains(n)) {
				nodeWeightsToDelete.add(u);
			}
		}
		b.removeAll(nodeWeightsToDelete);
		stats.setDeletedNodeWeights(nodeWeightsToDelete.size());

		/**
		 * delete edge weight updates for edges that are removed anyways (either
		 * by themselves or because they point to a node that is to be removed)
		 */
		HashSet<EdgeWeight> edgeWeightsToDelete = new HashSet<EdgeWeight>();
		for (EdgeWeight u : b.getEdgeWeights()) {
			Edge e = (Edge) u.getEdge();
			Node[] n = BatchSanitization.getNodesFromEdge(e);
			if (removedE.contains(e) || removedN.contains(n[0])
					|| removedN.contains(n[1])) {
				edgeWeightsToDelete.add(u);
				continue;
			}
		}
		b.removeAll(edgeWeightsToDelete);
		stats.setDeletedEdgeWeights(edgeWeightsToDelete.size());

		return stats;
	}

	private static Node[] getNodesFromEdge(Edge e) {
		if (e instanceof DirectedEdge) {
			return new Node[] { ((DirectedEdge) e).getSrc(),
					((DirectedEdge) e).getDst() };
		} else if (e instanceof UndirectedEdge) {
			return new Node[] { ((UndirectedEdge) e).getNode1(),
					((UndirectedEdge) e).getNode2() };
		}else if (e instanceof DirectedBlueprintsEdge) {
			return new Node[] { ((DirectedBlueprintsEdge) e).getSrc(),
					((DirectedBlueprintsEdge) e).getDst() };
		} else if (e instanceof UndirectedBlueprintsEdge) {
			return new Node[] { ((UndirectedBlueprintsEdge) e).getNode1(),
					((UndirectedBlueprintsEdge) e).getNode2() };
		} else {
			Log.error("edge type '" + e.getClass().getCanonicalName()
					+ "' not supported in batch sanitization");
			return null;
		}
	}
}
