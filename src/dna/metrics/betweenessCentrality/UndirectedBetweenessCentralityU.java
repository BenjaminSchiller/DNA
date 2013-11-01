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
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class UndirectedBetweenessCentralityU extends
		UndirectedBetweenessCentrality {

	Queue<UndirectedNode>[] qLevel;
	Queue<UndirectedNode>[] qALevel;
	HashMap<UndirectedNode, Long> visited;
	long counter;

	public UndirectedBetweenessCentralityU() {
		super("BCDyn", ApplicationType.AfterUpdate);
	}

	@Override
	public void init_() {
		super.init_();
		qALevel = new LinkedList[g.getNodeCount()];
		qLevel = new LinkedList[g.getNodeCount()];
		visited = new HashMap<UndirectedNode, Long>();
		counter = 0;
		for (int i = 0; i < qALevel.length; i++) {
			visited.put((UndirectedNode) g.getNode(i), counter);
			qALevel[i] = new LinkedList<UndirectedNode>();
			qLevel[i] = new LinkedList<UndirectedNode>();
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
		UndirectedEdge e = (UndirectedEdge) ((EdgeRemoval) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		for (IElement iE : g.getNodes()) {
			UndirectedNode root = (UndirectedNode) iE;

			HashMap<UndirectedNode, Integer> d = distances.get(root);
			HashMap<UndirectedNode, HashSet<UndirectedNode>> p = parents
					.get(root);

			// Find the above Tree Element
			if (d.get(n1) > d.get(n2)) {
				n1 = n2;
				n2 = e.getDifferingNode(n1);
			}

			if (!p.get(n2).contains(n1) || d.get(n1).equals(Integer.MAX_VALUE)
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

		return true;
	}

	private boolean removeEdgeOneToMany(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst) {

		counter++;

		HashMap<UndirectedNode, Integer> d = distances.get(root);
		HashMap<UndirectedNode, HashSet<UndirectedNode>> p = parents.get(root);
		HashMap<UndirectedNode, Double> oldSums = accSums.get(root);
		HashMap<UndirectedNode, Integer> oldSpc = spcs.get(root);

		// Queues and data structure for tree change
		HashSet<UndirectedNode> uncertain = new HashSet<UndirectedNode>();

		// data structure for Updates
		HashMap<UndirectedNode, Integer> newSpc = new HashMap<UndirectedNode, Integer>(
				oldSpc);
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();
		HashMap<UndirectedNode, HashSet<UndirectedNode>> newParents = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();

		// set data structure for dst Node
		qLevel[d.get(dst)].add(dst);
		uncertain.add(dst);
		newASums.put(dst, 0d);
		newParents.put(dst, new HashSet<UndirectedNode>());
		visited.put(dst, counter);
		int max = d.get(dst);
		for (int i = max; i < qLevel.length && i < max + 1; i++) {
			while (!qLevel[i].isEmpty()) {

				UndirectedNode w = qLevel[i].poll();
				int dist = Integer.MAX_VALUE;
				ArrayList<UndirectedNode> min = new ArrayList<UndirectedNode>();
				for (IElement iEdges : w.getEdges()) {
					UndirectedEdge ed = (UndirectedEdge) iEdges;
					UndirectedNode z = ed.getDifferingNode(w);

					if (d.get(z).equals(d.get(w) + 1)
							&& Math.abs(visited.get(z)) < counter) {
						qLevel[i + 1].add(z);
						newASums.put(z, 0d);
						newParents.put(z, new HashSet<UndirectedNode>());
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

		// Stage 3
		for (int i = qALevel.length - 1; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				UndirectedNode w = qALevel[i].poll();

				for (UndirectedNode v : p.get(w)) {
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

				for (UndirectedNode v : newParents.get(w)) {
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
					double currentScore = this.bC.get(w);
					this.bC.put(w,
							currentScore + newASums.get(w) - oldSums.get(w));
				}
			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newASums);
		p.putAll(newParents);

		return true;
	}

	private boolean removeEdgeManyToMany(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst) {
		counter++;

		HashMap<UndirectedNode, Integer> d = distances.get(root);
		HashMap<UndirectedNode, HashSet<UndirectedNode>> p = parents.get(root);
		HashMap<UndirectedNode, Double> oldSums = accSums.get(root);
		HashMap<UndirectedNode, Integer> oldSpc = spcs.get(root);

		// Queue for BFS Search
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();

		// data structure for Updates
		HashMap<UndirectedNode, Integer> dP = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Integer> newSpc = new HashMap<UndirectedNode, Integer>(
				oldSpc);
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();

		// setup changes for dst node
		qBFS.add(dst);
		qALevel[d.get(dst)].add(dst);
		visited.put(dst, counter);
		dP.put(dst, oldSpc.get(src));
		newASums.put(dst, 0d);
		newSpc.put(dst, newSpc.get(dst) - dP.get(dst));
		int maxHeight = d.get(dst);

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
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

		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				UndirectedNode w = qALevel[i].poll();
				for (UndirectedNode v : p.get(w)) {
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
					double currentScore = this.bC.get(w);
					this.bC.put(w,
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
		UndirectedEdge e = (UndirectedEdge) ((EdgeAddition) u).getEdge();
		UndirectedNode n1 = e.getNode1();
		UndirectedNode n2 = e.getNode2();

		for (IElement iE : g.getNodes()) {
			UndirectedNode root = (UndirectedNode) iE;

			HashMap<UndirectedNode, Integer> d = distances.get(root);

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
				// to components merge therefore new Nodes add to shortest path
				// tree
				mergeOfComponentsInsertion(root, n1, n2);
				continue;
			}
			if (d.get(n1).intValue() + 1 == d.get(n2).intValue()) {
				// the added edge connects nodes in adjacent Levels therefore
				// only the new tree edge is added
				adjacentLevelInsertion(root, n1, n2);
				continue;
			}
			if (d.get(n1).intValue() + 1 < d.get(n2).intValue()) {
				// the added edge connects nodes in non adjacent Levels
				// therefore all nodes in the subtree need to be checked if they
				// move up in the shortest path tree
				nonAdjacentLevelInsertion(root, n1, n2);
				continue;

			}

			System.err.println(" shit" + d.get(n1) + " " + d.get(n2));
			return false;

		}
		return true;
	}

	private void mergeOfComponentsInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst) {

		counter++;

		HashMap<UndirectedNode, Integer> d = distances.get(root);
		HashMap<UndirectedNode, HashSet<UndirectedNode>> p = parents.get(root);
		HashMap<UndirectedNode, Double> oldSums = accSums.get(root);
		HashMap<UndirectedNode, Integer> oldSpc = spcs.get(root);

		// Queue for the BFS search down the shortes Path tree
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();

		// data structure for Updates
		HashMap<UndirectedNode, Integer> newSpc = new HashMap<UndirectedNode, Integer>(
				oldSpc);
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();

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
			UndirectedNode v = qBFS.poll();
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
				UndirectedNode w = qALevel[i].poll();

				for (UndirectedNode v : p.get(w)) {
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
					double currentScore = this.bC.get(w);
					this.bC.put(w,
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
		HashMap<UndirectedNode, Integer> d = distances.get(root);
		HashMap<UndirectedNode, HashSet<UndirectedNode>> p = parents.get(root);
		HashMap<UndirectedNode, Double> oldSums = accSums.get(root);
		HashMap<UndirectedNode, Integer> oldSpc = spcs.get(root);

		// Data Structure for BFS Search
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();

		// data structure for Updates
		HashMap<UndirectedNode, Integer> newSpc = new HashMap<UndirectedNode, Integer>(
				oldSpc);
		HashMap<UndirectedNode, Double> newASums = new HashMap<UndirectedNode, Double>();
		HashMap<UndirectedNode, HashSet<UndirectedNode>> newParents = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();

		// set Up data Structure for the lower node

		qBFS.add(dst);
		visited.put(dst, counter);
		newSpc.put(dst, newSpc.get(src));
		d.put(dst, d.get(src) + 1);
		qALevel[d.get(dst)].add(dst);
		newASums.put(dst, 0d);
		newParents.put(dst, new HashSet<UndirectedNode>());

		int maxHeight = d.get(dst);

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();
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
					newParents.put(n, new HashSet<UndirectedNode>());
					visited.put(n, counter);
					maxHeight = Math.max(maxHeight, d.get(n));
					continue;
				}

				// lower Node get a new Parent
				if (d.get(n).intValue() == d.get(v).intValue() + 1) {
					if (Math.abs(visited.get(n)) < counter) {
						visited.put(n, counter);
						qALevel[d.get(n)].add(n);
						newParents.put(n, new HashSet<UndirectedNode>());
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
				UndirectedNode w = qALevel[i].poll();
				for (UndirectedNode v : p.get(w)) {
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

				for (UndirectedNode v : newParents.get(w)) {

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
					double currentScore = this.bC.get(w);
					this.bC.put(w,
							currentScore + newASums.get(w) - oldSums.get(w));
				}
			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newASums);
		p.putAll(newParents);
	}

	private boolean adjacentLevelInsertion(UndirectedNode root,
			UndirectedNode src, UndirectedNode dst) {
		// Queue for BFS Search
		Queue<UndirectedNode> qBFS = new LinkedList<UndirectedNode>();
		counter++;

		// old values
		HashMap<UndirectedNode, Integer> d = distances.get(root);
		HashMap<UndirectedNode, HashSet<UndirectedNode>> p = parents.get(root);
		HashMap<UndirectedNode, Double> oldSums = accSums.get(root);
		HashMap<UndirectedNode, Integer> oldSpc = spcs.get(root);

		// data structure for Updates
		HashMap<UndirectedNode, Integer> dP = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Integer> newSpc = new HashMap<UndirectedNode, Integer>(
				oldSpc);
		HashMap<UndirectedNode, Double> newSums = new HashMap<UndirectedNode, Double>();

		// setup changes for dst node
		qBFS.add(dst);
		qALevel[d.get(dst)].add(dst);
		visited.put(dst, counter);
		newSums.put(dst, 0d);
		dP.put(dst, oldSpc.get(src));
		newSpc.put(dst, newSpc.get(dst) + dP.get(dst));
		p.get(dst).add(src);
		int maxHeight = d.get(dst);

		// Stage 2
		while (!qBFS.isEmpty()) {
			UndirectedNode v = qBFS.poll();

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

		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				UndirectedNode w = qALevel[i].poll();

				for (UndirectedNode v : p.get(w)) {
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
					double currentScore = this.bC.get(w);
					this.bC.put(w,
							currentScore + newSums.get(w) - oldSums.get(w));
				}

			}
		}

		spcs.put(root, newSpc);
		oldSums.putAll(newSums);
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeRemoval) u).getNode();

		g.addNode(node);
		HashSet<UndirectedEdge> bla = new HashSet<>();
		for (IElement ie : node.getEdges()) {
			UndirectedEdge e = (UndirectedEdge) ie;
			e.connectToNodes();
			bla.add(e);
		}
		for (UndirectedEdge e : bla) {
			e.disconnectFromNodes();
			applyAfterEdgeRemoval(new EdgeRemoval(e));
		}

		for (UndirectedNode n : this.accSums.get(node).keySet()) {
			this.bC.put(n, this.bC.get(n) - this.accSums.get(node).get(n));
		}

		this.spcs.remove(node);
		this.distances.remove(node);
		this.accSums.remove(node);
		this.parents.remove(node);
		g.removeNode(node);
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		UndirectedNode node = (UndirectedNode) ((NodeAddition) u).getNode();
		HashMap<UndirectedNode, HashSet<UndirectedNode>> p = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();
		HashMap<UndirectedNode, Integer> spc = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Integer> d = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Double> sums = new HashMap<UndirectedNode, Double>();

		for (IElement ieE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) ieE;
			if (t == node) {
				d.put(t, 0);
				spc.put(t, 1);
			} else {
				spc.put(t, 0);
				d.put(t, Integer.MAX_VALUE);
				this.spcs.get(t).put(node, 0);
				this.distances.get(t).put(node, Integer.MAX_VALUE);
				this.accSums.get(t).put(node, 0d);
				this.parents.get(t).put(node, new HashSet<UndirectedNode>());
			}
			sums.put(t, 0d);
			p.put(t, new HashSet<UndirectedNode>());
		}
		this.spcs.put(node, spc);
		this.distances.put(node, d);
		this.accSums.put(node, sums);
		this.parents.put(node, p);
		bC.put(node, 0d);
		visited.put(node, 0L);
		return true;
	}

	private boolean recomp(Graph g, UndirectedNode root) {
		// stage ONE
		Stack<UndirectedNode> s = new Stack<UndirectedNode>();
		Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();
		HashMap<UndirectedNode, HashSet<UndirectedNode>> p = new HashMap<UndirectedNode, HashSet<UndirectedNode>>();
		HashMap<UndirectedNode, Integer> d = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Integer> spc = new HashMap<UndirectedNode, Integer>();
		HashMap<UndirectedNode, Double> sums = new HashMap<UndirectedNode, Double>();
		for (IElement ieE : g.getNodes()) {
			UndirectedNode t = (UndirectedNode) ieE;
			if (t == root) {
				d.put(t, 0);
				spc.put(t, 1);
				sums.put(t, 0d);
			} else {
				spc.put(t, 0);
				sums.put(t, 0d);
				d.put(t, Integer.MAX_VALUE);
			}
			p.put(t, new HashSet<UndirectedNode>());
		}

		q.add(root);

		// stage 2
		while (!q.isEmpty()) {
			UndirectedNode v = q.poll();
			s.push(v);
			for (IElement iEdges : v.getEdges()) {
				UndirectedEdge edge = (UndirectedEdge) iEdges;
				UndirectedNode w = edge.getDifferingNode(v);

				if (d.get(w).equals(Integer.MAX_VALUE)) {
					q.add(w);
					d.put(w, d.get(v) + 1);
				}
				if (d.get(w).equals(d.get(v) + 1)) {
					spc.put(w, spc.get(w) + spc.get(v));
					p.get(w).add(v);
				}
			}
		}

		// stage 3
		while (!s.isEmpty()) {
			UndirectedNode w = s.pop();
			for (UndirectedNode parent : p.get(w)) {
				double sumForCurretConnection = spc.get(parent)
						* (1 + sums.get(w)) / spc.get(w);
				sums.put(parent, sums.get(parent) + sumForCurretConnection);
			}
			if (w != root) {
				double currentScore = this.bC.get(w) - accSums.get(root).get(w);
				this.bC.put(w, currentScore + sums.get(w));
			}
		}
		parents.put(root, p);
		distances.put(root, d);
		spcs.put(root, spc);
		accSums.put(root, sums);

		return true;
	}
}
