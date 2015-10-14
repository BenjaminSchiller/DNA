package dna.metrics.similarityMeasures.jaccard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.DoubleWeight;
import dna.metrics.algorithms.IAfterUpdates;
import dna.metrics.algorithms.IAfterUpdatesWeighted;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.metrics.algorithms.IBeforeUpdatesWeighted;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.metrics.similarityMeasures.Measures;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;

/**
 * {@link IDynamicAlgorithm} of {@link Jaccard}.
 */
public class JaccardU extends Jaccard implements IBeforeUpdates, IAfterUpdates,
		IBeforeUpdatesWeighted, IAfterUpdatesWeighted {

	/**
	 * Initializes {@link JaccardU}. Implicitly sets degree type for directed
	 * graphs to outdegree and ignores wedge weights (if any).
	 */
	public JaccardU() {
		super("JaccardU");
	}

	/**
	 * Initializes {@link JaccardU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs. Will be ignored for undirected
	 *            graphs.
	 * @param edgeWeightType
	 *            <i>weighted</i> or <i>unweighted</i>, determining whether to
	 *            use edge weights in weighted graphs or not. Will be ignored
	 *            for unweighted graphs.
	 */
	public JaccardU(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super("JaccardU", directedDegreeType, edgeWeightType);
	}

	/**
	 * Add the Src node of the new {@link DirectedEdge} to the neighbors
	 * {@link Map} entry of the Dst node.
	 */
	private void addNeighborNodesDstDirectedUnweighted(DirectedEdge newEdge) {
		if (this.neighborNodesUnweighted.containsKey(newEdge.getDst()))
			this.neighborNodesUnweighted.get(newEdge.getDst()).add(
					newEdge.getSrc());
		else {
			HashSet<Node> set = new HashSet<Node>();
			set.add(newEdge.getSrc());
			this.neighborNodesUnweighted.put(newEdge.getDst(), set);

		}
	}

	/**
	 * Add the Src node of the new {@link DirectedEdge} to the neighbors
	 * {@link Map} entry of the Dst node.
	 */
	private void addNeighborNodesDstDirectedWeighted(
			DirectedWeightedEdge newEdge) {
		if (this.neighborNodesWeighted.containsKey(newEdge.getDst())) {
			this.neighborNodesWeighted.get(newEdge.getDst()).put(
					newEdge.getSrc(),
					((DoubleWeight) newEdge.getWeight()).getWeight());
		} else {
			HashMap<Node, Double> map = new HashMap<Node, Double>();
			map.put(newEdge.getSrc(),
					((DoubleWeight) newEdge.getWeight()).getWeight());
			this.neighborNodesWeighted.put(newEdge.getDst(), map);
		}
	}

	/**
	 * Add the Dst node of the new {@link DirectedEdge} to the neighbors
	 * {@link Map} entry of the Src node.
	 */
	private void addNeighborNodesSrcDirectedUnweighted(DirectedEdge newEdge) {
		if (this.neighborNodesUnweighted.containsKey(newEdge.getSrc()))
			this.neighborNodesUnweighted.get(newEdge.getSrc()).add(
					newEdge.getDst());
		else {
			HashSet<Node> set = new HashSet<Node>();
			set.add(newEdge.getDst());
			this.neighborNodesUnweighted.put(newEdge.getSrc(), set);

		}
	}

	/**
	 * Add the Dst node of the new {@link DirectedEdge} to the neighbors
	 * {@link Map} entry of the Src node.
	 */
	private void addNeighborNodesSrcDirectedWeighted(
			DirectedWeightedEdge newEdge) {
		if (this.neighborNodesWeighted.containsKey(newEdge.getSrc())) {
			this.neighborNodesWeighted.get(newEdge.getSrc()).put(
					newEdge.getDst(), weight(newEdge.getWeight()));
		} else {
			HashMap<Node, Double> map = new HashMap<Node, Double>();
			map.put(newEdge.getDst(), weight(newEdge.getWeight()));
			this.neighborNodesWeighted.put(newEdge.getSrc(), map);
		}
	}

	/**
	 * Add the two nodes of the new {@link UndirectedEdge} to the neighbors
	 * {@link Map} containing all neighbors to each node
	 */
	private void addNeighborUndirectedUnweighted(UndirectedEdge newEdge) {
		if (this.neighborNodesUnweighted.containsKey(newEdge.getNode1()))
			this.neighborNodesUnweighted.get(newEdge.getNode1()).add(
					newEdge.getNode2());
		else {
			HashSet<Node> set = new HashSet<Node>();
			set.add(newEdge.getNode2());
			this.neighborNodesUnweighted.put(newEdge.getNode1(), set);
		}

		if (this.neighborNodesUnweighted.containsKey(newEdge.getNode2()))
			this.neighborNodesUnweighted.get(newEdge.getNode2()).add(
					newEdge.getNode1());
		else {
			HashSet<Node> set = new HashSet<Node>();
			set.add(newEdge.getNode1());
			this.neighborNodesUnweighted.put(newEdge.getNode2(), set);
		}

	}

	/**
	 * Add the two nodes of the new {@link UndirectedEdge} to the neighbors
	 * {@link Map} containing all neighbors to each node
	 */
	private void addNeighborUndirectedWeighted(UndirectedWeightedEdge newEdge) {
		if (this.neighborNodesWeighted.containsKey(newEdge.getNode1()))
			this.neighborNodesWeighted.get(newEdge.getNode1()).put(
					newEdge.getNode2(), weight(newEdge.getWeight()));
		else {
			HashMap<Node, Double> map = new HashMap<Node, Double>();
			map.put(newEdge.getNode2(), weight(newEdge.getWeight()));
			this.neighborNodesWeighted.put(newEdge.getNode1(), map);
		}

		if (this.neighborNodesWeighted.containsKey(newEdge.getNode2()))
			this.neighborNodesWeighted.get(newEdge.getNode2()).put(
					newEdge.getNode1(), weight(newEdge.getWeight()));
		else {
			HashMap<Node, Double> map = new HashMap<Node, Double>();
			map.put(newEdge.getNode1(), weight(newEdge.getWeight()));
			this.neighborNodesWeighted.put(newEdge.getNode2(), map);
		}

	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAdditionDirectedUnweighted(EdgeAddition u) {
		final DirectedEdge newEdge = (DirectedEdge) u.getEdge();

		HashSet<Node> neighborsIn = this
				.getNeighborsInDirectedUnweighted(newEdge.getDst());
		HashSet<Node> neighborsOut = this
				.getNeighborsOutDirectedUnweighted(newEdge.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatchingUnweighted(neighborsIn, newEdge.getSrc());
			addNeighborNodesSrcDirectedUnweighted(newEdge);

		} else {
			this.increaseMatchingUnweighted(neighborsOut, newEdge.getDst());
			addNeighborNodesDstDirectedUnweighted(newEdge);
		}
		this.updateDirectedUnweighted(newEdge, neighborsIn, neighborsOut);
		return true;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAdditionDirectedWeighted(
			DirectedWeightedEdge directedDoubleWeightedEdge) {
		final DirectedWeightedEdge newEdge = directedDoubleWeightedEdge;

		HashMap<Node, Double> neighborsIn = this
				.getNeighborsInDirectedWeighted(newEdge.getDst());
		HashMap<Node, Double> neighborsOut = this
				.getNeighborsOutDirectedWeighted(newEdge.getSrc());

		this.getNeighborsOutDirectedWeighted(newEdge.getSrc());
		if (isOutgoingMeasure()) {
			addNeighborNodesSrcDirectedWeighted(newEdge);
			this.increaseMatchingWeighted(neighborsIn, newEdge.getSrc());
		} else {
			addNeighborNodesDstDirectedWeighted(newEdge);
			this.increaseMatchingWeighted(neighborsOut, newEdge.getDst());
		}

		this.updateDirectedWeighted(newEdge, neighborsIn, neighborsOut);

		return true;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAdditionUndirectedUnweighted(EdgeAddition u) {
		final UndirectedEdge newEdge = (UndirectedEdge) u.getEdge();
		addNeighborUndirectedUnweighted(newEdge);

		HashSet<Node> neighborsNode1 = this
				.getNeighborNodesUndirectedUnweighted(newEdge.getNode1());
		HashSet<Node> neighborsNode2 = this
				.getNeighborNodesUndirectedUnweighted(newEdge.getNode2());

		this.increaseMatchingUnweighted(neighborsNode1, newEdge.getNode2());
		this.increaseMatchingUnweighted(neighborsNode2, newEdge.getNode1());

		this.updateUndirectedUnweighted(newEdge, neighborsNode1, neighborsNode2);

		return true;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAdditionUndirectedWeighted(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge newEdge = undirectedDoubleWeightedEdge;
		HashMap<Node, Double> neighborsNode1 = this
				.getNeighborNodesUndirectedWeighted(newEdge.getNode1());
		HashMap<Node, Double> neighborsNode2 = this
				.getNeighborNodesUndirectedWeighted(newEdge.getNode2());

		addNeighborUndirectedWeighted(newEdge);

		this.increaseMatchingWeighted(neighborsNode1, newEdge.getNode2());
		this.increaseMatchingWeighted(neighborsNode2, newEdge.getNode1());

		this.updateUndirectedWeighted(newEdge, neighborsNode1, neighborsNode2);

		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS)) {
				return this
						.applyAfterEdgeAdditionDirectedWeighted(((DirectedWeightedEdge) ((EdgeAddition) ea)
								.getEdge()));
			} else if (this.edgeWeightType
					.equals(EdgeWeightType.IGNORE_WEIGHTS)) {
				return applyAfterEdgeAdditionDirectedUnweighted((EdgeAddition) ea);
			}

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {
			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS)) {
				return this
						.applyAfterEdgeAdditionUndirectedWeighted(((UndirectedWeightedEdge) ((EdgeAddition) ea)
								.getEdge()));
			} else if (this.edgeWeightType
					.equals(EdgeWeightType.IGNORE_WEIGHTS)) {
				return applyAfterEdgeAdditionUndirectedUnweighted((EdgeAddition) ea);
			}

		} else if (DirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			// directed unweighted graph
			return applyAfterEdgeAdditionDirectedUnweighted((EdgeAddition) ea);

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			// undirected unweighted graph
			return applyAfterEdgeAdditionUndirectedUnweighted((EdgeAddition) ea);

		}

		return false;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeWeight ew) {
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		for (int i = 0; i < this.g.getNodeCount(); i++)
			this.binnedDistribution.incr(0.0);
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeWeight nw) {
		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeRemovalDirectedUnweighted(EdgeRemoval u) {
		final DirectedEdge edgeToRemove = (DirectedEdge) u.getEdge();

		HashSet<Node> neighborsIn = this
				.getNeighborsInDirectedUnweighted(edgeToRemove.getDst());
		HashSet<Node> neighborsOut = this
				.getNeighborsOutDirectedUnweighted(edgeToRemove.getSrc());

		if (isOutgoingMeasure()) {
			this.decreaseMatchingUnweighted(neighborsIn, edgeToRemove.getSrc());
			this.neighborNodesUnweighted.get(edgeToRemove.getSrc()).remove(
					edgeToRemove.getDst());

		} else {
			this.decreaseMatchingUnweighted(neighborsOut, edgeToRemove.getDst());
			this.neighborNodesUnweighted.get(edgeToRemove.getDst()).remove(
					edgeToRemove.getSrc());

		}
		this.updateDirectedUnweighted(edgeToRemove, neighborsIn, neighborsOut);
		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeRemovalDirectedWeighted(
			DirectedWeightedEdge directedDoubleWeightedEdge) {
		final DirectedWeightedEdge edgeToRemove = directedDoubleWeightedEdge;

		HashMap<Node, Double> neighborsIn = this
				.getNeighborsInDirectedWeighted(edgeToRemove.getDst());
		HashMap<Node, Double> neighborsOut = this
				.getNeighborsOutDirectedWeighted(edgeToRemove.getSrc());
		if (isOutgoingMeasure()) {
			this.decreaseMatchingWeighted(neighborsIn, edgeToRemove.getSrc());
			this.neighborNodesWeighted.get(edgeToRemove.getSrc()).remove(
					edgeToRemove.getDst());

		} else {
			this.decreaseMatchingWeighted(neighborsOut, edgeToRemove.getDst());
			this.neighborNodesWeighted.get(edgeToRemove.getDst()).remove(
					edgeToRemove.getSrc());

		}

		this.updateDirectedWeighted(edgeToRemove, neighborsIn, neighborsOut);

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeRemovalUndirectedUnweighted(EdgeRemoval u) {
		final UndirectedEdge edgeToRemove = (UndirectedEdge) u.getEdge();

		HashSet<Node> neighborsNode1 = this
				.getNeighborNodesUndirectedUnweighted(edgeToRemove.getNode1());
		HashSet<Node> neighborsNode2 = this
				.getNeighborNodesUndirectedUnweighted(edgeToRemove.getNode2());

		this.decreaseMatchingUnweighted(neighborsNode1, edgeToRemove.getNode2());
		this.decreaseMatchingUnweighted(neighborsNode2, edgeToRemove.getNode1());
		this.neighborNodesUnweighted.get(edgeToRemove.getNode1()).remove(
				edgeToRemove.getNode2());
		this.neighborNodesUnweighted.get(edgeToRemove.getNode2()).remove(
				edgeToRemove.getNode1());

		this.updateUndirectedUnweighted(edgeToRemove, neighborsNode1,
				neighborsNode2);

		return true;

	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param EdgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeRemovalUndirectedWeighted(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge edgeToRemove = undirectedDoubleWeightedEdge;

		HashMap<Node, Double> neighborsNode1 = this
				.getNeighborNodesUndirectedWeighted(edgeToRemove.getNode1());
		HashMap<Node, Double> neighborsNode2 = this
				.getNeighborNodesUndirectedWeighted(edgeToRemove.getNode2());

		this.neighborNodesWeighted.get(edgeToRemove.getNode1()).remove(
				edgeToRemove.getNode2());
		this.neighborNodesWeighted.get(edgeToRemove.getNode2()).remove(
				edgeToRemove.getNode1());

		this.decreaseMatchingWeighted(neighborsNode1, edgeToRemove.getNode2());
		this.decreaseMatchingWeighted(neighborsNode2, edgeToRemove.getNode1());

		this.updateUndirectedWeighted(edgeToRemove, neighborsNode1,
				neighborsNode2);

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
	private boolean applyBeforeEdgeWeightUpdateDirectedWeighted(
			DirectedWeightedEdge directedDoubleWeightedEdge, double weight) {
		applyEdgeWeightedUpdateDirectedWeighted(directedDoubleWeightedEdge,
				weight);
		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param undirectedDoubleWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeWeightUpdateUndirectedWeighted(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge, double weight) {
		final UndirectedWeightedEdge edgeToBeUpdated = undirectedDoubleWeightedEdge;

		this.neighborNodesWeighted.get(edgeToBeUpdated.getNode1()).remove(
				edgeToBeUpdated.getNode2());
		this.neighborNodesWeighted.get(edgeToBeUpdated.getNode2()).remove(
				edgeToBeUpdated.getNode1());

		this.decreaseMatchingWeighted(
				this.getNeighborNodesUndirectedWeighted(edgeToBeUpdated
						.getNode1()), edgeToBeUpdated.getNode2());
		this.decreaseMatchingWeighted(
				this.getNeighborNodesUndirectedWeighted(edgeToBeUpdated
						.getNode2()), edgeToBeUpdated.getNode1());

		edgeToBeUpdated.setWeight(new DoubleWeight(weight));

		HashMap<Node, Double> neighborsNode1 = this
				.getNeighborNodesUndirectedWeighted(edgeToBeUpdated.getNode1());
		HashMap<Node, Double> neighborsNode2 = this
				.getNeighborNodesUndirectedWeighted(edgeToBeUpdated.getNode2());

		addNeighborUndirectedWeighted(edgeToBeUpdated);

		this.increaseMatchingWeighted(neighborsNode1,
				edgeToBeUpdated.getNode2());
		this.increaseMatchingWeighted(neighborsNode2,
				edgeToBeUpdated.getNode1());

		this.updateUndirectedWeighted(edgeToBeUpdated, neighborsNode1,
				neighborsNode2);
		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeNodeRemovalDirectedUnweighted(NodeRemoval u) {
		final DirectedNode nodeToRemove = (DirectedNode) u.getNode();

		HashSet<Node> neighborsIn = this
				.getNeighborsInDirectedUnweighted(nodeToRemove);
		HashSet<Node> neighborsOut = this
				.getNeighborsOutDirectedUnweighted(nodeToRemove);

		if (isOutgoingMeasure())
			this.decreaseMatchingNodeRemoveUnweighted(neighborsIn);
		else
			this.decreaseMatchingNodeRemoveUnweighted(neighborsOut);

		this.removeFromNeighborNodesUnweighted(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.result.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0.0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		if (isOutgoingMeasure()) {
			this.updateDirectNeighborsMeasureUnweighted(neighborsIn);
			this.updateNodeRemoveMeasuresOutgoingDirectedUnweighted(nodeToRemove);
		} else {
			this.updateDirectNeighborsMeasureUnweighted(neighborsOut);
			this.updateNodeRemoveMeasuresIncomingDirectedUnweighted(nodeToRemove);
		}

		// remove the results of the removed node calculated so far
		this.neighborNodesUnweighted.remove(nodeToRemove);
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);
		this.result.removeRow(nodeToRemove);
		this.result.removeColumn(nodeToRemove);

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeNodeRemovalDirectedWeighted(NodeRemoval u) {
		final DirectedNode nodeToRemove = (DirectedNode) u.getNode();
		if (isOutgoingMeasure())
			this.decreaseMatchingNodeRemoveWeighted(this
					.getNeighborsInDirectedWeighted(nodeToRemove));
		else
			this.decreaseMatchingNodeRemoveWeighted(this
					.getNeighborsOutDirectedWeighted(nodeToRemove));

		this.removeFromNeighborNodesWeighted(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.result.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0.0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		if (isOutgoingMeasure()) {
			this.updateDirectNeighborsMeasureWeighted(this
					.getNeighborsInDirectedWeighted(nodeToRemove));
			this.updateNodeRemoveMeasuresOutgoingDirectedWeighted(nodeToRemove);
		} else {
			this.updateDirectNeighborsMeasureWeighted(this
					.getNeighborsOutDirectedWeighted(nodeToRemove));
			this.updateNodeRemoveMeasuresIncomingDirectedWeighted(nodeToRemove);
		}

		// remove the results of the removed node calculated so far
		this.neighborNodesWeighted.remove(nodeToRemove);
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);
		this.result.removeRow(nodeToRemove);
		this.result.removeColumn(nodeToRemove);

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeNodeRemovalUndirectedUnweighted(NodeRemoval u) {
		final UndirectedNode nodeToRemove = (UndirectedNode) u.getNode();

		this.decreaseMatchingNodeRemoveUnweighted(this
				.getNeighborNodesUndirectedUnweighted(nodeToRemove));
		this.removeFromNeighborNodesUnweighted(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0.0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		this.updateDirectNeighborsMeasureUnweighted(this
				.getNeighborNodesUndirectedUnweighted(nodeToRemove));
		this.updateNodeRemoveMeasureUndirectedUnweighted(nodeToRemove);

		// remove the results of the removed node calculated so far
		this.neighborNodesUnweighted.remove(nodeToRemove);
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);
		this.result.removeRow(nodeToRemove);
		this.result.removeColumn(nodeToRemove);
		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeNodeRemovalUndirectedWeighted(NodeRemoval u) {
		final UndirectedNode nodeToRemove = (UndirectedNode) u.getNode();

		HashMap<Node, Double> neighborsNodeToRemove = this
				.getNeighborNodesUndirectedWeighted(nodeToRemove);

		this.decreaseMatchingNodeRemoveWeighted(neighborsNodeToRemove);

		this.removeFromNeighborNodesWeighted(nodeToRemove);

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.binnedDistribution.decr(0.0);
			else
				this.binnedDistribution.decr(this.result
						.get(nodeToRemove, node));
		}

		this.updateDirectNeighborsMeasureWeighted(neighborsNodeToRemove);
		this.updateNodeRemoveMeasureUndirectedWeighted(nodeToRemove);

		// remove the results of the removed node calculated so far
		this.neighborNodesWeighted.remove(nodeToRemove);
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);
		this.result.removeRow(nodeToRemove);
		this.result.removeColumn(nodeToRemove);

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeRemoval er) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return applyBeforeEdgeRemovalDirectedWeighted(((DirectedWeightedEdge) ((EdgeRemoval) er)
						.getEdge()));
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return applyBeforeEdgeRemovalDirectedUnweighted((EdgeRemoval) er);

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return this
						.applyBeforeEdgeRemovalUndirectedWeighted(((UndirectedWeightedEdge) ((EdgeRemoval) er)
								.getEdge()));
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return applyBeforeEdgeRemovalUndirectedUnweighted((EdgeRemoval) er);

		} else if (DirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			// directed unweighted graph
			return applyBeforeEdgeRemovalDirectedUnweighted((EdgeRemoval) er);

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			// undirected unweighted graph
			return applyBeforeEdgeRemovalUndirectedUnweighted((EdgeRemoval) er);

		}

		return false;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeWeight ew) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {
			DirectedWeightedEdge edgeD = ((DirectedWeightedEdge) ((EdgeWeight) ew)
					.getEdge());
			return applyBeforeEdgeWeightUpdateDirectedWeighted(edgeD,
					weight(ew.getWeight()));
		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {
			UndirectedWeightedEdge edgeD = ((UndirectedWeightedEdge) ((EdgeWeight) ew)
					.getEdge());
			return applyBeforeEdgeWeightUpdateUndirectedWeighted(edgeD,
					weight(ew.getWeight()));

		}
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeRemoval nr) {
		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS)) {
				return this
						.applyBeforeNodeRemovalDirectedWeighted((NodeRemoval) nr);
			} else if (this.edgeWeightType
					.equals(EdgeWeightType.IGNORE_WEIGHTS)) {

				return applyBeforeNodeRemovalDirectedUnweighted((NodeRemoval) nr);
			}
		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS)) {

				return this
						.applyBeforeNodeRemovalUndirectedWeighted((NodeRemoval) nr);

			} else if (this.edgeWeightType
					.equals(EdgeWeightType.IGNORE_WEIGHTS)) {

				return applyBeforeNodeRemovalUndirectedUnweighted((NodeRemoval) nr);
			}
		} else if (DirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			// directed unweighted graph
			return applyBeforeNodeRemovalDirectedUnweighted((NodeRemoval) nr);

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			// undirected unweighted graph
			return applyBeforeNodeRemovalUndirectedUnweighted((NodeRemoval) nr);

		}
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(NodeWeight nw) {
		return true;
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
	private void applyEdgeWeightedUpdateDirectedWeighted(
			DirectedWeightedEdge directedDoubleWeightedEdge, double weight) {
		final DirectedWeightedEdge edgeToBeUpdated = directedDoubleWeightedEdge;
		if (isOutgoingMeasure()) {
			this.decreaseMatchingWeighted(this
					.getNeighborsInDirectedWeighted(edgeToBeUpdated.getDst()),
					edgeToBeUpdated.getSrc());
			this.neighborNodesWeighted.get(edgeToBeUpdated.getSrc()).remove(
					edgeToBeUpdated.getDst());

		} else {
			this.decreaseMatchingWeighted(this
					.getNeighborsOutDirectedWeighted(edgeToBeUpdated.getSrc()),
					edgeToBeUpdated.getDst());
			this.neighborNodesWeighted.get(edgeToBeUpdated.getDst()).remove(
					edgeToBeUpdated.getSrc());
		}

		edgeToBeUpdated.setWeight(new DoubleWeight(weight));

		HashMap<Node, Double> neighborsIn = this
				.getNeighborsInDirectedWeighted(edgeToBeUpdated.getDst());
		HashMap<Node, Double> neighborsOut = this
				.getNeighborsOutDirectedWeighted(edgeToBeUpdated.getSrc());

		if (isOutgoingMeasure()) {
			this.increaseMatchingWeighted(this
					.getNeighborsInDirectedWeighted(edgeToBeUpdated.getDst()),
					edgeToBeUpdated.getSrc());
			this.neighborNodesWeighted.get(edgeToBeUpdated.getSrc()).put(
					edgeToBeUpdated.getDst(),
					((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		} else {
			this.increaseMatchingWeighted(this
					.getNeighborsOutDirectedWeighted(edgeToBeUpdated.getSrc()),
					edgeToBeUpdated.getDst());
			this.neighborNodesWeighted.get(edgeToBeUpdated.getDst()).put(
					edgeToBeUpdated.getSrc(),
					((DoubleWeight) edgeToBeUpdated.getWeight()).getWeight());

		}
		updateDirectedWeighted(edgeToBeUpdated, neighborsIn, neighborsOut);

	}

	@Override
	public boolean init() {
		reset_();
		return compute();
	}

	/**
	 * Removes a {@link Node} from the neighborNodes {@link Map}.
	 * 
	 * @param nodeToRemove
	 *            {@link Node} to remove from the {@link Map}
	 */
	private void removeFromNeighborNodesUnweighted(Node nodeToRemove) {
		this.neighborNodesUnweighted.remove(nodeToRemove);
		for (Entry<Node, HashSet<Node>> iterable_element : this.neighborNodesUnweighted
				.entrySet())
			if (iterable_element.getValue().contains(nodeToRemove))
				this.neighborNodesUnweighted.get(iterable_element.getKey())
						.remove(nodeToRemove);

	}

	/**
	 * Removes a {@link Node} from the neighborNodes {@link Map}.
	 * 
	 * @param nodeToRemove
	 *            {@link Node} to remove from the {@link Map}
	 */
	private void removeFromNeighborNodesWeighted(Node nodeToRemove) {
		this.neighborNodesWeighted.remove(nodeToRemove);
		for (Entry<Node, HashMap<Node, Double>> iterable_element : this.neighborNodesWeighted
				.entrySet())
			if (iterable_element.getValue().containsKey(nodeToRemove))
				this.neighborNodesWeighted.get(iterable_element.getKey())
						.remove(nodeToRemove);

	}

	/**
	 * Updates the jaccard similarity measure between the two given nodes.
	 */
	@Override
	protected void update(Node node1, Node node2) {
		if (Measures.EdgeWeightType.USE_WEIGHTS.equals(this.edgeWeightType))
			updateWeighted(node1, node2);
		else
			updateUnweighted(node1, node2);
	}

	/**
	 * Updates the jaccard similarity measure for unweighted graphs.
	 */
	private void updateUnweighted(Node node1, Node node2) {
		HashSet<Node> denominator = this.getUnionUnweighted(
				this.neighborNodesUnweighted.get(node1),
				this.neighborNodesUnweighted.get(node2));
		double newJaccard;
		Double matchingG = this.matching.get(node1, node2);
		if (matchingG == null || matchingG == 0 || denominator.size() == 0)
			newJaccard = 0;
		else
			newJaccard = matchingG / (double) denominator.size();

		Double jaccardG = this.result.get(node1, node2);
		if (jaccardG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(jaccardG);
		this.result.put(node1, node2, newJaccard);
		this.binnedDistribution.incr(newJaccard);

	}

	/**
	 * Updates the jaccard similarity measure for weighted graphs.
	 */
	private void updateWeighted(Node node1, Node node2) {
		HashMap<Node, Double> denominator = this.getUnionWeighted(
				this.neighborNodesWeighted.get(node1),
				this.neighborNodesWeighted.get(node2));
		double newJaccard;
		if (this.matching.get(node1, node2) == null
				|| this.matching.get(node1, node2) == 0
				|| denominator.size() == 0)
			newJaccard = 0;
		else
			newJaccard = this.matching.get(node1, node2)
					/ (double) getMapValueSum(denominator);

		if (newJaccard < 0.0 && Math.abs(newJaccard) <= 1.0E-4) {
			newJaccard = 0.0;
		}
		Double jaccardG = this.result.get(node1, node2);
		if (jaccardG == null)
			this.binnedDistribution.decr(0.0);
		else
			this.binnedDistribution.decr(jaccardG);
		this.result.put(node1, node2, newJaccard);
		this.binnedDistribution.incr(newJaccard);
	}
}
