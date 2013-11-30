package dna.metrics.richClubConnectivity.richClubConnectivityInterval;

import java.util.HashMap;
import java.util.LinkedList;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

/**
 * 
 * Calculates the rich club connectivity values for all richClubs with size n*
 * interval
 * 
 */
public class RichClubConnectivityIntervalU extends RichClubConnectivityInterval {

	public RichClubConnectivityIntervalU(int interval) {
		super("RichClubConnectivityIntervalU", ApplicationType.AfterUpdate,
				interval);
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterNodeAdditionDirected(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterNodeRemovalDirected(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterEdgeAdditionDirected(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterEdgeRemovalDirected(u);
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			if (u instanceof NodeAddition) {
				return applyAfterNodeAddition(u);
			} else if (u instanceof NodeRemoval) {
				return applyAfterUndirectedNodeRemoval(u);
			} else if (u instanceof EdgeAddition) {
				return applyAfterUndirectedEdgeAddition(u);
			} else if (u instanceof EdgeRemoval) {
				return applyAfterUndirectedEdgeRemoval(u);
			}
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
		HashMap<Integer, LinkedList<Node>> srcRC = this.richClubs
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
			HashMap<Integer, LinkedList<Node>> newRC = this.richClubs
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
					DirectedNode changedNode = (DirectedNode) newRC.get(high)
							.getFirst();
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

			}

		}

		return true;
	}

	/*
	 * Check whether the Node Addition has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterNodeAdditionDirected(Update u) {
		DirectedNode n = (DirectedNode) ((NodeAddition) u).getNode();
		int size = 0;
		if (this.richClubs.isEmpty()) {
			this.richClubs.put(0, new HashMap<Integer, LinkedList<Node>>());
			this.richClubEdges.put(0, 0);
			this.nodesRichClub.put(n, 0);
			addToRichClub(this.richClubs.get(0), n);
			return true;
		}
		for (int i : this.richClubs.get(this.richClubs.size() - 1).keySet()) {
			size += this.richClubs.get(this.richClubs.size() - 1).get(i).size();
		}
		if (size == this.richClubIntervall) {
			this.richClubs.put(this.richClubs.size(),
					new HashMap<Integer, LinkedList<Node>>());
			this.richClubEdges.put(this.richClubs.size() - 1, 0);
			this.nodesRichClub.put(n, this.richClubs.size() - 1);
			addToRichClub(this.richClubs.get(this.richClubs.size() - 1), n);
		} else {
			this.nodesRichClub.put(n, this.richClubs.size() - 1);
			addToRichClub(this.richClubs.get(this.richClubs.size() - 1), n);
		}

		return true;
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

			HashMap<Integer, LinkedList<Node>> newRC = this.richClubs
					.get(i + 1);
			int high = getHighestDegree(newRC);
			DirectedNode changedNode = (DirectedNode) newRC.get(high)
					.getFirst();

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
		HashMap<Integer, LinkedList<Node>> srcRC = this.richClubs
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
			HashMap<Integer, LinkedList<Node>> newRC = this.richClubs
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
					DirectedNode changedNode = (DirectedNode) newRC.get(low)
							.getLast();
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

			}
		}
		return true;
	}

	/**
	 * Check whether the Edge Removal has influence on RichClub
	 * 
	 * @param u
	 * 
	 * @return boolean
	 */
	private boolean applyAfterUndirectedEdgeRemoval(Update u) {
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
		HashMap<Integer, LinkedList<Node>> n1RC = this.richClubs.get(n1RCNum);

		if (n1RC.containsKey(n1.getDegree())
				|| n1RCNum == this.richClubs.size() - 1) {

			removeFromRichClub(n1RC, n1, n1.getDegree() + 1);
			addToRichClub(n1RC, n1);

		} else {
			HashMap<Integer, LinkedList<Node>> newRC = this.richClubs
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
					UndirectedNode changedNode = (UndirectedNode) newRC.get(
							high).getFirst();
					if (changedNode == other) {
						changedNode = (UndirectedNode) newRC.get(high)
								.getLast();
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
	private int getlowestDegree(HashMap<Integer, LinkedList<Node>> newRC) {
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
	private void addToRichClub(HashMap<Integer, LinkedList<Node>> newRC,
			UndirectedNode node) {
		int degree = node.getDegree();
		if (newRC.containsKey(degree)) {
			newRC.get(degree).add(node);
		} else {
			LinkedList<Node> temp = new LinkedList<Node>();
			temp.add(node);
			newRC.put(degree, temp);
		}
	}

	/**
	 * Removes node from richclub
	 * 
	 */
	private void removeFromRichClub(HashMap<Integer, LinkedList<Node>> n1rc,
			Node node, int degree) {
		n1rc.get(degree).remove(node);
		if (n1rc.get(degree).isEmpty()) {
			n1rc.remove(degree);
		}
	}

	/**
	 * calculat highestDegree of RichClub
	 * 
	 */
	private int getHighestDegree(HashMap<Integer, LinkedList<Node>> newRC) {
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
	private boolean applyAfterUndirectedEdgeAddition(Update u) {
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
		HashMap<Integer, LinkedList<Node>> nRC = this.richClubs.get(nRCNum);

		if (nRC.containsKey(n.getDegree()) || nRCNum == 0) {

			removeFromRichClub(nRC, n, n.getDegree() - 1);
			addToRichClub(nRC, n);

		} else {
			HashMap<Integer, LinkedList<Node>> newRC = this.richClubs
					.get(nRCNum - 1);
			int low = getlowestDegree(newRC);
			if (low >= n.getDegree()) {
				removeFromRichClub(nRC, n, n.getDegree() - 1);
				addToRichClub(nRC, n);
			} else {
				int newRCNum = nRCNum - 1;
				while (low < n.getDegree() && newRCNum >= 0) {
					newRC = this.richClubs.get(newRCNum);
					UndirectedNode changedNode = (UndirectedNode) newRC
							.get(low).getLast();

					if (changedNode == other) {
						changedNode = (UndirectedNode) newRC.get(low)
								.getFirst();
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
	private boolean applyAfterUndirectedNodeRemoval(Update u) {
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

			HashMap<Integer, LinkedList<Node>> newRC = this.richClubs
					.get(i + 1);
			int high = getHighestDegree(newRC);
			UndirectedNode changedNode = (UndirectedNode) newRC.get(high)
					.getFirst();

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
					new HashMap<Integer, LinkedList<Node>>());
			this.richClubEdges.put(this.richClubs.size() - 1, 0);
			this.nodesRichClub.put(n, this.richClubs.size() - 1);
			addToRichClub(this.richClubs.get(this.richClubs.size() - 1), n);
		} else {
			this.nodesRichClub.put(n, this.richClubs.size() - 1);
			addToRichClub(this.richClubs.get(this.richClubs.size() - 1), n);
		}

		return true;
	}

	private void addToRichClub(HashMap<Integer, LinkedList<Node>> newRC,
			DirectedNode node) {
		int degree = node.getOutDegree();
		if (newRC.containsKey(degree)) {
			newRC.get(degree).add(node);
		} else {
			LinkedList<Node> temp = new LinkedList<Node>();
			temp.add(node);
			newRC.put(degree, temp);
		}
	}

}
