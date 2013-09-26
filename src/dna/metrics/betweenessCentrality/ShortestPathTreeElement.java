package dna.metrics.betweenessCentrality;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
public class ShortestPathTreeElement {
	private List<ShortestPathTreeElement> parents;
	private List<ShortestPathTreeElement> children;
=======
import dna.graph.undirected.UndirectedNode;

public class ShortestPathTreeElement {
	private List<UndirectedNode> parents;
>>>>>>> some stuff

	private int distanceToRoot;
	private int shortestPathCount;
	private int nodeIndex;
	private double accumulativSum;

	public ShortestPathTreeElement(int nodeIndex) {
		super();
<<<<<<< HEAD
		this.parents = new ArrayList<>();
		this.children = new ArrayList<>();
		this.distanceToRoot = -1;
		this.shortestPathCount = -1;
		this.nodeIndex = nodeIndex;
		this.setAccumulativSum(0);
=======
		this.parents = new ArrayList<UndirectedNode>();
		this.distanceToRoot = Integer.MAX_VALUE;
		this.shortestPathCount = 0;
		this.nodeIndex = nodeIndex;
		this.accumulativSum = 0d;
>>>>>>> some stuff
	}

	public int getNodeIndex() {
		return nodeIndex;
	}

<<<<<<< HEAD
	public void addChild(ShortestPathTreeElement child) {
		parents.add(child);
	}

=======
>>>>>>> some stuff
	public void deleteAllParents() {
		parents.clear();
	}

<<<<<<< HEAD
	public void addParent(ShortestPathTreeElement parent) {
		parents.add(parent);
	}

	public void removeChild(ShortestPathTreeElement child) {
		parents.remove(child);
	}

	public void removeParent(ShortestPathTreeElement parent) {
		parents.remove(parent);
	}

	public boolean containsChild(ShortestPathTreeElement child) {
		return parents.contains(child);
	}

=======
	public void addParent(UndirectedNode parent) {
		parents.add(parent);
	}

	public void removeParent(UndirectedNode parent) {
		parents.remove(parent);
	}

>>>>>>> some stuff
	public void setShortestPathCount(int shortestPathCount) {
		this.shortestPathCount = shortestPathCount;
	}

	public void setDistanceToRoot(int distanceToRoot) {
		this.distanceToRoot = distanceToRoot;
	}

<<<<<<< HEAD
	public boolean containsParent(ShortestPathTreeElement parent) {
=======
	public boolean containsParent(UndirectedNode parent) {
>>>>>>> some stuff
		return parents.contains(parent);
	}

	public int getDistanceToRoot() {
		return distanceToRoot;
	}

	public int getShortestPathCount() {
		return shortestPathCount;
	}

<<<<<<< HEAD
	public List<ShortestPathTreeElement> getParents() {
		return parents;
	}

	public List<ShortestPathTreeElement> getChildren() {
		return children;
	}

=======
	public List<UndirectedNode> getParents() {
		return parents;
	}

>>>>>>> some stuff
	public double getAccumulativSum() {
		return accumulativSum;
	}

	public void setAccumulativSum(double accumulativSum) {
		this.accumulativSum = accumulativSum;
	}

}
<<<<<<< HEAD
=======
// Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
// // TODO:Levels einrichten
// Queue<UndirectedNode>[] qLevel = new Queue[this.g.getNodes().size()];
// Map<Integer, Integer> distanceP = new HashMap<>();
// Map<Integer, Integer> shortestPathsCount = new HashMap<>();
// Set<Integer> touched = new HashSet<>();
//
// UndirectedGraph g = (UndirectedGraph) this.g;
//
// UndirectedNode dst = g.getNode(node2TreeElement.getNodeIndex());
// UndirectedNode src = g.getNode(node1TreeElement.getNodeIndex());
//
// for (int i = 0; i < qLevel.length; i++) {
// qLevel[i] = new LinkedList<UndirectedNode>();
// }
//
// while (!qBFS.isEmpty()) {
// UndirectedNode v = qBFS.poll();
// ShortestPathTreeElement spTEofV = shortestPathTree
// .get(v.getIndex());
// // all neighbours of v
// for (UndirectedEdge ed : v.getEdges()) {
// UndirectedNode n = ed.getNode1();
// if (n == v)
// n = ed.getNode2();
// ShortestPathTreeElement spTEofN = shortestPathTree.get(n
// .getIndex());
//
// if (shortestPathTree.get(n.getIndex()).getParents().size() > 1) {
//
// if (spTEofN.getDistanceToRoot() == spTEofV
// .getDistanceToRoot() + 1) {
// if (!touched.contains(n.getIndex())) {
// qBFS.add(n);
// qLevel[spTEofN.getDistanceToRoot()].add(n);
// touched.add(n.getIndex());
// spTEofN.setDistanceToRoot(spTEofV
// .getDistanceToRoot() + 1);
// distanceP.put(n.getIndex(),
// distanceP.get(v.getIndex()));
// } else {
// distanceP.put(
// n.getIndex(),
// distanceP.get(n.getIndex())
// + distanceP.get(v.getIndex()));
// }
// if (shortestPathsCount.containsKey(n.getIndex())) {
// shortestPathsCount.put(n.getIndex(),
// shortestPathsCount.get(n.getIndex())
// + distanceP.get(v.getIndex()));
// } else {
// shortestPathsCount.put(
// n.getIndex(),
// spTEofN.getShortestPathCount()
// + distanceP.get(v.getIndex()));
// }
//
// }
//
// } else if (g.getNode(n.getIndex()).getDegree() >= shortestPathTree
// .get(n.getIndex()).getParents().size()) {
//
// // +shortestPathTree.get(n.getIndex()).getChildren()
// // .size()
// List<Integer> sameLevelNodes = new ArrayList<Integer>();
// for (UndirectedEdge edge : n.getEdges()) {
// // TODO:passt das???
// if (edge.getNode1().getIndex() != n.getIndex()) {
// if (shortestPathTree
// .get(edge.getNode1().getIndex())
// .getParents().size() > 1
// || (touched.contains(edge.getNode1()
// .getIndex()) && !qBFS.contains(edge
// .getNode1()))) {
// sameLevelNodes.add(edge.getNode1().getIndex());
// }
// } else if (shortestPathTree
// .get(edge.getNode2().getIndex()).getParents()
// .size() > 1
// || (touched
// .contains(edge.getNode2().getIndex()) && !qBFS
// .contains(edge.getNode2()))) {
// sameLevelNodes.add(edge.getNode2().getIndex());
// }
// }
//
// for (int i : sameLevelNodes) {
// ShortestPathTreeElement iTE = shortestPathTree.get(i);
// spTEofN.deleteAllParents();
// spTEofN.setDistanceToRoot(iTE.getDistanceToRoot());
// // iTE.addChild(spTEofN);
// spTEofN.addParent(iTE);
// shortestPathsCount.put(n.getIndex(),
// shortestPathsCount.get(n.getIndex())
// + shortestPathsCount.get(i));
// }
// qBFS.add(n);
// qLevel[spTEofN.getDistanceToRoot()].add(n);
//
// } else {
// // recomp: tree
// }
// }
//
// }
//
// // dependencyAccumulation(src, dst, shortestPathTree, qLevel,
// // shortestPathsCount, touched);
>>>>>>> some stuff
