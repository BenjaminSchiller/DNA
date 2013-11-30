package dna.metrics.connectedComponents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.EdgeUpdate;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;
import dna.updates.update.Update;

public class DirectedConnectedComponentU extends DirectedConnectedComponent {

	public DirectedConnectedComponentU() {
		super("DirectedConnectedComponentU", ApplicationType.AfterUpdate);
	}

	@Override
	public void init_() {
		super.init_();
		visited = new HashMap<DirectedNode, Long>();
		extractedCounter = 0;
		for (IElement ie : g.getNodes()) {
			visited.put((DirectedNode) ie, extractedCounter);
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

		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		int srcIndex = lookup(src);
		int dstIndex = lookup(dst);

		DirectedComponent srcDAGNode = this.dag.get(srcIndex);

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
		HashMap<DirectedComponent, LinkedList<DirectedNode>> list = new HashMap<DirectedComponent, LinkedList<DirectedNode>>();
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

	private void updateDAGEdges(DirectedComponent srcDAGNode,
			HashMap<DirectedComponent, LinkedList<DirectedNode>> list,
			DirectedNode src) {
		for (DirectedComponent cV : list.keySet()) {
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
						DirectedComponent dV = this.dag.get(dIndex);
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
						DirectedComponent dV = this.dag.get(dIndex);
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

	private HashMap<DirectedNode, Integer> lowLink;
	private HashMap<DirectedNode, Integer> dfs;
	private HashMap<DirectedNode, Long> visited;
	private int counter;
	private long extractedCounter;

	private void initForDeletion() {
		lowLink = new HashMap<DirectedNode, Integer>();
		dfs = new HashMap<DirectedNode, Integer>();
		counter = 0;
	}

	private boolean extractComponent(DirectedNode n1, DirectedNode n2,
			int srcIndex, Queue<DirectedNode> q,
			HashMap<DirectedComponent, LinkedList<DirectedNode>> list) {
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
			DirectedComponent cV = new DirectedComponent(componentCounter);
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

	private boolean applyAfterEdgeAddition(Update u) {

		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
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
		DirectedComponent srcDAGNode = this.dag.get(srcIndex);
		DirectedComponent dstDagNode = this.dag.get(dstIndex);
		if (srcDAGNode.ed.containsKey(dstIndex)) {
			srcDAGNode.ed.put(dstIndex, srcDAGNode.ed.get(dstIndex) + 1);
		} else {
			srcDAGNode.ed.put(dstIndex, 1);
		}

		HashSet<DirectedComponent> mergedComponets = findComponentsToMerge(
				dstDagNode, srcDAGNode);
		// no paths between the two components
		if (mergedComponets.isEmpty()) {
			return true;
		}

		// update the component and the dag edges to other nodes
		for (DirectedComponent v : mergedComponets) {
			if (v.getIndex() != srcIndex) {
				srcDAGNode.ed.remove(v.getIndex());
				srcDAGNode.increaseSize(v.getSize());
				this.containmentEdgesForComponents.put(v.getIndex(), srcIndex);
				this.dagExpired.put(v.getIndex(), v);
				this.dag.remove(v.getIndex());
				for (int i : v.ed.keySet()) {
					if (!mergedComponets.contains(dag.get(i))
							&& !dagExpired.containsKey(i)) {
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
		for (DirectedComponent v : this.dag.values()) {
			Map<Integer, Integer> temp = new HashMap<Integer, Integer>();

			for (int i : v.ed.keySet()) {
				if (i != srcIndex
						&& mergedComponets.contains(dagExpired.get(i))) {

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

	private HashSet<DirectedComponent> findComponentsToMerge(
			DirectedComponent v, DirectedComponent srcComp) {
		HashSet<DirectedComponent> seen = new HashSet<DirectedComponent>();
		HashSet<DirectedComponent> comp = new HashSet<DirectedComponent>();

		dfs(v, srcComp, seen, comp);
		return comp;
	}

	private HashSet<DirectedComponent> dfs(DirectedComponent v,
			DirectedComponent target, HashSet<DirectedComponent> seen,
			HashSet<DirectedComponent> comp) {
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

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
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
			applyAfterEdgeRemoval(new EdgeRemoval(e));
		}
		int iNow = lookup(node);
		for (IElement ie : node.getIncomingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			int iSrc = lookup(e.getSrc());
			DirectedComponent cSRC = this.dag.get(iSrc);
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

	private boolean applyAfterNodeAddition(Update u) {
		DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();
		visited.put(node, 0L);
		DirectedComponent cV = new DirectedComponent(this.componentCounter);
		cV.setSize(1);
		this.dag.put(componentCounter, cV);
		this.containmentEdges.put(node, this.componentCounter);
		componentCounter++;
		return true;
	}

}
