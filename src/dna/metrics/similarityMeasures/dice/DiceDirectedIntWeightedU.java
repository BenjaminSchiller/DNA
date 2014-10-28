package dna.metrics.similarityMeasures.dice;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IntWeight;
import dna.metrics.algorithms.IAfterUpdatesWeighted;
import dna.metrics.algorithms.IBeforeUpdatesWeighted;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by updating the dice similarity measure.
 * 
 * @see DiceDirectedIntWeighted
 */

public class DiceDirectedIntWeightedU extends DiceDirectedIntWeighted implements
		IBeforeUpdatesWeighted, IAfterUpdatesWeighted {

	/**
	 * Initializes {@link DiceDirectedIntWeightedU}.
	 */
	public DiceDirectedIntWeightedU() {
		super("DiceDirectedIntWeightedU");
	}

	/**
	 * Initializes {@link DiceDirectedIntWeightedU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs.
	 */
	public DiceDirectedIntWeightedU(Parameter parameter) {
		super("DiceDirectedIntWeightedU", parameter);
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link DirectedEdge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAddition(
			DirectedWeightedEdge directedIntWeightedEdge) {
		final DirectedWeightedEdge newEdge = (DirectedWeightedEdge) directedIntWeightedEdge;

		HashMap<DirectedNode, Integer> neighborsIn = this
				.getNeighborsIn(newEdge.getDst());
		HashMap<DirectedNode, Integer> neighborsOut = this
				.getNeighborsOut(newEdge.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatching(neighborsIn, newEdge.getSrc());
			this.increaseAmountOfNeighbor(newEdge.getSrc(),
					((IntWeight) newEdge.getWeight()).getWeight());

		} else {
			this.increaseMatching(neighborsOut, newEdge.getDst());
			this.increaseAmountOfNeighbor(newEdge.getDst(),
					((IntWeight) newEdge.getWeight()).getWeight());

		}
		this.update(newEdge, neighborsIn, neighborsOut);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		return applyAfterEdgeAddition(((DirectedWeightedEdge) ((EdgeAddition) ea)
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
			DirectedWeightedEdge directedIntWeightedEdge) {
		final DirectedWeightedEdge edgeToRemove = directedIntWeightedEdge;

		HashMap<DirectedNode, Integer> neighborsIn = this
				.getNeighborsIn(edgeToRemove.getDst());
		HashMap<DirectedNode, Integer> neighborsOut = this
				.getNeighborsOut(edgeToRemove.getSrc());

		if (isOutgoingMeasure()) {
			this.decreaseMatching(neighborsIn, edgeToRemove.getSrc());
			this.decreaseAmountOfNeighbor(edgeToRemove.getSrc(),
					((IntWeight) edgeToRemove.getWeight()).getWeight());

		} else {
			this.decreaseMatching(neighborsOut, edgeToRemove.getDst());
			this.decreaseAmountOfNeighbor(edgeToRemove.getDst(),
					((IntWeight) edgeToRemove.getWeight()).getWeight());

		}
		this.update(edgeToRemove, neighborsIn, neighborsOut);

		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param directedDoubleWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeWeightUpdate(
			DirectedWeightedEdge directedIntWeightedEdge, int weight) {
		applyEdgeWeightedUpdate(directedIntWeightedEdge, weight);
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
		final DirectedNode nodeToRemove = (DirectedNode) u.getNode();
		if (isOutgoingMeasure())
			this.decreaseMatchingNodeRemove(this.getNeighborsIn(nodeToRemove));
		else
			this.decreaseMatchingNodeRemove(this.getNeighborsOut(nodeToRemove));

		this.decreaseAmountOfNeighbors(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.result.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		if (isOutgoingMeasure()) {
			this.updateDirectedNeighborsMeasure(this
					.getNeighborsIn(nodeToRemove));
			this.updateNodeRemoveMeasuresOutgoing(nodeToRemove);
		} else {
			this.updateDirectedNeighborsMeasure(this
					.getNeighborsOut(nodeToRemove));
			this.updateNodeRemoveMeasuresIncoming(nodeToRemove);
		}

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
		return applyBeforeEdgeRemoval(((DirectedWeightedEdge) ((EdgeRemoval) er)
				.getEdge()));
	}

	@Override
	public boolean applyBeforeUpdate(EdgeWeight ew) {
		DirectedWeightedEdge edgeD = ((DirectedWeightedEdge) ((EdgeWeight) ew)
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
	 * @param directedIntWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private void applyEdgeWeightedUpdate(
			DirectedWeightedEdge directedIntWeightedEdge, int weight) {
		final DirectedWeightedEdge edgeToBeUpdated = directedIntWeightedEdge;
		if (isOutgoingMeasure()) {
			this.decreaseMatching(
					this.getNeighborsIn(edgeToBeUpdated.getDst()),
					edgeToBeUpdated.getSrc());
			this.decreaseAmountOfNeighbor(edgeToBeUpdated.getSrc(),
					((IntWeight) edgeToBeUpdated.getWeight()).getWeight());

		} else {
			this.decreaseMatching(
					this.getNeighborsOut(edgeToBeUpdated.getSrc()),
					edgeToBeUpdated.getDst());
			this.decreaseAmountOfNeighbor(edgeToBeUpdated.getDst(),
					((IntWeight) edgeToBeUpdated.getWeight()).getWeight());

		}

		edgeToBeUpdated.setWeight(new IntWeight(weight));

		HashMap<DirectedNode, Integer> neighborsIn = this
				.getNeighborsIn(edgeToBeUpdated.getDst());
		HashMap<DirectedNode, Integer> neighborsOut = this
				.getNeighborsOut(edgeToBeUpdated.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatching(neighborsIn, edgeToBeUpdated.getSrc());
			this.increaseAmountOfNeighbor(edgeToBeUpdated.getSrc(),
					((IntWeight) edgeToBeUpdated.getWeight()).getWeight());

		} else {
			this.increaseMatching(neighborsOut, edgeToBeUpdated.getDst());
			this.increaseAmountOfNeighbor(edgeToBeUpdated.getDst(),
					((IntWeight) edgeToBeUpdated.getWeight()).getWeight());

		}

		update(edgeToBeUpdated, neighborsIn, neighborsOut);

	}

	/**
	 * Decreases the number of neighbors of the given node.
	 */
	private void decreaseAmountOfNeighbor(DirectedNode directedNode,
			double weight) {

		this.amountOfNeighbors.put(directedNode,
				this.amountOfNeighbors.get(directedNode) - weight);
	}

	/**
	 * Decreases the number of neighbors for each node.
	 * 
	 * @see #decreaseAmountOfNeighbors(UndirectedNode)
	 */
	private void decreaseAmountOfNeighbors(DirectedNode nodeToRemove) {
		if (isOutgoingMeasure())
			for (IElement iEdge : nodeToRemove.getIncomingEdges()) {
				decreaseAmountOfNeighbor(((DirectedWeightedEdge) iEdge)
						.getSrc(), ((IntWeight) ((DirectedWeightedEdge) iEdge)
						.getWeight()).getWeight());
			}
		else
			for (IElement iEdge : nodeToRemove.getOutgoingEdges()) {
				decreaseAmountOfNeighbor(((DirectedWeightedEdge) iEdge)
						.getDst(), ((IntWeight) ((DirectedWeightedEdge) iEdge)
						.getWeight()).getWeight());
			}

	}

	/**
	 * Increases the number of neighbors of the given node.
	 */
	private void increaseAmountOfNeighbor(DirectedNode node, double weight) {
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
	 * Updates the dice similarity measure between the given nodes.
	 */
	@Override
	protected void update(DirectedNode node1, DirectedNode node2) {
		double fraction;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| (this.amountOfNeighbors.get(node1) + this.amountOfNeighbors
						.get(node2)) == 0)
			fraction = 0;
		else
			fraction = ((2 * this.matching.get(node1, node2)) / (this.amountOfNeighbors
					.get(node1) + this.amountOfNeighbors.get(node2)));
		if (fraction < 0.0 && Math.abs(fraction) <= 1.0E-4) {
			fraction = 0.0;
		}

		Double diceG = this.result.get(node1, node2);
		if (diceG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(diceG);
		this.result.put(node1, node2, fraction);
		this.binnedDistribution.incr(fraction);

	}

}
