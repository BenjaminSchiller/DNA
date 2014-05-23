package dna.metrics.similarityMeasures.dice;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
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
 * @see DiceUndirectedDoubleWeighted
 */
public class DiceUndirectedDoubleWeightedU extends DiceUndirectedDoubleWeighted {

	/**
	 * Initializes {@link DiceUndirectedDoubleWeightedU}.
	 */
	public DiceUndirectedDoubleWeightedU() {
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
	 * @return true, if successful; false otherwise
	 */
	private boolean applyAfterEdgeAddition(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge newEdge = undirectedDoubleWeightedEdge;

		HashMap<UndirectedNode, Double> neighborsNode1 = this
				.getNeighborNodes(newEdge.getNode1());
		HashMap<UndirectedNode, Double> neighborsNode2 = this
				.getNeighborNodes(newEdge.getNode2());

		this.increaseMatching(neighborsNode1, newEdge.getNode2());

		this.increaseMatching(neighborsNode2, newEdge.getNode1());

		// Increasing the number of the neighbors of the two nodes of the edge
		// by 1
		this.increaseAmountOfNeighbor(newEdge.getNode1(),
				((DoubleWeight) newEdge.getWeight()).getWeight());
		this.increaseAmountOfNeighbor(newEdge.getNode2(),
				((DoubleWeight) newEdge.getWeight()).getWeight());

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
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeEdgeRemoval(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge edgeToRemove = undirectedDoubleWeightedEdge;
		HashMap<UndirectedNode, Double> neighborsNode1 = this
				.getNeighborNodes(edgeToRemove.getNode1());
		HashMap<UndirectedNode, Double> neighborsNode2 = this
				.getNeighborNodes(edgeToRemove.getNode2());
		// decrease the matching of every neighbor of each adjacent node by one
		// because they each loose one node through the edge which is going to
		// be removed
		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode1()),
				edgeToRemove.getNode2());
		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode2()),
				edgeToRemove.getNode1());
		// Decreasing the number of the neighbors of the two nodes of the edge
		// by 1
		this.decreaseAmountOfNeighbor(edgeToRemove.getNode1(),
				((DoubleWeight) edgeToRemove.getWeight()).getWeight());
		this.decreaseAmountOfNeighbor(edgeToRemove.getNode2(),
				((DoubleWeight) edgeToRemove.getWeight()).getWeight());

		this.update(edgeToRemove, neighborsNode1, neighborsNode2);

		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param undirectedDoubleWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeEdgeWeightUpdate(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge, double weight) {
		applyEdgeWeightedUpdate(undirectedDoubleWeightedEdge, weight);
		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeNodeRemoval(NodeRemoval u) {
		final UndirectedNode nodeToRemove = (UndirectedNode) u.getNode();

		// System.out.println("Node Remove: " + nodeToRemove);
		// decrease the matching of every neighbor of the removed node by one
		// because the matching is this one node smaller
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
					((DoubleWeight) ((EdgeWeight) u).getWeight()).getWeight());
		}

		return false;
	}

	private void applyEdgeWeightedUpdate(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge, double weight) {

		final UndirectedWeightedEdge edgeToBeUpdated = undirectedDoubleWeightedEdge;
		final double edgeToBeUpdatedWeight = ((DoubleWeight) edgeToBeUpdated
				.getWeight()).getWeight();

		// decrease the matching of every neighbor of each adjacent node by one
		// because they each loose one node through the edge which is going to
		// be removed
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

		// neues gewicht bei oldEdge setzen
		edgeToBeUpdated.setWeight(new DoubleWeight(weight));

		this.increaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode1()),
				edgeToBeUpdated.getNode2());

		this.increaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode2()),
				edgeToBeUpdated.getNode1());

		// update the dice Measure of every neighbor of each adjacent with the
		// new calculated values.
		this.updateDirectNeighborsMeasure(
				this.getNeighborNodes(edgeToBeUpdated.getNode1()),
				edgeToBeUpdated.getNode2());
		this.updateDirectNeighborsMeasure(
				this.getNeighborNodes(edgeToBeUpdated.getNode2()),
				edgeToBeUpdated.getNode1());
		this.updateMeasureMatching(edgeToBeUpdated.getNode1(),
				edgeToBeUpdated.getNode2());
		this.updateMeasureMatching(edgeToBeUpdated.getNode2(),
				edgeToBeUpdated.getNode1());
	}

	/**
	 * Decreases the number of neighbors of the given node by 1.
	 */
	private void decreaseAmountOfNeighbor(UndirectedNode undirectedNode,
			double weight) {
		double aoN = this.amountOfNeighbors.get(undirectedNode) - weight;

		if (aoN < 0.0 && Math.abs(aoN) <= 1.0E-4 || aoN > 0.0 && aoN < 1.0E-6) {
			System.err.println("AOM  -- Dec");
			aoN = 0.0;
		}

		this.amountOfNeighbors.put(undirectedNode, aoN);
	}

	/**
	 * Decreases the number of neighbors for each node by 1.
	 * 
	 * @see #decreaseAmountOfNeighbors(UndirectedNode)
	 */
	private void decreaseAmountOfNeighbors(UndirectedNode nodeToRemove) {
		for (IElement iEdge : nodeToRemove.getEdges()) {
			UndirectedWeightedEdge e = ((UndirectedWeightedEdge) iEdge);
			if (e.getNode1().equals(nodeToRemove))
				decreaseAmountOfNeighbor(e.getNode2(),
						((DoubleWeight) e.getWeight()).getWeight());
			else
				decreaseAmountOfNeighbor(e.getNode1(),
						((DoubleWeight) e.getWeight()).getWeight());
		}

	}

	/**
	 * Increases the number of neighbors of the given node by 1.
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

		if (fraction < 0.0 && Math.abs(fraction) <= 1.0E-4) {
			System.err.println("Jetzt fraction minus");
			fraction = 0.0;
		}
		this.result.put(node1, node2, fraction);

		this.binnedDistribution.incr(fraction);
	}

}
