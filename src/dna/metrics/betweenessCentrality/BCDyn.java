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

<<<<<<< HEAD
<<<<<<< HEAD
						removeEdgeManyToMany(node2TreeElement);

						// case 2: the lower node has only one parent
					} else if (node2TreeElement.getParents().size() == 1) {
						Queue<ShortestPathTreeElement> q1 = new LinkedList<ShortestPathTreeElement>();
						Queue<ShortestPathTreeElement> q2 = new LinkedList<ShortestPathTreeElement>();
						q1.add(node2TreeElement);
						Set<ShortestPathTreeElement> seenNodes = new HashSet<ShortestPathTreeElement>();

						while (!q1.isEmpty()) {
							ShortestPathTreeElement temp = q1.poll();
							if (temp.getChildren().isEmpty()) {
								q2.add(temp);
							}
							if (temp.getParents().size() > 1) {

								removeEdgeManyToMany(temp);

							} else if (temp.getParents().size() == 1) {
								// case2.1 connection to the same level
								if (g.getNode(temp.getNodeIndex()).getDegree() >= temp
										.getParents().size()
										+ temp.getChildren().size()) {
									UndirectedNode node = g.getNode(temp
											.getNodeIndex());
									List<Integer> sameLevelNodes = new ArrayList<Integer>();
									for (UndirectedEdge edge : node.getEdges()) {
										if (edge.getNode1().getIndex() != temp
												.getNodeIndex()) {
											if (!seenNodes
													.contains(shortestPathNodeN
															.get(edge
																	.getNode1()
																	.getIndex()))) {
												sameLevelNodes.add(edge
														.getNode1().getIndex());
											}
										} else if (!seenNodes
												.contains(shortestPathNodeN
														.get(edge.getNode2()
																.getIndex()))) {
											sameLevelNodes.add(edge.getNode2()
													.getIndex());
										}
									}

									for (int i : sameLevelNodes) {
										ShortestPathTreeElement iTE = shortestPathNodeN
												.get(i);
										for (ShortestPathTreeElement ste : temp
												.getChildren()) {
											q2.add(ste);
										}
										iTE.addChild(temp);
										temp.addParent(iTE);
									}

									int shortestPathCount = 0;
									for (ShortestPathTreeElement ste : temp
											.getParents()) {
										shortestPathCount += ste
												.getShortestPathCount();
									}
									temp.setShortestPathCount(shortestPathCount);
									temp.setDistanceToRoot(temp.getParents()
											.get(0).getDistanceToRoot());

									// childs to queue
									for (ShortestPathTreeElement ste : temp
											.getChildren()) {
										q1.add(ste);
										seenNodes.add(ste);
									}
								} else {
									// case 2.2: no connection to same level
									List<ShortestPathTreeElement> manyToMany = new ArrayList<ShortestPathTreeElement>();
									List<ShortestPathTreeElement> oneToMany = new ArrayList<ShortestPathTreeElement>();

									// Check the connection status of the
									// children
									for (ShortestPathTreeElement ste : temp
											.getChildren()) {
										if (ste.getParents().size() > 1) {
											manyToMany.add(ste);
										} else {
											oneToMany.add(ste);
										}
										q1.add(ste);
									}
									if (!manyToMany.isEmpty()) {
										for (ShortestPathTreeElement ste : manyToMany) {
											ste.addChild(temp);
											ste.removeParent(temp);
											ste.setShortestPathCount(ste
													.getShortestPathCount() - 1);
											temp.addParent(ste);
										}
									} else {
										for (ShortestPathTreeElement ste : oneToMany) {
											ste.addChild(temp);
											ste.removeParent(temp);
											ste.setShortestPathCount(ste
													.getShortestPathCount() - 1);
											temp.addParent(ste);
										}
									}

								}

							}
						}

						while (!q2.isEmpty()) {
							ShortestPathTreeElement temp = q1.poll();
							for (ShortestPathTreeElement node : temp
									.getParents()) {
								q2.add(node);

							}
						}

					}
				}

			}
		}
		return true;
	}

	private void removeEdgeManyToMany(ShortestPathTreeElement node2TreeElement) {
		// Queue for update the tree entries down the shortest
		// path tree
		Queue<ShortestPathTreeElement> q1 = new LinkedList<ShortestPathTreeElement>();

		// Queue for update the betweenesscentrality score up
		// the shortest path tree
		Queue<ShortestPathTreeElement> q2 = new LinkedList<ShortestPathTreeElement>();
		q1.add(node2TreeElement);

		// change the shortestpath count down the tree
		while (!q1.isEmpty()) {
			ShortestPathTreeElement temp = q1.poll();
			for (ShortestPathTreeElement node : temp.getChildren()) {
				q1.add(node);
				node.setShortestPathCount(node.getShortestPathCount() - 1);
				if (node.getChildren().isEmpty()) {
					q2.add(node);
				}
			}
		}

		while (!q2.isEmpty()) {
			ShortestPathTreeElement temp = q1.poll();
			for (ShortestPathTreeElement node : temp.getParents()) {
				q2.add(node);

			}
		}
=======
						removeEdgeManyToMany(node2TreeElement,
								node1TreeElement, shortestPathNodeN);
=======
			ShortestPathTreeElement n1TE = shortestPathTree.get(n1);
			ShortestPathTreeElement n2TE = shortestPathTree.get(n2);
>>>>>>> some stuff

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

<<<<<<< HEAD
		dependencyAccumulation(src, dst, shortestPathTree, qLevel,
				shortestPathsCount, touched);

>>>>>>> some stuff
=======
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
>>>>>>> some stuff
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

<<<<<<< HEAD
	private void mergeOfComponentsInsertion(UndirectedNode src,
			UndirectedNode dst,
			HashMap<Integer, ShortestPathTreeElement> shortestPathTree) {
<<<<<<< HEAD
=======
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
		// TODO:Levels einrichten
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		Map<Integer, Integer> distanceP = new HashMap<>();
		Map<Integer, Integer> shortestPathsCount = new HashMap<>();
		Set<Integer> touched = new HashSet<>();
=======
	private void mergeOfComponentsInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst,
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree) {
<<<<<<< HEAD
		UndirectedGraph g = (UndirectedGraph) this.g;
>>>>>>> some stuff
=======
>>>>>>> generator for google

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

<<<<<<< HEAD
	private boolean adjacentLevelInsertion(UndirectedNode src,
			UndirectedNode dst,
			HashMap<Integer, ShortestPathTreeElement> shortestPathTree) {
>>>>>>> some stuff
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
		// TODO:Levels einrichten
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		Map<Integer, Integer> distanceP = new HashMap<>();
		Map<Integer, Integer> shortestPathsCount = new HashMap<>();
		Set<Integer> touched = new HashSet<>();

		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}

<<<<<<< HEAD
		ShortestPathTreeElement dstTE = new ShortestPathTreeElement(
				dst.getIndex());
		dstTE.setDistanceToRoot(shortestPathTree.get(src.getIndex())
				.getDistanceToRoot() + 1);
		dstTE.setShortestPathCount(shortestPathTree.get(src.getIndex())
				.getShortestPathCount());
		shortestPathTree.put(dst.getIndex(), dstTE);

		qBFS.add(dst);

=======
>>>>>>> some stuff
		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();

			// all neighbours of v
			for (UndirectedEdge ed : v.getEdges()) {

				UndirectedNode n = ed.getNode1();
				if (n == v) {
					n = ed.getNode2();
				}
				if (!touched.contains(n.getIndex())) {
					qBFS.add(n);

					ShortestPathTreeElement temp = new ShortestPathTreeElement(
							n.getIndex());
					temp.setDistanceToRoot(shortestPathTree.get(v.getIndex())
							.getDistanceToRoot() + 1);
					qLevel[temp.getDistanceToRoot()].add(n);
				}
				if (shortestPathTree.get(n.getIndex()).getDistanceToRoot() == shortestPathTree
						.get(v.getIndex()).getDistanceToRoot() + 1) {
					shortestPathTree.get(n.getIndex()).setShortestPathCount(
							shortestPathTree.get(n.getIndex())
									.getShortestPathCount()
									+ shortestPathTree.get(v.getIndex())
											.getShortestPathCount());
					shortestPathTree.get(n).addParent(
							shortestPathTree.get(v.getIndex()));

				}

			}
		}

		dependencyAccumulation(src, dst, shortestPathTree, qLevel,
				shortestPathsCount, touched);

	}

	private void nonAdjacentLevelInsertion(UndirectedNode src,
			UndirectedNode dst,
			HashMap<Integer, ShortestPathTreeElement> shortestPathTree) {
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
		// TODO:Levels einrichten
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		Map<Integer, Integer> distanceP = new HashMap<>();
		Map<Integer, Integer> shortestPathsCount = new HashMap<>();
		Set<Integer> touched = new HashSet<>();

		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}

		distanceP.put(dst.getIndex(), shortestPathTree.get(src.getIndex())
				.getDistanceToRoot() + 1);
		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
			// all neighbours of v
			for (UndirectedEdge ed : v.getEdges()) {

				UndirectedNode n = ed.getNode1();
				if (n == v) {
					n = ed.getNode2();
				}

				ShortestPathTreeElement spTEofN = shortestPathTree.get(n
						.getIndex());
				ShortestPathTreeElement spTEofV = shortestPathTree.get(v
						.getIndex());
				if (spTEofN.getDistanceToRoot() > distanceP.get(v.getIndex()) + 1) {
					if (!touched.contains(n.getIndex())) {
						qBFS.add(n);
						touched.add(n.getIndex());
						spTEofN.setDistanceToRoot(spTEofV.getDistanceToRoot() + 1);
						distanceP
								.put(n.getIndex(), distanceP.get(v.getIndex()));
						qLevel[spTEofN.getDistanceToRoot()].add(n);
						spTEofN.deleteAllParents();
						spTEofN.addParent(spTEofV);
						spTEofV.addChild(spTEofN);
					}

				} else if (spTEofN.getDistanceToRoot() == distanceP.get(v
						.getIndex()) + 1) {
					if (!touched.contains(n.getIndex())) {
						qBFS.add(n);
						touched.add(n.getIndex());
						qLevel[spTEofN.getDistanceToRoot()].add(n);
						if (!spTEofV.getChildren().contains(spTEofN)) {
							spTEofV.addChild(spTEofN);
						}
						if (!spTEofN.getParents().contains(spTEofV)) {
							spTEofN.addParent(spTEofV);
						}
					}

				}
				if (shortestPathsCount.containsKey(n.getIndex())) {
					shortestPathsCount.put(
							n.getIndex(),
							shortestPathsCount.get(n.getIndex())
									+ distanceP.get(v.getIndex()));
				} else {
					shortestPathsCount.put(
							n.getIndex(),
							spTEofN.getShortestPathCount()
									+ distanceP.get(v.getIndex()));
				}
			}

		}

		dependencyAccumulation(src, dst, shortestPathTree, qLevel,
				shortestPathsCount, touched);
	}

	private boolean adjacentLevelInsertion(UndirectedNode src,
			UndirectedNode dst,
			HashMap<Integer, ShortestPathTreeElement> shortestPathTree) {
=======
	private boolean adjacentLevelInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst,
			HashMap<UndirectedNode, ShortestPathTreeElement> shortestPathTree) {
		// Queue for BFS Search
>>>>>>> some stuff
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
<<<<<<< HEAD
<<<<<<< HEAD
			for (UndirectedEdge ed : v.getEdges()) {
				UndirectedNode n = ed.getNode1();
				if (n == v)
					n = ed.getNode2();
<<<<<<< HEAD

=======
>>>>>>> some stuff
				ShortestPathTreeElement spTEofN = shortestPathTree.get(n
						.getIndex());
				ShortestPathTreeElement spTEofV = shortestPathTree.get(v
						.getIndex());
<<<<<<< HEAD
=======

>>>>>>> some stuff
				if (spTEofN.getDistanceToRoot() == spTEofV.getDistanceToRoot() + 1) {
					if (!touched.contains(n.getIndex())) {
						qBFS.add(n);
						qLevel[spTEofN.getDistanceToRoot()].add(n);
						touched.add(n.getIndex());
						spTEofN.setDistanceToRoot(spTEofV.getDistanceToRoot() + 1);
						distanceP
								.put(n.getIndex(), distanceP.get(v.getIndex()));
<<<<<<< HEAD
					} else {
						distanceP.put(n.getIndex(), distanceP.get(n.getIndex())
								+ distanceP.get(v.getIndex()));
					}
					if (shortestPathsCount.containsKey(n.getIndex())) {
						shortestPathsCount.put(n.getIndex(),
								shortestPathsCount.get(n.getIndex())
										+ distanceP.get(v.getIndex()));
					} else {
=======
=======
			for (UndirectedEdge edge : v.getEdges()) {
=======
			for (IElement iEdges : v.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdges;
>>>>>>> generator for google
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
>>>>>>> some stuff
					} else {
						dP.put(w, dP.get(w) + dP.get(v));
					}
<<<<<<< HEAD
					if (shortestPathsCount.containsKey(n.getIndex())) {
						shortestPathsCount.put(n.getIndex(),
								shortestPathsCount.get(n.getIndex())
										+ distanceP.get(v.getIndex()));
					} else {
>>>>>>> some stuff
						shortestPathsCount.put(
								n.getIndex(),
								spTEofN.getShortestPathCount()
										+ distanceP.get(v.getIndex()));
					}
<<<<<<< HEAD
=======

>>>>>>> some stuff
=======
					spcUpdate.put(w, spcUpdate.get(w) + dP.get(v));
>>>>>>> some stuff
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
