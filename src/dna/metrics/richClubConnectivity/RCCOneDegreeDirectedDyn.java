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
<<<<<<< HEAD:src/dna/metrics/richClubConnectivity/RCCOneDegreeDyn.java
public class RCCOneDegreeDyn extends RCCOneDegree {
=======
public class RCCOneDegreeDirectedDyn extends RCCOneDegreeDirected {
>>>>>>> some stuff:src/dna/metrics/richClubConnectivity/RCCOneDegreeDirectedDyn.java

	public RCCOneDegreeDirectedDyn() {
		super("RCCOneDegreeDyn", ApplicationType.AfterUpdate);
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

	private boolean applyAfterEdgeRemoval(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (src.getOutDegree() + 1 > this.k) {
			if (dst.getOutDegree() >= this.k) {
				this.richClubEdges--;
			}
		} else if (src.getOutDegree() + 1 == this.k) {
			for (DirectedEdge n : src.getOutgoingEdges()) {
				if (n.getDst().getOutDegree() >= this.k) {
					this.richClubEdges--;
				}
			}
			for (DirectedEdge n : src.getIncomingEdges()) {
				if (n.getSrc().getOutDegree() >= this.k) {
					this.richClubEdges--;
				}
			}
			if (dst.getOutDegree() >= this.k) {
				this.richClubEdges--;
			}
			richClub.remove(src);

		}

<<<<<<< HEAD
<<<<<<< HEAD:src/dna/metrics/richClubConnectivity/RCCOneDegreeDyn.java
=======
>>>>>>> some stuff
		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));

<<<<<<< HEAD
=======
>>>>>>> some stuff:src/dna/metrics/richClubConnectivity/RCCOneDegreeDirectedDyn.java
=======
>>>>>>> some stuff
		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (src.getOutDegree() > this.k) {
			if (dst.getOutDegree() >= this.k) {
				this.richClubEdges++;
			}
		} else if (src.getOutDegree() == this.k) {
			this.richClub.add(src);
			for (DirectedEdge n : src.getOutgoingEdges()) {
				if (n.getDst().getOutDegree() >= this.k) {
					this.richClubEdges++;
				}
			}
			for (DirectedEdge n : src.getIncomingEdges()) {
				if (n.getSrc().getOutDegree() >= this.k) {
					this.richClubEdges++;
				}
			}
		}

		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
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

		int richClubMembers = richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		return true;
	}

}
