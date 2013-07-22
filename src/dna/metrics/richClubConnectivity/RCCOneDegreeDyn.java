package dna.metrics.richClubConnectivity;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;

public class RCCOneDegreeDyn extends RCCOneDegree {

	public RCCOneDegreeDyn() {
		super("RCCOneDegreeDyn", false, true, true);
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("before diff");

	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e) {
		Node src = e.getSrc();
		Node dst = e.getDst();

		if (richClub.contains(src)) {
			if (richClub.contains(dst)) {
				this.richClubEdges++;
			}
		} else if (src.getOut().size() >= this.k && !richClub.contains(src)) {
			this.richClub.add(src);
			for (Node n : src.getOut()) {
				if (this.richClub.contains(n)) {
					this.richClubEdges++;
				}
			}
			for (Node n : src.getIn()) {
				if (this.richClub.contains(n)) {
					this.richClubEdges++;
				}
			}

		}
		return true;
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e) {
		Node src = e.getSrc();
		Node dst = e.getDst();

		if (richClub.contains(src)) {
			if (src.getOut().size() < k) {
				richClub.remove(src);
				for (Node n : src.getOut()) {
					if (richClub.contains(n)) {
						this.richClubEdges--;
					}
				}
				for (Node n : src.getIn()) {
					if (richClub.contains(n)) {
						this.richClubEdges--;
					}
				}
			} else if (richClub.contains(dst)) {
				this.richClubEdges--;
			}
		}
		return true;
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) {

		int richClubMembers = this.richClub.size();
		this.richClubCoeffizient = (double) this.richClubEdges
				/ (double) (richClubMembers * (richClubMembers - 1));

		return true;
	}
}
