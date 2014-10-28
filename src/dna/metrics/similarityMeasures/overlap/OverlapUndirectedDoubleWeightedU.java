package dna.metrics.similarityMeasures.overlap;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.metrics.algorithms.IAfterUpdatesWeighted;
import dna.metrics.algorithms.IBeforeUpdatesWeighted;
import dna.metrics.similarityMeasures.jaccard.JaccardUndirectedDoubleWeightedU;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;

/**
 * /** The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by updating the overlap similarity measure.
 * 
 * @see OverlapUndirectedDoubleWeighted
 */
public class OverlapUndirectedDoubleWeightedU extends
		OverlapUndirectedDoubleWeighted implements IBeforeUpdatesWeighted,
		IAfterUpdatesWeighted {

	/**
	 * Initializes {@link JaccardUndirectedDoubleWeightedU}.
	 */
	public OverlapUndirectedDoubleWeightedU() {
		super("OverlapUndirectedWeightedU");
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
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

		this.increaseAmountOfNeighbor(newEdge.getNode1(),
				((DoubleWeight) newEdge.getWeight()).getWeight());
		this.increaseAmountOfNeighbor(newEdge.getNode2(),
				((DoubleWeight) newEdge.getWeight()).getWeight());

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
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge edgeToRemove = undirectedDoubleWeightedEdge;

		HashMap<UndirectedNode, Double> neighborsNode1 = this
				.getNeighborNodes(edgeToRemove.getNode1());
		HashMap<UndirectedNode, Double> neighborsNode2 = this
				.getNeighborNodes(edgeToRemove.getNode2());

		this.decreaseMatching(neighborsNode1, edgeToRemove.getNode2());
		this.decreaseMatching(neighborsNode2, edgeToRemove.getNode1());

		this.decreaseAmountOfNeighbor(edgeToRemove.getNode1(),
				((DoubleWeight) edgeToRemove.getWeight()).getWeight());
		this.decreaseAmountOfNeighbor(edgeToRemove.getNode2(),
				((DoubleWeight) edgeToRemove.getWeight()).getWeight());

		this.update(edgeToRemove, neighborsNode1, neighborsNode2);
		return true;
	}

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
				((DoubleWeight) ((EdgeWeight) ew).getWeight()).getWeight());
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
	 * @param undirectedDoubleWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private void applyEdgeWeightedUpdate(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge, double weight) {

		final UndirectedWeightedEdge edgeToBeUpdated = undirectedDoubleWeightedEdge;

		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode1()),
				edgeToBeUpdated.getNode2());
		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode2()),
				edgeToBeUpdated.getNode1());

		this.decreaseAmountOfNeighbor(edgeToBeUpdated.getNode1(),
				((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());
		this.decreaseAmountOfNeighbor(edgeToBeUpdated.getNode2(),
				((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		edgeToBeUpdated.setWeight(new DoubleWeight(weight));

		HashMap<UndirectedNode, Double> neighborsNode1 = this
				.getNeighborNodes(edgeToBeUpdated.getNode1());
		HashMap<UndirectedNode, Double> neighborsNode2 = this
				.getNeighborNodes(edgeToBeUpdated.getNode2());

		this.increaseMatching(neighborsNode1, edgeToBeUpdated.getNode2());

		this.increaseMatching(neighborsNode2, edgeToBeUpdated.getNode1());

		this.increaseAmountOfNeighbor(edgeToBeUpdated.getNode1(),
				((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());
		this.increaseAmountOfNeighbor(edgeToBeUpdated.getNode2(),
				((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		this.update(edgeToBeUpdated, neighborsNode1, neighborsNode2);
	}

	/**
	 * Decreases the number of neighbors of the given node.
	 */
	private void decreaseAmountOfNeighbor(UndirectedNode undirectedNode,
			double weight) {
		double aoN = this.amountOfNeighbors.get(undirectedNode) - weight;

		if (aoN < 0.0 && Math.abs(aoN) <= 1.0E-4 || aoN > 0.0 && aoN < 1.0E-6) {
			aoN = 0.0;
		}

		this.amountOfNeighbors.put(undirectedNode, aoN);
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
						((DoubleWeight) e.getWeight()).getWeight());
			else
				decreaseAmountOfNeighbor(e.getNode1(),
						((DoubleWeight) e.getWeight()).getWeight());
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
