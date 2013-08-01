package dna.metrics.richClubConnectivity;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.EdgeUpdate;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;
import dna.util.Log;

@SuppressWarnings("rawtypes")
public class RCCKNodeIntervalDyn extends RCCKNodeInterval {

	public RCCKNodeIntervalDyn() {
		super("RCCKNodeIntervalComp", ApplicationType.Recomputation);
	}

	// @Override
	// protected boolean applyAfterDiff_(Diff d) throws
	// DiffNotApplicableException {
	// for (int j = 0; j < richClubs.size(); j++) {
	// double divisor = (j + 1) * richClubIntervall
	// * ((j + 1) * richClubIntervall - 1);
	// richClubCoefficienten[j] = (double) (richClubEdges[j]) / divisor;
	// }
	//
	// return true;
	// }
	//
	// @Override
	// protected boolean applyAfterEdgeAddition_(Diff d, Edge e) {
	// Node src = e.getSrc();
	// Node dst = e.getDst();
	// int degree = src.getOut().size();
	//
	// boolean noRichClubChange = degree <= richClubs
	// .get(this.nodesRichClub[src.getIndex()]).getFirst().getOut()
	// .size()
	// || degree <= richClubs
	// .get(this.nodesRichClub[src.getIndex()] - 1).getLast()
	// .getOut().size();
	//
	// if (noRichClubChange) {
	// if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
	// .getIndex()]) {
	//
	// this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
	// this.nodesRichClub[src.getIndex()]);
	//
	// }
	// } else {
	// for (Node n : src.getIn()) {
	// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
	// .getIndex()] - 1) {
	// this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
	// this.nodesRichClub[src.getIndex()] - 1);
	// }
	// }
	// for (Node n : src.getOut()) {
	// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
	// .getIndex()] - 1) {
	// this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
	// this.nodesRichClub[src.getIndex()] - 1);
	// }
	// }
	// for (Node n : this.richClubs
	// .get(this.nodesRichClub[src.getIndex()] - 1).getLast()
	// .getOut()) {
	// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
	// .getIndex()] - 1) {
	// this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
	// this.nodesRichClub[src.getIndex()] - 1);
	// ;
	// }
	// }
	// for (Node n : this.richClubs
	// .get(this.nodesRichClub[src.getIndex()] - 1).getLast()
	// .getIn()) {
	// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
	// .getIndex()] - 1) {
	// this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
	// this.nodesRichClub[src.getIndex()] - 1);
	// ;
	// }
	// }
	// if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
	// .getIndex()]) {
	//
	// this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
	// this.nodesRichClub[src.getIndex()]);
	//
	// }
	//
	// // TODO:RichClubCahnge
	// }
	//
	// return true;
	// }

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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			return this.applyAfterUpdateDirected(u);
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			return this.applyAfterUpdateUndirected(u);
		}
		Log.error("DD - unsupported node type "
				+ this.g.getGraphDatastructures().getNodeType());
		return false;
	}

	private boolean applyAfterUpdateUndirected(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterUpdateDirected(Update u) {
		if (u instanceof NodeAddition) {
			return applyAfterNodeAdditionDirected(u);
		} else if (u instanceof NodeRemoval) {
			return applyAfterNodeRemovalDirected(u);
		} else if (u instanceof EdgeAddition) {
			return applyAfterEdgeAdditionDirected(u);
		} else if (u instanceof EdgeRemoval) {
			return applyAfterEdgeRemovalDirected(u);
		}
		return false;
	}

	private boolean applyAfterEdgeRemovalDirected(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int degree = src.getOutDegree();

		// boolean noRichClubChange = degree >= richClubs.get(
		// this.nodesRichClub[src.getIndex()]).getLast().get
		// || degree <= richClubs
		// .get(this.nodesRichClub[src.getIndex()] + 1).getFirst()
		// .getOut().size();
		//
		// if (noRichClubChange) {
		// if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
		// .getIndex()]) {
		//
		// this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
		// this.nodesRichClub[src.getIndex()]);
		//
		// }
		// } else {
		//
		// for (Node n : src.getIn()) {
		// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
		// .getIndex()] + 1) {
		// this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
		// this.nodesRichClub[src.getIndex()] - 1);
		// }
		// }
		// for (Node n : src.getOut()) {
		// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
		// .getIndex()] + 1) {
		// this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
		// this.nodesRichClub[src.getIndex()] - 1);
		// }
		// }
		// for (Node n : this.richClubs
		// .get(this.nodesRichClub[src.getIndex()] + 1).getFirst()
		// .getOut()) {
		// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
		// .getIndex()] + 1) {
		// this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
		// this.nodesRichClub[src.getIndex()] - 1);
		// ;
		// }
		// }
		// for (Node n : this.richClubs
		// .get(this.nodesRichClub[src.getIndex()] + 1).getFirst()
		// .getIn()) {
		// if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
		// .getIndex()] + 1) {
		// this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
		// this.nodesRichClub[src.getIndex()] - 1);
		// ;
		// }
		// }
		// if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
		// .getIndex()]) {
		//
		// this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
		// this.nodesRichClub[src.getIndex()]);
		//
		// }
		//
		// // TODO:RichClubCahnge
		//
		// }

		return true;
	}

	private boolean applyAfterEdgeAdditionDirected(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterNodeRemovalDirected(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterNodeAdditionDirected(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

}
