package dna.metrics.richClubConnectivity;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
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
			for (IElement iE : src.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (edge.getDst().getOutDegree() >= this.k) {
					this.richClubEdges--;
				}
			}
			for (IElement iE : src.getIncomingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (edge.getSrc().getOutDegree() >= this.k) {
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
			for (IElement iE : src.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (edge.getDst().getOutDegree() >= this.k) {
					this.richClubEdges++;
				}
			}
			for (IElement iE : src.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iE;
				if (edge.getSrc().getOutDegree() >= this.k) {
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
			for (IElement iE : n.getOutgoingEdges()) {
				DirectedEdge ed = (DirectedEdge) iE;
				if (richClub.contains(ed.getDst())) {
					this.richClubEdges--;
				}
			}
			for (IElement iE : n.getIncomingEdges()) {
				DirectedEdge ed = (DirectedEdge) iE;
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
