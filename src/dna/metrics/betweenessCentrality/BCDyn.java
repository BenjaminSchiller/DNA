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

						removeEdgeManyToMany(node2TreeElement,
								node1TreeElement, shortestPathNodeN);

						// case 2: the lower node has only one parent
					} else if (node2TreeElement.getParents().size() == 1) {
						removeEdgeOneToMany(shortestPathNodeN,
								node2TreeElement, node1TreeElement);

					}
				}

			}
		}
		return true;
	}

	private void removeEdgeOneToMany(
			HashMap<Integer, ShortestPathTreeElement> shortestPathTree,
			ShortestPathTreeElement node2TreeElement,
			ShortestPathTreeElement node1TreeElement) {
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
		// TODO:Levels einrichten
		Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
		Map<Integer, Integer> distanceP = new HashMap<>();
		Map<Integer, Integer> shortestPathsCount = new HashMap<>();
		Set<Integer> touched = new HashSet<>();

		UndirectedGraph g = (UndirectedGraph) this.g;

		UndirectedNode dst = g.getNode(node2TreeElement.getNodeIndex());
		UndirectedNode src = g.getNode(node1TreeElement.getNodeIndex());

		for (int i = 0; i < qLevel.length; i++) {
			qLevel[i] = new LinkedList<UndirectedNode>();
		}

		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
			ShortestPathTreeElement spTEofV = shortestPathTree
					.get(v.getIndex());
			// all neighbours of v
			for (UndirectedEdge ed : v.getEdges()) {
				UndirectedNode n = ed.getNode1();
				if (n == v)
					n = ed.getNode2();
				ShortestPathTreeElement spTEofN = shortestPathTree.get(n
						.getIndex());

				if (shortestPathTree.get(n.getIndex()).getParents().size() > 1) {

					if (spTEofN.getDistanceToRoot() == spTEofV
							.getDistanceToRoot() + 1) {
						if (!touched.contains(n.getIndex())) {
							qBFS.add(n);
							qLevel[spTEofN.getDistanceToRoot()].add(n);
							touched.add(n.getIndex());
							spTEofN.setDistanceToRoot(spTEofV
									.getDistanceToRoot() + 1);
							distanceP.put(n.getIndex(),
									distanceP.get(v.getIndex()));
						} else {
							distanceP.put(
									n.getIndex(),
									distanceP.get(n.getIndex())
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

				} else if (g.getNode(n.getIndex()).getDegree() >= shortestPathTree
						.get(n.getIndex()).getParents().size()
						+ shortestPathTree.get(n.getIndex()).getChildren()
								.size()) {
					List<Integer> sameLevelNodes = new ArrayList<Integer>();
					for (UndirectedEdge edge : n.getEdges()) {
						// TODO:passt das???
						if (edge.getNode1().getIndex() != n.getIndex()) {
							if (shortestPathTree
									.get(edge.getNode1().getIndex())
									.getParents().size() > 1
									|| (touched.contains(edge.getNode1()
											.getIndex()) && !qBFS.contains(edge
											.getNode1()))) {
								sameLevelNodes.add(edge.getNode1().getIndex());
							}
						} else if (shortestPathTree
								.get(edge.getNode2().getIndex()).getParents()
								.size() > 1
								|| (touched
										.contains(edge.getNode2().getIndex()) && !qBFS
										.contains(edge.getNode2()))) {
							sameLevelNodes.add(edge.getNode2().getIndex());
						}
					}

					for (int i : sameLevelNodes) {
						ShortestPathTreeElement iTE = shortestPathTree.get(i);
						spTEofN.deleteAllParents();
						spTEofN.setDistanceToRoot(iTE.getDistanceToRoot());
						iTE.addChild(spTEofN);
						spTEofN.addParent(iTE);
						shortestPathsCount.put(n.getIndex(),
								shortestPathsCount.get(n.getIndex())
										+ shortestPathsCount.get(i));
					}
					qBFS.add(n);
					qLevel[spTEofN.getDistanceToRoot()].add(n);

				} else {
					// recomp: tree
				}
			}

		}

		dependencyAccumulation(src, dst, shortestPathTree, qLevel,
				shortestPathsCount, touched);
	}

	private void removeEdgeManyToMany(ShortestPathTreeElement node2TreeElement,
			ShortestPathTreeElement node1TreeElement,
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
		UndirectedGraph g = (UndirectedGraph) this.g;

		UndirectedNode dst = g.getNode(node2TreeElement.getNodeIndex());
		UndirectedNode src = g.getNode(node1TreeElement.getNodeIndex());

		qBFS.add(dst);
		distanceP.put(dst.getIndex(),
				node2TreeElement.getShortestPathCount() - 1);

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
