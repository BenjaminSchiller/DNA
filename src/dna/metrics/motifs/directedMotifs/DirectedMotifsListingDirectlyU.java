package dna.metrics.motifs.directedMotifs;

import java.util.HashSet;

import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeAdditionException;
import dna.metrics.motifs.directedMotifs.exceptions.DirectedMotifInvalidEdgeRemovalException;
import dna.metrics.motifs.directedMotifs.exceptions.DirectedMotifSplittingException;
import dna.metrics.motifs.directedMotifs.exceptions.InvalidDirectedMotifException;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedMotifsListingDirectlyU extends DirectedMotifsComputation {

	public DirectedMotifsListingDirectlyU() {
		super("DirectedMotifsListingDirectlyU",
				ApplicationType.BeforeAndAfterUpdate, MetricType.exact);
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
				this.transformCombinations(a, b,
						this.getConnectedNodesIntersection(a, b), u);
			} else {
				this.transformCombinations(a, b,
						this.getConnectedNodesUnion(a, b), u);
			}
		} else if (u instanceof EdgeRemoval) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode a = e.getSrc();
			DirectedNode b = e.getDst();
			if (!e.getSrc().hasEdge(e.invert())) {
				HashSet<DirectedNode> a_ = this.getConnectedNodes(a);
				HashSet<DirectedNode> b_ = this.getConnectedNodes(b);
				this.removeCombinations(a, b,
						this.getConnectedNodesExcluding(a, b_, b));
				this.removeCombinations(a, b,
						this.getConnectedNodesExcluding(b, a_, a));
				this.transformCombinations(a, b,
						this.getConnectedNodesIntersection(a, b), u);
			} else {
				this.transformCombinations(a, b,
						this.getConnectedNodesUnion(a, b), u);
			}
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
			if (!e.getSrc().hasEdge(e.invert())) {
				HashSet<DirectedNode> a_ = this.getConnectedNodes(a);
				HashSet<DirectedNode> b_ = this.getConnectedNodes(b);
				this.addCombinations(a, b,
						this.getConnectedNodesExcluding(a, b_, b));
				this.addCombinations(a, b,
						this.getConnectedNodesExcluding(b, a_, a));
			}
		} else if (u instanceof NodeRemoval) {
			// TODO implement node removal
		}
		return true;
	}

}
