package dna.metrics.connectivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IAfterNA;
import dna.metrics.algorithms.IAfterNR;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class StrongConnectivityU extends StrongConnectivity implements
		IAfterNA, IAfterNR, IAfterEA, IAfterER {

	private HashMap<DirectedNode, Integer> lowLink;
	private HashMap<DirectedNode, Integer> dfs;
	private HashMap<DirectedNode, Long> visited;
	private int counter;
	private long extractedCounter;

	public StrongConnectivityU() {
		super("StrongConnectivityU", MetricType.exact);
	}

	@Override
	public boolean init() {
		visited = new HashMap<DirectedNode, Long>();
		extractedCounter = 0;
		for (IElement ie : g.getNodes()) {
			visited.put((DirectedNode) ie, extractedCounter);
		}
		return this.compute();
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		DirectedEdge e = (DirectedEdge) er.getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		int srcIndex = lookup(src);
		int dstIndex = lookup(dst);

		StrongComponent srcDAGNode = this.dag.get(srcIndex);

		// check if a split into two components may occur
		if (srcIndex != dstIndex) {
			// see if deleted edge connects to components
			if (srcDAGNode.ed.containsKey(dstIndex)) {
				srcDAGNode.ed.put(dstIndex, srcDAGNode.ed.get(dstIndex) - 1);
			}
			if (srcDAGNode.ed.get(dstIndex).equals(0)) {
				srcDAGNode.ed.remove(dstIndex);
			}

			return true;
		}

		Queue<DirectedNode> q = new LinkedList<DirectedNode>();
		q.add(src);
		HashMap<StrongComponent, LinkedList<DirectedNode>> list = new HashMap<StrongComponent, LinkedList<DirectedNode>>();
		initForDeletion();
		s.clear();
		extractedCounter++;
		while (!q.isEmpty()) {
			DirectedNode n = q.poll();
			if (Math.abs(visited.get(n)) < extractedCounter && !n.equals(dst)) {
				extractComponent(n, dst, srcIndex, q, list);
			}
		}

		if (list.isEmpty()) {
			return true;
		}
		updateDAGEdges(srcDAGNode, list, src);
		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		DirectedEdge e = (DirectedEdge) ea.getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		int srcIndex = lookup(src);
		int dstIndex = lookup(dst);

		// check if a merge of two components may occur
		if (srcIndex == dstIndex) {
			return true;
		}

		// case1: no edge from component dst and componet src and vice versa
		// ==> add inserted add
		StrongComponent srcDAGNode = this.dag.get(srcIndex);
		StrongComponent dstDagNode = this.dag.get(dstIndex);
		if (srcDAGNode.ed.containsKey(dstIndex)) {
			srcDAGNode.ed.put(dstIndex, srcDAGNode.ed.get(dstIndex) + 1);
		} else {
			srcDAGNode.ed.put(dstIndex, 1);
		}

		HashSet<StrongComponent> mergedComponets = findComponentsToMerge(
				dstDagNode, srcDAGNode);
		// no paths between the two components
		if (mergedComponets.isEmpty()) {
			return true;
		}

		HashSet<Integer> hasexpired = new HashSet<>();
		// update the component and the dag edges to other nodes
		for (StrongComponent v : mergedComponets) {
			if (v.getIndex() != srcIndex) {
				srcDAGNode.ed.remove(v.getIndex());
				srcDAGNode.increaseSize(v.getSize());
				this.containmentEdgesForComponents.put(v.getIndex(), srcIndex);
				hasexpired.add(v.getIndex());
				this.dag.remove(v.getIndex());
				for (int i : v.ed.keySet()) {
					if (!mergedComponets.contains(dag.get(i))
							&& !hasexpired.contains(i)) {
						if (srcDAGNode.ed.containsKey(i)) {
							srcDAGNode.ed.put(i,
									srcDAGNode.ed.get(i) + v.ed.get(i));
						} else {
							srcDAGNode.ed.put(i, v.ed.get(i));
						}
					}
				}
			}
		}
		// update all dag Edges to new merged component
		for (StrongComponent v : this.dag.values()) {
			Map<Integer, Integer> temp = new HashMap<Integer, Integer>();

			for (int i : v.ed.keySet()) {
				if (i != srcIndex && hasexpired.contains(i)) {

					if (temp.containsKey(srcDAGNode.getIndex())) {
						temp.put(srcDAGNode.getIndex(),
								temp.get(srcDAGNode.getIndex()) + v.ed.get(i));
					} else {
						temp.put(srcDAGNode.getIndex(), v.ed.get(i));
					}
				} else {
					if (temp.containsKey(i)) {
						temp.put(i, temp.get(i) + v.ed.get(i));
					} else {
						temp.put(i, v.ed.get(i));

					}
				}
			}
			v.ed = temp;
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		DirectedNode node = (DirectedNode) nr.getNode();
		HashSet<DirectedEdge> out = new HashSet<>();
		g.addNode(node);
		for (IElement ie : node.getOutgoingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			e.connectToNodes();
			if (e.getSrc().equals(node)) {
				out.add(e);
			}
		}
		for (DirectedEdge e : out) {
			e.disconnectFromNodes();
			g.removeEdge(e);
			this.applyAfterUpdate(new EdgeRemoval(e));
		}
		int iNow = lookup(node);
		for (IElement ie : node.getIncomingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			int iSrc = lookup(e.getSrc());
			StrongComponent cSRC = this.dag.get(iSrc);
			cSRC.ed.put(iNow, cSRC.ed.get(iNow) - 1);
			if (cSRC.ed.get(iNow).equals(0)) {
				cSRC.ed.remove(iNow);
			}
		}
		this.g.removeNode(node);
		this.containmentEdges.remove(node);
		this.dag.remove(iNow);
		this.visited.remove(node);
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		DirectedNode node = (DirectedNode) na.getNode();
		visited.put(node, 0L);
		StrongComponent cV = new StrongComponent(this.componentCounter);
		cV.setSize(1);
		this.dag.put(componentCounter, cV);
		this.containmentEdges.put(node, this.componentCounter);
		componentCounter++;
		return true;
	}

	private void updateDAGEdges(StrongComponent srcDAGNode,
			HashMap<StrongComponent, LinkedList<DirectedNode>> list,
			DirectedNode src) {
		for (StrongComponent cV : list.keySet()) {
			for (DirectedNode n : list.get(cV)) {
				for (IElement ie : n.getOutgoingEdges()) {
					DirectedEdge edge = (DirectedEdge) ie;
					int dIndex = lookup(edge.getDst());
					if (dIndex != cV.getIndex()) {
						if (cV.ed.containsKey(dIndex)) {
							cV.ed.put(dIndex, cV.ed.get(dIndex) + 1);
						} else {
							cV.ed.put(dIndex, 1);
						}
						if (dIndex != srcDAGNode.getIndex()
								&& !list.containsKey(this.dag.get(dIndex))) {
							srcDAGNode.ed.put(dIndex,
									srcDAGNode.ed.get(dIndex) - 1);
							if (srcDAGNode.ed.get(dIndex).equals(0)) {
								srcDAGNode.ed.remove(dIndex);
							}
						}
					}
				}
				if (n.equals(src)) {
					for (IElement ie : n.getIncomingEdges()) {
						DirectedEdge edge = (DirectedEdge) ie;

						int dIndex = lookup(edge.getSrc());
						StrongComponent dV = this.dag.get(dIndex);
						if (edge.getSrc().hasEdge(edge)) {
							if (list.containsKey(dV))
								continue;

							if (dIndex != cV.getIndex()) {
								if (dV.ed.containsKey(cV.getIndex())) {
									dV.ed.put(cV.getIndex(),
											dV.ed.get(cV.getIndex()) + 1);
								} else {
									dV.ed.put(cV.getIndex(), 1);
								}

								if (dIndex != srcDAGNode.getIndex()
										&& !list.containsKey(dV)) {
									dV.ed.put(
											srcDAGNode.getIndex(),
											dV.ed.get(srcDAGNode.getIndex()) - 1);
									if (dV.ed.get(srcDAGNode.getIndex())
											.equals(0)) {
										dV.ed.remove(srcDAGNode.getIndex());
									}
								}
							}
							continue;
						}
						if (dIndex != cV.getIndex()) {
							if (dV.ed.containsKey(cV.getIndex())) {

								dV.ed.put(cV.getIndex(),
										dV.ed.get(cV.getIndex()) + 1);
							} else {
								dV.ed.put(cV.getIndex(), 1);
							}
							if (dIndex != srcDAGNode.getIndex()
									&& !list.containsKey(this.dag.get(dIndex))) {
								if (!dV.ed.containsKey(srcDAGNode.getIndex())) {
									continue;
								}
								dV.ed.put(srcDAGNode.getIndex(),
										dV.ed.get(srcDAGNode.getIndex()) - 1);
								if (dV.ed.get(srcDAGNode.getIndex()).equals(0)) {
									dV.ed.remove(srcDAGNode.getIndex());
								}
							}
						}
					}
				} else {
					for (IElement ie : n.getIncomingEdges()) {
						DirectedEdge edge = (DirectedEdge) ie;
						int dIndex = lookup(edge.getSrc());
						StrongComponent dV = this.dag.get(dIndex);
						if (list.containsKey(dV))
							continue;

						if (dIndex != cV.getIndex()) {
							if (dV.ed.containsKey(cV.getIndex())) {
								dV.ed.put(cV.getIndex(),
										dV.ed.get(cV.getIndex()) + 1);
							} else {
								dV.ed.put(cV.getIndex(), 1);
							}

							if (dIndex != srcDAGNode.getIndex()
									&& !list.containsKey(this.dag.get(dIndex))) {
								dV.ed.put(srcDAGNode.getIndex(),
										dV.ed.get(srcDAGNode.getIndex()) - 1);
								if (dV.ed.get(srcDAGNode.getIndex()).equals(0)) {
									dV.ed.remove(srcDAGNode.getIndex());
								}
							}
						}
					}
				}
			}
		}
	}

	private void initForDeletion() {
		lowLink = new HashMap<DirectedNode, Integer>();
		dfs = new HashMap<DirectedNode, Integer>();
		counter = 0;
	}

	private boolean extractComponent(DirectedNode n1, DirectedNode n2,
			int srcIndex, Queue<DirectedNode> q,
			HashMap<StrongComponent, LinkedList<DirectedNode>> list) {
		visited.put(n1, extractedCounter);
		lowLink.put(n1, counter);
		dfs.put(n1, counter);
		counter++;
		if (n1.equals(n2)) {
			visited.put(n1, -extractedCounter);
			return false;
		}
		s.push(n1);
		for (IElement ie : n1.getOutgoingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;

			if (srcIndex != lookup(e.getDst())) {
				continue;
			}

			if (Math.abs(visited.get(e.getDst())) < extractedCounter) {
				boolean split = extractComponent(e.getDst(), n2, srcIndex, q,
						list);
				if (!split) {
					visited.put(n1, -extractedCounter);
					return false;
				}
				lowLink.put(n1,
						Math.min(lowLink.get(n1), lowLink.get(e.getDst())));
			} else if (visited.get(e.getDst()).equals(-extractedCounter)) {
				visited.put(n1, -extractedCounter);
				return false;
			} else if (s.contains(e.getDst())) {
				lowLink.put(n1, Math.min(lowLink.get(n1), dfs.get(e.getDst())));
			}

		}

		if (dfs.get(n1).equals(lowLink.get(n1))) {
			DirectedNode node;
			StrongComponent cV = new StrongComponent(componentCounter);
			list.put(cV, new LinkedList<DirectedNode>());
			int size = 0;
			do {
				node = s.pop();
				size++;
				list.get(cV).add(node);
				this.containmentEdges.put(node, componentCounter);
				for (IElement ie : node.getIncomingEdges()) {
					DirectedEdge e = (DirectedEdge) ie;
					if (srcIndex == lookup(e.getSrc())) {
						q.add(e.getSrc());
					}
				}
			} while (!n1.equals(node));
			cV.setSize(size);
			this.dag.get(srcIndex).decreaseSize(size);
			this.dag.put(componentCounter, cV);
			componentCounter++;
		}
		return true;
	}

	private HashSet<StrongComponent> findComponentsToMerge(
			StrongComponent v, StrongComponent srcComp) {
		HashSet<StrongComponent> seen = new HashSet<StrongComponent>();
		HashSet<StrongComponent> comp = new HashSet<StrongComponent>();

		dfs(v, srcComp, seen, comp);
		return comp;
	}

	private HashSet<StrongComponent> dfs(StrongComponent v,
			StrongComponent target, HashSet<StrongComponent> seen,
			HashSet<StrongComponent> comp) {
		seen.add(v);

		for (int cIndex : v.ed.keySet()) {
			if (!seen.contains(this.dag.get(cIndex))) {
				if (cIndex == target.getIndex()) {
					if (!comp.contains(v))
						comp.add(v);
					if (!comp.contains(target))
						comp.add(target);
				} else {
					dfs(this.dag.get(cIndex), target, seen, comp);
					if (comp.contains(this.dag.get(cIndex))) {
						comp.add(v);
					}
				}
			} else {
				if (comp.contains(this.dag.get(cIndex))) {
					comp.add(v);
				}
			}
		}
		return comp;
	}

}
