package dna.metrics.similarityMeasures.matching;

import java.util.HashSet;
import java.util.Set;

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
 * {@link DirectedEdge}s by updating the matching similarity measure.
 * 
 * @see MatchingDirected
 */
public class MatchingDirectedU extends MatchingDirected {

	/**
	 * Initializes {@link MatchingDirectedU}. Implicitly sets degree type for
	 * directed graphs to outdegree.
	 */
	public MatchingDirectedU() {
		super("MatchingDirectedU", ApplicationType.BeforeAndAfterUpdate);
	}

	/**
	 * Initializes {@link MatchingDirectedU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs.
	 */
	public MatchingDirectedU(Parameter directedDegreeType) {
		super("MatchingDirectedU", ApplicationType.BeforeAndAfterUpdate,
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
		if (isOutgoingMatching())
			this.increaseMatching(this.getNeighborsIn(newEdge.getDst()),
					newEdge.getSrc());
		else
			this.increaseMatching(this.getNeighborsOut(newEdge.getSrc()),
					newEdge.getDst());
		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof NodeAddition)
			for (int i = 0; i < this.g.getNodeCount(); i++)
				this.matchingDirectedD.incr(0.0);
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
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeEdgeRemoval(EdgeRemoval u) {
		final DirectedEdge edgeToRemove = (DirectedEdge) u.getEdge();
		if (isOutgoingMatching())
			this.decreaseMatching(this.getNeighborsIn(edgeToRemove.getDst()),
					edgeToRemove.getSrc());
		else
			this.decreaseMatching(this.getNeighborsOut(edgeToRemove.getSrc()),
					edgeToRemove.getDst());

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
		if (isOutgoingMatching()) {
			this.decreaseMatchingNodeRemove(this.getNeighborsIn(nodeToRemove));
		} else {
			this.decreaseMatchingNodeRemove(this.getNeighborsOut(nodeToRemove));
		}

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matchings.get(nodeToRemove, node) == null)
				this.matchingDirectedD.decr(0);
			else
				this.matchingDirectedD.decr(this.matchings.get(nodeToRemove,
						node));
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
			return applyBeforeEdgeRemoval((EdgeRemoval) u);
		return false;
	}

	/**
	 * Decreases the matching between the given nodes by 1.
	 */
	private void decreaseMatching(DirectedNode node1, DirectedNode node2) {
		this.matchingDirectedD.decr(this.matchings.get(node1, node2));
		this.matchings.put(node1, node2, this.matchings.get(node1, node2) - 1);
		this.matchingDirectedD.incr(this.matchings.get(node1, node2));
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @param directedNode
	 * 
	 * @see #decreaseMatching(DirectedNode, DirectedNode)
	 */
	private void decreaseMatching(HashSet<DirectedNode> nodes,
			DirectedNode directedNode) {
		for (DirectedNode node1 : nodes) {
			this.decreaseMatching(node1, directedNode);
		}
	}

	/**
	 * Decrease the matching between all neighbors of the node to remove.
	 * 
	 * @param nodeToRemove
	 *            Node from which the neighbors node matching are to be
	 *            decreased.
	 */
	private void decreaseMatchingNodeRemove(HashSet<DirectedNode> neighborNodes) {
		for (DirectedNode directedNode1 : neighborNodes) {
			for (DirectedNode directedNode2 : neighborNodes) {
				if (directedNode1.getIndex() > directedNode2.getIndex())
					continue;
				decreaseMatching(directedNode1, directedNode2);
			}
		}

	}

	/**
	 * Get all incoming neighbors for a given node.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all incoming neighbors of given node.
	 */
	private HashSet<DirectedNode> getNeighborsIn(DirectedNode node) {
		final HashSet<DirectedNode> neighbors = new HashSet<DirectedNode>();
		DirectedEdge edge;
		for (IElement iEdge : node.getIncomingEdges()) {
			edge = (DirectedEdge) iEdge;
			neighbors.add(edge.getSrc());
		}
		return neighbors;
	}

	/**
	 * Get all outgoing neighbors for a given node.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all outgoing neighbors of given node.
	 */
	private HashSet<DirectedNode> getNeighborsOut(DirectedNode node) {
		final HashSet<DirectedNode> neighbors = new HashSet<DirectedNode>();
		DirectedEdge edge;
		for (IElement iEdge : node.getOutgoingEdges()) {
			edge = (DirectedEdge) iEdge;
			neighbors.add(edge.getDst());
		}
		return neighbors;
	}

	/**
	 * Increases the matching between the given nodes by 1.
	 */
	private void increaseMatching(DirectedNode node1, DirectedNode node2) {
		Double matchingG = this.matchings.get(node1, node2);
		if (matchingG == null)
			this.matchingDirectedD.decr(0.0);
		else
			this.matchingDirectedD.decr(matchingG);
		this.matchings.put(node1, node2, matchingG == null ? 1 : matchingG + 1);
		this.matchingDirectedD.incr(this.matchings.get(node1, node2));
	}

	/**
	 * Increases the matching between each pair of the given nodes by 1.
	 * 
	 * @param directedNode
	 * 
	 * @see #increaseMatching(DirectedNode, DirectedNode)
	 */
	private void increaseMatching(HashSet<DirectedNode> nodes,
			DirectedNode directedNode) {
		for (DirectedNode node1 : nodes) {
			this.increaseMatching(node1, directedNode);
		}
	}
}
