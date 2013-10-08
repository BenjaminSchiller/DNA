package dna.metrics.richClubConnectivity.undirectedRichClubConnectivityInterval;

import java.util.HashMap;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedRichClubConnectivityIntervalU extends
		UndirectedRichClubConnectivityInterval {

	public UndirectedRichClubConnectivityIntervalU(int interval) {
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

	/**
	 * Check whether the Edge Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		checkRemovalForNodeN(n1, n2);
		checkRemovalForNodeN(n2, n1);

		return true;
	}

	/**
	 * Check whether the Edge Removal for n1
	 * 
	 * @param n1
	 *            , other
	 * 
	 * @return void
	 */
	private void checkRemovalForNodeN(UndirectedNode n1, UndirectedNode other) {
		int n1RCNum = this.nodesRichClub.get(n1);
		HashMap<Integer, LinkedList<UndirectedNode>> n1RC = this.richClubs
				.get(n1RCNum);

		if (n1RC.containsKey(n1.getDegree())
				|| n1RCNum == this.richClubs.size() - 1) {

			removeFromRichClub(n1RC, n1, n1.getDegree() + 1);
			addToRichClub(n1RC, n1);

		} else {
			HashMap<Integer, LinkedList<UndirectedNode>> newRC = this.richClubs
					.get(n1RCNum + 1);
			int high = getHighestDegree(newRC);
			if (high <= n1.getDegree()) {
				removeFromRichClub(n1RC, n1, n1.getDegree() + 1);
				addToRichClub(n1RC, n1);

			} else {

				int newRCNum = n1RCNum + 1;
				while (high > n1.getDegree()
						&& newRCNum <= this.richClubs.size() - 1) {
					newRC = this.richClubs.get(newRCNum);
					UndirectedNode changedNode = newRC.get(high).getFirst();
					if (changedNode == other) {
						changedNode = newRC.get(high).getLast();
					}
					removeFromRichClub(newRC, changedNode,
							changedNode.getDegree());
					addToRichClub(this.richClubs.get(newRCNum - 1), changedNode);
					this.nodesRichClub.put(changedNode, newRCNum - 1);
					int changedNodeEdges = 0;
					for (IElement ie : changedNode.getEdges()) {
						UndirectedEdge edge = (UndirectedEdge) ie;
						UndirectedNode node = edge
								.getDifferingNode(changedNode);
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

				removeFromRichClub(n1RC, n1, n1.getDegree() + 1);
				addToRichClub(newRC, n1);
				int n1Edges = 0;

				// calculate changes for richclub connectivity
				for (IElement ie : n1.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) ie;
					UndirectedNode node = edge.getDifferingNode(n1);
					if (this.nodesRichClub.get(node) <= this.nodesRichClub
							.get(n1)) {
						n1Edges++;
					}
				}
				this.richClubEdges.put(n1RCNum, this.richClubEdges.get(n1RCNum)
						- n1Edges);
				this.richClubEdges.put(newRCNum - 1,
						this.richClubEdges.get(newRCNum - 1) + n1Edges);
				this.nodesRichClub.put(n1, newRCNum - 1);

			}

		}

	}

	/**
	 * calculate lowest Degree in richClub
	 * 
	 * @param richClub
	 * 
	 * @return int
	 */
	private int getlowestDegree(
			HashMap<Integer, LinkedList<UndirectedNode>> newRC) {
		int min = Integer.MAX_VALUE;
		for (int i : newRC.keySet())
			min = Math.min(min, i);
		return min;
	}

	/**
	 * adds node to richClub
	 * 
	 * @param richclub
	 *            , node
	 * 
	 * @return boolean
	 */
	private void addToRichClub(
			HashMap<Integer, LinkedList<UndirectedNode>> newRC,
			UndirectedNode node) {
		int degree = node.getDegree();
		if (newRC.containsKey(degree)) {
			newRC.get(degree).add(node);
		} else {
			LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
			temp.add(node);
			newRC.put(degree, temp);
		}
	}

	/**
	 * Removes node from richclub
	 * 
	 */
	private void removeFromRichClub(
			HashMap<Integer, LinkedList<UndirectedNode>> newRC,
			UndirectedNode node, int degree) {
		newRC.get(degree).remove(node);
		if (newRC.get(degree).isEmpty()) {
			newRC.remove(degree);
		}
	}

	/**
	 * calculat highestDegree of RichClub
	 * 
	 */
	private int getHighestDegree(
			HashMap<Integer, LinkedList<UndirectedNode>> newRC) {
		int max = 0;
		for (int i : newRC.keySet())
			max = Math.max(max, i);
		return max;
	}

	/**
	 * Check whether the Edge Addition has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeUpdate) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		checkEdgeAddForNodeN(n1, n2);
		checkEdgeAddForNodeN(n2, n1);

		return true;
	}

	/**
	 * Check Additon for single node
	 * 
	 * @param n
	 *            , other
	 */
	private void checkEdgeAddForNodeN(UndirectedNode n, UndirectedNode other) {
		int nRCNum = this.nodesRichClub.get(n);
		HashMap<Integer, LinkedList<UndirectedNode>> nRC = this.richClubs
				.get(nRCNum);

		if (nRC.containsKey(n.getDegree()) || nRCNum == 0) {

			removeFromRichClub(nRC, n, n.getDegree() - 1);
			addToRichClub(nRC, n);

		} else {
			HashMap<Integer, LinkedList<UndirectedNode>> newRC = this.richClubs
					.get(nRCNum - 1);
			int low = getlowestDegree(newRC);
			if (low >= n.getDegree()) {
				removeFromRichClub(nRC, n, n.getDegree() - 1);
				addToRichClub(nRC, n);
			} else {
				int newRCNum = nRCNum - 1;
				while (low < n.getDegree() && newRCNum >= 0) {
					newRC = this.richClubs.get(newRCNum);
					UndirectedNode changedNode = newRC.get(low).getLast();

					if (changedNode == other) {
						changedNode = newRC.get(low).getFirst();
					}
					removeFromRichClub(newRC, changedNode,
							changedNode.getDegree());
					addToRichClub(this.richClubs.get(newRCNum + 1), changedNode);
					this.nodesRichClub.put(changedNode, newRCNum + 1);
					int changedNodeEdges = 0;
					for (IElement ie : changedNode.getEdges()) {
						UndirectedEdge edge = (UndirectedEdge) ie;
						UndirectedNode node = edge
								.getDifferingNode(changedNode);
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

				removeFromRichClub(nRC, n, n.getDegree() - 1);
				addToRichClub(newRC, n);
				int nEdges = 0;

				// calculate changes for richclub connectivity
				for (IElement ie : n.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) ie;
					UndirectedNode node = edge.getDifferingNode(n);
					if (this.nodesRichClub.get(node) <= this.nodesRichClub
							.get(n)) {
						nEdges++;
					}
				}
				this.richClubEdges.put(nRCNum, this.richClubEdges.get(nRCNum)
						- nEdges);
				this.richClubEdges.put(newRCNum + 1,
						this.richClubEdges.get(newRCNum + 1) + nEdges);
				this.nodesRichClub.put(n, newRCNum + 1);
			}
		}
	}

	/**
	 * Check whether the Node Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeRemoval) u).getNode();

		int max = 0;
		HashMap<Integer, LinkedList<UndirectedNode>> order = new HashMap<>();
		for (IElement ie : n.getEdges()) {
			UndirectedEdge edge = (UndirectedEdge) ie;
			UndirectedNode n1 = (UndirectedNode) edge.getDifferingNode(n);
			max = Math.max(max, this.nodesRichClub.get(n1));
			if (!order.containsKey(this.nodesRichClub.get(n1))) {
				LinkedList<UndirectedNode> l = new LinkedList<>();
				l.add(n1);
				order.put(this.nodesRichClub.get(n1), l);
			} else {
				order.get(this.nodesRichClub.get(n1)).add(n1);
			}
		}

		for (int i = max; i >= 0; i--) {
			if (order.containsKey(i)) {
				for (UndirectedNode n1 : order.get(i)) {
					checkRemovalForNodeN(n1, n);
				}
			}
		}

		int nRCNum = this.nodesRichClub.get(n);
		for (int i = nRCNum; i < this.richClubs.size() - 1; i++) {

			HashMap<Integer, LinkedList<UndirectedNode>> newRC = this.richClubs
					.get(i + 1);
			int high = getHighestDegree(newRC);
			UndirectedNode changedNode = newRC.get(high).getFirst();

			removeFromRichClub(newRC, changedNode, changedNode.getDegree());
			addToRichClub(this.richClubs.get(i), changedNode);
			this.nodesRichClub.put(changedNode, i);
			int changedNodeEdges = 0;
			for (IElement ie : changedNode.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) ie;
				UndirectedNode node = edge.getDifferingNode(changedNode);
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

		removeFromRichClub(this.richClubs.get(nRCNum), n, n.getDegree());
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
	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode n = (UndirectedNode) ((NodeAddition) u).getNode();
		int size = 0;
		for (int i : this.richClubs.get(this.richClubs.size() - 1).keySet()) {
			size += this.richClubs.get(this.richClubs.size() - 1).get(i).size();
		}
		if (size == this.richClubIntervall) {
			this.richClubs.put(this.richClubs.size(),
					new HashMap<Integer, LinkedList<UndirectedNode>>());
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
