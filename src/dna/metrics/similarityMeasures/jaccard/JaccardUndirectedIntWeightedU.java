package dna.metrics.similarityMeasures.jaccard;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

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
 * {@link UndirectedEdge}s by updating the jaccard similarity measure.
 * 
 * @see JaccardUndirectedIntWeighted
 */
public class JaccardUndirectedIntWeightedU extends JaccardUndirectedIntWeighted {

	/**
	 * Initializes {@link JaccardUndirectedIntWeightedU}.
	 */
	public JaccardUndirectedIntWeightedU() {
		super("JaccardUndirectedIntWeightedU",
				ApplicationType.BeforeAndAfterUpdate);
	}

	/**
	 * Add the two nodes of the new {@link UndirectedEdge} to the neighbors
	 * {@link Map} containing all neighbors to each node
	 */
	private void addNeighbor(UndirectedWeightedEdge newEdge) {
		if (this.neighborNodes.containsKey(newEdge.getNode1()))
			this.neighborNodes.get(newEdge.getNode1()).put(newEdge.getNode2(),
					((IntWeight) newEdge.getWeight()).getWeight());
		else {
			HashMap<UndirectedNode, Integer> map = new HashMap<UndirectedNode, Integer>();
			map.put(newEdge.getNode2(),
					((IntWeight) newEdge.getWeight()).getWeight());
			this.neighborNodes.put(newEdge.getNode1(), map);
		}

		if (this.neighborNodes.containsKey(newEdge.getNode2()))
			this.neighborNodes.get(newEdge.getNode2()).put(newEdge.getNode1(),
					((IntWeight) newEdge.getWeight()).getWeight());
		else {
			HashMap<UndirectedNode, Integer> map = new HashMap<UndirectedNode, Integer>();
			map.put(newEdge.getNode1(),
					((IntWeight) newEdge.getWeight()).getWeight());
			this.neighborNodes.put(newEdge.getNode2(), map);
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
	private boolean applyAfterEdgeAddition(
			UndirectedWeightedEdge undirectedIntWeightedEdge) {
		final UndirectedWeightedEdge newEdge = undirectedIntWeightedEdge;
		HashMap<UndirectedNode, Integer> neighborsNode1 = this
				.getNeighborNodes(newEdge.getNode1());
		HashMap<UndirectedNode, Integer> neighborsNode2 = this
				.getNeighborNodes(newEdge.getNode2());

		addNeighbor(newEdge);

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
			return applyAfterEdgeAddition(((UndirectedWeightedEdge) ((EdgeAddition) u)
					.getEdge()));
		else if (u instanceof EdgeRemoval)
			// New jaccard similarity measure for EdgeRemovals are calculated
			// before the update.
			;
		else if (u instanceof EdgeWeight)
			// New jaccard similarity measure for EdgeWeight Updates are
			// calculated
			// before the update
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

		this.neighborNodes.get(edgeToRemove.getNode1()).remove(
				edgeToRemove.getNode2());
		this.neighborNodes.get(edgeToRemove.getNode2()).remove(
				edgeToRemove.getNode1());

		this.decreaseMatching(neighborsNode1, edgeToRemove.getNode2());
		this.decreaseMatching(neighborsNode2, edgeToRemove.getNode1());

		this.update(edgeToRemove, neighborsNode1, neighborsNode2);

		return true;

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
	private boolean applyBeforeEdgeWeightUpdate(
			UndirectedWeightedEdge undirectedIntWeightedEdge, int weight) {
		final UndirectedWeightedEdge edgeToBeUpdated = undirectedIntWeightedEdge;

		this.neighborNodes.get(edgeToBeUpdated.getNode1()).remove(
				edgeToBeUpdated.getNode2());
		this.neighborNodes.get(edgeToBeUpdated.getNode2()).remove(
				edgeToBeUpdated.getNode1());

		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode1()),
				edgeToBeUpdated.getNode2());
		this.decreaseMatching(
				this.getNeighborNodes(edgeToBeUpdated.getNode2()),
				edgeToBeUpdated.getNode1());

		edgeToBeUpdated.setWeight(new IntWeight(weight));

		HashMap<UndirectedNode, Integer> neighborsNode1 = this
				.getNeighborNodes(edgeToBeUpdated.getNode1());
		HashMap<UndirectedNode, Integer> neighborsNode2 = this
				.getNeighborNodes(edgeToBeUpdated.getNode2());

		addNeighbor(edgeToBeUpdated);

		this.increaseMatching(neighborsNode1, edgeToBeUpdated.getNode2());
		this.increaseMatching(neighborsNode2, edgeToBeUpdated.getNode1());

		this.update(edgeToBeUpdated, neighborsNode1, neighborsNode2);
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

		HashMap<UndirectedNode, Integer> neighborsNodeToRemove = this
				.getNeighborNodes(nodeToRemove);

		this.decreaseMatchingNodeRemove(neighborsNodeToRemove);

		this.removeFromNeighborNodes(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		this.updateDirectNeighborsMeasure(neighborsNodeToRemove, nodeToRemove);
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
	 * Removes a {@link Node} from the neighborNodes {@link Map}.
	 * 
	 * @param nodeToRemove
	 *            {@link Node} to remove from the {@link Map}
	 */
	private void removeFromNeighborNodes(UndirectedNode nodeToRemove) {
		this.neighborNodes.remove(nodeToRemove);
		for (Entry<Node, HashMap<UndirectedNode, Integer>> iterable_element : this.neighborNodes
				.entrySet())
			if (iterable_element.getValue().containsKey(nodeToRemove))
				this.neighborNodes.get(iterable_element.getKey()).remove(
						nodeToRemove);

	}

	/**
	 * Updates the jaccard similarity measure between the given nodes.
	 */
	@Override
	protected void update(UndirectedNode node1, UndirectedNode node2) {
		HashMap<UndirectedNode, Integer> denominator = this.getUnion(
				this.neighborNodes.get(node1), this.neighborNodes.get(node2));
		double fraction;

		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| getMapValueSum(denominator) == 0)
			fraction = 0.0;
		else
			fraction = (double) this.matching.get(node1, node2)
					/ (double) getMapValueSum(denominator);
		Double jaccardG = this.result.get(node1, node2);
		if (jaccardG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(jaccardG);

		this.result.put(node1, node2, fraction);

		this.binnedDistribution.incr(fraction);
	}
}
