package dna.metrics.betweenessCentrality;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedBetweenessCentralityU extends DirectedBetweenessCentrality {

	public DirectedBetweenessCentralityU() {
		super("BCDyn", ApplicationType.AfterUpdate);
	}

	Queue<DirectedNode>[] qLevel;
	Queue<DirectedNode>[] qALevel;
	HashMap<DirectedNode, Long> visited;
	long counter;

	@Override
	public void init_() {
		super.init_();
		qALevel = new LinkedList[g.getNodeCount()];
		qLevel = new LinkedList[g.getNodeCount()];
		visited = new HashMap<DirectedNode, Long>();
		counter = 0;
		for (int i = 0; i < qALevel.length; i++) {
			visited.put((DirectedNode) g.getNode(i), counter);
			qALevel[i] = new LinkedList<DirectedNode>();
			qLevel[i] = new LinkedList<DirectedNode>();
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
		DirectedEdge e = (DirectedEdge) ((EdgeRemoval) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		for (IElement iE : g.getNodes()) {
			DirectedNode root = (DirectedNode) iE;

			HashMap<DirectedNode, Integer> d = distances.get(root);
			HashMap<DirectedNode, HashSet<DirectedNode>> p = parents.get(root);

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

		return true;
	}

	private boolean removeEdgeOneToMany(DirectedNode root, DirectedNode src,
			DirectedNode dst) {
		counter++;

		HashMap<DirectedNode, Integer> d = distances.get(root);
		HashMap<DirectedNode, HashSet<DirectedNode>> p = parents.get(root);
		HashMap<DirectedNode, Double> oldSums = accSums.get(root);
		HashMap<DirectedNode, Integer> oldSpc = spcs.get(root);

		// Queues and data structure for tree change
		HashSet<DirectedNode> uncertain = new HashSet<DirectedNode>();

		// data structure for Updates
		HashMap<DirectedNode, Integer> newSpc = new HashMap<DirectedNode, Integer>(
				oldSpc);
		HashMap<DirectedNode, Double> newASums = new HashMap<DirectedNode, Double>();
		HashMap<DirectedNode, HashSet<DirectedNode>> newParents = new HashMap<DirectedNode, HashSet<DirectedNode>>();

		// set data structure for dst Node
		qLevel[d.get(dst)].add(dst);
		uncertain.add(dst);
		visited.put(dst, counter);
		newASums.put(dst, 0d);
		newParents.put(dst, new HashSet<DirectedNode>());
		int max = d.get(dst);
		for (int i = max; i < qLevel.length && i < max + 1; i++) {
			while (!qLevel[i].isEmpty()) {

				DirectedNode w = qLevel[i].poll();

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
						newParents.put(z, new HashSet<DirectedNode>());
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
		// Stage 3
		for (int i = qALevel.length - 1; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				DirectedNode w = qALevel[i].poll();
				for (DirectedNode v : p.get(w)) {

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

				for (DirectedNode v : newParents.get(w)) {
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

		oldSums.putAll(newASums);
		p.putAll(newParents);
		spcs.put(root, newSpc);
		return true;
	}

	private boolean removeEdgeManyToMany(DirectedNode root, DirectedNode src,
			DirectedNode dst) {

		counter++;

		HashMap<DirectedNode, Integer> d = distances.get(root);
		HashMap<DirectedNode, HashSet<DirectedNode>> p = parents.get(root);
		HashMap<DirectedNode, Double> oldSums = accSums.get(root);
		HashMap<DirectedNode, Integer> oldSpc = spcs.get(root);

		// Queue for BFS Search
		Queue<DirectedNode> qBFS = new LinkedList<DirectedNode>();

		// data structure for Updates
		HashMap<DirectedNode, Integer> dP = new HashMap<DirectedNode, Integer>();
		HashMap<DirectedNode, Integer> newSpc = new HashMap<DirectedNode, Integer>(
				oldSpc);
		HashMap<DirectedNode, Double> newASums = new HashMap<DirectedNode, Double>();

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
			DirectedNode v = qBFS.poll();
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

		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				DirectedNode w = qALevel[i].poll();
				for (DirectedNode v : p.get(w)) {
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

	private boolean recomp(Graph g, DirectedNode root) {
		// stage ONE
		Stack<DirectedNode> s = new Stack<DirectedNode>();
		Queue<DirectedNode> q = new LinkedList<DirectedNode>();
		HashMap<DirectedNode, HashSet<DirectedNode>> p = new HashMap<DirectedNode, HashSet<DirectedNode>>();
		HashMap<DirectedNode, Integer> d = new HashMap<DirectedNode, Integer>();
		HashMap<DirectedNode, Integer> spc = new HashMap<DirectedNode, Integer>();
		HashMap<DirectedNode, Double> sums = new HashMap<DirectedNode, Double>();
		for (IElement ieE : g.getNodes()) {
			DirectedNode t = (DirectedNode) ieE;
			if (t == root) {
				d.put(t, 0);
				spc.put(t, 1);
				sums.put(t, 0d);
			} else {
				spc.put(t, 0);
				sums.put(t, 0d);
				d.put(t, Integer.MAX_VALUE);
			}
			p.put(t, new HashSet<DirectedNode>());
		}

		q.add(root);

		// stage 2
		while (!q.isEmpty()) {
			DirectedNode v = q.poll();
			s.push(v);
			for (IElement iEdges : v.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iEdges;
				DirectedNode w = edge.getDifferingNode(v);

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
			DirectedNode w = s.pop();
			for (DirectedNode parent : p.get(w)) {
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

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeAddition) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		for (IElement iE : g.getNodes()) {
			DirectedNode root = (DirectedNode) iE;
			HashMap<DirectedNode, Integer> d = this.distances.get(root);

			if (d.get(src).equals(Integer.MAX_VALUE)
					|| d.get(src).equals(d.get(dst))
					|| d.get(src).intValue() > d.get(dst).intValue()) {
				// no change to shortes path tree
				continue;
			}
			if (d.get(dst).equals(Integer.MAX_VALUE)) {
				// to components merge therefore new Nodes add to shortest path
				// tree
				nonAdjacentLevelInsertion(root, src, dst);
				continue;
			}
			if (d.get(src).intValue() + 1 == d.get(dst).intValue()) {
				// the added edge connects nodes in adjacent Levels therefore
				// only the new tree edge is added

				adjacentLevelInsertion(root, src, dst);
				continue;
			}
			if (d.get(src).intValue() + 1 < d.get(dst).intValue()) {
				// the added edge connects nodes in non adjacent Levels
				// therefore all nodes in the subtree need to be checked if they
				// move up in the shortest path tree
				nonAdjacentLevelInsertion(root, src, dst);
				continue;

			}
			System.err.println("err for edge insertion");
			return false;

		}
		return true;
	}

	private void nonAdjacentLevelInsertion(DirectedNode root, DirectedNode src,
			DirectedNode dst) {
		counter++;

		HashMap<DirectedNode, Integer> d = distances.get(root);
		HashMap<DirectedNode, HashSet<DirectedNode>> p = parents.get(root);
		HashMap<DirectedNode, Double> oldSums = accSums.get(root);
		HashMap<DirectedNode, Integer> oldSpc = spcs.get(root);

		// Data Structure for BFS Search
		Queue<DirectedNode> qBFS = new LinkedList<DirectedNode>();

		// data structure for Updates
		HashMap<DirectedNode, Integer> newSpc = new HashMap<DirectedNode, Integer>(
				oldSpc);
		HashMap<DirectedNode, Double> newASums = new HashMap<DirectedNode, Double>();
		HashMap<DirectedNode, HashSet<DirectedNode>> newParents = new HashMap<DirectedNode, HashSet<DirectedNode>>();

		// set Up data Structure for the lower node

		qBFS.add(dst);
		visited.put(dst, counter);
		newSpc.put(dst, newSpc.get(src));
		d.put(dst, d.get(src) + 1);
		qALevel[d.get(dst)].add(dst);
		newASums.put(dst, 0d);
		newParents.put(dst, new HashSet<DirectedNode>());

		int maxHeight = d.get(dst);
		HashSet<DirectedNode> bal = new HashSet<>();

		// Stage 2
		while (!qBFS.isEmpty()) {
			DirectedNode v = qBFS.poll();
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
					newParents.put(n, new HashSet<DirectedNode>());
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
						newParents.put(n, new HashSet<DirectedNode>());
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
				DirectedNode w = qALevel[i].poll();

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
				for (DirectedNode v : newParents.get(w)) {

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

	private boolean adjacentLevelInsertion(DirectedNode root, DirectedNode src,
			DirectedNode dst) {
		// Queue for BFS Search
		counter++;
		Queue<DirectedNode> qBFS = new LinkedList<DirectedNode>();

		HashMap<DirectedNode, Integer> d = distances.get(root);
		HashMap<DirectedNode, HashSet<DirectedNode>> p = parents.get(root);
		HashMap<DirectedNode, Double> oldSums = accSums.get(root);
		HashMap<DirectedNode, Integer> oldSpc = spcs.get(root);

		// data structure for Updates
		HashMap<DirectedNode, Integer> dP = new HashMap<DirectedNode, Integer>();
		HashMap<DirectedNode, Integer> newSpc = new HashMap<DirectedNode, Integer>(
				oldSpc);
		HashMap<DirectedNode, Double> newASums = new HashMap<DirectedNode, Double>();

		// setup changes for dst node
		qBFS.add(dst);
		qALevel[d.get(dst)].add(dst);
		newASums.put(dst, 0d);
		visited.put(dst, counter);
		dP.put(dst, oldSpc.get(src));
		newSpc.put(dst, newSpc.get(dst) + dP.get(dst));
		p.get(dst).add(src);
		int maxHeight = d.get(dst);

		// Stage 2
		while (!qBFS.isEmpty()) {
			DirectedNode v = qBFS.poll();

			// all neighbours of v
			for (IElement iEdges : v.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) iEdges;
				DirectedNode w = edge.getDst();

				if (d.get(w).equals(d.get(v).intValue() + 1)) {
					if (Math.abs(visited.get(w)) < counter) {
						qBFS.add(w);
						qALevel[d.get(w)].add(w);
						newASums.put(w, 0d);
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

		// Stage 3
		// traverse the shortest path tree from leaves to root
		for (int i = maxHeight; i >= 0; i--) {
			while (!qALevel[i].isEmpty()) {
				DirectedNode w = qALevel[i].poll();

				for (DirectedNode v : p.get(w)) {
					if (Math.abs(visited.get(v)) < counter) {
						qALevel[i - 1].add(v);
						visited.put(v, -counter);
						newASums.put(v, oldSums.get(v));
					}
					double t = newASums.get(v) + newSpc.get(v)
							* (1 + newASums.get(w)) / newSpc.get(w);
					newASums.put(v, t);
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
		return true;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();

		g.addNode(node);
		HashSet<DirectedEdge> bla = new HashSet<>();
		for (IElement ie : node.getEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			e.connectToNodes();
			bla.add(e);
		}
		for (DirectedEdge e : bla) {
			e.disconnectFromNodes();
			applyAfterEdgeRemoval(new EdgeRemoval(e));
		}

		for (DirectedNode n : this.accSums.get(node).keySet()) {
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
		DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();

		HashMap<DirectedNode, HashSet<DirectedNode>> p = new HashMap<DirectedNode, HashSet<DirectedNode>>();
		HashMap<DirectedNode, Integer> spc = new HashMap<DirectedNode, Integer>();
		HashMap<DirectedNode, Integer> d = new HashMap<DirectedNode, Integer>();
		HashMap<DirectedNode, Double> sums = new HashMap<DirectedNode, Double>();

		for (IElement ieE : g.getNodes()) {
			DirectedNode t = (DirectedNode) ieE;
			if (t == node) {
				d.put(t, 0);
				spc.put(t, 1);
			} else {
				spc.put(t, 0);
				d.put(t, Integer.MAX_VALUE);
				this.spcs.get(t).put(node, 0);
				this.distances.get(t).put(node, Integer.MAX_VALUE);
				this.accSums.get(t).put(node, 0d);
				this.parents.get(t).put(node, new HashSet<DirectedNode>());
			}
			sums.put(t, 0d);
			p.put(t, new HashSet<DirectedNode>());
		}
		this.spcs.put(node, spc);
		this.distances.put(node, d);
		this.accSums.put(node, sums);
		this.parents.put(node, p);
		bC.put(node, 0d);
		visited.put(node, 0L);
		return true;
	}
}
