package dna.metrics.similarityMeasures.overlap;

import java.util.HashSet;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IAfterUpdates;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

/**
 * The class implements the changes of {@link UndirectedNode}s and unweighted
 * {@link UndirectedEdge}s by updating the overlap similarity measure.
 * 
 * @see OverlapUndirected
 */
public class OverlapUndirectedU extends OverlapUndirected implements
		IBeforeUpdates, IAfterUpdates {

	/**
	 * Initializes {@link OverlapUndirectedU}.
	 */
	public OverlapUndirectedU() {
		super("OverlapUndirectedU");
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

		HashSet<UndirectedNode> neighborsNode1 = this.getNeighborNodes(newEdge
				.getNode1());
		HashSet<UndirectedNode> neighborsNode2 = this.getNeighborNodes(newEdge
				.getNode2());

		this.increaseMatching(neighborsNode1, newEdge.getNode2());
		this.increaseMatching(neighborsNode2, newEdge.getNode1());

		this.increaseAmountOfNeighbor(newEdge.getNode1());
		this.increaseAmountOfNeighbor(newEdge.getNode2());

		this.update(newEdge, neighborsNode1, neighborsNode2);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		return applyAfterEdgeAddition((EdgeAddition) ea);
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		for (int i = 0; i < this.g.getNodeCount(); i++)
			this.binnedDistribution.incr(0.0);
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		return false;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful
	 */
	private boolean applyBeforeEdgeRemoval(EdgeRemoval u) {
		final UndirectedEdge edgeToRemove = (UndirectedEdge) u.getEdge();

		HashSet<UndirectedNode> neighborsNode1 = this
				.getNeighborNodes(edgeToRemove.getNode1());
		HashSet<UndirectedNode> neighborsNode2 = this
				.getNeighborNodes(edgeToRemove.getNode2());

		this.decreaseMatching(neighborsNode1, edgeToRemove.getNode2());
		this.decreaseMatching(neighborsNode2, edgeToRemove.getNode1());

		this.decreaseAmountOfNeighbor(edgeToRemove.getNode1());
		this.decreaseAmountOfNeighbor(edgeToRemove.getNode2());

		this.update(edgeToRemove, neighborsNode1, neighborsNode2);

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param nodeRemove
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful
	 */
	private boolean applyBeforeNodeRemoval(NodeRemoval nodeRemove) {
		final UndirectedNode nodeToRemove = (UndirectedNode) nodeRemove
				.getNode();

		this.decreaseMatchingNodeRemove(this.getNeighborNodes(nodeToRemove));

		this.decreaseAmountOfNeighbors(this.getNeighborNodes(nodeToRemove));

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.result.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		this.updateDirectNeighborsMeasures(this.getNeighborNodes(nodeToRemove));

		this.updateNodeRemoveMeasure(nodeToRemove);

		// remove the results of the removed node calculated so far
		this.amountOfNeighbors.remove(nodeToRemove);
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);
		this.result.removeRow(nodeToRemove);
		this.result.removeColumn(nodeToRemove);

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		return applyBeforeEdgeRemoval((EdgeRemoval) er);
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		return applyBeforeNodeRemoval((NodeRemoval) nr);
	}

	/**
	 * Decreases the number of neighbors of the given node by 1.
	 */
	private void decreaseAmountOfNeighbor(UndirectedNode node) {
		this.amountOfNeighbors.put(node, this.amountOfNeighbors.get(node) - 1);
	}

	/**
	 * Decreases the number of neighbors for each node by 1.
	 * 
	 * @see #decreaseAmountOfNeighbors(UndirectedNode)
	 */
	private void decreaseAmountOfNeighbors(HashSet<UndirectedNode> neighborNodes) {
		for (UndirectedNode undirectedNode : neighborNodes) {
			decreaseAmountOfNeighbor(undirectedNode);
		}

	}

	/**
	 * Increases the number of neighbors of the given node by 1.
	 */
	private void increaseAmountOfNeighbor(UndirectedNode node) {
		if (amountOfNeighbors.containsKey(node))
			this.amountOfNeighbors.put(node,
					this.amountOfNeighbors.get(node) + 1);
		else
			this.amountOfNeighbors.put(node, 1);
	}

	@Override
	public boolean init() {
		return false;
	}

	/**
	 * Calculates the new overlap similarity measure of the given node.
	 */
	@Override
	protected void update(UndirectedNode node1, UndirectedNode node2) {
		double fraction;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| (this.amountOfNeighbors.get(node1) + this.amountOfNeighbors
						.get(node2)) == 0)
			fraction = 0;
		else
			fraction = ((double) (this.matching.get(node1, node2)))
					/ ((double) Math.min(this.amountOfNeighbors.get(node1),
							this.amountOfNeighbors.get(node2)));

		Double overlapG = this.result.get(node1, node2);
		if (overlapG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(overlapG);
		this.result.put(node1, node2, fraction);
		this.binnedDistribution.incr(fraction);
	}

}
