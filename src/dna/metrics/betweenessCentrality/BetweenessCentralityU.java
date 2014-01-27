package dna.metrics.betweenessCentrality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class BetweenessCentralityU extends BetweenessCentrality {

	Queue<Node>[] qLevel;
	Queue<Node>[] qALevel;
	HashMap<Node, Long> visited;
	long counter;

	public BetweenessCentralityU() {
		super("BetweenessCentralityU", ApplicationType.AfterUpdate);
	}

	@Override
	public void init_() {
		super.init_();
		int length = 1000;
		qALevel = new LinkedList[length];
		qLevel = new LinkedList[length];
		visited = new HashMap<Node, Long>();
		counter = 0;
		for (int i = 0; i < qALevel.length; i++) {
			qALevel[i] = new LinkedList<Node>();
			qLevel[i] = new LinkedList<Node>();
		}
		for (IElement ie : g.getNodes()) {
			visited.put((Node) ie, counter);
		}
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
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
			DirectedNode src = e.getSrc();
			DirectedNode dst = e.getDst();

			for (IElement iE : g.getNodes()) {
				DirectedNode root = (DirectedNode) iE;

				HashMap<Node, Integer> d = distances.get(root);
				HashMap<Node, HashSet<Node>> p = parents.get(root);

				if (!p.get(dst).contains(src)
						|| d.get(src).equals(Integer.MAX_VALUE)
						|| d.get(dst).equals(Integer.MAX_VALUE)) {
					continue;
				}

				// case 1: more than one parent => no shortest path tree
				// change
				if (p.get(dst).size() > 1) {
					removeEdgeManyToMany(root, src, dst);
					// case 2: the lower node has only one parent
				} else if (p.get(dst).size() == 1) {
					removeEdgeOneToMany(root, src, dst);
				}
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
			UndirectedNode n1 = e.getNode1();
			UndirectedNode n2 = e.getNode2();

			for (IElement iE : g.getNodes()) {
				UndirectedNode root = (UndirectedNode) iE;

				HashMap<Node, Integer> d = distances.get(root);
				HashMap<Node, HashSet<Node>> p = parents.get(root);

				// Find the above Tree Element
				if (d.get(n1) > d.get(n2)) {
					n1 = n2;
					n2 = e.getDifferingNode(n1);
				}

				if (!p.get(n2).contains(n1)
						|| d.get(n1).equals(Integer.MAX_VALUE)
						|| d.get(n2).equals(Integer.MAX_VALUE)) {
					continue;
				}

				// case 1: more than one parent => no shortest path tree
				// change
				if (p.get(n2).size() > 1) {

					removeEdgeManyToMany(root, n1, n2);
					// case 2: the lower node has only one parent
				} else if (p.get(n2).size() == 1) {
					// recomp(g, root);
					removeEdgeOneToMany(root, n1, n2);
				}
			}
		}

		return true;
	}

	private boolean removeEdgeOneToMany(Node root, Node src, Node dst) {

		counter++;

		HashMap<Node, Integer> d = distances.get(root);
		HashMap<Node, HashSet<Node>> p = parents.get(root);
		HashMap<Node, Double> oldSums = accSums.get(root);
		HashMap<Node, Integer> oldSpc = spcs.get(root);

		// Queues and data structure for tree change
		HashSet<Node> uncertain = new HashSet<Node>();

		// data structure for Updates
		HashMap<Node, Integer> newSpc = new HashMap<Node, Integer>(oldSpc);
		HashMap<Node, Double> newASums = new HashMap<Node, Double>();
		HashMap<Node, HashSet<Node>> newParents = new HashMap<Node, HashSet<Node>>();

		// set data structure for dst Node
		qLevel[d.get(dst)].add(dst);
		uncertain.add(dst);
		newASums.put(dst, 0d);
		newParents.put(dst, new HashSet<Node>());
		visited.put(dst, counter);
		int max = d.get(dst);
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {

			for (int i = max; i < qLevel.length && i < max + 1; i++) {
				while (!qLevel[i].isEmpty()) {

					DirectedNode w = (DirectedNode) qLevel[i].poll();

					int dist = Integer.MAX_VALUE;
					ArrayList<DirectedNode> min = new ArrayList<DirectedNode>();
					for (IElement iEdges : w.getIncomingEdges()) {
						DirectedEdge ed = (DirectedEdge) iEdges;
						DirectedNode z = ed.getDifferingNode(w);

						if (d.get(z) < dist) {
							min.clear();
							min.add(z);
							dist = d.get(z);
							continue;
						}
						if (d.get(z).equals(dist)) {
							min.add(z);
							continue;
						}
					}

					for (IElement iEdges : w.getOutgoingEdges()) {
						DirectedEdge ed = (DirectedEdge) iEdges;
						DirectedNode z = ed.getDifferingNode(w);
						if (d.get(z).equals(d.get(w) + 1)
								&& Math.abs(visited.get(z)) < counter) {
							qLevel[i + 1].add(z);
							newASums.put(z, 0d);
							newParents.put(z, new HashSet<Node>());
							max = Math.max(max, i + 1);
							uncertain.add(z);
							visited.put(z, counter);
						}
					}

					// if their is no connection to the three, remove node form
					// data set
					if (dist == Integer.MAX_VALUE || dist >= qLevel.length - 1) {
						d.put(w, Integer.MAX_VALUE);
						newSpc.put(w, 0);
						newParents.get(w).clear();
						qALevel[qALevel.length - 1].add(w);
						uncertain.remove(w);
						continue;
					}

					// connect to the highest uncertain node
					boolean found = false;
					newSpc.put(w, 0);
					newParents.get(w).clear();
					for (DirectedNode mNode : min) {

						if ((!uncertain.contains(mNode))
								&& d.get(mNode).intValue() + 1 == i) {
							uncertain.remove(w);
							newSpc.put(w, newSpc.get(w) + newSpc.get(mNode));
							found = true;
							newParents.get(w).add(mNode);
						}
						d.put(w, d.get(mNode).intValue() + 1);

					}
					// else connect to another node
					if (!found) {
						qLevel[d.get(w)].add(w);
						max = Math.max(max, d.get(w));
					} else {
						qALevel[d.get(w)].add(w);
					}
				}
			}

		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {

			for (int i = max; i < qLevel.length && i < max + 1; i++) {
				while (!qLevel[i].isEmpty()) {

					UndirectedNode w = (UndirectedNode) qLevel[i].poll();
					int dist = Integer.MAX_VALUE;
					ArrayList<UndirectedNode> min = new ArrayList<UndirectedNode>();
					for (IElement iEdges : w.getEdges()) {
						UndirectedEdge ed = (UndirectedEdge) iEdges;
						UndirectedNode z = ed.getDifferingNode(w);

						if (d.get(z).equals(d.get(w) + 1)
								&& Math.abs(visited.get(z)) < counter) {
							qLevel[i + 1].add(z);
							newASums.put(z, 0d);
							newParents.put(z, new HashSet<Node>());
							uncertain.add(z);
							visited.put(z, counter);
							max = Math.max(max, i + 1);
						}
						if (d.get(z) < dist) {
							min.clear();
							min.add(z);
							dist = d.get(z);
							continue;
						}
						if (d.get(z).equals(dist)) {
							min.add(z);
							continue;
						}
					}

					// if their is no connection to the three, remove node form
					// data set
					if (dist == Integer.MAX_VALUE || dist >= qALevel.length - 1) {
						d.put(w, Integer.MAX_VALUE);
						newSpc.put(w, 0);
						newParents.get(w).clear();
						qALevel[qALevel.length - 1].add(w);
						uncertain.remove(w);
						continue;
					}

					// connect to the highest uncertain node
					boolean found = false;
					newSpc.put(w, 0);
					newParents.get(w).clear();
					for (UndirectedNode mNode : min) {
						if ((!uncertain.contains(mNode))
								&& d.get(mNode).intValue() + 1 == i) {
							uncertain.remove(w);
							newSpc.put(w, newSpc.get(w) + newSpc.get(mNode));
							found = true;
							newParents.get(w).add(mNode);
						}
						d.put(w, d.get(mNode).intValue() + 1);

					}
					// else connect to another node
					if (!found) {
						qLevel[d.get(w)].add(w);
						max = Math.max(max, d.get(w));
					} else {
						qALevel[d.get(w)].add(w);
					}
				}
			}
		}
		// Stage 3
		for (int i = qALevel.length - 1; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				Node w = qALevel[i].poll();

				for (Node v : p.get(w)) {
					if (!newParents.get(w).contains(v)) {

						if (Math.abs(visited.get(v)) < counter) {
							qALevel[d.get(v)].add(v);
							visited.put(v, -counter);
							newASums.put(v, oldSums.get(v));
							newParents.put(v, p.get(v));
						}
						if (visited.get(v).equals(-counter)) {
							double temp = newASums.get(v) - oldSpc.get(v)
									* (1 + oldSums.get(w)) / oldSpc.get(w);
							newASums.put(v, temp);
						}
					}
				}

				for (Node v : newParents.get(w)) {
					if (d.get(v).equals(d.get(w) - 1)) {
						if (Math.abs(visited.get(v)) < counter) {
							qALevel[i - 1].add(v);
							visited.put(v, -counter);
							newASums.put(v, oldSums.get(v));
							newParents.put(v, p.get(v));
						}

						double t = newASums.get(v) + newSpc.get(v)
								* (1 + newASums.get(w)) / newSpc.get(w);

						newASums.put(v, t);

						if (visited.get(v).equals(-counter)
								&& p.get(w).contains(v)) {
							double temp = newASums.get(v) - oldSpc.get(v)
									* (1 + oldSums.get(w)) / oldSpc.get(w);
							newASums.put(v, temp);
						}
					}
				}

				if (!w.equals(root)) {
					double currentScore = this.bCC.getValue(w.getIndex());
					// this.bC.get(w);
					// this.bC.put(w,
					// currentScore + newASums.get(w) - oldSums.get(w));
					this.bCSum = this.bCSum + newASums.get(w) - oldSums.get(w);
					this.bCC.setValue(w.getIndex(),
							currentScore + newASums.get(w) - oldSums.get(w));
				}
			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newASums);
		p.putAll(newParents);

		return true;
	}

	private boolean removeEdgeManyToMany(Node root, Node src, Node dst) {
		counter++;

		HashMap<Node, Integer> d = distances.get(root);
		HashMap<Node, HashSet<Node>> p = parents.get(root);
		HashMap<Node, Double> oldSums = accSums.get(root);
		HashMap<Node, Integer> oldSpc = spcs.get(root);

		// Queue for BFS Search
		Queue<Node> qBFS = new LinkedList<Node>();

		// data structure for Updates
		HashMap<Node, Integer> dP = new HashMap<Node, Integer>();
		HashMap<Node, Integer> newSpc = new HashMap<Node, Integer>(oldSpc);
		HashMap<Node, Double> newASums = new HashMap<Node, Double>();

		// setup changes for dst node
		qBFS.add(dst);
		qALevel[d.get(dst)].add(dst);
		visited.put(dst, counter);
		dP.put(dst, oldSpc.get(src));
		newASums.put(dst, 0d);
		newSpc.put(dst, newSpc.get(dst) - dP.get(dst));
		int maxHeight = d.get(dst);

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			// Stage 2
			while (!qBFS.isEmpty()) {
				DirectedNode v = (DirectedNode) qBFS.poll();
				// all neighbours of v
				for (IElement iEdge : v.getOutgoingEdges()) {
					DirectedEdge edge = (DirectedEdge) iEdge;
					DirectedNode w = edge.getDifferingNode(v);

					if (d.get(w).equals(d.get(v) + 1)) {
						if (Math.abs(visited.get(w)) < counter) {
							qBFS.add(w);
							newASums.put(w, 0d);
							qALevel[d.get(w)].add(w);
							maxHeight = Math.max(maxHeight, d.get(w));
							visited.put(w, counter);
							dP.put(w, dP.get(v));
						} else {
							dP.put(w, dP.get(w) + dP.get(v));
						}
						newSpc.put(w, newSpc.get(w) - dP.get(v));
					}
				}
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			// Stage 2
			while (!qBFS.isEmpty()) {
				UndirectedNode v = (UndirectedNode) qBFS.poll();
				// all neighbours of v
				for (IElement iEdge : v.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) iEdge;
					UndirectedNode w = edge.getDifferingNode(v);

					if (d.get(w).equals(d.get(v) + 1)) {
						if (Math.abs(visited.get(w)) < counter) {
							qBFS.add(w);
							newASums.put(w, 0d);
							qALevel[d.get(w)].add(w);
							maxHeight = Math.max(maxHeight, d.get(w));
							visited.put(w, counter);
							dP.put(w, dP.get(v));
						} else {
							dP.put(w, dP.get(w) + dP.get(v));
						}
						newSpc.put(w, newSpc.get(w) - dP.get(v));
					}
				}
			}

		}

		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				Node w = qALevel[i].poll();
				for (Node v : p.get(w)) {
					if (Math.abs(visited.get(v)) < counter) {
						qALevel[i - 1].add(v);
						visited.put(v, -counter);
						newASums.put(v, oldSums.get(v));
					}

					if (!(v == src && w == dst)) {
						double t = newASums.get(v) + newSpc.get(v)
								* (1 + newASums.get(w)) / newSpc.get(w);
						newASums.put(v, t);
					}
					if (visited.get(v).equals(-counter)) {
						double temp = newASums.get(v) - oldSpc.get(v)
								* (1 + oldSums.get(w)) / oldSpc.get(w);
						newASums.put(v, temp);
					}
				}
				if (!w.equals(root)) {
					double currentScore = this.bCC.getValue(w.getIndex());
					// this.bC.get(w);
					// this.bC.put(w,
					// currentScore + newASums.get(w) - oldSums.get(w));
					this.bCSum = this.bCSum + newASums.get(w) - oldSums.get(w);
					this.bCC.setValue(w.getIndex(),
							currentScore + newASums.get(w) - oldSums.get(w));
				}

			}
		}

		p.get(dst).remove(src);
		spcs.put(root, newSpc);
		oldSums.putAll(newASums);
		return true;

	}

	private boolean applyAfterEdgeAddition(Update u) {

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
			DirectedNode src = e.getSrc();
			DirectedNode dst = e.getDst();

			for (IElement iE : g.getNodes()) {
				DirectedNode root = (DirectedNode) iE;
				HashMap<Node, Integer> d = this.distances.get(root);

				if (d.get(src).equals(Integer.MAX_VALUE)
						|| d.get(src).equals(d.get(dst))
						|| d.get(src).intValue() > d.get(dst).intValue()) {
					// no change to shortes path tree
					continue;
				}
				if (d.get(dst).equals(Integer.MAX_VALUE)) {
					// to components merge therefore new Nodes add to shortest
					// path
					// tree
					nonAdjacentLevelInsertion(root, src, dst);
					continue;
				}
				if (d.get(src).intValue() + 1 == d.get(dst).intValue()) {
					// the added edge connects nodes in adjacent Levels
					// therefore
					// only the new tree edge is added

					adjacentLevelInsertion(root, src, dst);
					continue;
				}
				if (d.get(src).intValue() + 1 < d.get(dst).intValue()) {
					// the added edge connects nodes in non adjacent Levels
					// therefore all nodes in the subtree need to be checked if
					// they
					// move up in the shortest path tree
					nonAdjacentLevelInsertion(root, src, dst);
					continue;

				}
				System.err.println("err for edge insertion");
				return false;

			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
			UndirectedNode n1 = e.getNode1();
			UndirectedNode n2 = e.getNode2();
			for (IElement iE : g.getNodes()) {
				UndirectedNode root = (UndirectedNode) iE;

				HashMap<Node, Integer> d = distances.get(root);

				if (d.get(n1) > d.get(n2)) {
					n2 = n1;
					n1 = e.getDifferingNode(n2);
				}

				if ((d.get(n1).equals(Integer.MAX_VALUE) && d.get(n2).equals(
						Integer.MAX_VALUE))
						|| d.get(n1).equals(d.get(n2))) {
					// no change to shortes path tree
					continue;
				}
				if (d.get(n2).equals(Integer.MAX_VALUE)) {
					// to components merge therefore new Nodes add to shortest
					// path
					// tree
					mergeOfComponentsInsertion(root, n1, n2);
					continue;
				}
				if (d.get(n1).intValue() + 1 == d.get(n2).intValue()) {
					// the added edge connects nodes in adjacent Levels
					// therefore
					// only the new tree edge is added
					adjacentLevelInsertion(root, n1, n2);
					continue;
				}
				if (d.get(n1).intValue() + 1 < d.get(n2).intValue()) {
					// the added edge connects nodes in non adjacent Levels
					// therefore all nodes in the subtree need to be checked if
					// they
					// move up in the shortest path tree
					nonAdjacentLevelInsertion(root, n1, n2);
					continue;

				}

				System.err.println(" shit" + d.get(n1) + " " + d.get(n2));
				return false;

			}
		}
		return true;
	}

	private void nonAdjacentLevelInsertion(DirectedNode root, DirectedNode src,
			DirectedNode dst) {
		counter++;

		HashMap<Node, Integer> d = distances.get(root);
		HashMap<Node, HashSet<Node>> p = parents.get(root);
		HashMap<Node, Double> oldSums = accSums.get(root);
		HashMap<Node, Integer> oldSpc = spcs.get(root);

		// Data Structure for BFS Search
		Queue<Node> qBFS = new LinkedList<Node>();

		// data structure for Updates
		HashMap<Node, Integer> newSpc = new HashMap<Node, Integer>(oldSpc);
		HashMap<Node, Double> newASums = new HashMap<Node, Double>();
		HashMap<Node, HashSet<Node>> newParents = new HashMap<Node, HashSet<Node>>();

		// set Up data Structure for the lower node

		qBFS.add(dst);
		visited.put(dst, counter);
		newSpc.put(dst, newSpc.get(src));
		d.put(dst, d.get(src) + 1);
		qALevel[d.get(dst)].add(dst);
		newASums.put(dst, 0d);
		newParents.put(dst, new HashSet<Node>());

		int maxHeight = d.get(dst);
		HashSet<DirectedNode> bal = new HashSet<>();

		// Stage 2
		while (!qBFS.isEmpty()) {
			DirectedNode v = (DirectedNode) qBFS.poll();
			newSpc.put(v, 0);

			// all neighbours of v
			for (IElement iEdge : v.getOutgoingEdges()) {
				DirectedEdge ed = (DirectedEdge) iEdge;
				DirectedNode n = ed.getDst();

				// Lower Node moves up
				if (d.get(n).intValue() > d.get(v).intValue() + 1) {
					d.put(n, d.get(v).intValue() + 1);
					qBFS.add(n);
					qALevel[d.get(n)].add(n);
					newASums.put(n, 0d);
					newParents.put(n, new HashSet<Node>());
					bal.remove(n);
					visited.put(n, counter);
					maxHeight = Math.max(maxHeight, d.get(n));
					continue;
				}

				// lower Node get a new Parent
				if (d.get(n).intValue() == d.get(v).intValue() + 1) {
					if (!visited.get(n).equals(counter)) {
						visited.put(n, counter);
						qALevel[d.get(n)].add(n);
						newParents.put(n, new HashSet<Node>());
						qBFS.add(n);
						newASums.put(n, 0d);
						bal.remove(n);
						maxHeight = Math.max(maxHeight, d.get(n));
					}
					continue;
				}
			}

			for (IElement iEdge : v.getIncomingEdges()) {
				DirectedEdge ed = (DirectedEdge) iEdge;
				DirectedNode n = ed.getSrc();

				boolean b1 = p.get(v).contains(n);
				boolean b2 = Math.abs(visited.get(n)) < counter;
				boolean b3 = d.get(n).intValue() >= d.get(v).intValue();
				if (b1 && b2 && b3) {
					visited.put(n, -counter);
					bal.add(n);
				}

				if (d.get(n).intValue() + 1 == d.get(v).intValue()) {
					newSpc.put(v, newSpc.get(v).intValue()
							+ newSpc.get(n).intValue());
					newParents.get(v).add(n);

				}
			}
		}
		for (DirectedNode directedNode : bal) {
			if (visited.get(directedNode).equals(-counter)) {
				newASums.put(directedNode, oldSums.get(directedNode));
				newParents.put(directedNode, p.get(directedNode));
				qALevel[d.get(directedNode)].add(directedNode);
				maxHeight = Math.max(maxHeight, d.get(directedNode));
			}
		}

		// Stage 3
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				DirectedNode w = (DirectedNode) qALevel[i].poll();

				if (visited.get(w).equals(-counter)) {
					for (IElement ie : w.getOutgoingEdges()) {
						DirectedNode v = ((DirectedEdge) ie).getDst();
						if (p.get(v).contains(w)
								&& Math.abs(visited.get(v)) == counter) {
							double temp = newASums.get(w) - oldSpc.get(w)
									* (1 + oldSums.get(v)) / oldSpc.get(v);
							newASums.put(w, temp);

						}
					}
				}
				for (Node v : newParents.get(w)) {

					if (d.get(v).intValue() == d.get(w).intValue() - 1) {

						if (Math.abs(visited.get(v)) < counter) {
							newASums.put(v, oldSums.get(v));
							newParents.put(v, p.get(v));
							qALevel[d.get(v)].add(v);
							visited.put(v, -counter);
						}

						double t1 = newASums.get(v) + newSpc.get(v)
								* (1 + newASums.get(w)) / newSpc.get(w);
						newASums.put(v, t1);
					}
				}
				if (!w.equals(root)) {
					double currentScore = this.bCC.getValue(w.getIndex());
					// this.bC.get(w);
					// this.bC.put(w,
					// currentScore + newASums.get(w) - oldSums.get(w));
					this.bCSum = this.bCSum + newASums.get(w) - oldSums.get(w);
					this.bCC.setValue(w.getIndex(),
							currentScore + newASums.get(w) - oldSums.get(w));
				}

			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newASums);
		p.putAll(newParents);

	}

	private void mergeOfComponentsInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst) {

		counter++;

		HashMap<Node, Integer> d = distances.get(root);
		HashMap<Node, HashSet<Node>> p = parents.get(root);
		HashMap<Node, Double> oldSums = accSums.get(root);
		HashMap<Node, Integer> oldSpc = spcs.get(root);

		// Queue for the BFS search down the shortes Path tree
		Queue<Node> qBFS = new LinkedList<Node>();

		// data structure for Updates
		HashMap<Node, Integer> newSpc = new HashMap<Node, Integer>(oldSpc);
		HashMap<Node, Double> newASums = new HashMap<Node, Double>();

		// new TreeElement and the current Values for the Tree Position
		d.put(dst, d.get(src) + 1);
		newSpc.put(dst, newSpc.get(src));
		newASums.put(dst, 0d);
		p.get(dst).add(src);
		visited.put(dst, counter);
		int maxHeight = 0;

		qBFS.add(dst);
		// stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = (UndirectedNode) qBFS.poll();
			qALevel[d.get(v)].add(v);
			maxHeight = Math.max(maxHeight, d.get(v));
			for (IElement iEdge : v.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iEdge;
				UndirectedNode n = ed.getDifferingNode(v);
				if (Math.abs(visited.get(n)) < counter && n != src
						&& d.get(n).equals(Integer.MAX_VALUE)) {
					qBFS.add(n);
					visited.put(n, counter);
					newASums.put(n, 0d);
					d.put(n, d.get(v) + 1);
				}
				if (d.get(n).intValue() == d.get(v).intValue() + 1) {
					newSpc.put(n, newSpc.get(n) + newSpc.get(v));
					p.get(n).add(v);
				}
			}
		}

		// Stage 3
		// search the shortest path tree from leaves to root
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				UndirectedNode w = (UndirectedNode) qALevel[i].poll();

				for (Node v : p.get(w)) {
					if (Math.abs(visited.get(v)) < counter) {
						qALevel[i - 1].add(v);
						visited.put(v, -counter);
						newASums.put(v, oldSums.get(v));
					}
					double t = newSpc.get(v) * (1 + newASums.get(w))
							/ newSpc.get(w);
					newASums.put(v, newASums.get(v) + t);

					if (visited.get(v).equals(-counter)
							&& (v != src || w != dst)) {
						double temp = newASums.get(v) - oldSpc.get(v)
								* (1 + oldSums.get(w)) / oldSpc.get(w);
						newASums.put(v, temp);
					}
				}
				if (!w.equals(root)) {
					double currentScore = this.bCC.getValue(w.getIndex());
					// this.bC.get(w);
					// this.bC.put(w,
					// currentScore + newASums.get(w) - oldSums.get(w));
					this.bCSum = this.bCSum + newASums.get(w) - oldSums.get(w);
					this.bCC.setValue(w.getIndex(),
							currentScore + newASums.get(w) - oldSums.get(w));
				}

			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newASums);

	}

	private void nonAdjacentLevelInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst) {

		counter++;
		// old values
		HashMap<Node, Integer> d = distances.get(root);
		HashMap<Node, HashSet<Node>> p = parents.get(root);
		HashMap<Node, Double> oldSums = accSums.get(root);
		HashMap<Node, Integer> oldSpc = spcs.get(root);

		// Data Structure for BFS Search
		Queue<Node> qBFS = new LinkedList<Node>();

		// data structure for Updates
		HashMap<Node, Integer> newSpc = new HashMap<Node, Integer>(oldSpc);
		HashMap<Node, Double> newASums = new HashMap<Node, Double>();
		HashMap<Node, HashSet<Node>> newParents = new HashMap<Node, HashSet<Node>>();

		// set Up data Structure for the lower node

		qBFS.add(dst);
		visited.put(dst, counter);
		newSpc.put(dst, newSpc.get(src));
		d.put(dst, d.get(src) + 1);
		qALevel[d.get(dst)].add(dst);
		newASums.put(dst, 0d);
		newParents.put(dst, new HashSet<Node>());

		int maxHeight = d.get(dst);

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = (UndirectedNode) qBFS.poll();
			newSpc.put(v, 0);

			// all neighbours of v
			for (IElement iEdge : v.getEdges()) {
				UndirectedEdge ed = (UndirectedEdge) iEdge;
				UndirectedNode n = ed.getDifferingNode(v);

				// Lower Node moves up
				if (d.get(n).intValue() > d.get(v).intValue() + 1) {
					d.put(n, d.get(v).intValue() + 1);
					qBFS.add(n);
					qALevel[d.get(n)].add(n);
					newASums.put(n, 0d);
					newParents.put(n, new HashSet<Node>());
					visited.put(n, counter);
					maxHeight = Math.max(maxHeight, d.get(n));
					continue;
				}

				// lower Node get a new Parent
				if (d.get(n).intValue() == d.get(v).intValue() + 1) {
					if (Math.abs(visited.get(n)) < counter) {
						visited.put(n, counter);
						qALevel[d.get(n)].add(n);
						newParents.put(n, new HashSet<Node>());
						qBFS.add(n);
						newASums.put(n, 0d);
						maxHeight = Math.max(maxHeight, d.get(n));
					}
					continue;
				}

				if (d.get(n).intValue() < d.get(v).intValue()) {
					newSpc.put(v, newSpc.get(v) + newSpc.get(n));
					if (!newParents.get(v).contains(n)) {
						newParents.get(v).add(n);
					}
				}
			}
		}

		// Stage 3
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				UndirectedNode w = (UndirectedNode) qALevel[i].poll();
				for (Node v : p.get(w)) {
					if (!newParents.get(w).contains(v)) {
						if (Math.abs(visited.get(v)) < counter) {
							qALevel[d.get(v)].add(v);
							visited.put(v, -counter);
							newASums.put(v, oldSums.get(v));
							newParents.put(v, p.get(v));
						}
						if (visited.get(v).equals(-counter)) {
							double temp = newASums.get(v) - oldSpc.get(v)
									* (1 + oldSums.get(w)) / oldSpc.get(w);
							newASums.put(v, temp);
						}
					}
				}

				for (Node v : newParents.get(w)) {

					if (Math.abs(visited.get(v)) < counter) {
						qALevel[i - 1].add(v);
						visited.put(v, -counter);
						newASums.put(v, oldSums.get(v));
						newParents.put(v, p.get(v));
					}
					double t = newASums.get(v) + newSpc.get(v)
							* (1 + newASums.get(w)) / newSpc.get(w);
					newASums.put(v, t);

					if (visited.get(v).equals(-counter)
							&& (dst != w || src != v)) {
						double temp = newASums.get(v) - oldSpc.get(v)
								* (1 + oldSums.get(w)) / oldSpc.get(w);
						newASums.put(v, temp);
					}

				}
				if (!w.equals(root)) {
					double currentScore = this.bCC.getValue(w.getIndex());
					// this.bC.get(w);
					// this.bC.put(w,
					// currentScore + newASums.get(w) - oldSums.get(w));
					this.bCSum = this.bCSum + newASums.get(w) - oldSums.get(w);
					this.bCC.setValue(w.getIndex(),
							currentScore + newASums.get(w) - oldSums.get(w));
				}
			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newASums);
		p.putAll(newParents);
	}

	private boolean adjacentLevelInsertion(Node root, Node src, Node dst) {
		// Queue for BFS Search
		Queue<Node> qBFS = new LinkedList<Node>();
		counter++;

		// old values
		HashMap<Node, Integer> d = distances.get(root);
		HashMap<Node, HashSet<Node>> p = parents.get(root);
		HashMap<Node, Double> oldSums = accSums.get(root);
		HashMap<Node, Integer> oldSpc = spcs.get(root);

		// data structure for Updates
		HashMap<Node, Integer> dP = new HashMap<Node, Integer>();
		HashMap<Node, Integer> newSpc = new HashMap<Node, Integer>(oldSpc);
		HashMap<Node, Double> newSums = new HashMap<Node, Double>();

		// setup changes for dst node
		qBFS.add(dst);
		qALevel[d.get(dst)].add(dst);
		visited.put(dst, counter);
		newSums.put(dst, 0d);
		dP.put(dst, oldSpc.get(src));
		newSpc.put(dst, newSpc.get(dst) + dP.get(dst));
		p.get(dst).add(src);
		int maxHeight = d.get(dst);

		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			// Stage 2
			while (!qBFS.isEmpty()) {
				DirectedNode v = (DirectedNode) qBFS.poll();

				// all neighbours of v
				for (IElement iEdges : v.getOutgoingEdges()) {
					DirectedEdge edge = (DirectedEdge) iEdges;
					DirectedNode w = edge.getDst();

					if (d.get(w).equals(d.get(v).intValue() + 1)) {
						if (Math.abs(visited.get(w)) < counter) {
							qBFS.add(w);
							qALevel[d.get(w)].add(w);
							newSums.put(w, 0d);
							maxHeight = Math.max(maxHeight, d.get(w));
							visited.put(w, counter);
							dP.put(w, dP.get(v));
						} else {
							dP.put(w, dP.get(w) + dP.get(v));
						}
						newSpc.put(w, newSpc.get(w) + dP.get(v));
					}
				}
			}
		} else if (UndirectedNode.class.isAssignableFrom(this.g
				.getGraphDatastructures().getNodeType())) {
			// Stage 2
			while (!qBFS.isEmpty()) {
				UndirectedNode v = (UndirectedNode) qBFS.poll();

				// all neighbours of v
				for (IElement iEdges : v.getEdges()) {
					UndirectedEdge edge = (UndirectedEdge) iEdges;
					UndirectedNode w = edge.getDifferingNode(v);

					if (d.get(w).equals(d.get(v).intValue() + 1)) {
						if (Math.abs(visited.get(w)) < counter) {
							qBFS.add(w);
							qALevel[d.get(w)].add(w);
							maxHeight = Math.max(maxHeight, d.get(w));
							newSums.put(w, 0d);
							visited.put(w, counter);
							dP.put(w, dP.get(v));
						} else {
							dP.put(w, dP.get(w) + dP.get(v));
						}
						newSpc.put(w, newSpc.get(w) + dP.get(v));
					}
				}
			}
		}
		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				Node w = (Node) qALevel[i].poll();

				for (Node v : p.get(w)) {
					if (Math.abs(visited.get(v)) < counter) {
						qALevel[i - 1].add(v);
						visited.put(v, -counter);
						newSums.put(v, oldSums.get(v));
					}
					double t = newSums.get(v) + newSpc.get(v)
							* (1 + newSums.get(w)) / newSpc.get(w);
					newSums.put(v, t);
					if (visited.get(v).equals(-counter)
							&& (v != src || w != dst)) {
						double temp = newSums.get(v) - oldSpc.get(v)
								* (1 + oldSums.get(w)) / oldSpc.get(w);
						newSums.put(v, temp);
					}
				}
				if (!w.equals(root)) {
					double currentScore = this.bCC.getValue(w.getIndex());
					// this.bC.get(w);
					// this.bC.put(w,
					// currentScore + newSums.get(w) - oldSums.get(w));
					this.bCSum = this.bCSum + newSums.get(w) - oldSums.get(w);
					this.bCC.setValue(w.getIndex(),
							currentScore + newSums.get(w) - oldSums.get(w));
				}

			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newSums);
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		Node node = (Node) ((NodeRemoval) u).getNode();

		g.addNode(node);
		HashSet<Edge> bla = new HashSet<>();
		for (IElement ie : node.getEdges()) {
			Edge e = (Edge) ie;
			e.connectToNodes();
			bla.add(e);
		}
		for (Edge e : bla) {
			e.disconnectFromNodes();
			applyAfterEdgeRemoval(new EdgeRemoval(e));
		}

		for (Node n : this.accSums.get(node).keySet()) {
			// this.bC.put(n, this.bC.get(n) - this.accSums.get(node).get(n));
			this.bCC.setValue(n.getIndex(), this.bCC.getValue(n.getIndex())
					- this.accSums.get(node).get(n));
			this.bCSum = this.bCSum - this.accSums.get(node).get(n);
		}

		this.spcs.remove(node);
		this.distances.remove(node);
		this.accSums.remove(node);
		this.parents.remove(node);
		g.removeNode(node);
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		Node node = (Node) ((NodeAddition) u).getNode();
		HashMap<Node, HashSet<Node>> p = new HashMap<Node, HashSet<Node>>();
		HashMap<Node, Integer> spc = new HashMap<Node, Integer>();
		HashMap<Node, Integer> d = new HashMap<Node, Integer>();
		HashMap<Node, Double> sums = new HashMap<Node, Double>();

		for (IElement ieE : g.getNodes()) {
			Node t = (Node) ieE;
			if (t == node) {
				d.put(t, 0);
				spc.put(t, 1);
			} else {
				spc.put(t, 0);
				d.put(t, Integer.MAX_VALUE);
				this.spcs.get(t).put(node, 0);
				this.distances.get(t).put(node, Integer.MAX_VALUE);
				this.accSums.get(t).put(node, 0d);
				this.parents.get(t).put(node, new HashSet<Node>());
			}
			sums.put(t, 0d);
			p.put(t, new HashSet<Node>());
		}
		this.spcs.put(node, spc);
		this.distances.put(node, d);
		this.accSums.put(node, sums);
		this.parents.put(node, p);
		bCC.setValue(node.getIndex(), 0d);
		visited.put(node, 0L);
		return true;
	}

}
