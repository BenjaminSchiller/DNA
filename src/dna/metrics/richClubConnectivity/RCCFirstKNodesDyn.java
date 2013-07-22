package dna.metrics.richClubConnectivity;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;

public class RCCFirstKNodesDyn extends RCCFirstKNodes {

	public RCCFirstKNodesDyn() {
		super("RCCFirstKNodesDyn", false, true, true);
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

		if (this.richClub.contains(src)) {
			int i = this.richClub.indexOf(src);
			while (this.richClub.get(i).getOut().size() > src.getOut().size()
					&& i >= 0) {
				i--;
			}
			this.richClub.remove(src);
			this.richClub.add(i, src);

			if (this.richClub.contains(dst)) {
				this.edgesBetweenRichClub++;
			}
		} else if (src.getOut().size() > this.richClub.getLast().getOut()
				.size()) {

			this.richClub.removeLast();

			for (Node n : src.getOut()) {
				if (this.richClub.contains(n)) {
					this.edgesBetweenRichClub++;
				}
			}
			for (Node n : src.getIn()) {
				if (this.richClub.contains(n)) {
					this.edgesBetweenRichClub++;
				}
			}
			for (Node n : this.richClub.getLast().getIn()) {
				if (this.richClub.contains(n)) {
					this.edgesBetweenRichClub--;
				}
			}
			for (Node n : this.richClub.getLast().getOut()) {
				if (this.richClub.contains(n)) {
					this.edgesBetweenRichClub--;
				}
			}
			this.richClub.addLast(src);
		} else if (src.getOut().size() > rest.getFirst().getOut().size()) {
			rest.remove(src);
			rest.addFirst(src);

		}
		return true;

	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e) {
		Node dst = e.getDst();
		Node src = e.getSrc();

		if (this.richClub.contains(src)) {
			if (src != this.richClub.getLast()) {
				if (src.getOut().size() < this.richClub.getLast().getOut()
						.size()) {
					richClub.remove(src);
					richClub.addLast(src);
				}
				if (this.richClub.contains(dst)) {
					this.edgesBetweenRichClub--;
				}
			} else if (src.getOut().size() >= this.rest.getFirst().getOut()
					.size()
					&& this.richClub.contains(dst)) {
				this.edgesBetweenRichClub--;
			} else {

				this.richClub.remove(src);

				for (Node n : src.getNeighbors()) {
					if (this.richClub.contains(n)) {
						this.edgesBetweenRichClub--;
					}
				}
				for (Node n : this.rest.getFirst().getNeighbors()) {
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
	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
		caculateRCC();
		return true;
	}

}
