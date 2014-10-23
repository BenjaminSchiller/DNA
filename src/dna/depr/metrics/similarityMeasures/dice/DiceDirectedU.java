package dna.depr.metrics.similarityMeasures.dice;

import java.util.HashSet;
import java.util.Map;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by updating the dice similarity measure.
 * 
 * @see DiceDirected
 */
public class DiceDirectedU extends DiceDirected {

	/**
	 * Initializes {@link DiceDirectedU}.
	 */
	public DiceDirectedU() {
		super("DiceDirectedU", ApplicationType.BeforeAndAfterUpdate);
	}

	/**
	 * Initializes {@link DiceDirectedU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs.
	 */
	public DiceDirectedU(Parameter parameter) {
		super("DiceDirectedU", ApplicationType.BeforeAndAfterUpdate, parameter);
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
	private boolean applyAfterEdgeAddition(EdgeAddition u) {
		final DirectedEdge newEdge = (DirectedEdge) u.getEdge();

		HashSet<DirectedNode> neighborsIn = this.getNeighborsIn(newEdge
				.getDst());
		HashSet<DirectedNode> neighborsOut = this.getNeighborsOut(newEdge
				.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatching(neighborsIn, newEdge.getSrc());
			this.increaseAmountOfNeighbors(newEdge.getSrc());

		} else {
			this.increaseMatching(neighborsOut, newEdge.getDst());
			this.increaseAmountOfNeighbors(newEdge.getDst());

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
			// New dice similarity measures for NodeRemovals are calculated
			// before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition((EdgeAddition) u);
		else if (u instanceof EdgeRemoval)
			// New dice similarity measures for EdgeRemovals are calculated
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
		final DirectedEdge edgeToRemove = (DirectedEdge) u.getEdge();

		HashSet<DirectedNode> neighborsIn = this.getNeighborsIn(edgeToRemove
				.getDst());
		HashSet<DirectedNode> neighborsOut = this.getNeighborsOut(edgeToRemove
				.getSrc());

		if (isOutgoingMeasure()) {
			this.decreaseMatching(neighborsIn, edgeToRemove.getSrc());
			this.decreaseAmountOfNeighbors(edgeToRemove.getSrc());

		} else {
			this.decreaseMatching(neighborsOut, edgeToRemove.getDst());
			this.decreaseAmountOfNeighbors(edgeToRemove.getDst());

		}

		this.update(edgeToRemove, neighborsIn, neighborsOut);

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

		this.removeFromNeighborNodes(nodeToRemove);

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
			return applyBeforeEdgeRemoval((EdgeRemoval) u);

		return false;
	}

	/**
	 * Decrease the number of neighbors of the given node by 1.
	 */
	private void decreaseAmountOfNeighbors(DirectedNode node) {
		if (this.amountOfNeighbors.containsKey(node))
			this.amountOfNeighbors.put(node,
					this.amountOfNeighbors.get(node) - 1);
		else
			this.amountOfNeighbors.put(node, 0);

	}

	/**
	 * Increases the number of neighbors of the given node by 1.
	 */
	private void increaseAmountOfNeighbors(DirectedNode node) {
		if (this.amountOfNeighbors.containsKey(node))
			this.amountOfNeighbors.put(node,
					this.amountOfNeighbors.get(node) + 1);
		else
			this.amountOfNeighbors.put(node, 1);

	}

	/**
	 * Removes a {@link Node} from the neighborNodes {@link Map}.
	 * 
	 * @param nodeToRemove
	 *            {@link Node} to remove from the {@link Map}
	 */
	private void removeFromNeighborNodes(DirectedNode nodeToRemove) {
		this.amountOfNeighbors.remove(nodeToRemove);
		if (isOutgoingMeasure())
			for (DirectedNode directedNode : this.getNeighborsIn(nodeToRemove))
				this.decreaseAmountOfNeighbors(directedNode);
		else
			for (DirectedNode directedNode : this.getNeighborsOut(nodeToRemove))
				this.decreaseAmountOfNeighbors(directedNode);
	}

	/**
	 * Updates the dice similarity measure between the given nodes.
	 */
	@Override
	protected void update(DirectedNode node1, DirectedNode node2) {
		double newDice;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| (this.amountOfNeighbors.get(node1) + this.amountOfNeighbors
						.get(node2)) == 0)
			newDice = 0;
		else
			newDice = ((2 * (double) this.matching.get(node1, node2)) / (double) (this.amountOfNeighbors
					.get(node1) + this.amountOfNeighbors.get(node2)));
		Double jaccardG = this.result.get(node1, node2);
		if (jaccardG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(jaccardG);
		this.result.put(node1, node2, newDice);
		this.binnedDistribution.incr(newDice);
	}

}
