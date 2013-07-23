package dna.metrics.richClubConnectivity;

import java.util.Collection;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.metrics.Metric;
import dna.updates.Batch;
import dna.updates.Update;

public class RCCOneDegreeComp extends RCCOneDegree {

	public RCCOneDegreeComp() {
		super("RCCOneDegreeComp", ApplicationType.Recomputation);
	}

	@Override
	public boolean compute() {

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : (Collection<DirectedNode>) this.g.getNodes()) {

				int degree = n.getOutDegree();
				if (degree >= k) {
					this.richClub.add(n);

				}
			}
			for (DirectedNode n : this.richClub) {
				for (DirectedEdge w : n.getOutgoingEdges()) {
					if (richClub.contains(w.getDst())) {
						this.richClubEdges++;
					}
				}
			}

			int richClubMembers = richClub.size();
			this.richClubCoeffizient = (double) this.richClubEdges
					/ (double) (richClubMembers * (richClubMembers - 1));
			return true;
		}
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void init_() {
		// TODO Auto-generated method stub

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

	@Override
	public boolean isComparableTo(Metric m) {
		// TODO Auto-generated method stub
		return false;
	}

}
