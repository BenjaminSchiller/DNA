package dna.metrics.similarityMeasures.matching;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.DirectedWeightedEdge;
import dna.graph.edges.Edge;
import dna.graph.nodes.DirectedNode;
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
import dna.util.parameters.Parameter;

/**
 * The class implements the changes of {@link DirectedNode}s and unweighted
 * {@link DirectedDoubleWeightedEdge}s by updating the matching similarity
 * measure.
 * 
 * @see MatchingDirectedDoubleWeighted
 */
public class MatchingDirectedIntWeightedU extends MatchingDirectedIntWeighted {

	/**
	 * Initializes {@link MatchingDirectedIntWeightedU}. Implicitly sets degree
	 * type for directed graphs to outdegree.
	 */
	public MatchingDirectedIntWeightedU() {
		super("MatchingDirectedIntWeightedU",
				ApplicationType.BeforeAndAfterUpdate);
	}

	/**
	 * Initializes {@link MatchingDirectedIntWeightedU}.
	 * 
	 * @param directedDegreeType
	 *            <i>in</i> or <i>out</i>, determining whether to use in- or
	 *            outdegree for directed graphs.
	 */
	public MatchingDirectedIntWeightedU(Parameter directedDegreeType) {
		super("MatchingDirectedIntWeightedU",
				ApplicationType.BeforeAndAfterUpdate, directedDegreeType);
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	/**
	 * Called after the update is applied to the graph.
	 * 
	 * @param directedDoubleWeightedEdge
	 *            The update from the {@link Edge} which has been added.
	 * @return true, if successful; false otherwise
	 */
	private boolean applyAfterEdgeAddition(
			DirectedWeightedEdge directedIntWeightedEdge) {
		final DirectedWeightedEdge newEdge = directedIntWeightedEdge;
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
			for (IElement iterable_element : this.g.getNodes()) {
				Node node = (Node) iterable_element;
				// if (!(node.getIndex() == ((NodeAddition) u).getNode()
				// .getIndex()))
				this.matchingDirectedWeightedD.incr(0.0);
			}
		else if (u instanceof NodeRemoval)
			// New matchings for NodeRemovals are calculated before the update.
			;
		else if (u instanceof EdgeAddition)
			return applyAfterEdgeAddition(((DirectedWeightedEdge) ((EdgeAddition) u)
					.getEdge()));
		else if (u instanceof EdgeRemoval)
			// New matchings for EdgeRemovals are calculated before the update.
			;
		else if (u instanceof EdgeWeight)
			// nothing to do
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
	private boolean applyBeforeEdgeRemoval(
			DirectedWeightedEdge directedIntWeightedEdge) {
		final DirectedEdge edgeToRemove = directedIntWeightedEdge;
		if (isOutgoingMatching())
			this.decreaseMatching(this.getNeighborsIn(edgeToRemove.getDst()),
					edgeToRemove.getSrc());
		else
			this.decreaseMatching(this.getNeighborsOut(edgeToRemove.getSrc()),
					edgeToRemove.getDst());
		return true;
	}

	/**
	 * Called before the edge weight update is applied to the graph.
	 * 
	 * @param directedDoubleWeightedEdge
	 *            The {@link Edge} whose edge weight changes.
	 * @param weight
	 *            The new weight of the Edge after the Update.
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeEdgeWeightUpdate(
			DirectedWeightedEdge directedIntWeightedEdge, int weight) {
		// System.err.println("Jetzt gewichtsupdate");
		// System.out.println("AKT Kantengewicht: " +
		// directedIntWeightedEdge.getWeight() + " nach dem Update: " + weight);
		applyBeforeEdgeRemoval(directedIntWeightedEdge);
		// System.out.println("vor update: " +
		// directedIntWeightedEdge.getWeight());
		directedIntWeightedEdge.setWeight(new IntWeight(weight));
		// System.out.println("nach update: " +
		// directedIntWeightedEdge.getWeight());
		applyAfterEdgeAddition(directedIntWeightedEdge);
		return false;
	}

	/**
	 * Called before the update is applied to the graph.
	 * 
	 * @param NodeRemoval
	 *            The update from the {@link Node} which is to be removed.
	 * @return true, if successful; false otherwise
	 */
	private boolean applyBeforeNodeRemoval(NodeRemoval nodeRemoval) {
		final DirectedNode nodeToRemove = (DirectedNode) nodeRemoval.getNode();
		if (isOutgoingMatching())
			this.decreaseMatchingNodeRemove(this.getNeighborsIn(nodeToRemove));
		else
			this.decreaseMatchingNodeRemove(this.getNeighborsOut(nodeToRemove));

		for (IElement iterable_element : this.g.getNodes()) {
			Node node = (Node) iterable_element;
			// if (!(nodeToRemove.getIndex() == node.getIndex()))
			if (this.matchings.get(nodeToRemove, node) == null)
				this.matchingDirectedWeightedD.decr(0.0);
			else
				this.matchingDirectedWeightedD.decr(this.matchings.get(
						nodeToRemove, node));
		}

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
			return applyBeforeEdgeRemoval(((DirectedWeightedEdge) ((EdgeRemoval) u)
					.getEdge()));
		else if (u instanceof EdgeWeight) {
			DirectedWeightedEdge edgeD = ((DirectedWeightedEdge) ((EdgeWeight) u)
					.getEdge());
			return applyBeforeEdgeWeightUpdate(edgeD,
					((IntWeight) ((EdgeWeight) u).getWeight()).getWeight());
		}
		return false;
	}

	private void decreaseMatching(DirectedNode node1, int value1,
			DirectedNode node2, int value2) {
		// if (!(node1.getIndex() == node2.getIndex()))
		this.matchingDirectedWeightedD.decr(this.matchings.get(node1, node2));
		int value = this.matchings.get(node1, node2) - Math.min(value1, value2);
		if (value < 0)
			System.out.println("DecreaseMatching: "
					+ this.matchings.get(node1, node2)
					+ " Math min von value1: " + value1 + " value2: " + value2
					+ " ist: " + Math.min(value1, value2) + " Ergebnis "
					+ value);

		this.matchings.put(node1, node2, value);
		// if (!(node1.getIndex() == node2.getIndex()))
		this.matchingDirectedWeightedD.incr(this.matchings.get(node1, node2));
	}

	/**
	 * Decreases the matching between each pair of the given nodes by 1.
	 * 
	 * @param node
	 * 
	 * @see #decreaseMatching(UndirectedNode, UndirectedNode)
	 */
	private void decreaseMatching(HashMap<DirectedNode, Integer> map,
			DirectedNode node) {
		for (Entry<DirectedNode, Integer> node1 : map.entrySet()) {
			this.decreaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));

		}
	}

