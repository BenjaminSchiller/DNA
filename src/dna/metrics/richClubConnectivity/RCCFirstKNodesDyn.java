package dna.metrics.richClubConnectivity;

import dna.graph.Graph;
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
public class RCCFirstKNodesDyn extends RCCFirstKNodes {

	public RCCFirstKNodesDyn() {
		super("RCCFirstKNodesDyn", ApplicationType.AfterUpdate);
	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		if (this.richClub.contains(src)) {
			int i = this.richClub.indexOf(src);
			while (this.richClub.get(i).getOutDegree() > src.getOutDegree()
					&& i >= 0) {
				i--;
			}
			this.richClub.remove(src);
			this.richClub.add(i, src);

			if (this.richClub.contains(dst)) {
				this.edgesBetweenRichClub++;
			}
		} else if (src.getOutDegree() > this.richClub.getLast().getOutDegree()) {

			this.richClub.removeLast();

			for (DirectedEdge edge : src.getOutgoingEdges()) {
				if (this.richClub.contains(edge.getDst())) {
					this.edgesBetweenRichClub++;
				}
			}
			for (DirectedEdge edge : src.getIncomingEdges()) {
				if (this.richClub.contains(edge.getSrc())) {
					this.edgesBetweenRichClub++;
				}
			}
			for (DirectedEdge edge : this.richClub.getLast().getIncomingEdges()) {
				if (this.richClub.contains(edge.getSrc())) {
					this.edgesBetweenRichClub--;
				}
			}
			for (DirectedEdge edge : this.richClub.getLast().getOutgoingEdges()) {
				if (this.richClub.contains(edge.getDst())) {
					this.edgesBetweenRichClub--;
				}
			}
			this.richClub.addLast(src);
		} else if (src.getOutDegree() > rest.getFirst().getOutDegree()) {
			rest.remove(src);
			rest.addFirst(src);

		}
		return true;

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

	private boolean applyAfterNodeRemoval(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean applyAfterEdgeRemoval(Update u) {

		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();

		DirectedNode dst = e.getDst();
		DirectedNode src = e.getSrc();

		if (this.richClub.contains(src)) {
			if (src != this.richClub.getLast()) {
				if (src.getOutDegree() < this.richClub.getLast().getOutDegree()) {
					richClub.remove(src);
					richClub.addLast(src);
				}
				if (this.richClub.contains(dst)) {
					this.edgesBetweenRichClub--;
				}
			} else if (src.getOutDegree() >= this.rest.getFirst()
					.getOutDegree() && this.richClub.contains(dst)) {
				this.edgesBetweenRichClub--;
			} else {

				this.richClub.remove(src);

				for (DirectedNode n : src.getNeighbors()) {
					if (this.richClub.contains(n)) {
						this.edgesBetweenRichClub--;
					}
				}
				for (DirectedNode n : this.rest.getFirst().getNeighbors()) {
					if (this.richClub.contains(n)) {
						this.edgesBetweenRichClub++;
					}
				}
				this.rest.addFirst(src);
				this.richClub.addLast(this.rest.getFirst());
			}
		}
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isApplicable(Batch b) {
		// TODO Auto-generated method stub
		return false;
	}

}
