package dna.metrics.richClubConnectivity;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.EdgeUpdate;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class RCCOneDegreeDyn extends RCCOneDegree {

	public RCCOneDegreeDyn() {
		super("RCCOneDegreeDyn", ApplicationType.AfterUpdate);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		// TODO Auto-generated method stub
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

	private boolean applyAfterEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (richClub.contains(src)) {
			if (src.getOutDegree() < k) {
				richClub.remove(src);
				for (DirectedEdge n : src.getOutgoingEdges()) {
					if (richClub.contains(n.getDst())) {
						this.richClubEdges--;
					}
				}
				for (DirectedEdge n : src.getIncomingEdges()) {
					if (richClub.contains(n.getSrc())) {
						this.richClubEdges--;
					}
				}
			} else if (richClub.contains(dst)) {
				this.richClubEdges--;
			}
		}
		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (richClub.contains(src)) {
			if (richClub.contains(dst)) {
				this.richClubEdges++;
			}
		} else if (src.getOutDegree() >= this.k && !richClub.contains(src)) {
			this.richClub.add(src);
			for (DirectedEdge n : src.getOutgoingEdges()) {
				if (this.richClub.contains(n.getDst())) {
					this.richClubEdges++;
				}
			}
			for (DirectedEdge n : src.getIncomingEdges()) {
				if (this.richClub.contains(n.getSrc())) {
					this.richClubEdges++;
				}
			}

		}
		return true;

	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();
		if (this.richClub.contains(n)) {
			richClub.remove(n);
			for (DirectedEdge ed : n.getOutgoingEdges()) {
				if (richClub.contains(ed.getDst())) {
					this.richClubEdges--;
				}
			}
			for (DirectedEdge ed : n.getIncomingEdges()) {
				if (richClub.contains(ed.getSrc())) {
					this.richClubEdges--;
				}
			}
		}
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		DirectedNode n = (DirectedNode) ((NodeAddition) u).getNode();
		if (n.getOutDegree() > this.k) {
			this.richClub.add(n);
			for (DirectedEdge ed : n.getOutgoingEdges()) {
				if (this.richClub.contains(ed.getDst())) {
					this.richClubEdges++;
				}
			}
			for (DirectedEdge ed : n.getIncomingEdges()) {
				if (this.richClub.contains(ed.getSrc())) {
					this.richClubEdges++;
				}
			}
		}
		return true;
	}

}
