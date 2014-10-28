package dna.metrics.similarityMeasures.overlap;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.algorithms.IAfterUpdatesWeighted;
import dna.metrics.algorithms.IBeforeUpdatesWeighted;
import dna.metrics.similarityMeasures.jaccard.JaccardUndirectedIntWeightedU;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by updating the overlap similarity measure.
 * 
 * @see OverlapUndirectedIntWeighted
 */
public class OverlapUndirectedIntWeightedU extends OverlapUndirectedIntWeighted
		implements IBeforeUpdatesWeighted, IAfterUpdatesWeighted {

	/**
	 * Initializes {@link JaccardUndirectedIntWeightedU}.
	 */
	public OverlapUndirectedIntWeightedU() {
		super("OverlapUndirectedIntWeightedU");
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAddition(
			UndirectedWeightedEdge undirectedIntWeightedEdge) {
		final UndirectedWeightedEdge newEdge = undirectedIntWeightedEdge;

		HashMap<UndirectedNode, Integer> neighborsNode1 = this
				.getNeighborNodes(newEdge.getNode1());
		HashMap<UndirectedNode, Integer> neighborsNode2 = this
				.getNeighborNodes(newEdge.getNode2());

		this.increaseMatching(neighborsNode1, newEdge.getNode2());

		this.increaseMatching(neighborsNode2, newEdge.getNode1());

		this.increaseAmountOfNeighbor(newEdge.getNode1(),
				((IntWeight) newEdge.getWeight()).getWeight());
		this.increaseAmountOfNeighbor(newEdge.getNode2(),
				((IntWeight) newEdge.getWeight()).getWeight());

		this.update(newEdge, neighborsNode1, neighborsNode2);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		return applyAfterEdgeAddition(((UndirectedWeightedEdge) ((EdgeAddition) ea)
				.getEdge()));
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(EdgeWeight ew) {
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

	@Override
	public boolean applyAfterUpdate(NodeWeight nw) {
		return false;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeRemoval(
			UndirectedWeightedEdge undirectedIntWeightedEdge) {
		final UndirectedWeightedEdge edgeToRemove = undirectedIntWeightedEdge;

		HashMap<UndirectedNode, Integer> neighborsNode1 = this
				.getNeighborNodes(edgeToRemove.getNode1());
		HashMap<UndirectedNode, Integer> neighborsNode2 = this
				.getNeighborNodes(edgeToRemove.getNode2());

		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode1()),
				edgeToRemove.getNode2());
		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode2()),
				edgeToRemove.getNode1());

		this.decreaseAmountOfNeighbor(edgeToRemove.getNode1(),
				((IntWeight) edgeToRemove.getWeight()).getWeight());
		this.decreaseAmountOfNeighbor(edgeToRemove.getNode2(),
				((IntWeight) edgeToRemove.getWeight()).getWeight());

		this.update(edgeToRemove, neighborsNode1, neighborsNode2);
		return true;
	}

	private boolean applyBeforeEdgeWeightUpdate(
			UndirectedWeightedEdge undirectedIntWeightedEdge, int weight) {
		applyEdgeWeightedUpdate(undirectedIntWeightedEdge, weight);
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

		this.decreaseAmountOfNeighbors(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.result.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		this.updateDirectNeighborsMeasure(this.getNeighborNodes(nodeToRemove),
				nodeToRemove);

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
		return applyBeforeEdgeRemoval(((UndirectedWeightedEdge) ((EdgeRemoval) er)
				.getEdge()));
	}

	@Override
	public boolean applyBeforeUpdate(EdgeWeight ew) {
		UndirectedWeightedEdge edgeD = ((UndirectedWeightedEdge) ((EdgeWeight) ew)
				.getEdge());
		return applyBeforeEdgeWeightUpdate(edgeD,
				((IntWeight) ((EdgeWeight) ew).getWeight()).getWeight());
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		return applyBeforeNodeRemoval((NodeRemoval) nr);
	}

	@Override
	public boolean applyBeforeUpdate(NodeWeight nw) {
		return false;
	}

	/**
	 * Applied the edge weight update to the graph.
	 * 
	 * @param undirectedIntWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private void applyEdgeWeightedUpdate(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge, int weight) {

		final UndirectedWeightedEdge edgeToBeUpdated = undirectedDoubleWeightedEdge;

		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode1()),
				edgeToBeUpdated.getNode2());
		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode2()),
				edgeToBeUpdated.getNode1());

		this.decreaseAmountOfNeighbor(edgeToBeUpdated.getNode1(),
				((IntWeight) edgeToBeUpdated.getWeight()).getWeight());
		this.decreaseAmountOfNeighbor(edgeToBeUpdated.getNode2(),
				((IntWeight) edgeToBeUpdated.getWeight()).getWeight());

		edgeToBeUpdated.setWeight(new IntWeight(weight));

		HashMap<UndirectedNode, Integer> neighborsNode1 = this
				.getNeighborNodes(edgeToBeUpdated.getNode1());
		HashMap<UndirectedNode, Integer> neighborsNode2 = this
				.getNeighborNodes(edgeToBeUpdated.getNode2());

		this.increaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode1()),
				edgeToBeUpdated.getNode2());

		this.increaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode2()),
				edgeToBeUpdated.getNode1());

		this.increaseAmountOfNeighbor(edgeToBeUpdated.getNode1(),
				((IntWeight) edgeToBeUpdated.getWeight()).getWeight());
		this.increaseAmountOfNeighbor(edgeToBeUpdated.getNode2(),
				((IntWeight) edgeToBeUpdated.getWeight()).getWeight());

		this.update(edgeToBeUpdated, neighborsNode1, neighborsNode2);
	}

	/**
	 * Decreases the number of neighbors of the given node.
	 */
	private void decreaseAmountOfNeighbor(UndirectedNode undirectedNode,
			double weight) {
		this.amountOfNeighbors.put(undirectedNode,
				this.amountOfNeighbors.get(undirectedNode) - weight);
	}

	/**
	 * Decreases the number of neighbors for each node.
	 * 
	 * @see #decreaseAmountOfNeighbors(UndirectedNode)
	 */
	private void decreaseAmountOfNeighbors(UndirectedNode nodeToRemove) {
		for (IElement iEdge : nodeToRemove.getEdges()) {
			UndirectedWeightedEdge e = ((UndirectedWeightedEdge) iEdge);
			if (e.getNode1().equals(nodeToRemove))
				decreaseAmountOfNeighbor(e.getNode2(),
						((IntWeight) e.getWeight()).getWeight());
			else
				decreaseAmountOfNeighbor(e.getNode1(),
						((IntWeight) e.getWeight()).getWeight());
		}

	}

	/**
	 * Increases the number of neighbors of the given node.
	 */
	private void increaseAmountOfNeighbor(UndirectedNode node, double weight) {
		if (amountOfNeighbors.containsKey(node))
			this.amountOfNeighbors.put(node, this.amountOfNeighbors.get(node)
					+ weight);
		else
			this.amountOfNeighbors.put(node, weight);
	}

	@Override
	public boolean init() {

		return false;
	}

	/**
	 * Calculates the new dice similarity measure of the given node.
	 */
	@Override
	protected void update(UndirectedNode node1, UndirectedNode node2) {
		double fraction;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| Math.min(this.amountOfNeighbors.get(node1),
						this.amountOfNeighbors.get(node2)) == 0)
			fraction = 0.0;
		else
			fraction = (this.matching.get(node1, node2))
					/ Math.min(this.amountOfNeighbors.get(node1),
							this.amountOfNeighbors.get(node2));
		Double overlapG = this.result.get(node1, node2);
		if (overlapG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(overlapG);

		this.result.put(node1, node2, fraction);
		this.binnedDistribution.incr(fraction);
	}
}
