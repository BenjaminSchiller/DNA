package dna.metrics.motifs.directedMotifs;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedMotifsListingU extends DirectedMotifsComputation {

	public DirectedMotifsListingU() {
		super("DirectedMotifsListingU", ApplicationType.BeforeAndAfterUpdate,
				MetricType.exact);
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
		if (u instanceof EdgeAddition) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (!e.getSrc().hasEdge(e.invert())) {
				this.removeCombinations(a, b,
						this.getConnectedNodesIntersection(a, b));
			} else {
				this.removeCombinations(a, b, this.getConnectedNodesUnion(a, b));
			}
		} else if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			this.removeCombinations(a, b, this.getConnectedNodesUnion(a, b));
		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		if (u instanceof EdgeAddition) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			this.addCombinations(a, b, this.getConnectedNodesUnion(a, b));
		} else if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (!e.getSrc().hasEdge(e.invert())) {
				this.addCombinations(a, b,
						this.getConnectedNodesIntersection(a, b));
			} else {
				this.addCombinations(a, b, this.getConnectedNodesUnion(a, b));
			}
		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal
		}
		return true;
	}

}
