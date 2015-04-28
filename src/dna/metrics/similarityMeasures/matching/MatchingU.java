package dna.metrics.similarityMeasures.matching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.edges.UndirectedWeightedEdge;
import dna.graph.generators.zalando.data.EventColumn;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.nodes.zalando.UndirectedZalandoNode;
import dna.graph.nodes.zalando.ZalandoNode;
import dna.graph.weights.DoubleWeight;
import dna.metrics.algorithms.IAfterUpdates;
import dna.metrics.algorithms.IAfterUpdatesWeighted;
import dna.metrics.algorithms.IBeforeUpdates;
import dna.metrics.algorithms.IBeforeUpdatesWeighted;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeWeight;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.NodeWeight;

/**
 * {@link IDynamicAlgorithm} of {@link Matching}.
 */
public class MatchingU extends Matching implements IBeforeUpdates,
		IAfterUpdates, IBeforeUpdatesWeighted, IAfterUpdatesWeighted {

	/**
	 * Initializes {@link MatchingU}. Implicitly sets degree type for directed
	 * graphs to outdegree and ignores wedge weights (if any).
	 */
	public MatchingU() {
		super("MatchingU");
	}

	/**
	 * Initializes {@link MatchingU}.
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
	public MatchingU(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType) {
		super("MatchingU", directedDegreeType, edgeWeightType);
	}

	public MatchingU(DirectedDegreeType directedDegreeType,
			EdgeWeightType edgeWeightType, EventColumn[] type) {
		super("MatchingU", directedDegreeType, edgeWeightType, type);
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param directedDoubleUnweightedEdge
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAdditionDirectedUnweighted(EdgeAddition u) {
		final DirectedEdge newEdge = (DirectedEdge) u.getEdge();
		if (isOutgoingMatching())
			this.increaseMatchingUnweighted(
					this.getNeighborsInDirectedUnweighted(newEdge.getDst()),
					newEdge.getSrc());
		else
			this.increaseMatchingUnweighted(
					this.getNeighborsOutDirectedUnweighted(newEdge.getSrc()),
					newEdge.getDst());
		return true;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param directedDoubleWeightedEdge
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful;
	 */
	private boolean applyAfterEdgeAdditionDirectedWeighted(
			DirectedWeightedEdge directedDoubleWeightedEdge) {
		final DirectedWeightedEdge newEdge = directedDoubleWeightedEdge;
		if (isOutgoingMatching())
			this.increaseMatchingWeighted(
					this.getNeighborsInDirectedWeighted(newEdge.getDst()),
					newEdge.getSrc());
		else
			this.increaseMatchingWeighted(
					this.getNeighborsOutDirectedWeighted(newEdge.getSrc()),
					newEdge.getDst());

		return true;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful
	 * 
	 * @see #increaseMatchingDirectedUnweighted(Set, UndirectedNode)
	 */
	private boolean applyAfterEdgeAdditionUndirectedUnweighted(
			EdgeAddition addedEdgeUpdate) {
		final UndirectedEdge newEdge = (UndirectedEdge) addedEdgeUpdate
				.getEdge();
		this.increaseMatchingUnweighted(
				this.getNeighborNodesUndirectedUnweighted(newEdge.getNode1()),
				newEdge.getNode2());
		this.increaseMatchingUnweighted(
				this.getNeighborNodesUndirectedUnweighted(newEdge.getNode2()),
				newEdge.getNode1());
		return true;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param addedEdgeUpdate
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful
	 * 
	 * @see #increaseMatchingDirectedWeighted(Set, UndirectedNode)
	 */
	private boolean applyAfterEdgeAdditionUndirectedWeighted(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge newEdge = undirectedDoubleWeightedEdge;

		if (type != null && newEdge.getNode1() instanceof UndirectedZalandoNode) {
			if (ZalandoNode.nodeIsOfType(newEdge.getNode1(), type)) {
				this.increaseMatchingWeighted(
						this.getNeighborNodesUndirectedWeighted(newEdge
								.getNode2()), newEdge.getNode1());
			} else {
				this.increaseMatchingWeighted(
						this.getNeighborNodesUndirectedWeighted(newEdge
								.getNode1()), newEdge.getNode2());
			}
		} else {
			// normaler standartfall ohne Filtern
			this.increaseMatchingWeighted(
					this.getNeighborNodesUndirectedWeighted(newEdge.getNode1()),
					newEdge.getNode2());
			this.increaseMatchingWeighted(
					this.getNeighborNodesUndirectedWeighted(newEdge.getNode2()),
					newEdge.getNode1());
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {

		if (DirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return this
						.applyAfterEdgeAdditionDirectedWeighted(((DirectedWeightedEdge) ((EdgeAddition) ea)
								.getEdge()));
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return applyAfterEdgeAdditionDirectedUnweighted((EdgeAddition) ea);

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return this
						.applyAfterEdgeAdditionUndirectedWeighted(((UndirectedWeightedEdge) ((EdgeAddition) ea)
								.getEdge()));
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return applyAfterEdgeAdditionUndirectedUnweighted((EdgeAddition) ea);

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
		if (type != null && na.getNode() instanceof UndirectedZalandoNode
				&& ZalandoNode.nodeIsOfType((Node) na.getNode(), type)) {
			for (IElement iterable_element : this.g.getNodes()) {
				Node node = (Node) iterable_element;
				if (!(node.getIndex() == na.getNode().getIndex()))
					if(ZalandoNode.nodeIsOfType(node, type))
					this.matchingD.incr(0.0);
			}
		} else if (type == null) {

			for (int i = 0; i < this.g.getNodeCount(); i++)
				this.matchingD.incr(0.0);
		}
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
		if (isOutgoingMatching())
			this.decreaseMatchingUnweighted(this
					.getNeighborsInDirectedUnweighted(edgeToRemove.getDst()),
					edgeToRemove.getSrc());
		else
			this.decreaseMatchingUnweighted(this
					.getNeighborsOutDirectedUnweighted(edgeToRemove.getSrc()),
					edgeToRemove.getDst());

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
		final DirectedEdge edgeToRemove = directedDoubleWeightedEdge;
		if (isOutgoingMatching())
			this.decreaseMatchingWeighted(
					this.getNeighborsInDirectedWeighted(edgeToRemove.getDst()),
					edgeToRemove.getSrc());
		else
			this.decreaseMatchingWeighted(
					this.getNeighborsOutDirectedWeighted(edgeToRemove.getSrc()),
					edgeToRemove.getDst());
		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param edgeRemoval
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful
	 * 
	 * @see #decreaseMatchingDirectedUnweighted(Set, UndirectedNode)
	 */
	private boolean applyBeforeEdgeRemovalUndirectedUnweighted(
			EdgeRemoval edgeRemoval) {
		final UndirectedEdge edgeToRemove = (UndirectedEdge) edgeRemoval
				.getEdge();
		// decrease the Matching of the neighbors by 1
		this.decreaseMatchingUnweighted(this
				.getNeighborNodesUndirectedUnweighted(edgeToRemove.getNode1()),
				edgeToRemove.getNode2());
		this.decreaseMatchingUnweighted(this
				.getNeighborNodesUndirectedUnweighted(edgeToRemove.getNode2()),
				edgeToRemove.getNode1());

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param undirectedDoubleWeightedEdge
	 *            The update from the {@link Edge} which is to be removed.
	 * @return true, if successful
	 */
	private boolean applyBeforeEdgeRemovalUndirectedWeighted(
			UndirectedWeightedEdge undirectedDoubleWeightedEdge) {
		final UndirectedWeightedEdge edgeToRemove = undirectedDoubleWeightedEdge;

		if (type != null
				&& edgeToRemove.getNode1() instanceof UndirectedZalandoNode) {
			if (ZalandoNode.nodeIsOfType(edgeToRemove.getNode1(), type)) {
				this.decreaseMatchingWeighted(this
						.getNeighborNodesUndirectedWeighted(edgeToRemove
								.getNode2()), edgeToRemove.getNode1());
			} else {
				this.decreaseMatchingWeighted(this
						.getNeighborNodesUndirectedWeighted(edgeToRemove
								.getNode1()), edgeToRemove.getNode2());
			}
		} else {
			// normaler standartfall ohne Filtern
			this.decreaseMatchingWeighted(
					this.getNeighborNodesUndirectedWeighted(edgeToRemove
							.getNode1()), edgeToRemove.getNode2());
			this.decreaseMatchingWeighted(
					this.getNeighborNodesUndirectedWeighted(edgeToRemove
							.getNode2()), edgeToRemove.getNode1());
		}
		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param directedWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful;
	 */
	private boolean applyBeforeEdgeWeightUpdateDirectedWeighted(
			DirectedWeightedEdge directedWeightedEdge, double weight) {
		applyBeforeEdgeRemovalDirectedWeighted(directedWeightedEdge);
		directedWeightedEdge.setWeight(new DoubleWeight(weight));
		applyAfterEdgeAdditionDirectedWeighted(directedWeightedEdge);
		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param undirectedWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful
	 */
	private boolean applyBeforeEdgeWeightUpdateUndirectedWeighted(
			UndirectedWeightedEdge undirectedWeightedEdge, double weight) {
		applyBeforeEdgeRemovalUndirectedWeighted(undirectedWeightedEdge);
		undirectedWeightedEdge.setWeight(new DoubleWeight(weight));
		applyAfterEdgeAdditionUndirectedWeighted(undirectedWeightedEdge);
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
		if (isOutgoingMatching()) {
			this.decreaseMatchingNodeRemoveUnweighted(this
					.getNeighborsInDirectedUnweighted(nodeToRemove));
		} else {
			this.decreaseMatchingNodeRemoveUnweighted(this
					.getNeighborsOutDirectedUnweighted(nodeToRemove));
		}

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.matchingD.decr(0);
			else
				this.matchingD.decr(this.matching.get(nodeToRemove, node));
		}

		// remove the results of the removed node calculated so far
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful;
	 */
	private boolean applyBeforeNodeRemovalDirectedWeighted(
			NodeRemoval nodeRemoval) {
		final DirectedNode nodeToRemove = (DirectedNode) nodeRemoval.getNode();
		if (isOutgoingMatching())
			this.decreaseMatchingNodeRemoveWeighted(this
					.getNeighborsInDirectedWeighted(nodeToRemove));
		else
			this.decreaseMatchingNodeRemoveWeighted(this
					.getNeighborsOutDirectedWeighted(nodeToRemove));

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.matchingD.decr(0.0);
			else
				this.matchingD.decr(this.matching.get(nodeToRemove, node));
		}

		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param nodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful
	 * 
	 * @see #decreaseMatchingNodeRemoveDirectedUnweighted(Set)
	 */
	private boolean applyBeforeNodeRemovalUndirectedUnweighted(
			NodeRemoval nodeRemove) {
		final UndirectedNode nodeToRemove = (UndirectedNode) nodeRemove
				.getNode();

		this.decreaseMatchingNodeRemoveUnweighted(this
				.getNeighborNodesUndirectedUnweighted(nodeToRemove));

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			if (this.matching.get(nodeToRemove, node) == null)
				this.matchingD.decr(0);
			else
				this.matchingD.decr(this.matching.get(nodeToRemove, node));
		}
		// remove the results of the removed node calculated so far
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);

		return true;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param nodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful
	 */
	private boolean applyBeforeNodeRemovalUndirectedWeighted(
			NodeRemoval nodeRemoval) {
		final UndirectedNode nodeToRemove = (UndirectedNode) nodeRemoval
				.getNode();

		this.decreaseMatchingNodeRemoveWeighted(this
				.getNeighborNodesUndirectedWeighted(nodeToRemove));

		// FILTER
		if (type != null) {
			if (nodeToRemove instanceof UndirectedZalandoNode
					&& ZalandoNode.nodeIsOfType(nodeToRemove, type)) {
				for (IElement iterable_element : this.g.getNodes()) {
					Node node = (Node) iterable_element;
					if (!(nodeToRemove.getIndex() == node.getIndex()))
						if (this.matching.get(nodeToRemove, node) == null)
							this.matchingD.decr(0.0);
						else
							this.matchingD.decr(this.matching.get(nodeToRemove,
									node));
				}
			}
			// NORMALFALL
		} else {
			for (IElement iterable_element : this.g.getNodes()) {
				Node node = (Node) iterable_element;
				if (this.matching.get(nodeToRemove, node) == null)
					this.matchingD.decr(0.0);
				else
					this.matchingD.decr(this.matching.get(nodeToRemove, node));
			}

		}

		// remove the results of the removed node calculated so far
		this.matching.removeRow(nodeToRemove);
		this.matching.removeColumn(nodeToRemove);

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
				return applyBeforeEdgeRemovalUndirectedWeighted(((UndirectedWeightedEdge) ((EdgeRemoval) er)
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

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return this
						.applyBeforeNodeRemovalDirectedWeighted((NodeRemoval) nr);
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return this
						.applyBeforeNodeRemovalDirectedUnweighted((NodeRemoval) nr);

		} else if (UndirectedWeightedEdge.class.isAssignableFrom(this.g
				.getGraphDatastructures().getEdgeType())) {

			if (this.edgeWeightType.equals(EdgeWeightType.USE_WEIGHTS))
				return this
						.applyBeforeNodeRemovalUndirectedWeighted((NodeRemoval) nr);
			else if (this.edgeWeightType.equals(EdgeWeightType.IGNORE_WEIGHTS))
				return applyBeforeNodeRemovalUndirectedUnweighted((NodeRemoval) nr);

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
	 * Decrease the matching between all neighbors of the node to remove.
	 * 
	 * @param nodeToRemove
	 *            Node from which the neighbors node matching are to be
	 *            decreased.
	 */
	private void decreaseMatchingNodeRemoveUnweighted(
			HashSet<Node> neighborNodes) {
		for (Node node1 : neighborNodes) {
			for (Node node2 : neighborNodes) {
				if (node1.getIndex() > node2.getIndex())
					continue;
				decreaseMatchingUnweighted(node1, node2);
			}
		}

	}

	/**
	 * Decrease the matching between all neighbors of the node to remove.
	 * 
	 * @param nodeToRemove
	 *            Node from which the neighbors node matching are to be
	 *            decreased.
	 */
	private void decreaseMatchingNodeRemoveWeighted(
			HashMap<Node, Double> neighbors) {
		for (Entry<Node, Double> node1 : neighbors.entrySet()) {
			for (Entry<Node, Double> node2 : neighbors.entrySet()) {
				if (node1.getKey().getIndex() > node2.getKey().getIndex())
					continue;
				this.decreaseMatchingWeighted(node1.getKey(), node1.getValue(),
						node2.getKey(), node2.getValue());
			}
		}
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @param node
	 * 
	 * @see #decreaseMatchingDirectedUnweighted(DirectedNode, DirectedNode)
	 */
	private void decreaseMatchingUnweighted(HashSet<Node> nodes, Node node) {
		for (Node node1 : nodes) {
			this.decreaseMatchingUnweighted(node1, node);
		}
	}

	/**
	 * Decreases the matching between the given nodes by 1.
	 */
	private void decreaseMatchingUnweighted(Node node1, Node node2) {
		this.matchingD.decr(this.matching.get(node1, node2));
		this.matching.put(node1, node2, this.matching.get(node1, node2) - 1);
		this.matchingD.incr(this.matching.get(node1, node2));
	}

	/**
	 * Decreases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #decreaseMatchingDU(UndirectedNode, UndirectedNode)
	 */
	private void decreaseMatchingWeighted(HashMap<Node, Double> map, Node node) {
		for (Entry<Node, Double> node1 : map.entrySet()) {
			this.decreaseMatchingWeighted(node1.getKey(), node1.getValue(),
					node, map.get(node));

		}
	}

	/**
	 * Decreases the matching between the given nodes.
	 */
	private void decreaseMatchingWeighted(Node node1, Double value1,
			Node node2, Double value2) {

		if (type != null) {
			if (node1 instanceof UndirectedZalandoNode
					&& ZalandoNode.nodeIsOfType(node1, type)
					&& node2 instanceof UndirectedZalandoNode
					&& ZalandoNode.nodeIsOfType(node2, type))
				if (!(node1.getIndex() == node2.getIndex()))
					this.matchingD.decr(this.matching.get(node1, node2));

			double value = this.matching.get(node1, node2)
					- Math.min(value1, value2);
			this.matching.put(node1, node2, value);
			if (!(node1.getIndex() == node2.getIndex()))
				this.matchingD.incr(this.matching.get(node1, node2));
		} else {

			this.matchingD.decr(this.matching.get(node1, node2));
			double value = this.matching.get(node1, node2)
					- Math.min(value1, value2);
			if ((value < 0.0) && (Math.abs(value) <= 1.0E-4))
				value = 0.0;
			this.matching.put(node1, node2, value);
			this.matchingD.incr(this.matching.get(node1, node2));
		}
	}

	/**
	 * Get all incoming neighbors for a given node.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Set} containing all incoming neighbors of given node.
	 */
	private HashSet<Node> getNeighborsInDirectedUnweighted(DirectedNode node) {
		final HashSet<Node> neighbors = new HashSet<Node>();
		DirectedEdge edge;
		for (IElement iEdge : node.getIncomingEdges()) {
			edge = (DirectedEdge) iEdge;
			neighbors.add(edge.getSrc());
		}
		return neighbors;
	}

	/**
	 * Get all neighbors of a node are connected by incoming
	 * {@link DirectedEdge}s with him.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Map} containing all neighbors of given node.
	 * 
	 */
	private HashMap<Node, Double> getNeighborsInDirectedWeighted(
			DirectedNode node) {
		final HashMap<Node, Double> neighbors = new HashMap<Node, Double>();
		for (IElement iEdge : node.getIncomingEdges()) {
			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getSrc(), weight(edgeD.getWeight()));

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
	private HashSet<Node> getNeighborsOutDirectedUnweighted(DirectedNode node) {
		final HashSet<Node> neighbors = new HashSet<Node>();
		DirectedEdge edge;
		for (IElement iEdge : node.getOutgoingEdges()) {
			edge = (DirectedEdge) iEdge;
			neighbors.add(edge.getDst());
		}
		return neighbors;
	}

	/**
	 * Get all neighbors of a node are connected by outgoing
	 * {@link DirectedEdge} with him.
	 * 
	 * @param node
	 *            The {@link Node} which neighbors are wanted.
	 * @return A {@link Map} containing all neighbors of given node.
	 * 
	 */
	private HashMap<Node, Double> getNeighborsOutDirectedWeighted(
			DirectedNode node) {
		final HashMap<Node, Double> neighbors = new HashMap<Node, Double>();
		for (IElement iEdge : node.getOutgoingEdges()) {

			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getDst(), weight(edgeD.getWeight()));

		}
		return neighbors;
	}

	/**
	 * Increases the matching between each pair of the given nodes by 1.
	 * 
	 * @param directedNode
	 * 
	 * @see #increaseMatchingDirectedUnweighted(DirectedNode, DirectedNode)
	 */
	private void increaseMatchingUnweighted(HashSet<Node> nodes, Node node) {
		for (Node node1 : nodes) {
			this.increaseMatchingUnweighted(node1, node);
		}
	}

	/**
	 * Increases the matching between the given nodes by 1.
	 */
	private void increaseMatchingUnweighted(Node node1, Node node2) {
		Double matchingG = this.matching.get(node1, node2);
		if (matchingG == null)
			this.matchingD.decr(0.0);
		else
			this.matchingD.decr(matchingG);
		this.matching.put(node1, node2, matchingG == null ? 1 : matchingG + 1);
		this.matchingD.incr(this.matching.get(node1, node2));
	}

	/**
	 * Increases the matching between each pair of the given nodes.
	 * 
	 * @param node
	 * 
	 * @see #increaseMatchingDU(UndirectedNode, UndirectedNode)
	 */
	private void increaseMatchingWeighted(HashMap<Node, Double> map, Node node) {
		for (Entry<Node, Double> node1 : map.entrySet()) {
			this.increaseMatchingWeighted(node1.getKey(), node1.getValue(),
					node, map.get(node));
		}
	}

	/**
	 * Increases the matching between the given nodes.
	 */
	private void increaseMatchingWeighted(Node node1, Double value1,
			Node node2, Double value2) {
		Double matchingG = this.matching.get(node1, node2);

		if (type != null) {
			if (node1 instanceof UndirectedZalandoNode
					&& ZalandoNode.nodeIsOfType(node1, type)
					&& node2 instanceof UndirectedZalandoNode
					&& ZalandoNode.nodeIsOfType(node2, type)) {
				if (!(node1.getIndex() == node2.getIndex()))
					if (matchingG == null)
						this.matchingD.decr(0.0);
					else
						this.matchingD.decr(matchingG);

				// System.err.println("Min " + Math.min(value1, value2));

				double value = matchingG == null ? Math.min(value1, value2)
						: matchingG + Math.min(value1, value2);

				this.matching.put(node1, node2, value);
				// this.matchingUndirectedWeightedD.incr(this.matchings.get(node1,
				// node2));
				if (!(node1.getIndex() == node2.getIndex()))
					this.matchingD.incr(this.matching.get(node1, node2));
			}
		} else {
			if (matchingG == null)
				this.matchingD.decr(0.0);
			else
				this.matchingD.decr(matchingG);

			double value = matchingG == null ? Math.min(value1, value2)
					: matchingG + Math.min(value1, value2);
			if ((value < 0.0) && (Math.abs(value) <= 1.0E-4))
				value = 0.0;
			this.matching.put(node1, node2, value);

			this.matchingD.incr(this.matching.get(node1, node2));
		}
	}

	@Override
	public boolean init() {
		reset_();
		return compute();
	}
}