	/**
	 * Decrease the matching between all neighbors of the node to remove.
	 * 
	 * @param nodeToRemove
	 *            Node from which the neighbors node matching are to be
	 *            decreased.
	 */
	private void decreaseMatchingNodeRemove(
			HashMap<DirectedNode, Integer> neighbors) {
		for (Entry<DirectedNode, Integer> node1 : neighbors.entrySet()) {
			for (Entry<DirectedNode, Integer> node2 : neighbors.entrySet()) {
				if (node1.getKey().getIndex() > node2.getKey().getIndex())
					continue;
				this.decreaseMatching(node1.getKey(), node1.getValue(),
						node2.getKey(), node2.getValue());
			}
		}
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
	private HashMap<DirectedNode, Integer> getNeighborsIn(DirectedNode node) {
		final HashMap<DirectedNode, Integer> neighbors = new HashMap<DirectedNode, Integer>();
		for (IElement iEdge : node.getIncomingEdges()) {

			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getSrc(),
					((IntWeight) edgeD.getWeight()).getWeight());

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
	private HashMap<DirectedNode, Integer> getNeighborsOut(DirectedNode node) {
		final HashMap<DirectedNode, Integer> neighbors = new HashMap<DirectedNode, Integer>();
		for (IElement iEdge : node.getOutgoingEdges()) {
			DirectedWeightedEdge edgeD = (DirectedWeightedEdge) iEdge;
			neighbors.put(edgeD.getDst(),
					((IntWeight) edgeD.getWeight()).getWeight());

		}
		return neighbors;
	}

	private void increaseMatching(DirectedNode node1, int value1,
			DirectedNode node2, int value2) {
		Integer matchingG = this.matchings.get(node1, node2);
		// if (!(node1.getIndex() == node2.getIndex()))
		if (matchingG == null)
			this.matchingDirectedWeightedD.decr(0.0);
		else
			this.matchingDirectedWeightedD.decr(matchingG);

		Integer value = matchingG == null ? Math.min(value1, value2)
				: matchingG + Math.min(value1, value2);
		// if ((value < 0.0) && (Math.abs(value) <= 1.0E-4))
		// value = 0.0;
		if (value < 0)
			System.out.println("IncreaseMatching: "
					+ this.matchings.get(node1, node2)
					+ " Math min von value1: " + value1 + " value2: " + value2
					+ " ist: " + Math.min(value1, value2) + " Ergebnis "
					+ value);
		this.matchings.put(node1, node2, value);
		// if (!(node1.getIndex() == node2.getIndex()))
		this.matchingDirectedWeightedD.incr(this.matchings.get(node1, node2));
	}

	/**
	 * Increases the matching between each pair of the given nodes by 1.
	 * 
	 * @param node
	 * 
	 * @see #increaseMatching(UndirectedNode, UndirectedNode)
	 */
	private void increaseMatching(HashMap<DirectedNode, Integer> map,
			DirectedNode node) {
		for (Entry<DirectedNode, Integer> node1 : map.entrySet()) {
			this.increaseMatching(node1.getKey(), node1.getValue(), node,
					map.get(node));
		}
	}

}
