package dna.metrics.similarityMeasures.jaccard;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IAfterUpdates;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedEdge}s by updating the jaccard similarity measure.
 * 
 * @see JaccardDirected
 */
public class JaccardDirectedU extends JaccardDirected implements
		IBeforeUpdates, IAfterUpdates {

	/**
	 * Initializes {@link JaccardDirectedU}.
	 */
	public JaccardDirectedU() {
		super("JaccardDirectedU");
	}

	/**
	 * Initializes {@link JaccardDirectedU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs.
	 */
	public JaccardDirectedU(Parameter directedDegreeType) {
		super("JaccardDirectedU", directedDegreeType);
	}

	/**
	 * Add the Src node of the new {@link DirectedEdge} to the neighbors
	 * {@link Map} entry of the Dst node.
	 */
	private void addNeighborNodesDst(DirectedEdge newEdge) {
		if (this.neighborNodes.containsKey(newEdge.getDst()))
			this.neighborNodes.get(newEdge.getDst()).add(newEdge.getSrc());
		else {
			HashSet<DirectedNode> set = new HashSet<DirectedNode>();
			set.add(newEdge.getSrc());
			this.neighborNodes.put(newEdge.getDst(), set);

		}
	}

	/**
	 * Add the Dst node of the new {@link DirectedEdge} to the neighbors
	 * {@link Map} entry of the Src node.
	 */
	private void addNeighborNodesSrc(DirectedEdge newEdge) {
		if (this.neighborNodes.containsKey(newEdge.getSrc()))
			this.neighborNodes.get(newEdge.getSrc()).add(newEdge.getDst());
		else {
			HashSet<DirectedNode> set = new HashSet<DirectedNode>();
			set.add(newEdge.getDst());
			this.neighborNodes.put(newEdge.getSrc(), set);

		}
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
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
			addNeighborNodesSrc(newEdge);

		} else {
			this.increaseMatching(neighborsOut, newEdge.getDst());
			addNeighborNodesDst(newEdge);

		}
		this.update(newEdge, neighborsIn, neighborsOut);
		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		return applyAfterEdgeAddition((EdgeAddition) ea);
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
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
			this.neighborNodes.get(edgeToRemove.getSrc()).remove(
					edgeToRemove.getDst());

		} else {
			this.decreaseMatching(neighborsOut, edgeToRemove.getDst());
			this.neighborNodes.get(edgeToRemove.getDst()).remove(
					edgeToRemove.getSrc());

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

		HashSet<DirectedNode> neighborsIn = this.getNeighborsIn(nodeToRemove);
		HashSet<DirectedNode> neighborsOut = this.getNeighborsOut(nodeToRemove);

		if (isOutgoingMeasure())
			this.decreaseMatchingNodeRemove(neighborsIn);
		else
			this.decreaseMatchingNodeRemove(neighborsOut);

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
			this.updateDirectedNeighborsMeasure(neighborsIn);
			this.updateNodeRemoveMeasuresOutgoing(nodeToRemove);
		} else {
			this.updateDirectedNeighborsMeasure(neighborsOut);
			this.updateNodeRemoveMeasuresIncoming(nodeToRemove);
		}

		// remove the results of the removed node calculated so far
		this.neighborNodes.remove(nodeToRemove);
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
		return applyBeforeEdgeRemoval((EdgeRemoval) er);
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
	public boolean init() {
		return false;
	}

	/**
	 * Removes a {@link Node} from the neighborNodes {@link Map}.
	 * 
	 * @param nodeToRemove
	 *            {@link Node} to remove from the {@link Map}
	 */
	private void removeFromNeighborNodes(DirectedNode nodeToRemove) {
		this.neighborNodes.remove(nodeToRemove);
		for (Entry<Node, HashSet<DirectedNode>> iterable_element : this.neighborNodes
				.entrySet())
			if (iterable_element.getValue().contains(nodeToRemove))
				this.neighborNodes.get(iterable_element.getKey()).remove(
						nodeToRemove);

	}

	/**
	 * Updates the jaccard similarity measure between the given nodes.
	 */
	@Override
	protected void update(DirectedNode node1, DirectedNode node2) {
		HashSet<DirectedNode> denominator = this.getUnion(
				this.neighborNodes.get(node1), this.neighborNodes.get(node2));
		double newJaccard;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| denominator.size() == 0)
			newJaccard = 0;
		else
			newJaccard = (double) this.matching.get(node1, node2)
					/ (double) denominator.size();
		Double jaccardG = this.result.get(node1, node2);
		if (jaccardG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(jaccardG);
		this.result.put(node1, node2, newJaccard);
		this.binnedDistribution.incr(newJaccard);
	}
}
