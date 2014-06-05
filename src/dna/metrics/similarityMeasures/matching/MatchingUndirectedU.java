package dna.metrics.similarityMeasures.matching;

import java.util.HashSet;
import java.util.Set;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * The class implements the changes of {@link UndirectedNode}s and unweighted
 * {@link UndirectedEdge}s by updating the matching similarity measure.
 * 
 * @see MatchingUndirected
 */
public class MatchingUndirectedU extends MatchingUndirected {

	/**
	 * Initializes {@link MatchingUndirectedU}.
	 */
	public MatchingUndirectedU() {
		super("MatchingUndirectedU", ApplicationType.BeforeAndAfterUpdate);
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
	 * @return true, if successful
	 * 
	 * @see #increaseMatching(Set, UndirectedNode)
	 */
	private boolean applyAfterEdgeAddition(EdgeAddition addedEdgeUpdate) {
		final UndirectedEdge newEdge = (UndirectedEdge) addedEdgeUpdate
				.getEdge();
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
				this.matchingUndirectedD.incr(0.0);

		else if (u instanceof NodeRemoval)
			// New matchings for NodeRemovals are calculated before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition((EdgeAddition) u);
		else if (u instanceof EdgeRemoval)
			// New matchings for EdgeRemovals are calculated before the update.
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
	 * @param edgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful
	 * 
	 * @see #decreaseMatching(Set, UndirectedNode)
	 */
	private boolean applyBeforeEdgeRemoval(EdgeRemoval edgeRemoval) {
		final UndirectedEdge edgeToRemove = (UndirectedEdge) edgeRemoval
				.getEdge();
		// decrease the Matching of the neighbors by 1
		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode1()),
				edgeToRemove.getNode2());
		this.decreaseMatching(this.getNeighborNodes(edgeToRemove.getNode2()),
				edgeToRemove.getNode1());

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param nodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful
	 * 
	 * @see #decreaseMatchingNodeRemove(Set)
	 */
	private boolean applyBeforeNodeRemoval(NodeRemoval nodeRemove) {
		final UndirectedNode nodeToRemove = (UndirectedNode) nodeRemove
				.getNode();

		this.decreaseMatchingNodeRemove(this.getNeighborNodes(nodeToRemove));

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.matchingUndirectedD.decr(0);
			else
				this.matchingUndirectedD.decr(this.matching.get(nodeToRemove,
						node));
		}
		// remove the results of the removed node calculated so far
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);

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
			return applyBeforeEdgeRemoval((EdgeRemoval) u);

		return false;
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	private void decreaseMatching(HashSet<UndirectedNode> nodes,
			UndirectedNode node) {
		for (UndirectedNode node1 : nodes) {
			this.decreaseMatching(node1, node);
		}
	}

	/**
	 * Decreases the matching between the given nodes by 1.
	 */
	private void decreaseMatching(UndirectedNode node1, UndirectedNode node2) {
		this.matchingUndirectedD.decr(this.matching.get(node1, node2));
		this.matching.put(node1, node2, this.matching.get(node1, node2) - 1);
		this.matchingUndirectedD.incr(this.matching.get(node1, node2));
	}

	/**
	 * Decrease the matching after a node has been removed
	 * 
	 * @param neighborNodes
	 *            The neighbors between which the matching has to be decreased.
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	private void decreaseMatchingNodeRemove(
			HashSet<UndirectedNode> neighborNodes) {
		for (UndirectedNode node1 : neighborNodes)
			for (UndirectedNode node2 : neighborNodes) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				decreaseMatching(node1, node2);

			}
	}

	/**
	 * Increases the matching between each pair of the given nodes by 1.
	 * 
	 * @see #increaseMatching(UndirectedNode, UndirectedNode)
	 */
	private void increaseMatching(HashSet<UndirectedNode> nodes,
			UndirectedNode node) {
		for (UndirectedNode node1 : nodes) {
			this.increaseMatching(node1, node);
		}
	}

	/**
	 * Increases the matching between the given nodes by 1.
	 */
	private void increaseMatching(UndirectedNode node1, UndirectedNode node2) {
		Double matchingG = this.matching.get(node1, node2);
		if (matchingG == null)
			this.matchingUndirectedD.decr(0.0);
		else
			this.matchingUndirectedD.decr(matchingG);
		this.matching.put(node1, node2, matchingG == null ? 1 : matchingG + 1);
		this.matchingUndirectedD.incr(this.matching.get(node1, node2));
	}

}
