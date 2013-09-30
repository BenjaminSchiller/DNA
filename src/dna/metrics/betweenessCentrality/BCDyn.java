package dna.metrics.betweenessCentrality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class BCDyn extends BetweenessCentrality {

	public static enum TouchedType {
		UP, DOWN, NOT
	};

	public BCDyn() {
		super("BCDyn", ApplicationType.AfterUpdate);
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

	private boolean applyAfterEdgeRemoval(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		for (IElement iE : g.getNodes()) {
			UndirectedNode root = (UndirectedNode) iE;

			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree = shortestPathTrees
					.get(root);

			ShortestPathTreeElement n1TE = shortestPathTree.get(n1);
			ShortestPathTreeElement n2TE = shortestPathTree.get(n2);

			// Find the above Tree Element
			if (n1TE.getDistanceToRoot() > n2TE.getDistanceToRoot()) {
				n1TE = n2TE;
				n2TE = shortestPathTree.get(n1);
				n1 = n2;
				n2 = e.getDifferingNode(n1);
			}

			if (!n2TE.getParents().contains(n1)
					|| n1TE.getDistanceToRoot() == Integer.MAX_VALUE
					|| n2TE.getDistanceToRoot() == Integer.MAX_VALUE) {
				continue;
			}

			// case 1: more than one parent => no shortest path tree
			// change
			if (n2TE.getParents().size() > 1) {

				// removeEdgeManyToMany(root, n1, n2, shortestPathTree);
				recomp(g, root);
				// case 2: the lower node has only one parent
			} else if (n2TE.getParents().size() == 1) {
				removeEdgeOneToMany(root, n1, n2, shortestPathTree);

			}
		}

		return true;
	}

	private boolean removeEdgeOneToMany(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst,
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree) {

		// Queues and data structure for tree change
		HashSet<UndirectedNode> uncertain = new HashSet<UndirectedNode>();
		Queue<UndirectedNode>[] qLevel = new LinkedList[g.getNodeCount()];
		Queue<UndirectedNode>[] qLeveldA = new LinkedList[g.getNodeCount()];

		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
			qLeveldA[i] = new LinkedList<UndirectedNode>();
		}

		// data structure for Updates
		HashMap<UndirectedNode, Integer> dP = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, TouchedType> touched = new HashMap<UndirectedNode, TouchedType>();
		HashMap<UndirectedNode, Integer> spcUpdate = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();
		HashMap<UndirectedNode, HashSet<UndirectedNode>> newParents = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();

		for (IElement iE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) iE;
			dP.put(t, 0);
			touched.put(t, TouchedType.NOT);
			spcUpdate.put(t, shortestPathTree.get(t).getShortestPathCount());
			newASums.put(t, 0d);
			newParents.put(t, new HashSet<UndirectedNode>());
		}

		ShortestPathTreeElement dstTE = shortestPathTree.get(dst);

		// set data structure for dst Node
		qLevel[dstTE.getDistanceToRoot()].add(dst);
		uncertain.add(dst);
		touched.put(dst, TouchedType.DOWN);

		for (int i = 0; i < qLevel.length; i++) {
			while (!qLevel[i].isEmpty()) {

				UndirectedNode w = qLevel[i].poll();
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				int dist = Integer.MAX_VALUE;
				ArrayList<UndirectedNode> min = new ArrayList<UndirectedNode>();
				for (IElement iEdges : w.getEdges()) {
					UndirectedEdge ed = (UndirectedEdge) iEdges;
					UndirectedNode z = ed.getDifferingNode(w);
					ShortestPathTreeElement zTE = shortestPathTree.get(z);

					if (zTE.getDistanceToRoot() == wTE.getDistanceToRoot() + 1
							&& touched.get(z) == TouchedType.NOT) {
						qLevel[i + 1].add(z);
						uncertain.add(z);
						touched.put(z, TouchedType.DOWN);
					}
					if (zTE.getDistanceToRoot() < dist) {
						min.clear();
						min.add(z);
						dist = zTE.getDistanceToRoot();
						continue;
					}
					if (zTE.getDistanceToRoot() == dist) {
						min.add(z);
						continue;
					}
				}

				// if their is no connection to the three, remove node form
				// data set
				if (dist == Integer.MAX_VALUE || dist >= g.getNodeCount() - 1) {
					wTE.setDistanceToRoot(Integer.MAX_VALUE);
					newParents.get(w).clear();
					qLeveldA[g.getNodeCount() - 1].add(w);
					uncertain.remove(w);
					continue;
				}

				// connect to the highest uncertain node
				boolean found = false;
				spcUpdate.put(w, 0);
				newParents.get(w).clear();
				for (UndirectedNode mNode : min) {
					ShortestPathTreeElement mNodeTE = shortestPathTree
							.get(mNode);
					if ((!uncertain.contains(mNode))
							&& mNodeTE.getDistanceToRoot() + 1 == i) {
						uncertain.remove(w);
						spcUpdate.put(w,
								spcUpdate.get(w) + spcUpdate.get(mNode));
						found = true;
					}
					wTE.setDistanceToRoot(mNodeTE.getDistanceToRoot() + 1);
					newParents.get(w).add(mNode);

				}
				// else connect to another node
				if (!found) {
					qLevel[wTE.getDistanceToRoot()].add(w);
				} else {
					qLeveldA[wTE.getDistanceToRoot()].add(w);
				}
			}
		}

		// Stage 3
		for (int i = qLeveldA.length - 1; i >= 0; i--) {
			while (!qLeveldA[i].isEmpty()) {

				UndirectedNode w = qLeveldA[i].poll();
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				for (UndirectedNode v : wTE.getParents()) {

					ShortestPathTreeElement vTE = shortestPathTree.get(v);
					if (!newParents.get(w).contains(v)) {

						if (touched.get(v) == TouchedType.NOT) {
							qLevel[vTE.getDistanceToRoot()].add(v);
							touched.put(v, TouchedType.UP);
							newASums.put(v, vTE.getAccumulativSum());
							newParents.get(v).addAll(vTE.getParents());
						}
						if (touched.get(v) == TouchedType.UP) {
							double temp = newASums.get(v)
									- vTE.getShortestPathCount()
									* (1 + wTE.getAccumulativSum())
									/ wTE.getShortestPathCount();
							newASums.put(v, temp);
						}
					}
				}

				for (UndirectedNode v : newParents.get(w)) {
					ShortestPathTreeElement vTE = shortestPathTree.get(v);

					if (vTE.getDistanceToRoot() == wTE.getDistanceToRoot() - 1) {
						if (touched.get(v) == TouchedType.NOT) {
							qLevel[i - 1].add(v);
							touched.put(v, TouchedType.UP);
							newASums.put(v, vTE.getAccumulativSum());
							newParents.get(v).addAll(vTE.getParents());
						}

						double d = newASums.get(v) + spcUpdate.get(v)
								* (1 + newASums.get(w)) / spcUpdate.get(w);

						newASums.put(v, d);

						if (touched.get(v) == TouchedType.UP
								&& wTE.containsParent(v)) {
							double temp = newASums.get(v)
									- vTE.getShortestPathCount()
									* (1 + wTE.getAccumulativSum())
									/ wTE.getShortestPathCount();
							newASums.put(v, temp);
						}
					}
				}

				if (w != root) {
					double currentScore = this.betweeneesCentralityScore.get(w);
					this.betweeneesCentralityScore.put(w, currentScore
							+ newASums.get(w) - wTE.getAccumulativSum());
				}
			}
		}

		for (IElement iE : g.getNodes()) {
			UndirectedNode i = (UndirectedNode) iE;
			ShortestPathTreeElement shortestPathTreeElement = shortestPathTree
					.get(i);
			if (touched.get(i) != TouchedType.NOT) {
				shortestPathTreeElement.setAccumulativSum(newASums.get(i));
				shortestPathTreeElement.getParents().clear();
				shortestPathTreeElement.getParents().addAll(newParents.get(i));
			}

			shortestPathTreeElement.setShortestPathCount(spcUpdate.get(i));

		}

		return true;
	}

	private boolean removeEdgeManyToMany(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst,
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree) {

		// Queue for BFS Search
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();

		// Queues for dependency acc
		Queue<UndirectedNode>[] qLevel = new Queue[g.getNodeCount()];
		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}

		// data structure for Updates
		HashMap<UndirectedNode, Integer> dP = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, TouchedType> touched = new HashMap<UndirectedNode, TouchedType>();
		HashMap<UndirectedNode, Integer> spcUpdate = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();

		for (IElement iE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) iE;
			dP.put(t, 0);
			touched.put(t, TouchedType.NOT);
			spcUpdate.put(t, shortestPathTree.get(t).getShortestPathCount());
			newASums.put(t, 0d);
		}

		// setup changes for dst node
		qBFS.add(dst);
		ShortestPathTreeElement dstTE = shortestPathTree.get(dst);
		qLevel[dstTE.getDistanceToRoot()].add(dst);
		touched.put(dst, TouchedType.DOWN);
		dP.put(dst, shortestPathTree.get(src).getShortestPathCount());
		spcUpdate.put(dst, spcUpdate.get(dst) - dP.get(dst));
		int maxHeight = dstTE.getDistanceToRoot();

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
			ShortestPathTreeElement vTE = shortestPathTree.get(v);

			// all neighbours of v
			for (IElement iEdge : v.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdge;
				UndirectedNode w = edge.getDifferingNode(v);
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				if (wTE.getDistanceToRoot() == vTE.getDistanceToRoot() + 1) {
					if (touched.get(w) == TouchedType.NOT) {
						qBFS.add(w);
						qLevel[wTE.getDistanceToRoot()].add(w);
						maxHeight = Math
								.max(maxHeight, wTE.getDistanceToRoot());
						touched.put(w, TouchedType.DOWN);
						dP.put(w, dP.get(v));
					} else {
						dP.put(w, dP.get(w) + dP.get(v));
					}
					spcUpdate.put(w, spcUpdate.get(w) - dP.get(v));
				}
			}
		}

		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i > 0; i--) {
			while (!qLevel[i].isEmpty()) {
				UndirectedNode w = qLevel[i].poll();
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				for (UndirectedNode v : wTE.getParents()) {
					ShortestPathTreeElement vTE = shortestPathTree.get(v);
					if (touched.get(v) == TouchedType.NOT) {
						qLevel[i - 1].add(v);
						touched.put(v, TouchedType.UP);
						newASums.put(v, vTE.getAccumulativSum());
					}

					if (!(v == src && w == dst)) {
						double d = newASums.get(v) + spcUpdate.get(v)
								* (1 + newASums.get(w)) / spcUpdate.get(w);
						newASums.put(v, d);
					}
					if (touched.get(v) == TouchedType.UP) {
						double temp = newASums.get(v)
								- vTE.getShortestPathCount()
								* (1 + wTE.getAccumulativSum())
								/ wTE.getShortestPathCount();
						newASums.put(v, temp);
					}
					if (w != root) {
						double currentScore = this.betweeneesCentralityScore
								.get(w);
						this.betweeneesCentralityScore.put(w, currentScore
								+ newASums.get(w) - wTE.getAccumulativSum());
					}
				}
			}
		}

		dstTE.removeParent(src);
		for (IElement iE : g.getNodes()) {
			UndirectedNode i = (UndirectedNode) iE;
			ShortestPathTreeElement shortestPathTreeElement = shortestPathTree
					.get(i);
			shortestPathTreeElement.setShortestPathCount(spcUpdate.get(i));
			if (touched.get(i) != TouchedType.NOT) {
				shortestPathTreeElement.setAccumulativSum(newASums.get(i));
			}
		}
		return true;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		for (IElement iE : g.getNodes()) {
			UndirectedNode root = (UndirectedNode) iE;
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPath = this.shortestPathTrees
					.get(root);

			ShortestPathTreeElement n1TE = shortestPath.get(n1);

			ShortestPathTreeElement n2TE = shortestPath.get(n2);

			if (n1TE.getDistanceToRoot() > n2TE.getDistanceToRoot()) {
				ShortestPathTreeElement temp = n2TE;
				n2TE = n1TE;
				n1TE = temp;
				n2 = n1;
				n1 = e.getDifferingNode(n2);
			}

			if (n1TE.getDistanceToRoot() == Integer.MAX_VALUE
					&& n2TE.getDistanceToRoot() == Integer.MAX_VALUE
					|| n1TE.getDistanceToRoot() == n2TE.getDistanceToRoot()) {
				// no change to shortes path tree
				continue;
			}
			if (n2TE.getDistanceToRoot() == Integer.MAX_VALUE) {
				// to components merge therefore new Nodes add to shortest path
				// tree

				mergeOfComponentsInsertion(root, n1, n2, shortestPath);
				continue;
			}
			if (n1TE.getDistanceToRoot() + 1 == n2TE.getDistanceToRoot()) {
				// the added edge connects nodes in adjacent Levels therefore
				// only the new tree edge is added
				adjacentLevelInsertion(root, n1, n2, shortestPath);
				continue;
			}
			if (n1TE.getDistanceToRoot() + 1 < n2TE.getDistanceToRoot()) {
				// the added edge connects nodes in non adjacent Levels
				// therefore all nodes in the subtree need to be checked if they
				// move up in the shortest path tree
				nonAdjacentLevelInsertion(root, n1, n2, shortestPath);
				continue;

			}

			System.err.println(" shit" + n1TE.getDistanceToRoot() + " "
					+ n2TE.getDistanceToRoot());
			return false;

		}
		return true;
	}

	private void mergeOfComponentsInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst,
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree) {

		// Queues for the dependency Acc
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}
		// Queue for the BFS search down the shortes Path tree
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();

		// data structure for Updates
		HashMap<UndirectedNode, Integer> dP = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, TouchedType> touched = new HashMap<UndirectedNode, TouchedType>();
		HashMap<UndirectedNode, Integer> spcUpdate = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();

		for (IElement iE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) iE;
			dP.put(t, 0);
			touched.put(t, TouchedType.NOT);
			spcUpdate.put(t, shortestPathTree.get(t).getShortestPathCount());
			newASums.put(t, 0d);
		}

		// new TreeElement and the current Values for the Tree Position
		ShortestPathTreeElement dstTE = shortestPathTree.get(dst);
		ShortestPathTreeElement srcTE = shortestPathTree.get(src);
		dstTE.setDistanceToRoot(srcTE.getDistanceToRoot() + 1);
		dP.put(dst, srcTE.getShortestPathCount());
		spcUpdate.put(dst, spcUpdate.get(src));
		dstTE.addParent(src);
		touched.put(dst, TouchedType.DOWN);

		qBFS.add(dst);
		// stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
			ShortestPathTreeElement vTE = shortestPathTree.get(v);
			qLevel[vTE.getDistanceToRoot()].add(v);
			for (IElement iEdge : v.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iEdge;
				UndirectedNode n = ed.getDifferingNode(v);
				ShortestPathTreeElement nTE = shortestPathTree.get(n);
				if (touched.get(n) == TouchedType.NOT && n != src
						&& nTE.getDistanceToRoot() == Integer.MAX_VALUE) {
					qBFS.add(n);
					touched.put(n, TouchedType.DOWN);
					nTE.setDistanceToRoot(vTE.getDistanceToRoot() + 1);
				}
				if (nTE.getDistanceToRoot() == vTE.getDistanceToRoot() + 1) {
					spcUpdate.put(n, spcUpdate.get(n) + spcUpdate.get(v));
					nTE.addParent(v);
				}
			}
		}

		// Stage 3
		// search the shortest path tree from leaves to root
		for (int i = qLevel.length - 1; i > 0; i--) {
			while (!qLevel[i].isEmpty()) {
				UndirectedNode w = qLevel[i].poll();
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				for (UndirectedNode v : wTE.getParents()) {
					ShortestPathTreeElement vTE = shortestPathTree.get(v);
					if (touched.get(v) == TouchedType.NOT) {
						qLevel[i - 1].add(v);
						touched.put(v, TouchedType.UP);
						newASums.put(v, vTE.getAccumulativSum());
					}
					double d = spcUpdate.get(v) * (1 + newASums.get(w))
							/ spcUpdate.get(w);
					newASums.put(v, newASums.get(v) + d);

					if (touched.get(v) == TouchedType.UP
							&& (v != src || w != dst)) {
						double temp = newASums.get(v)
								- vTE.getShortestPathCount()
								* (1 + wTE.getAccumulativSum())
								/ wTE.getShortestPathCount();
						newASums.put(v, temp);
					}

					if (w != root) {
						double currentScore = this.betweeneesCentralityScore
								.get(w);
						this.betweeneesCentralityScore.put(w, currentScore
								+ newASums.get(w) - wTE.getAccumulativSum());
					}

				}

			}
		}

		for (IElement iE : g.getNodes()) {
			UndirectedNode i = (UndirectedNode) iE;
			ShortestPathTreeElement shortestPathTreeElement = shortestPathTree
					.get(i);
			shortestPathTreeElement.setShortestPathCount(spcUpdate.get(i));
			if (touched.get(i) != TouchedType.NOT) {
				shortestPathTreeElement.setAccumulativSum(newASums.get(i));
			}
		}

	}

	private void nonAdjacentLevelInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst,
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree) {

		// Data Structure for BFS Search
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();

		// Queue for dependency Accumulation
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}

		// data structure for Updates
		HashMap<UndirectedNode, Integer> dP = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, TouchedType> touched = new HashMap<UndirectedNode, TouchedType>();
		HashMap<UndirectedNode, Integer> spcUpdate = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();
		HashMap<UndirectedNode, HashSet<UndirectedNode>> newParents = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();

		for (IElement iE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) iE;
			dP.put(t, 0);
			touched.put(t, TouchedType.NOT);
			spcUpdate.put(t, shortestPathTree.get(t).getShortestPathCount());
			newASums.put(t, 0d);
			newParents.put(t, new HashSet<UndirectedNode>());
		}

		// set Up data Structure for the lower node

		qBFS.add(dst);
		touched.put(dst, TouchedType.DOWN);
		shortestPathTree.get(dst).setDistanceToRoot(
				shortestPathTree.get(src).getDistanceToRoot() + 1);
		qLevel[shortestPathTree.get(dst).getDistanceToRoot()].add(dst);

		// Stage 2
		while (!qBFS.isEmpty()) {

			UndirectedNode v = qBFS.poll();

			spcUpdate.put(v, 0);

			ShortestPathTreeElement vTE = shortestPathTree.get(v);
			// all neighbours of v
			for (IElement iEdge : v.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iEdge;
				UndirectedNode n = ed.getDifferingNode(v);
				ShortestPathTreeElement nTE = shortestPathTree.get(n);

				// Lower Node moves up
				if (nTE.getDistanceToRoot() > vTE.getDistanceToRoot() + 1) {
					nTE.setDistanceToRoot(vTE.getDistanceToRoot() + 1);
					qBFS.add(n);
					qLevel[nTE.getDistanceToRoot()].add(n);
					touched.put(n, TouchedType.DOWN);
					continue;
				}

				// lower Node get a new Parent
				if (nTE.getDistanceToRoot() == vTE.getDistanceToRoot() + 1) {
					if (touched.get(n) == TouchedType.NOT) {
						touched.put(n, TouchedType.DOWN);
						qLevel[nTE.getDistanceToRoot()].add(n);
						qBFS.add(n);
					}
					continue;
				}

				if (nTE.getDistanceToRoot() < vTE.getDistanceToRoot()) {
					spcUpdate.put(v, spcUpdate.get(v) + spcUpdate.get(n));
					if (!newParents.get(v).contains(n)) {
						newParents.get(v).add(n);
					}
				}
			}
		}

		// Stage 3
		for (int i = qLevel.length - 1; i >= 0; i--) {
			while (!qLevel[i].isEmpty()) {

				UndirectedNode w = qLevel[i].poll();
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				for (UndirectedNode v : wTE.getParents()) {

					ShortestPathTreeElement vTE = shortestPathTree.get(v);
					if (!newParents.get(w).contains(v)) {
						if (touched.get(v) == TouchedType.NOT) {
							qLevel[vTE.getDistanceToRoot()].add(v);
							touched.put(v, TouchedType.UP);
							newASums.put(v, vTE.getAccumulativSum());
							newParents.get(v).addAll(vTE.getParents());
						}
						if (touched.get(v) == TouchedType.UP) {
							double temp = newASums.get(v)
									- vTE.getShortestPathCount()
									* (1 + wTE.getAccumulativSum())
									/ wTE.getShortestPathCount();
							newASums.put(v, temp);
						}
					}
				}

				for (UndirectedNode v : newParents.get(w)) {

					ShortestPathTreeElement vTE = shortestPathTree.get(v);
					if (vTE.getDistanceToRoot() == wTE.getDistanceToRoot() - 1) {
						if (touched.get(v) == TouchedType.NOT) {
							qLevel[i - 1].add(v);
							touched.put(v, TouchedType.UP);
							newASums.put(v, vTE.getAccumulativSum());
							newParents.get(v).addAll(vTE.getParents());
						}
						double d = newASums.get(v) + spcUpdate.get(v)
								* (1 + newASums.get(w)) / spcUpdate.get(w);
						newASums.put(v, d);

						if (touched.get(v) == TouchedType.UP
								&& (dst != w || src != v)) {
							double temp = newASums.get(v)
									- vTE.getShortestPathCount()
									* (1 + wTE.getAccumulativSum())
									/ wTE.getShortestPathCount();
							newASums.put(v, temp);
						}
					}
				}
				if (w != root) {
					double currentScore = this.betweeneesCentralityScore.get(w);
					this.betweeneesCentralityScore.put(w, currentScore
							+ newASums.get(w) - wTE.getAccumulativSum());
				}
			}
		}

		for (IElement iE : g.getNodes()) {
			UndirectedNode i = (UndirectedNode) iE;
			ShortestPathTreeElement shortestPathTreeElement = shortestPathTree
					.get(i);

			if (touched.get(i) != TouchedType.NOT) {

				if (newASums.get(i) < 0)
					newASums.put(i, 0d);
				shortestPathTreeElement.setAccumulativSum(newASums.get(i));
				shortestPathTreeElement.getParents().clear();
				shortestPathTreeElement.getParents().addAll(newParents.get(i));
			}

			shortestPathTreeElement.setShortestPathCount(spcUpdate.get(i));

		}
	}

	private boolean recomp(Graph g, UndirectedNode n) {
		// stage ONE
		Stack<UndirectedNode> s = new Stack<UndirectedNode>();
		Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();
		HashMap<UndirectedNode, ShortestPathTreeElement> shortestPath = new HashMap<UndirectedNode, ShortestPathTreeElement>();

		for (IElement iE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) iE;
			if (t == n) {
				ShortestPathTreeElement temp = new ShortestPathTreeElement(
						t.getIndex());
				temp.setDistanceToRoot(0);
				temp.setShortestPathCount(1);
				shortestPath.put(t, temp);
			} else {
				ShortestPathTreeElement temp = new ShortestPathTreeElement(
						t.getIndex());
				shortestPath.put(t, temp);
			}
		}

		q.add(n);

		// stage 2
		while (!q.isEmpty()) {
			UndirectedNode v = q.poll();
			s.push(v);
			ShortestPathTreeElement vTE = shortestPath.get(v);

			for (IElement iEdges : v.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iEdges;
				UndirectedNode neighbour = ed.getDifferingNode(v);
				ShortestPathTreeElement nTE = shortestPath.get(neighbour);

				if (nTE.getDistanceToRoot() == Integer.MAX_VALUE) {
					q.add(neighbour);
					nTE.setDistanceToRoot(vTE.getDistanceToRoot() + 1);
				}
				if (nTE.getDistanceToRoot() == vTE.getDistanceToRoot() + 1) {
					nTE.setShortestPathCount(nTE.getShortestPathCount()
							+ vTE.getShortestPathCount());
					nTE.addParent(v);
				}
			}
		}

		// stage 3

		HashMap<UndirectedNode, ShortestPathTreeElement> oldshortestPathTree = this.shortestPathTrees
				.get(n);

		while (!s.isEmpty()) {
			UndirectedNode w = s.pop();
			ShortestPathTreeElement wTE = shortestPath.get(w);
			for (UndirectedNode parent : wTE.getParents()) {
				ShortestPathTreeElement pTE = shortestPath.get(parent);
				double sumForCurretConnection = pTE.getShortestPathCount()
						* (1 + wTE.getAccumulativSum())
						/ wTE.getShortestPathCount();
				pTE.setAccumulativSum(pTE.getAccumulativSum()
						+ sumForCurretConnection);
			}
			if (w != n) {
				double currentScore = this.betweeneesCentralityScore.get(w)
						- oldshortestPathTree.get(w).getAccumulativSum();
				this.betweeneesCentralityScore.put(w,
						currentScore + wTE.getAccumulativSum());
			}
		}
		this.shortestPathTrees.put(n, shortestPath);

		return true;
	}

	private boolean adjacentLevelInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst,
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree) {
		// Queue for BFS Search
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();

		// Queues for dependency acc
		Queue<UndirectedNode>[] qLevel = new Queue[g.getNodeCount()];
		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}

		// data structure for Updates
		HashMap<UndirectedNode, Integer> dP = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, TouchedType> touched = new HashMap<UndirectedNode, TouchedType>();
		HashMap<UndirectedNode, Integer> spcUpdate = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();

		for (IElement iE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) iE;
			dP.put(t, 0);
			touched.put(t, TouchedType.NOT);
			spcUpdate.put(t, shortestPathTree.get(t).getShortestPathCount());
			newASums.put(t, 0d);
		}

		// setup changes for dst node
		qBFS.add(dst);
		ShortestPathTreeElement dstTE = shortestPathTree.get(dst);
		qLevel[dstTE.getDistanceToRoot()].add(dst);
		touched.put(dst, TouchedType.DOWN);
		dP.put(dst, shortestPathTree.get(src).getShortestPathCount());
		spcUpdate.put(dst, spcUpdate.get(dst) + dP.get(dst));
		dstTE.addParent(src);
		int maxHeight = dstTE.getDistanceToRoot();

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
			ShortestPathTreeElement vTE = shortestPathTree.get(v);

			// all neighbours of v
			for (IElement iEdges : v.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdges;
				UndirectedNode w = edge.getDifferingNode(v);
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				if (wTE.getDistanceToRoot() == vTE.getDistanceToRoot() + 1) {
					if (touched.get(w) == TouchedType.NOT) {
						qBFS.add(w);
						qLevel[wTE.getDistanceToRoot()].add(w);
						maxHeight = Math
								.max(maxHeight, wTE.getDistanceToRoot());
						touched.put(w, TouchedType.DOWN);
						dP.put(w, dP.get(v));
					} else {
						dP.put(w, dP.get(w) + dP.get(v));
					}
					spcUpdate.put(w, spcUpdate.get(w) + dP.get(v));
				}
			}
		}

		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i > 0; i--) {
			while (!qLevel[i].isEmpty()) {
				UndirectedNode w = qLevel[i].poll();
				ShortestPathTreeElement wTE = shortestPathTree.get(w);

				for (UndirectedNode v : wTE.getParents()) {
					ShortestPathTreeElement vTE = shortestPathTree.get(v);
					if (touched.get(v) == TouchedType.NOT) {
						qLevel[i - 1].add(v);
						touched.put(v, TouchedType.UP);
						newASums.put(v, vTE.getAccumulativSum());
					}
					double d = newASums.get(v) + spcUpdate.get(v)
							* (1 + newASums.get(w)) / spcUpdate.get(w);
					newASums.put(v, d);
					if (touched.get(v) == TouchedType.UP
							&& (v != src || w != dst)) {
						double temp = newASums.get(v)
								- vTE.getShortestPathCount()
								* (1 + wTE.getAccumulativSum())
								/ wTE.getShortestPathCount();
						newASums.put(v, temp);
					}
					if (w != root) {
						double currentScore = this.betweeneesCentralityScore
								.get(w);
						this.betweeneesCentralityScore.put(w, currentScore
								+ newASums.get(w) - wTE.getAccumulativSum());
					}
				}
			}
		}

		for (IElement iE : g.getNodes()) {
			UndirectedNode i = (UndirectedNode) iE;
			ShortestPathTreeElement shortestPathTreeElement = shortestPathTree
					.get(i);
			shortestPathTreeElement.setShortestPathCount(spcUpdate.get(i));
			if (touched.get(i) != TouchedType.NOT) {
				shortestPathTreeElement.setAccumulativSum(newASums.get(i));
			}
		}
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
		HashMap<UndirectedNode, ShortestPathTreeElement> temp = new HashMap<UndirectedNode, ShortestPathTreeElement>();
		temp.put(node, new ShortestPathTreeElement(node.getIndex()));
		this.shortestPathTrees.put(node, temp);
		return true;
	}
}
