package dna.metrics.old;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedRichClubConnectivityForOneDegreeU extends
		DirectedRichClubConnectivityForOneDegree {

	public DirectedRichClubConnectivityForOneDegreeU(int minDegree) {
		super("RCCOneDegreeDyn", ApplicationType.AfterUpdate, minDegree);
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
		if (u instanceof NodeAddition) {
			return applyAfterNodeAddition(u);
		} else if (u instanceof NodeRemoval) {
			return applyAfterNodeRemoval(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterEdgeAddition(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterEdgeRemoval(u);
		}
		return false;
	}

	/**
	 * Check whether the Edge Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterEdgeRemoval(Update u) {
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
	private boolean applyAfterEdgeAddition(Update u) {
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

	/**
	 * Check whether the Node Removal has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterNodeRemoval(Update u) {
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
	 * Check whether the Node Addition has influence on RichClub
	 * 
	 * @param u
	 * @return boolean
	 */
	private boolean applyAfterNodeAddition(Update u) {
		return true;
	}

}
