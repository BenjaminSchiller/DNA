package dna.metrics.richClubConnectivity.directedRichClubConnectivityInterval;

import java.util.HashMap;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedRichClubConnectivityIntervalU extends
		DirectedRichClubConnectivityInterval {

	public DirectedRichClubConnectivityIntervalU(int interval) {
		super("RCCKNodeIntervalDyn", ApplicationType.AfterUpdate, interval);
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

	/**
	 * Check whether the Edge Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterEdgeRemovalDirected(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcRCNumber = this.nodesRichClub.get(src);
		int dstRCNumber = this.nodesRichClub.get(dst);
		HashMap<Integer, LinkedList<DirectedNode>> srcRC = this.richClubs
				.get(srcRCNumber);

		if (srcRC.containsKey(src.getOutDegree())
				|| srcRCNumber == this.richClubs.size() - 1) {

			removeFromRichClub(srcRC, src, src.getOutDegree() + 1);
			addToRichClub(srcRC, src);
			if (srcRCNumber >= dstRCNumber) {
				this.richClubEdges.put(srcRCNumber,
						this.richClubEdges.get(srcRCNumber) - 1);
			} else {
				this.richClubEdges.put(srcRCNumber,
						this.richClubEdges.get(srcRCNumber) - 1);
			}

		} else {
			HashMap<Integer, LinkedList<DirectedNode>> newRC = this.richClubs
					.get(srcRCNumber + 1);
			int high = getHighestDegree(newRC);
			if (high <= src.getOutDegree()) {
				removeFromRichClub(srcRC, src, src.getOutDegree() + 1);
				addToRichClub(srcRC, src);
				if (srcRCNumber >= dstRCNumber) {
					this.richClubEdges.put(srcRCNumber,
							this.richClubEdges.get(srcRCNumber) - 1);
				} else {
					this.richClubEdges.put(srcRCNumber,
							this.richClubEdges.get(srcRCNumber) - 1);
				}

			} else {

				int newRCNum = srcRCNumber + 1;
				while (high > src.getOutDegree()
						&& newRCNum <= this.richClubs.size() - 1) {
					newRC = this.richClubs.get(newRCNum);
					DirectedNode changedNode = newRC.get(high).getFirst();
					removeFromRichClub(newRC, changedNode,
							changedNode.getOutDegree());
					addToRichClub(this.richClubs.get(newRCNum - 1), changedNode);
					this.nodesRichClub.put(changedNode, newRCNum - 1);
					int changedNodeEdges = 0;
					for (IElement ie : changedNode.getEdges()) {
						DirectedEdge edge = (DirectedEdge) ie;
						DirectedNode node = edge.getDifferingNode(changedNode);
						if (this.nodesRichClub.get(node) < this.nodesRichClub
								.get(changedNode)) {
							changedNodeEdges++;
						}
					}
					this.richClubEdges.put(newRCNum - 1,
							this.richClubEdges.get(newRCNum - 1)
									+ changedNodeEdges);
					this.richClubEdges
							.put(newRCNum, this.richClubEdges.get(newRCNum)
									- changedNodeEdges);
					newRCNum++;
					if (newRCNum <= this.richClubs.size() - 1) {
						high = getHighestDegree(this.richClubs.get(newRCNum));
					}
				}

				removeFromRichClub(srcRC, src, src.getOutDegree() + 1);
				addToRichClub(newRC, src);
				int srcEdges = 0;

				// calculate changes for richclub connectivity
				for (IElement ie : src.getEdges()) {
					DirectedEdge edge = (DirectedEdge) ie;
					DirectedNode node = edge.getDifferingNode(src);
					if (this.nodesRichClub.get(node) <= this.nodesRichClub
							.get(src)) {
						srcEdges++;
					}
				}
				this.richClubEdges.put(srcRCNumber,
						this.richClubEdges.get(srcRCNumber) - srcEdges);
				this.richClubEdges.put(newRCNum - 1,
						this.richClubEdges.get(newRCNum - 1) + srcEdges);
				this.nodesRichClub.put(src, newRCNum - 1);
				HashMap<Integer, LinkedList<DirectedNode>> hashMap = this.richClubs
						.get(this.nodesRichClub.get(src));

			}

		}

		return true;
	}

	private int getHighestDegree(
			HashMap<Integer, LinkedList<DirectedNode>> newRC) {
		int max = 0;
		for (int i : newRC.keySet())
			max = Math.max(max, i);
		return max;
	}

	/**
	 * Check whether the edge addition has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterEdgeAdditionDirected(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();
		int srcRCNumber = this.nodesRichClub.get(src);
		int dstRCNumber = this.nodesRichClub.get(dst);
		HashMap<Integer, LinkedList<DirectedNode>> srcRC = this.richClubs
				.get(srcRCNumber);

		if (srcRC.containsKey(src.getOutDegree()) || srcRCNumber == 0) {

			removeFromRichClub(srcRC, src, src.getOutDegree() - 1);
			addToRichClub(srcRC, src);
			if (srcRCNumber >= dstRCNumber) {
				this.richClubEdges.put(srcRCNumber,
						this.richClubEdges.get(srcRCNumber) + 1);
			} else {
				this.richClubEdges.put(srcRCNumber,
						this.richClubEdges.get(srcRCNumber) + 1);
			}

		} else {
			HashMap<Integer, LinkedList<DirectedNode>> newRC = this.richClubs
					.get(srcRCNumber - 1);
			int low = getlowestDegree(newRC);
			if (low >= src.getOutDegree()) {
				removeFromRichClub(srcRC, src, src.getOutDegree() - 1);
				addToRichClub(srcRC, src);
				if (srcRCNumber >= dstRCNumber) {
					this.richClubEdges.put(srcRCNumber,
							this.richClubEdges.get(srcRCNumber) + 1);
				} else {
					this.richClubEdges.put(srcRCNumber,
							this.richClubEdges.get(srcRCNumber) + 1);
				}

			} else {
				int newRCNum = srcRCNumber - 1;
				while (low < src.getOutDegree() && newRCNum >= 0) {
					newRC = this.richClubs.get(newRCNum);
					DirectedNode changedNode = newRC.get(low).getLast();
					removeFromRichClub(newRC, changedNode,
							changedNode.getOutDegree());
					addToRichClub(this.richClubs.get(newRCNum + 1), changedNode);
					this.nodesRichClub.put(changedNode, newRCNum + 1);
					int changedNodeEdges = 0;
					for (IElement ie : changedNode.getEdges()) {
						DirectedEdge edge = (DirectedEdge) ie;
						DirectedNode node = edge.getDifferingNode(changedNode);
						if (this.nodesRichClub.get(node) < this.nodesRichClub
								.get(changedNode)) {
							changedNodeEdges++;
						}
					}
					this.richClubEdges.put(newRCNum + 1,
							this.richClubEdges.get(newRCNum + 1)
									+ changedNodeEdges);
					this.richClubEdges
							.put(newRCNum, this.richClubEdges.get(newRCNum)
									- changedNodeEdges);
					newRCNum--;
					if (newRCNum >= 0)
						low = getlowestDegree(this.richClubs.get(newRCNum));
				}

				removeFromRichClub(srcRC, src, src.getOutDegree() - 1);
				addToRichClub(newRC, src);
				int srcEdges = 0;

				// calculate changes for richclub connectivity
				for (IElement ie : src.getEdges()) {
					DirectedEdge edge = (DirectedEdge) ie;
					DirectedNode node = edge.getDifferingNode(src);
					if (this.nodesRichClub.get(node) <= this.nodesRichClub
							.get(src)) {
						srcEdges++;
					}
				}
				this.richClubEdges.put(srcRCNumber,
						this.richClubEdges.get(srcRCNumber) - srcEdges);
				this.richClubEdges.put(newRCNum + 1,
						this.richClubEdges.get(newRCNum + 1) + srcEdges);
				this.nodesRichClub.put(src, newRCNum + 1);
				HashMap<Integer, LinkedList<DirectedNode>> hashMap = this.richClubs
						.get(this.nodesRichClub.get(src));

			}
		}
		return true;
	}

	private int getlowestDegree(HashMap<Integer, LinkedList<DirectedNode>> newRC) {
		int min = Integer.MAX_VALUE;
		for (int i : newRC.keySet())
			min = Math.min(min, i);
		return min;
	}

	private void addToRichClub(
			HashMap<Integer, LinkedList<DirectedNode>> newRC, DirectedNode node) {
		int degree = node.getOutDegree();
		if (newRC.containsKey(degree)) {
			newRC.get(degree).add(node);
		} else {
			LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
			temp.add(node);
			newRC.put(degree, temp);
		}
	}

	private void addToRichClub(
			HashMap<Integer, LinkedList<DirectedNode>> newRC,
			DirectedNode node, int degree) {
		if (newRC.containsKey(degree)) {
			newRC.get(degree).add(node);
		} else {
			LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
			temp.add(node);
			newRC.put(degree, temp);
		}
	}

	private boolean removeFromRichClub(
			HashMap<Integer, LinkedList<DirectedNode>> newRC,
			DirectedNode node, int degree) {
		boolean b = newRC.get(degree).remove(node);
		if (newRC.get(degree).isEmpty()) {
			newRC.remove(degree);
		}
		return b;
	}

	/**
	 * Check whether the Node Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterNodeRemovalDirected(Update u) {

		DirectedNode n = (DirectedNode) ((NodeRemoval) u).getNode();

		int max = 0;
		HashMap<Integer, LinkedList<DirectedEdge>> order = new HashMap<>();
		for (IElement ie : n.getIncomingEdges()) {
			DirectedEdge edge = (DirectedEdge) ie;
			DirectedNode n1 = (DirectedNode) edge.getSrc();
			max = Math.max(max, this.nodesRichClub.get(n1));
			if (!order.containsKey(this.nodesRichClub.get(n1))) {
				LinkedList<DirectedEdge> l = new LinkedList<>();
				l.add(edge);
				order.put(this.nodesRichClub.get(n1), l);
			} else {
				order.get(this.nodesRichClub.get(n1)).add(edge);
			}
		}

		for (int i = max; i >= 0; i--) {
			if (order.containsKey(i)) {
				for (DirectedEdge e : order.get(i)) {
					applyAfterEdgeRemovalDirected(new EdgeRemoval(e));
				}
			}
		}

		int nRCNum = this.nodesRichClub.get(n);
		for (int i = nRCNum; i < this.richClubs.size() - 1; i++) {

			HashMap<Integer, LinkedList<DirectedNode>> newRC = this.richClubs
					.get(i + 1);
			int high = getHighestDegree(newRC);
			DirectedNode changedNode = newRC.get(high).getFirst();

			removeFromRichClub(newRC, changedNode, changedNode.getOutDegree());
			addToRichClub(this.richClubs.get(i), changedNode);
			this.nodesRichClub.put(changedNode, i);
			int changedNodeEdges = 0;
			for (IElement ie : changedNode.getEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
				DirectedNode node = edge.getDifferingNode(changedNode);
				if (this.nodesRichClub.get(node) < this.nodesRichClub
						.get(changedNode)) {
					changedNodeEdges++;
				}
			}
			this.richClubEdges.put(i, this.richClubEdges.get(i)
					+ changedNodeEdges);
			this.richClubEdges.put(i + 1, this.richClubEdges.get(i + 1)
					- changedNodeEdges);

		}

		removeFromRichClub(this.richClubs.get(nRCNum), n, n.getOutDegree());
		this.nodesRichClub.remove(n);

		if (this.richClubs.get(this.richClubs.size() - 1).isEmpty()) {
			this.richClubs.remove(this.richClubs.size() - 1);
		}
		return true;
	}

	/**
	 * Check whether the Node Addition has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterNodeAdditionDirected(Update u) {
		DirectedNode n = (DirectedNode) ((NodeAddition) u).getNode();
		int size = 0;
		for (int i : this.richClubs.get(this.richClubs.size() - 1).keySet()) {
			size += this.richClubs.get(this.richClubs.size() - 1).get(i).size();
		}
		if (size == this.richClubIntervall) {
			this.richClubs.put(this.richClubs.size(),
					new HashMap<Integer, LinkedList<DirectedNode>>());
			this.richClubEdges.put(this.richClubs.size() - 1, 0);
			this.nodesRichClub.put(n, this.richClubs.size() - 1);
			addToRichClub(this.richClubs.get(this.richClubs.size() - 1), n);
		} else {
			this.nodesRichClub.put(n, this.richClubs.size() - 1);
			addToRichClub(this.richClubs.get(this.richClubs.size() - 1), n);
		}

		return true;
	}

}
