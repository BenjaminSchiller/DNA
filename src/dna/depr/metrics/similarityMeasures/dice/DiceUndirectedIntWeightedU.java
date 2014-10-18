package dna.depr.metrics.similarityMeasures.dice;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IntWeight;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by updating the dice similarity measure.
 * 
 * @see DiceUndirectedIntWeighted
 */
public class DiceUndirectedIntWeightedU extends DiceUndirectedIntWeighted {

	/**
	 * Initializes {@link DiceUndirectedIntWeightedU}.
	 */
	public DiceUndirectedIntWeightedU() {
		super("DiceUndirectedWeightedU", ApplicationType.BeforeAndAfterUpdate);
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
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition)
			for (int i = 0; i < this.g.getNodeCount(); i++)
				this.binnedDistribution.incr(0.0);
		else if (u instanceof NodeRemoval)
			// New dice similarity measures for NodeRemovals are calculated
			// before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition(((UndirectedWeightedEdge) ((EdgeAddition) u)
					.getEdge()));
		else if (u instanceof EdgeRemoval)
			// New dice similarity measures for EdgeRemovals are calculated
			// before the update.
			;
		else if (u instanceof EdgeWeight)
			// nothing to do
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
	private boolean applyBeforeEdgeRemoval(
			UndirectedWeightedEdge undirectedIntWeightedEdge) {
		final UndirectedWeightedEdge edgeToRemove = undirectedIntWeightedEdge;

		HashMap<UndirectedNode, Integer> neighborsNode1 = this
				.getNeighborNodes(edgeToRemove.getNode1());
		HashMap<UndirectedNode, Integer> neighborsNode2 = this
				.getNeighborNodes(edgeToRemove.getNode2());

		this.decreaseMatching(neighborsNode1, edgeToRemove.getNode2());
		this.decreaseMatching(neighborsNode2, edgeToRemove.getNode1());

		this.decreaseAmountOfNeighbor(edgeToRemove.getNode1(),
				((IntWeight) edgeToRemove.getWeight()).getWeight());
		this.decreaseAmountOfNeighbor(edgeToRemove.getNode2(),
				((IntWeight) edgeToRemove.getWeight()).getWeight());

		this.update(edgeToRemove, neighborsNode1, neighborsNode2);

		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param undirectedIntWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeWeightUpdate(
			UndirectedWeightedEdge undirectedIntWeightedEdge, int weight) {
		return applyEdgeWeightedUpdate(undirectedIntWeightedEdge, weight);

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
	public boolean applyBeforeUpdate(Update u) {
		if (u instanceof NodeAddition)
			// New dice similarity measures for NodeAdditions are calculated
			// after the update.
			;
		else if (u instanceof NodeRemoval)
			return applyBeforeNodeRemoval((NodeRemoval) u);
		else if (u instanceof EdgeAddition)
			// New dice similarity measures for EdgeAdditions are calculated
			// after the update.
			;
		else if (u instanceof EdgeRemoval)
			return applyBeforeEdgeRemoval(((UndirectedWeightedEdge) ((EdgeRemoval) u)
					.getEdge()));
		else if (u instanceof EdgeWeight) {
			UndirectedWeightedEdge edgeD = ((UndirectedWeightedEdge) ((EdgeWeight) u)
					.getEdge());
			return applyBeforeEdgeWeightUpdate(edgeD,
					((IntWeight) ((EdgeWeight) u).getWeight()).getWeight());
		}

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
	private boolean applyEdgeWeightedUpdate(
			UndirectedWeightedEdge undirectedIntWeightedEdge, int weight) {

		final UndirectedWeightedEdge edgeToBeUpdated = undirectedIntWeightedEdge;
		final int edgeToBeUpdatedWeight = ((IntWeight) edgeToBeUpdated
				.getWeight()).getWeight();

		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode1()),
				edgeToBeUpdated.getNode2());
		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode2()),
				edgeToBeUpdated.getNode1());

		if (weight < edgeToBeUpdatedWeight) {

			this.decreaseAmountOfNeighbor(edgeToBeUpdated.getNode1(),
					(edgeToBeUpdatedWeight - weight));
			this.decreaseAmountOfNeighbor(edgeToBeUpdated.getNode2(),
					(edgeToBeUpdatedWeight - weight));

		} else {
			this.increaseAmountOfNeighbor(edgeToBeUpdated.getNode1(),
					(weight - edgeToBeUpdatedWeight));
			this.increaseAmountOfNeighbor(edgeToBeUpdated.getNode2(),
					(weight - edgeToBeUpdatedWeight));
		}

		// set new edge weight
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

		this.update(edgeToBeUpdated, neighborsNode1, neighborsNode2);
		return true;

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

	/**
	 * Calculates the new dice similarity measure of the given node.
	 */
	@Override
	protected void update(UndirectedNode node1, UndirectedNode node2) {
		double fraction;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| (this.amountOfNeighbors.get(node1) + this.amountOfNeighbors
						.get(node2)) == 0)
			fraction = 0.0;
		else
			fraction = ((2 * this.matching.get(node1, node2)) / (this.amountOfNeighbors
					.get(node1) + this.amountOfNeighbors.get(node2)));
		Double diceG = this.result.get(node1, node2);
		if (diceG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(diceG);

		this.result.put(node1, node2, fraction);

		this.binnedDistribution.incr(fraction);
	}

}
