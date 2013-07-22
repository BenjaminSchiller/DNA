package dna.metrics.richClubConnectivity;

import java.util.HashSet;
import java.util.Set;

import dna.diff.Diff;
import dna.diff.DiffNotApplicableException;
import dna.graph.Edge;
import dna.graph.Node;

public class RCCPerDegreeDyn extends RCCPerDegree {

	public RCCPerDegreeDyn() {
		super("RCCPerDegreeDyn", false, true, true);
	}

	@Override
	protected boolean applyBeforeDiff_(Diff d)
			throws DiffNotApplicableException {
		throw new DiffNotApplicableException("BEFORE DIFF");
	}

	@Override
	protected boolean applyAfterEdgeAddition_(Diff d, Edge e) {

		Node src = e.getSrc();
		Node dst = e.getDst();
		int srcDegree = src.getOut().size();
		int dstDegree = dst.getOut().size();

		if (this.richClubs.containsKey(srcDegree)) {
			int updateEdgesNew = 0;
			for (Node n : src.getIn()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (Node n : src.getOut()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}

			int temp = this.richClubEdges.get(srcDegree) + updateEdgesNew;
			this.richClubEdges.put(srcDegree, temp);
			temp = this.richClubEdges.get(srcDegree - 1) - updateEdgesNew;
			this.richClubEdges.put(srcDegree - 1, temp);
		} else {

			int updateEdgesNew = 0;
			for (Node n : src.getIn()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (Node n : src.getOut()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			Set<Node> temp = new HashSet<Node>();
			temp.add(src);
			this.richClubs.put(srcDegree, temp);
			this.richClubEdges.put(srcDegree, updateEdgesNew);
			this.highestDegree = Math.max(highestDegree, srcDegree);
		}

		if (srcDegree >= dstDegree) {
			int temp = this.richClubEdges.get(srcDegree) + 1;
			this.richClubEdges.put(srcDegree, temp);
		} else {
			int temp = this.richClubEdges.get(dstDegree) + 1;
			this.richClubEdges.put(dstDegree, temp);
		}
		this.richClubs.get(srcDegree).add(src);
		this.richClubs.get(srcDegree - 1).remove(src);
		// // Change EdgeCout for richClubChange
		// if (srcDegree > this.highestDegree) {
		//
		// Set<Node> temp = new HashSet<Node>();
		// temp.add(src);
		// this.richClubs.put(srcDegree, temp);
		// this.richClubEdges.put(srcDegree, 0);
		// this.highestDegree = Math.max(highestDegree, srcDegree);
		//
		// } else {
		//
		// this.richClubs.get(srcDegree).add(src);
		//
		// Set<Node> newRichClub = this.richClubs.get(srcDegree);
		// Set<Node> oldRichClub = this.richClubs.get(srcDegree - 1);
		//
		// int updateEdgesNew = 0;
		// int updateEdgesOld = 0;
		//
		// for (Node n : src.getIn()) {
		// if (n != dst && newRichClub.contains(n)) {
		// updateEdgesNew++;
		// if (oldRichClub.contains(n)) {
		// updateEdgesOld--;
		// }
		// }
		// }
		// for (Node n : src.getOut()) {
		// if (newRichClub.contains(n) && n != dst) {
		// updateEdgesNew++;
		// if (oldRichClub.contains(n)) {
		// updateEdgesOld--;
		// }
		// }
		// }
		//
		// this.richClubEdges.put(srcDegree, this.richClubEdges.get(srcDegree)
		// + updateEdgesNew);
		// this.richClubEdges.put(srcDegree - 1,
		// this.richClubEdges.get(srcDegree - 1) + updateEdgesOld);
		// }
		//
		// if (srcDegree > dstDegree) {
		// this.richClubEdges.put(dstDegree,
		// this.richClubEdges.get(dstDegree) + 1);
		// } else {
		// this.richClubEdges.put(srcDegree,
		// this.richClubEdges.get(srcDegree) + 1);
		// }

		return true;
	}

	@Override
	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e) {

		Node src = e.getSrc();
		Node dst = e.getDst();
		int srcDegree = src.getOut().size();
		int dstDegree = dst.getOut().size();

		this.richClubs.get(srcDegree + 1).remove(src);
		// Set<Node> oldRichClub = this.richClubs.get(srcDegree + 1);
		// int updateEdgesOld = 0;

		if (this.richClubs.containsKey(srcDegree)) {
			int updateEdgesNew = 0;
			for (Node n : src.getIn()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (Node n : src.getOut()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}

			int temp = this.richClubEdges.get(srcDegree) + updateEdgesNew;
			this.richClubEdges.put(srcDegree, temp);
			temp = this.richClubEdges.get(srcDegree + 1) - updateEdgesNew;
			this.richClubEdges.put(srcDegree - 1, temp);
		} else {

			int updateEdgesNew = 0;
			for (Node n : src.getIn()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			for (Node n : src.getOut()) {
				if (n != dst && n.getOut().size() >= srcDegree) {
					updateEdgesNew++;
				}
			}
			Set<Node> temp = new HashSet<Node>();
			temp.add(src);
			this.richClubs.put(srcDegree, temp);
			this.richClubEdges.put(srcDegree, updateEdgesNew);
			this.highestDegree = Math.max(highestDegree, srcDegree);
		}

		// // update EdgeCount for richclub change
		// for (Node n : src.getIn()) {
		// if (oldRichClub.contains(n)) {
		// updateEdgesOld--;
		// }
		// }
		//
		// for (Node n : src.getOut()) {
		// if (oldRichClub.contains(n)) {
		// updateEdgesOld--;
		// }
		// }
		//
		// this.richClubEdges.put(srcDegree + 1,
		// this.richClubEdges.get(srcDegree + 1) + updateEdgesOld);

		if (srcDegree > dstDegree) {
			this.richClubEdges.put(dstDegree,
					this.richClubEdges.get(dstDegree) - 1);
		} else {
			this.richClubEdges.put(srcDegree,
					this.richClubEdges.get(srcDegree) - 1);
		}

		return true;
	}

	@Override
	protected boolean applyAfterDiff_(Diff d) {
		calculateRCC();
		return true;
	}
}
