package dna.metrics.richClubConnectivity;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;
import dna.util.ArrayUtils;

public class RCCKNodeIntervalDyn extends RCCKNodeInterval {

	public RCCKNodeIntervalDyn() {
		super("RCCKNodeIntervalComp", false, true, true);
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		for (int j = 0; j < richClubs.size(); j++) {
			double divisor = (j + 1) * richClubIntervall
					* ((j + 1) * richClubIntervall - 1);
			richClubCoefficienten[j] = (double) (richClubEdges[j]) / divisor;
		}

		return true;
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e) {
		Node src = e.getSrc();
		Node dst = e.getDst();
		int degree = src.getOut().size();

		boolean noRichClubChange = degree <= richClubs
				.get(this.nodesRichClub[src.getIndex()]).getFirst().getOut()
				.size()
				|| degree <= richClubs
						.get(this.nodesRichClub[src.getIndex()] - 1).getLast()
						.getOut().size();

		if (noRichClubChange) {
			if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
					.getIndex()]) {

				this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
						this.nodesRichClub[src.getIndex()]);

			}
		} else {
			for (Node n : src.getIn()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] - 1) {
					this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
				}
			}
			for (Node n : src.getOut()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] - 1) {
					this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
				}
			}
			for (Node n : this.richClubs
					.get(this.nodesRichClub[src.getIndex()] - 1).getLast()
					.getOut()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] - 1) {
					this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
					;
				}
			}
			for (Node n : this.richClubs
					.get(this.nodesRichClub[src.getIndex()] - 1).getLast()
					.getIn()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] - 1) {
					this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
					;
				}
			}
			if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
					.getIndex()]) {

				this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
						this.nodesRichClub[src.getIndex()]);

			}

			// TODO:RichClubCahnge
		}

		return true;
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e) {
		Node src = e.getSrc();
		Node dst = e.getDst();
		int degree = src.getOut().size();

		boolean noRichClubChange = degree >= richClubs
				.get(this.nodesRichClub[src.getIndex()]).getLast().getOut()
				.size()
				|| degree <= richClubs
						.get(this.nodesRichClub[src.getIndex()] + 1).getFirst()
						.getOut().size();

		if (noRichClubChange) {
			if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
					.getIndex()]) {

				this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
						this.nodesRichClub[src.getIndex()]);

			}
		} else {

			for (Node n : src.getIn()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] + 1) {
					this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
				}
			}
			for (Node n : src.getOut()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] + 1) {
					this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
				}
			}
			for (Node n : this.richClubs
					.get(this.nodesRichClub[src.getIndex()] + 1).getFirst()
					.getOut()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] + 1) {
					this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
					;
				}
			}
			for (Node n : this.richClubs
					.get(this.nodesRichClub[src.getIndex()] + 1).getFirst()
					.getIn()) {
				if (this.nodesRichClub[n.getIndex()] <= this.nodesRichClub[src
						.getIndex()] + 1) {
					this.richClubEdges = ArrayUtils.decr(this.richClubEdges,
							this.nodesRichClub[src.getIndex()] - 1);
					;
				}
			}
			if (this.nodesRichClub[src.getIndex()] >= this.nodesRichClub[dst
					.getIndex()]) {

				this.richClubEdges = ArrayUtils.incr(this.richClubEdges,
						this.nodesRichClub[src.getIndex()]);

			}

			// TODO:RichClubCahnge

		}

		return true;
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("before diff");
	}

}
