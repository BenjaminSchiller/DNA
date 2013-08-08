package dna.metrics.betweenessCentrality;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class BCDyn extends BetweenessCentrality {

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
		UndirectedNode node1 = e.getNode1();
		UndirectedNode node2 = e.getNode2();

		UndirectedGraph g = (UndirectedGraph) this.g;
		for (UndirectedNode n : g.getNodes()) {
			HashMap<Integer, ShortestPathTreeElement> shortestPathNodeN = shortestPathTrees
					.get(n.getIndex());
			if (shortestPathNodeN.containsKey(node1.getIndex())
					&& shortestPathNodeN.containsKey(node2.getIndex())) {
				ShortestPathTreeElement node1TreeElement = shortestPathNodeN
						.get(node1.getIndex());
				ShortestPathTreeElement node2TreeElement = shortestPathNodeN
						.get(node2.getIndex());

				// check if deleted edge is in the shortest path tree
				if (node2TreeElement.getDistanceToRoot() != node1TreeElement
						.getDistanceToRoot()) {

					// Find the above Tree Element
					if (node2TreeElement.getDistanceToRoot() > node1TreeElement
							.getDistanceToRoot()) {
						node1TreeElement = node2TreeElement;
						node2TreeElement = shortestPathNodeN.get(node1
								.getIndex());
					}

					// case 1: more than one parent => no shortest path tree
					// change
					if (node2TreeElement.getParents().size() > 1) {

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
	}

	private boolean applyAfterEdgeAddition(Update u) {
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode src = e.getNode1();
		UndirectedNode dst = e.getNode2();

		for (int i : this.shortestPathTrees.keySet()) {
			HashMap<Integer, ShortestPathTreeElement> temp = this.shortestPathTrees
					.get(i);
			if (Math.abs(temp.get(src.getIndex()).getDistanceToRoot()
					- temp.get(dst.getIndex()).getDistanceToRoot()) == 1) {
				adjacentLevelInsertion(src, dst, temp);
			} else if (Math.abs(temp.get(src.getIndex()).getDistanceToRoot()
					- temp.get(dst.getIndex()).getDistanceToRoot()) >= 1) {
				nonAdjacentLevelInsertion(src, dst, temp);
			} else if (temp.get(src.getIndex()).getDistanceToRoot() == -1
					|| temp.get(dst.getIndex()).getDistanceToRoot() == -1) {
				mergeOfComponentsInsertion(src, dst, temp);
			}
		}

		return true;
	}

	private void mergeOfComponentsInsertion(UndirectedNode src,
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

		ShortestPathTreeElement dstTE = new ShortestPathTreeElement(
				dst.getIndex());
		dstTE.setDistanceToRoot(shortestPathTree.get(src.getIndex())
				.getDistanceToRoot() + 1);
		dstTE.setShortestPathCount(shortestPathTree.get(src.getIndex())
				.getShortestPathCount());
		shortestPathTree.put(dst.getIndex(), dstTE);

		qBFS.add(dst);

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
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
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
		// TODO:Levels einrichten
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		Map<Integer, Integer> distanceP = new HashMap<>();
		Map<Integer, Integer> shortestPathsCount = new HashMap<>();
		Set<Integer> touched = new HashSet<>();

		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();

			// all neighbours of v
			for (UndirectedEdge ed : v.getEdges()) {
				UndirectedNode n = ed.getNode1();
				if (n == v)
					n = ed.getNode2();

				ShortestPathTreeElement spTEofN = shortestPathTree.get(n
						.getIndex());
				ShortestPathTreeElement spTEofV = shortestPathTree.get(v
						.getIndex());
				if (spTEofN.getDistanceToRoot() == spTEofV.getDistanceToRoot() + 1) {
					if (!touched.contains(n.getIndex())) {
						qBFS.add(n);
						qLevel[spTEofN.getDistanceToRoot()].add(n);
						touched.add(n.getIndex());
						spTEofN.setDistanceToRoot(spTEofV.getDistanceToRoot() + 1);
						distanceP
								.put(n.getIndex(), distanceP.get(v.getIndex()));
					} else {
						distanceP.put(n.getIndex(), distanceP.get(n.getIndex())
								+ distanceP.get(v.getIndex()));
					}
					if (shortestPathsCount.containsKey(n.getIndex())) {
						shortestPathsCount.put(n.getIndex(),
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
		}

		dependencyAccumulation(src, dst, shortestPathTree, qLevel,
				shortestPathsCount, touched);
		return true;
	}

	private void dependencyAccumulation(UndirectedNode src, UndirectedNode dst,
			HashMap<Integer, ShortestPathTreeElement> shortestPathTree,
			Queue<UndirectedNode>[] qLevel,
			Map<Integer, Integer> shortestPathsCount, Set<Integer> touched) {
		// Stage 3
		double[] temp = new double[this.g.getNodes().size()];
		for (int i = qLevel.length - 1; i >= 0; i--) {
			while (!qLevel[i].isEmpty()) {
				UndirectedNode w = qLevel[i].poll();
				for (ShortestPathTreeElement n : shortestPathTree.get(
						w.getIndex()).getParents()) {
					if (!touched.contains(n.getNodeIndex())) {
						qLevel[i - 1].add((UndirectedNode) this.g.getNode(n
								.getNodeIndex()));
						touched.add(n.getNodeIndex());

						temp[n.getNodeIndex()] = n.getAccumulativSum();
					}
					temp[n.getNodeIndex()] = temp[n.getNodeIndex()]
							+ shortestPathsCount.get(n.getNodeIndex())
							/ shortestPathsCount.get(w.getIndex())
							* (1 + temp[w.getIndex()]);

					if (touched.contains(n.getNodeIndex())
							&& (n.getNodeIndex() != src.getIndex() || w != dst)) {
						temp[n.getNodeIndex()] = temp[n.getNodeIndex()]
								- n.getAccumulativSum()
								/ shortestPathTree.get(w.getIndex())
										.getAccumulativSum()
								* (1 + temp[w.getIndex()]);
					}

					if (w.getIndex() != n.getNodeIndex()) {
						this.betweeneesCentralityScore
								.put(w.getIndex(),
										this.betweeneesCentralityScore.get(w
												.getIndex())
												+ temp[w.getIndex()]
												- shortestPathTree.get(
														w.getIndex())
														.getAccumulativSum());
					}

				}

			}
		}

		for (UndirectedNode v : (Collection<UndirectedNode>) this.g.getNodes()) {
			if (touched.contains(v.getIndex())) {
				ShortestPathTreeElement shortestPathTreeElement = shortestPathTree
						.get(v.getIndex());
				shortestPathTreeElement.setAccumulativSum(temp[v.getIndex()]);
				shortestPathTreeElement.setShortestPathCount(shortestPathsCount
						.get(v.getIndex()));
			}
		}
	}

	private boolean applyAfterNodeRemoval(Update u) {
		return false;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
		HashMap<Integer, ShortestPathTreeElement> temp = new HashMap<Integer, ShortestPathTreeElement>();
		temp.put(node.getIndex(), new ShortestPathTreeElement(node.getIndex()));
		this.shortestPathTrees.put(node.getIndex(), temp);
		return true;
	}
}
