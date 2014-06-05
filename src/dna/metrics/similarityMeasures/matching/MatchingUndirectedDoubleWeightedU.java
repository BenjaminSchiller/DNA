package dna.metrics.similarityMeasures.matching;

import java.util.HashMap;
import java.util.Map.Entry;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.metrics.similarityMeasures.jaccard.JaccardUndirectedDoubleWeightedU;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * The class implements the changes of {@link UndirectedNode}s and weighted
 * {@link UndirectedEdge}s by updating the matching similarity measure.
 * 
 * @see MatchingUndirectedDoubleWeighted
 */
public class MatchingUndirectedDoubleWeightedU extends
		MatchingUndirectedDoubleWeighted {

	/**
	 * Initializes {@link JaccardUndirectedDoubleWeightedU}.
	 */
	public MatchingUndirectedDoubleWeightedU() {
		super("MatchingUndirectedDoubleWeightedU",
				ApplicationType.BeforeAndAfterUpdate);
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
	 * @return true
	 */
	private boolean applyAfterEdgeAddition(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge newEdge = undirectedDoubleWeightedEdge;
		this.increaseMatching(this.getNeighborNodes(newEdge.getNode1()),
				newEdge.getNode2());
		this.increaseMatching(this.getNeighborNodes(newEdge.getNode2()),
				newEdge.getNode1());

		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition)
			for (int i = 0; i < this.g.getNodeCount(); i++)
				this.matchingUndirectedWeightedD.incr(0.0);

		else if (u instanceof NodeRemoval)
			// New matchings for NodeRemovals are calculated before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition(((UndirectedWeightedEdge) ((EdgeAddition) u)
					.getEdge()));
		else if (u instanceof EdgeRemoval)
			// New matchings for EdgeRemovals are calculated before the update.
			;
		else if (u instanceof EdgeWeight)
			// Nothing to do
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
	 * @param undirectedDoubleWeightedEdge
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful
	 */
	private boolean applyBeforeEdgeRemoval(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge edgeToRemove = undirectedDoubleWeightedEdge;
		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode1()),
				edgeToRemove.getNode2());
		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode2()),
				edgeToRemove.getNode1());
		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param undirectedDoubleWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful
	 */
	private boolean applyBeforeEdgeWeightUpdate(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge, double weight) {
		applyBeforeEdgeRemoval(undirectedDoubleWeightedEdge);
		undirectedDoubleWeightedEdge.setWeight(new DoubleWeight(weight));
		applyAfterEdgeAddition(undirectedDoubleWeightedEdge);
		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param nodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful
	 */
	private boolean applyBeforeNodeRemoval(NodeRemoval nodeRemoval) {
		final UndirectedNode nodeToRemove = (UndirectedNode) nodeRemoval
				.getNode();

		this.decreaseMatchingNodeRemove(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matchings.get(nodeToRemove, node) == null)
				this.matchingUndirectedWeightedD.decr(0.0);
			else
				this.matchingUndirectedWeightedD.decr(this.matchings.get(
						nodeToRemove, node));
		}

		// remove the results of the removed node calculated so far
		this.matchings.removeRow(nodeToRemove);
		this.matchings.removeColumn(nodeToRemove);

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
					((DoubleWeight) ((EdgeWeight) u).getWeight()).getWeight());
		}
		return false;
	}

	/**
	 * Decreases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	private void decreaseMatching(HashMap<UndirectedNode, Double> map,
			UndirectedNode node) {
		for (Entry<UndirectedNode, Double> node1 : map.entrySet())
			this.decreaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));

	}

	/**
	 * Decreases the matching between the given nodes by the min weight.
	 */
	private void decreaseMatching(UndirectedNode node1, Double value1,
			UndirectedNode node2, Double value2) {
		this.matchingUndirectedWeightedD.decr(this.matchings.get(node1, node2));
		double value = this.matchings.get(node1, node2)
				- Math.min(value1, value2);
		if ((value < 0.0) && (Math.abs(value) <= 1.0E-4))
			value = 0.0;
		this.matchings.put(node1, node2, value);
		this.matchingUndirectedWeightedD.incr(this.matchings.get(node1, node2));
	}

	/**
	 * Decrease the matching between all neighbors of the node to remove.
	 * 
	 * @param nodeToRemove
	 *            Node from which the neighbors node matching are to be
	 *            decreased.
	 */
	private void decreaseMatchingNodeRemove(UndirectedNode nodeToRemove) {
		for (Entry<UndirectedNode, Double> node1 : this.getNeighborNodes(
				nodeToRemove).entrySet())
			for (Entry<UndirectedNode, Double> node2 : this.getNeighborNodes(
					nodeToRemove).entrySet()) {
				if (node1.getKey().getIndex() > node2.getKey().getIndex())
					continue;
				this.decreaseMatching(node1.getKey(), node1.getValue(),
						node2.getKey(), node2.getValue());
			}

	}

	/**
	 * Increases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #increaseMatching(UndirectedNode, UndirectedNode)
	 */
	private void increaseMatching(HashMap<UndirectedNode, Double> map,
			UndirectedNode node) {
		for (Entry<UndirectedNode, Double> node1 : map.entrySet())
			this.increaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));

	}
	
	/**
	 * Increases the matching between the given nodes by the min weight.
	 */
	private void increaseMatching(UndirectedNode node1, Double value1,
			UndirectedNode node2, Double value2) {
		Double matchingG = this.matchings.get(node1, node2);
		if (matchingG == null)
			this.matchingUndirectedWeightedD.decr(0.0);
		else
			this.matchingUndirectedWeightedD.decr(matchingG);

		double value = matchingG == null ? Math.min(value1, value2) : matchingG
				+ Math.min(value1, value2);
		if ((value < 0.0) && (Math.abs(value) <= 1.0E-4))
			value = 0.0;

		this.matchings.put(node1, node2, value);
		this.matchingUndirectedWeightedD.incr(this.matchings.get(node1, node2));
	}
}
