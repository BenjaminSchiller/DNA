package dna.metrics.richClubConnectivity;

import java.util.LinkedList;

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
public class RCCKNodeIntervalDirectedDyn extends RCCKNodeIntervalDirected {

	public RCCKNodeIntervalDirectedDyn() {
		super("RCCKNodeIntervalDyn", ApplicationType.AfterUpdate);
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
		LinkedList<DirectedNode> srcRC = this.richClubs.get(this.nodesRichClub
				.get(src.getIndex()));
		if (srcRC.getLast().getOutDegree() >= src.getOutDegree()
				|| this.nodesRichClub.get(src.getIndex()) == this.richClubs
						.size() - 1) {
			int i = srcRC.indexOf(src);
			srcRC.remove(src);

			while (i < srcRC.size() - 1
					&& srcRC.get(i + 1).getOutDegree() < src.getOutDegree()) {
				i++;
			}
			srcRC.add(i, src);

			if (srcRC.contains(dst)) {
				this.richClubEdges.put(src.getOutDegree(),
						this.richClubEdges.get(src.getOutDegree()) - 1);
			}
		} else {

			// int i = this.nodesRichClub.get(src.getIndex()) - 1;
			// while (i > 0
			// && richClubs.get(i).getFirst().getOutDegree() <= src
			// .getOutDegree()) {
			// i--;
			// }

			LinkedList<DirectedNode> newRC = this.richClubs
					.get(this.nodesRichClub.get(src.getIndex()) + 1);

			DirectedNode firstNode = newRC.removeFirst();
			srcRC.remove(src);
			srcRC.addLast(firstNode);

			int srcEdges = 0;
			int lastNodeEdges = 0;
			for (DirectedEdge edge : src.getOutgoingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) >= this.nodesRichClub
						.get(src.getIndex())) {
					srcEdges++;
				}
			}
			for (DirectedEdge edge : src.getIncomingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) >= this.nodesRichClub
						.get(src.getIndex())) {
					srcEdges++;
				}
			}
			for (DirectedEdge edge : firstNode.getIncomingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) > this.nodesRichClub
						.get(firstNode.getIndex())) {
					lastNodeEdges++;
				}
			}
			for (DirectedEdge edge : firstNode.getOutgoingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) > this.nodesRichClub
						.get(firstNode.getIndex())) {
					lastNodeEdges--;
				}
			}

			int j = newRC.size() - 1;

			while (j < newRC.size() - 1
					&& newRC.get(j + 1).getOutDegree() > src.getOutDegree()) {
				j++;
			}

			this.richClubEdges.put(
					this.nodesRichClub.get(src.getIndex()),
					this.richClubEdges.get(this.nodesRichClub.get(src
							.getIndex())) - srcEdges + lastNodeEdges);
			this.richClubEdges.put(
					this.nodesRichClub.get(src.getIndex() + 1),
					this.richClubEdges.get(this.nodesRichClub.get(src
							.getIndex()) + 1) + srcEdges - lastNodeEdges);

			newRC.add(j, src);
			newRC.remove(firstNode);

			this.nodesRichClub.put(src.getIndex(),
					this.nodesRichClub.get(src.getIndex()) + 1);
			this.nodesRichClub.put(firstNode.getIndex(),
					this.nodesRichClub.get(firstNode.getIndex()) - 1);

		}

		int edgesbetweenRCC = 0;
		for (int i = 0; i < richClubs.size(); i++) {
			edgesbetweenRCC += this.richClubEdges.get(i);
			richClubCoefficienten.put(i, (double) edgesbetweenRCC
					/ (double) ((i + 1) * this.richClubIntervall * ((i + 1)
							* this.richClubIntervall - 1)));
		}
		return true;
	}

	private boolean applyAfterEdgeAdditionDirected(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		LinkedList<DirectedNode> srcRC = this.richClubs.get(this.nodesRichClub
				.get(src.getIndex()));

		if (srcRC.getFirst().getOutDegree() >= src.getOutDegree()
				|| this.nodesRichClub.get(src.getIndex()) == 0) {
			int i = srcRC.indexOf(src);
			srcRC.remove(src);

			while (i > 0
					&& srcRC.get(i - 1).getOutDegree() < src.getOutDegree()) {
				i--;
			}
			srcRC.add(i, src);

			if (srcRC.contains(dst)) {
				this.richClubEdges.get(src.getOutDegree());
			}
		} else {

			// int i = this.nodesRichClub.get(src.getIndex()) - 1;
			// while (i > 0
			// && richClubs.get(i).getFirst().getOutDegree() <= src
			// .getOutDegree()) {
			// i--;
			// }

			LinkedList<DirectedNode> newRC = this.richClubs
					.get(this.nodesRichClub.get(src.getIndex()) - 1);

			DirectedNode lastNode = newRC.removeLast();
			srcRC.remove(src);
			srcRC.addFirst(lastNode);

			int srcEdges = 0;
			int lastNodeEdges = 0;
			for (DirectedEdge edge : src.getOutgoingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) > this.nodesRichClub
						.get(src.getIndex())) {
					srcEdges++;
				}
			}
			for (DirectedEdge edge : src.getIncomingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) > this.nodesRichClub
						.get(src.getIndex())) {
					srcEdges++;
				}
			}
			for (DirectedEdge edge : lastNode.getIncomingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) >= this.nodesRichClub
						.get(lastNode.getIndex())) {
					lastNodeEdges++;
				}
			}
			for (DirectedEdge edge : lastNode.getOutgoingEdges()) {
				if (this.nodesRichClub.get(edge.getDst().getIndex()) >= this.nodesRichClub
						.get(lastNode.getIndex())) {
					lastNodeEdges--;
				}
			}

			int j = newRC.size() - 1;

			newRC.remove(lastNode);
			while (j > 0
					&& newRC.get(j - 1).getOutDegree() < src.getOutDegree()) {
				j--;
			}

			this.richClubEdges.put(
					this.nodesRichClub.get(src.getIndex()),
					this.richClubEdges.get(this.nodesRichClub.get(src
							.getIndex())) - srcEdges + lastNodeEdges);
			this.richClubEdges.put(
					this.nodesRichClub.get(src.getIndex() - 1),
					this.richClubEdges.get(this.nodesRichClub.get(src
							.getIndex()) - 1) + srcEdges - lastNodeEdges);

			newRC.add(j, src);

			this.nodesRichClub.put(src.getIndex(),
					this.nodesRichClub.get(src.getIndex()) - 1);
			this.nodesRichClub.put(lastNode.getIndex(),
					this.nodesRichClub.get(lastNode.getIndex()) + 1);

		}

		int edgesbetweenRCC = 0;
		for (int i = 0; i < richClubs.size(); i++) {
			edgesbetweenRCC += this.richClubEdges.get(i);
			richClubCoefficienten.put(i, (double) edgesbetweenRCC
					/ (double) ((i + 1) * this.richClubIntervall * ((i + 1)
							* this.richClubIntervall - 1)));
		}

		return true;
	}

	private boolean applyAfterNodeRemovalDirected(Update u) {
		return false;
	}

	private boolean applyAfterNodeAdditionDirected(Update u) {
		return false;
	}

}
