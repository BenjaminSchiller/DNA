package dna.metrics.connectedComponents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedNode;
import dna.updates.Batch;
import dna.updates.EdgeAddition;
import dna.updates.EdgeRemoval;
import dna.updates.EdgeUpdate;
import dna.updates.NodeAddition;
import dna.updates.NodeRemoval;
import dna.updates.Update;

@SuppressWarnings("rawtypes")
public class CCDirectedDagger extends CCDirected {

	protected Map<Integer, Integer> containmentEdgesForComponents = new HashMap<>();

	public CCDirectedDagger() {
		super("CCDirectedDagger", ApplicationType.AfterUpdate);
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

		// check if a split into two components may occur
		if (this.containmentEdges.get(src.getIndex()) == this.containmentEdges
				.get(dst.getIndex())) {

			Queue<DirectedNode> q = new LinkedList<DirectedNode>();
			q.add(src);
			while (!q.isEmpty()) {
				DirectedNode v = q.poll();
				HashSet<DirectedNode> comp = extractComponent(v, dst);
				if (comp.contains(dst)) {
					break;
				} else {
					for (DirectedNode n : comp) {
						for (DirectedEdge edge : n.getIncomingEdges()) {
							if (!comp.contains(edge.getSrc())) {
								q.add(edge.getSrc());
							}
							this.containmentEdges.put(edge.getSrc().getIndex(),
									componentCounter);
							// TODO: put noch nicht richtig wird immer neues
							// erzeugt
							if (this.dag.containsKey(this.componentCounter)) {
								this.dag.put(componentCounter,
										new ComponentVertex(componentCounter));
							}
						}
					}
					this.componentCounter++;

				}
			}

		} else {
			// see if deleted edge connects to components
			for (DAGEdge edge : this.dag.get(
					this.containmentEdges.get(src.getIndex())).getEdges()) {
				if (edge.getDst().getIndex() == this.containmentEdges.get(dst
						.getIndex())) {
					if (edge.getEdges().contains(e)) {
						edge.removeEdge(e);
					}
				}

			}
		}

		return true;
	}

	private HashSet<DirectedNode> extractComponent(DirectedNode src,
			DirectedNode dst) {
		Queue<DirectedNode> q = new LinkedList<DirectedNode>();
		q.add(src);
		HashSet<DirectedNode> possibleNewComponent = new HashSet<>();
		possibleNewComponent.add(src);
		while (!q.isEmpty()) {
			DirectedNode v = q.poll();
			for (DirectedEdge edge : v.getOutgoingEdges()) {
				if (edge.getDst() != dst
						&& this.containmentEdges.get(edge.getDst().getIndex()) == this.containmentEdges
								.get(src.getIndex())
						&& possibleNewComponent.contains(edge.getDst())) {
					q.add(edge.getDst());
					possibleNewComponent.add(edge.getDst());
				} else if (edge.getDst() == dst) {
					possibleNewComponent.add(dst);
					return possibleNewComponent;
				}
			}
		}
		return possibleNewComponent;
	}

	private boolean applyAfterEdgeAddition(Update u) {
		DirectedEdge e = (DirectedEdge) ((EdgeUpdate) u).getEdge();
		DirectedNode src = e.getSrc();
		DirectedNode dst = e.getDst();

		int srcIndex = this.containmentEdges.get(src.getIndex());
		int dstIndex = this.containmentEdges.get(dst.getIndex());
		while (this.containmentEdgesForComponents.containsKey(srcIndex)) {
			srcIndex = this.containmentEdgesForComponents.get(srcIndex);
		}
		while (this.containmentEdgesForComponents.containsKey(dstIndex)) {
			srcIndex = this.containmentEdgesForComponents.get(dstIndex);
		}

		// check if a merge of two components may occur
		if (srcIndex != dstIndex) {

			// case1: no edge from component dst and componet src and vice versa
			// ==> add inserted add
			ComponentVertex srcDAGNode = this.dag.get(srcIndex);

			ComponentVertex dstDagNode = this.dag.get(dstIndex);

			if (srcDAGNode.ed.containsKey(this.containmentEdges.get(dst
					.getIndex()))) {
				srcDAGNode.ed.get(this.containmentEdges.get(dst.getIndex()))
						.add(e);
			} else {
				HashSet<DirectedEdge> temp = new HashSet<>();
				temp.add(e);
				srcDAGNode.ed.put(this.containmentEdges.get(dst.getIndex()),
						temp);
			}

			if (dstDagNode.ed.containsKey(srcDAGNode.getIndex())) {
				HashSet<ComponentVertex> mergedComponets = mergeComponent(
						dstDagNode, srcDAGNode);
				HashMap<Integer, Set<DirectedEdge>> newDagEdges = new HashMap<>();

				// update the component and the dag edges to other nodes
				for (ComponentVertex v : mergedComponets) {

					if (v.getIndex() != this.containmentEdges.get(src
							.getIndex())) {
						this.containmentEdgesForComponents.put(v.getIndex(),
								this.containmentEdges.get(src.getIndex()));
						this.dagExpired.put(v.getIndex(), v);
						for (int i : v.ed.keySet()) {
							if (!mergedComponets.contains(dag.get(i))) {
								if (newDagEdges.containsKey(i)) {
									newDagEdges.get(i).addAll(v.ed.get(i));
								} else {
									newDagEdges.put(i, v.ed.get(i));
								}
							}
						}
					}
				}

				// update all dag Edges to new merged component
				for (ComponentVertex v : this.dag.values()) {
					for (int i : v.ed.keySet()) {
						if (mergedComponets.contains(dag.get(i))) {
							if (v.ed.containsKey(srcDAGNode.getIndex())) {
								v.ed.get(srcDAGNode.getIndex()).addAll(
										v.ed.get(i));
							} else {
								v.ed.put(srcDAGNode.getIndex(), v.ed.get(i));
							}
						}
					}
				}

			}

		}

		return true;
	}

	private HashSet<ComponentVertex> mergeComponent(ComponentVertex v,
			ComponentVertex srcComp) {
		HashSet<ComponentVertex> comp = new HashSet<ComponentVertex>();
		for (int cV : v.ed.keySet()) {
			if (cV == srcComp.getIndex()) {
				comp.add(dag.get(cV));
			} else {
				HashSet<ComponentVertex> temp = mergeComponent(dag.get(cV),
						srcComp);
				if (temp.contains(srcComp.getIndex())) {
					comp.addAll(temp);
				}
			}

		}
		return comp;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		for (DirectedEdge e : node.getEdges()) {
			applyAfterEdgeRemoval(new EdgeRemoval(e));
		}
		this.containmentEdges.remove(node.getIndex());
		return true;
	}

	private boolean applyAfterNodeAddition(Update u) {
		DirectedNode node = (DirectedNode) ((NodeAddition) u).getNode();

		this.dag.put(node.getIndex(),
				new ComponentVertex(this.componentCounter));
		this.containmentEdges.put(node.getIndex(), this.componentCounter);
		componentCounter++;

		return false;
	}

}
