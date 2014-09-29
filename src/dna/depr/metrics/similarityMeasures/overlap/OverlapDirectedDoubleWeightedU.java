package dna.depr.metrics.similarityMeasures.overlap;

import java.util.HashMap;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
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
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and weighted
 * {@link DirectedEdge}s by updating the overlap similarity measure.
 * 
 * @see OverlapDirectedDoubleWeighted
 */
public class OverlapDirectedDoubleWeightedU extends
		OverlapDirectedDoubleWeighted {

	/**
	 * Initializes {@link OverlapDirectedWeightedU}.
	 */
	public OverlapDirectedDoubleWeightedU() {
		super("OverlapDirectedDoubleWeightedU",
				ApplicationType.BeforeAndAfterUpdate);
	}

	/**
	 * Initializes {@link OverlapDirectedWeightedU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs.
	 */
	public OverlapDirectedDoubleWeightedU(Parameter directedDegreeType) {
		super("OverlapDirectedDoubleWeightedU",
				ApplicationType.BeforeAndAfterUpdate, directedDegreeType);
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link DirectedEdge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAddition(
			DirectedWeightedEdge directedDoubleWeightedEdge) {
		final DirectedWeightedEdge newEdge = (DirectedWeightedEdge) directedDoubleWeightedEdge;

		HashMap<DirectedNode, Double> neighborsIn = this.getNeighborsIn(newEdge
				.getDst());
		HashMap<DirectedNode, Double> neighborsOut = this
				.getNeighborsOut(newEdge.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatching(neighborsIn, newEdge.getSrc());
			this.increaseAmountOfNeighbor(newEdge.getSrc(),
					((DoubleWeight) newEdge.getWeight()).getWeight());

		} else {
			this.increaseMatching(neighborsOut, newEdge.getDst());
			this.increaseAmountOfNeighbor(newEdge.getDst(),
					((DoubleWeight) newEdge.getWeight()).getWeight());

		}

		this.update(newEdge, neighborsIn, neighborsOut);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition)
			for (int i = 0; i < this.g.getNodeCount(); i++)
				this.binnedDistribution.incr(0.0);
		else if (u instanceof NodeRemoval)
			// New overlap similarity measures for NodeRemovals are calculated
			// before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition(((DirectedWeightedEdge) ((EdgeAddition) u)
					.getEdge()));
		else if (u instanceof EdgeRemoval)
			// New overlap similarity measures for EdgeRemovals are calculated
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
	private boolean applyBeforeEdgeRemoval(
			DirectedWeightedEdge directedDoubleWeightedEdge) {
		final DirectedWeightedEdge edgeToRemove = directedDoubleWeightedEdge;

		HashMap<DirectedNode, Double> neighborsIn = this
				.getNeighborsIn(edgeToRemove.getDst());
		HashMap<DirectedNode, Double> neighborsOut = this
				.getNeighborsOut(edgeToRemove.getSrc());

		if (isOutgoingMeasure()) {
			this.decreaseMatching(neighborsIn, edgeToRemove.getSrc());
			this.decreaseAmountOfNeighbor(edgeToRemove.getSrc(),
					((DoubleWeight) edgeToRemove.getWeight()).getWeight());

		} else {
			this.decreaseMatching(neighborsOut, edgeToRemove.getDst());
			this.decreaseAmountOfNeighbor(edgeToRemove.getDst(),
					((DoubleWeight) edgeToRemove.getWeight()).getWeight());

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
			DirectedWeightedEdge directedDoubleWeightedEdge, double weight) {
		applyEdgeWeightedUpdate(directedDoubleWeightedEdge, weight);
		return false;
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
	public boolean applyBeforeUpdate(Update u) {
		if (u instanceof NodeAddition)
			// New overlap similarity measures for NodeAdditions are calculated
			// after the update.
			;
		else if (u instanceof NodeRemoval)
			return applyBeforeNodeRemoval((NodeRemoval) u);
		else if (u instanceof EdgeAddition)
			// New overlap similarity measures for EdgeAdditions are calculated
			// after the update.
			;
		else if (u instanceof EdgeRemoval)
			return applyBeforeEdgeRemoval(((DirectedWeightedEdge) ((EdgeRemoval) u)
					.getEdge()));
		else if (u instanceof EdgeWeight) {
			DirectedWeightedEdge edgeD = ((DirectedWeightedEdge) ((EdgeWeight) u)
					.getEdge());
			return applyBeforeEdgeWeightUpdate(edgeD,
					((DoubleWeight) ((EdgeWeight) u).getWeight()).getWeight());
		}
		return false;
	}

	/**
	 * Applied the edge weight update to the graph.
	 * 
	 * @param directedDoubleWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private void applyEdgeWeightedUpdate(
			DirectedWeightedEdge directedDoubleWeightedEdge, double weight) {
		final DirectedWeightedEdge edgeToBeUpdated = directedDoubleWeightedEdge;
		if (isOutgoingMeasure()) {
			this.decreaseMatching(
					this.getNeighborsIn(edgeToBeUpdated.getDst()),
					edgeToBeUpdated.getSrc());
			this.decreaseAmountOfNeighbor(edgeToBeUpdated.getSrc(),
					((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		} else {
			this.decreaseMatching(
					this.getNeighborsOut(edgeToBeUpdated.getSrc()),
					edgeToBeUpdated.getDst());
			this.decreaseAmountOfNeighbor(edgeToBeUpdated.getDst(),
					((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		}
		edgeToBeUpdated.setWeight(new DoubleWeight(weight));

		HashMap<DirectedNode, Double> neighborsIn = this
				.getNeighborsIn(edgeToBeUpdated.getDst());
		HashMap<DirectedNode, Double> neighborsOut = this
				.getNeighborsOut(edgeToBeUpdated.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatching(neighborsIn, edgeToBeUpdated.getSrc());
			this.increaseAmountOfNeighbor(edgeToBeUpdated.getSrc(),
					((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		} else {
			this.increaseMatching(neighborsOut, edgeToBeUpdated.getDst());
			this.increaseAmountOfNeighbor(edgeToBeUpdated.getDst(),
					((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		}

		this.update(edgeToBeUpdated, neighborsIn, neighborsOut);

	}

	/**
	 * Decreases the number of neighbors of the given node.
	 */
	private void decreaseAmountOfNeighbor(DirectedNode directedNode,
			double weight) {
		double aoN = this.amountOfNeighbors.get(directedNode) - weight;

		if (aoN < 0.0 && Math.abs(aoN) <= 1.0E-4 || aoN > 0.0 && aoN < 1.0E-6) {
			aoN = 0.0;
		}

		this.amountOfNeighbors.put(directedNode, aoN);
	}

	/**
	 * Decreases the number of neighbors for each node.
	 * 
	 * @see #decreaseAmountOfNeighbors(UndirectedNode)
	 */
	private void decreaseAmountOfNeighbors(DirectedNode nodeToRemove) {
		if (isOutgoingMeasure())
			for (IElement iEdge : nodeToRemove.getIncomingEdges()) {
				decreaseAmountOfNeighbor(
						((DirectedWeightedEdge) iEdge).getSrc(),
						((DoubleWeight) ((DirectedWeightedEdge) iEdge)
								.getWeight()).getWeight());
			}
		else
			for (IElement iEdge : nodeToRemove.getOutgoingEdges()) {
				decreaseAmountOfNeighbor(
						((DirectedWeightedEdge) iEdge).getDst(),
						((DoubleWeight) ((DirectedWeightedEdge) iEdge)
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

	/**
	 * Updates the overlap similarity measure between the given nodes.
	 */
	@Override
	protected void update(DirectedNode node1, DirectedNode node2) {
		double fraction;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| Math.min(this.amountOfNeighbors.get(node1),
						this.amountOfNeighbors.get(node2)) == 0)
			fraction = 0;
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
