package dna.metrics.similarityMeasures.jaccard;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * The class implements the changes of {@link UndirectedNode}s and unweighted
 * {@link UndirectedEdge}s by updating the jaccard similarity measure.
 * 
 * @see JaccardUndirected
 */
public class JaccardUndirectedU extends JaccardUndirected {

	/**
	 * Initializes {@link JaccardUndirectedU}.
	 */
	public JaccardUndirectedU() {
		super("JaccardUndirectedU", ApplicationType.BeforeAndAfterUpdate);
	}

	/**
	 * Add the two nodes of the new {@link UndirectedEdge} to the neighbors
	 * {@link Map} containing all neighbors to each node
	 */
	private void addNeighbor(UndirectedEdge newEdge) {
		if (this.neighborNodes.containsKey(newEdge.getNode1()))
			this.neighborNodes.get(newEdge.getNode1()).add(newEdge.getNode2());
		else {
			HashSet<UndirectedNode> set = new HashSet<UndirectedNode>();
			set.add(newEdge.getNode2());
			this.neighborNodes.put(newEdge.getNode1(), set);
		}

		if (this.neighborNodes.containsKey(newEdge.getNode2()))
			this.neighborNodes.get(newEdge.getNode2()).add(newEdge.getNode1());
		else {
			HashSet<UndirectedNode> set = new HashSet<UndirectedNode>();
			set.add(newEdge.getNode1());
			this.neighborNodes.put(newEdge.getNode2(), set);
		}

	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAddition(EdgeAddition u) {
		final UndirectedEdge newEdge = (UndirectedEdge) u.getEdge();
		addNeighbor(newEdge);

		HashSet<UndirectedNode> neighborsNode1 = this.getNeighborNodes(newEdge
				.getNode1());
		HashSet<UndirectedNode> neighborsNode2 = this.getNeighborNodes(newEdge
				.getNode2());

		this.increaseMatching(neighborsNode1, newEdge.getNode2());
		this.increaseMatching(neighborsNode2, newEdge.getNode1());

		this.update(newEdge, neighborsNode1, neighborsNode2);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition)
			for (int i = 0; i < this.g.getNodeCount(); i++)
				this.binnedDistribution.incr(0.0);

		else if (u instanceof NodeRemoval)
			// New jaccard similarity measure for NodeRemovals are calculated
			// before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition((EdgeAddition) u);
		else if (u instanceof EdgeRemoval)
			// New jaccard similarity measure for EdgeRemovals are calculated
			// before the update.
			;

		return false;
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeRemoval(EdgeRemoval u) {
		final UndirectedEdge edgeToRemove = (UndirectedEdge) u.getEdge();

		HashSet<UndirectedNode> neighborsNode1 = this
				.getNeighborNodes(edgeToRemove.getNode1());
		HashSet<UndirectedNode> neighborsNode2 = this
				.getNeighborNodes(edgeToRemove.getNode2());

		this.decreaseMatching(neighborsNode1, edgeToRemove.getNode2());
		this.decreaseMatching(neighborsNode2, edgeToRemove.getNode1());
		this.neighborNodes.get(edgeToRemove.getNode1()).remove(
				edgeToRemove.getNode2());
		this.neighborNodes.get(edgeToRemove.getNode2()).remove(
				edgeToRemove.getNode1());

		this.update(edgeToRemove, neighborsNode1, neighborsNode2);

		return true;

	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeNodeRemoval(NodeRemoval u) {
		final UndirectedNode nodeToRemove = (UndirectedNode) u.getNode();
		
		this.decreaseMatchingNodeRemove(this.getNeighborNodes(nodeToRemove));
		this.removeFromNeighborNodes(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}


		this.updateDirectNeighborsMeasures(this.getNeighborNodes(nodeToRemove));
		this.updateNodeRemoveMeasure(nodeToRemove);
		
		// remove the results of the removed node calculated so far
		this.neighborNodes.remove(nodeToRemove);
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);
		this.result.removeRow(nodeToRemove);
		this.result.removeColumn(nodeToRemove);
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		if (u instanceof NodeAddition)
			// New matchings for NodeAdditions are calculated after the update.
			;
		else if (u instanceof NodeRemoval)
			return applyBeforeNodeRemoval((NodeRemoval) u);
		else if (u instanceof EdgeAddition)
			// New matchings for EdgeAdditions are calculated after the update.
			;
		else if (u instanceof EdgeRemoval)
			return applyBeforeEdgeRemoval((EdgeRemoval) u);

		return false;
	}

	/**
	 * Removes a {@link Node} from the neighborNodes {@link Map}.
	 * 
	 * @param nodeToRemove
	 *            {@link Node} to remove from the {@link Map}
	 */
	private void removeFromNeighborNodes(UndirectedNode nodeToRemove) {
		this.neighborNodes.remove(nodeToRemove);
		for (Entry<Node, HashSet<UndirectedNode>> iterable_element : this.neighborNodes
				.entrySet())
			if (iterable_element.getValue().contains(nodeToRemove))
				this.neighborNodes.get(iterable_element.getKey()).remove(
						nodeToRemove);

	}

	/**
	 * Updates the jaccard similarity measure between the given nodes.
	 */
	@Override
	protected void update(UndirectedNode node1, UndirectedNode node2) {
		HashSet<UndirectedNode> denominator = this.getUnion(
				this.neighborNodes.get(node1), this.neighborNodes.get(node2));
		double newJaccard;
		Double matchingG = this.matching.get(node1, node2);
		if (matchingG == null || matchingG == 0 || denominator.size() == 0)
			newJaccard = 0;
		else
			newJaccard = (double) matchingG / (double) denominator.size();

		Double jaccardG = this.result.get(node1, node2);
		if (jaccardG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(jaccardG);
		this.result.put(node1, node2, newJaccard);
		this.binnedDistribution.incr(newJaccard);
	}
}
