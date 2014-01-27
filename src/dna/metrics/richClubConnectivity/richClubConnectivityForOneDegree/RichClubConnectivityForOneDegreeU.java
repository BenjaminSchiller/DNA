package dna.metrics.richClubConnectivity.richClubConnectivityForOneDegree;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * Calculates the rich club connectivity value for richClub consiting of nodes
 * with degree >= minDegree
 * 
 * 
 */
public class RichClubConnectivityForOneDegreeU extends
		RichClubConnectivityForOneDegree {

	public RichClubConnectivityForOneDegreeU(int minDegree) {
		super("RichClubConnectivityForOneDegreeU-" + minDegree,
				ApplicationType.AfterUpdate, minDegree);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterDirectedNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterDirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterDirectedEdgeRemoval(u);
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterUndirectedNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterUndirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterUndirectedEdgeRemoval(u);
			}
		}

		return false;
	}

	private boolean applyAfterDirectedEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (src.getOutDegree() >= this.minDegree && this.richClub.contains(src)) {
			if (this.richClub.contains(dst)) {
				this.richClubEdges--;
			}
		} else {
			if (this.richClub.contains(src) && this.richClub.contains(dst)) {
				this.richClubEdges--;
			}
			checkNodeREM(src);
		}
		return true;
	}

	private boolean applyAfterDirectedEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (this.richClub.contains(src)) {
			if (this.richClub.contains(dst)) {
				this.richClubEdges++;
			}
		} else if (src.getOutDegree() >= this.minDegree
				&& !this.richClub.contains(src)) {
			this.richClub.add(src);
			for (IElement iE : src.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (this.richClub.contains(edge.getDst())) {
					this.richClubEdges++;
				}
			}
			for (IElement iE : src.getIncomingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (this.richClub.contains(edge.getSrc())) {
					this.richClubEdges++;
				}
			}
		}

		return true;
	}

	private boolean applyAfterDirectedNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		if (this.richClub.contains(node)) {
			this.richClub.remove(node);
			for (IElement iEdge : node.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iEdge;
				if (this.richClub.contains(edge.getDst())) {
					this.richClubEdges--;
				}
			}
			for (IElement iEdge : node.getIncomingEdges()) {
				DirectedEdge edge = (DirectedEdge) iEdge;
				if (this.richClub.contains(edge.getSrc())) {
					if (this.richClub.contains(edge.getSrc())) {
						checkNodeREM(edge.getSrc());
						this.richClubEdges--;
					}
				}
			}
		} else {
			for (IElement iEdge : node.getIncomingEdges()) {
				DirectedEdge edge = (DirectedEdge) iEdge;
				if (this.richClub.contains(edge.getSrc())) {
					if (this.richClub.contains(edge.getSrc())) {
						checkNodeREM(edge.getSrc());
					}
				}
			}
		}

		return true;
	}

	/**
	 * Check whether the Edge Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		if (this.richClub.contains(node1) && this.richClub.contains(node2)
				&& node1.getDegree() >= this.minDegree
				&& node2.getDegree() >= this.minDegree) {
			this.richClubEdges -= 2;
		} else {
			if (this.richClub.contains(node1) && this.richClub.contains(node2)) {
				this.richClubEdges -= 2;
			}
			checkNodeREM(node1);
			checkNodeREM(node2);
		}
		return true;
	}

	/**
	 * Check whether the Degree changes at node has influence on RichClub
	 * 
	 * @param node
	 * @return void
	 */
	private void checkNodeREM(UndirectedNode node) {
		if (node.getDegree() < this.minDegree && this.richClub.contains(node)) {
			this.richClub.remove(node);
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdge;
				UndirectedNode n = edge.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					this.richClubEdges -= 2;
				}
			}
		}
	}

	/**
	 * Check whether the Degree changes at node has influence on RichClub
	 * 
	 * @param node
	 * @return void
	 */
	private void checkNodeREM(DirectedNode node) {
		if (node.getOutDegree() < this.minDegree
				&& this.richClub.contains(node)) {
			richClub.remove(node);
			for (IElement iE : node.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (this.richClub.contains(edge.getDst())) {
					this.richClubEdges--;
				}
			}
			for (IElement iE : node.getIncomingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (this.richClub.contains(edge.getSrc())) {
					this.richClubEdges--;
				}
			}
		}
	}

	/**
	 * Check whether the Edge Addition has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterUndirectedEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		if (this.richClub.contains(node1) && this.richClub.contains(node2)) {
			this.richClubEdges += 2;
		} else {
			checkNodeADD(node1);
			checkNodeADD(node2);
		}
		return true;

	}

	/**
	 * Check whether the the Degree changes at node has influence on RichClub
	 * 
	 * @param node
	 * @return void
	 */
	private void checkNodeADD(UndirectedNode node) {
		if (node.getDegree() >= this.minDegree && !this.richClub.contains(node)) {
			this.richClub.add(node);
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iEdge;
				UndirectedNode n = ed.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					this.richClubEdges += 2;
				}
			}
		}
	}

	/**
	 * Check whether the Node Removal has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterUndirectedNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();
		if (this.richClub.contains(node)) {
			this.richClub.remove(node);
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdge;
				UndirectedNode n = edge.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					checkNodeREM(n);
					this.richClubEdges -= 2;
				}
			}
		} else {
			for (IElement iEdge : node.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdge;
				UndirectedNode n = edge.getDifferingNode(node);
				if (this.richClub.contains(n)) {
					checkNodeREM(n);
				}
			}
		}
		return true;
	}

	/**
	 * Check whether the Node Addition has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterNodeAddition(Update u) {
		return true;
	}

}
