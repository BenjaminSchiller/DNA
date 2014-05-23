package dna.metrics.similarityMeasures.overlap;

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
 * {@link DirectedEdge}s by updating the overlap similarity measure.
 * 
 * @see OverlapDirected
 */
public class OverlapDirectedU extends OverlapDirected {

	/**
	 * Initializes {@link OverlapDirectedU}. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 */
	public OverlapDirectedU() {
		super("OverlapDirectedU", ApplicationType.BeforeAndAfterUpdate);
	}

	/**
	 * Initializes {@link OverlapDirectedU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs.
	 */
	public OverlapDirectedU(Parameter directedDegreeType) {
		super("OverlapDirectedU", ApplicationType.BeforeAndAfterUpdate,
				directedDegreeType);
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
	 * @return true, if successful; false otherwise
	 */
	private boolean applyAfterEdgeAddition(EdgeAddition u) {
		final DirectedEdge newEdge = (DirectedEdge) u.getEdge();

		HashSet<DirectedNode> neighborsIn = this.getNeighborsIn(newEdge
				.getDst());
		HashSet<DirectedNode> neighborsOut = this.getNeighborsOut(newEdge
				.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatching(neighborsIn, newEdge.getSrc());
			// Add a new neighbor
			this.increaseAmountOfNeighbors(newEdge.getSrc());

		} else {
			this.increaseMatching(neighborsOut, newEdge.getDst());
			// Add a new neighbor
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
			// New overlap similarity measures for NodeRemovals are calculated
			// before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition((EdgeAddition) u);
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
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeEdgeRemoval(EdgeRemoval u) {
		final DirectedEdge edgeToRemove = (DirectedEdge) u.getEdge();
		HashSet<DirectedNode> neighborsIn = this.getNeighborsIn(edgeToRemove
				.getDst());
		HashSet<DirectedNode> neighborsOut = this.getNeighborsOut(edgeToRemove
				.getSrc());

		if (isOutgoingMeasure()) {
			this.decreaseMatching(neighborsIn, edgeToRemove.getSrc());
			// Add a new neighbor
			this.decreaseAmountOfNeighbors(edgeToRemove.getSrc());

		} else {
			this.decreaseMatching(neighborsOut, edgeToRemove.getDst());
			// Add a new neighbor
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
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeNodeRemoval(NodeRemoval u) {
		final DirectedNode nodeToRemove = (DirectedNode) u.getNode();
		if (isOutgoingMeasure())
			this.decreaseMatchingNodeRemove(this.getNeighborsIn(nodeToRemove));
		else
			this.decreaseMatchingNodeRemove(this.getNeighborsOut(nodeToRemove));

		// remove the node from the neighborNodes Map
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
			return applyBeforeEdgeRemoval((EdgeRemoval) u);

		return false;
	}

	/**
	 * Decreases the number of neighbors of the given node by 1.
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
	 * Updates the overlap similarity measure between the given nodes.
	 */
	@Override
	protected void update(DirectedNode node1, DirectedNode node2) {
		double newOverlap;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| Math.min(this.amountOfNeighbors.get(node1),
						this.amountOfNeighbors.get(node2)) == 0)
			newOverlap = 0;
		else
			newOverlap = ((double) (this.matching.get(node1, node2)) / (double) Math
					.min(this.amountOfNeighbors.get(node1),
							this.amountOfNeighbors.get(node2)));

		Double overlapG = this.result.get(node1, node2);
		if (overlapG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(overlapG);
		this.result.put(node1, node2, newOverlap);
		this.binnedDistribution.incr(newOverlap);
	}
}
