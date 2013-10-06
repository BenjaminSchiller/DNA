package dna.metrics.connectedComponents;

<<<<<<< HEAD
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
=======
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
>>>>>>> some stuff

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

public class CCDirectedDagger extends CCDirected {

	protected Map<Integer, Integer> containmentEdgesForComponents = new HashMap<>();
	private Map<Integer, LinkedList<Integer>> path = new HashMap<>();

	public CCDirectedDagger() {
		super("CCDirectedDagger", ApplicationType.AfterUpdate);
	}

	@Override
	public void init_() {
		super.init_();
		this.containmentEdgesForComponents = new HashMap<>();
		this.path = new HashMap<>();
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
						for (IElement ie : n.getIncomingEdges()) {
							DirectedEdge edge = (DirectedEdge) ie;
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
			for (IElement ie : v.getOutgoingEdges()) {
				DirectedEdge edge = (DirectedEdge) ie;
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

<<<<<<< HEAD
		// check if a merge of two components may occur
		if (this.containmentEdges.get(src.getIndex()) != this.containmentEdges
				.get(dst.getIndex())) {

			// case1: no edge from component dst and componet src and vice versa
			// ==> add inserted add
			ComponentVertex srcDAGNode = this.dag.get(this.containmentEdges
					.get(src.getIndex()));
			for (DAGEdge edge : srcDAGNode.getEdges()) {
				if (edge.getDst().getIndex() == this.containmentEdges.get(dst
						.getIndex())) {
					edge.addEdge(e);
				}

			}

			HashSet<ComponentVertex> mergedComponets = new HashSet<>();

			// Queue<ComponentVertex> q = new LinkedList<ComponentVertex>();
			// q.add(this.dag.get(this.containmentEdges.get(dst.getIndex())));

			// TODO: store path to src component

			ComponentVertex dstDagNode = this.dag.get(this.containmentEdges
					.get(dst.getIndex()));
			mergedComponets = mergeComponent(dstDagNode, srcDAGNode);

			// while (!q.isEmpty()) {
			// ComponentVertex v = q.poll();
			// for (DAGEdge edge : v.getEdges()) {
			// if (edge.getDst().getIndex() == this.containmentEdges
			// .get(src.getIndex())) {
			// // case1: edge from component dst and src ==> component
			// // merge
			// mergedComponets.add(edge.getDst());
			// } else {
			// q.add(dag.get(edge.getDst().getIndex()));
			// }
			//
			// }
			// }
			HashSet<DAGEdge> newDagEdges = new HashSet<>();

			// update the component and the dag edges to other nodes
			for (ComponentVertex v : mergedComponets) {

				if (v.getIndex() != this.containmentEdges.get(src.getIndex())) {
					this.containmentEdges.put(v.getIndex(),
							this.containmentEdges.get(src.getIndex()));
					this.dagExpired.put(v.getIndex(), v);
					for (DAGEdge dagEDGE : v.getEdges()) {
						if (!mergedComponets.contains(dagEDGE.getDst())) {
							newDagEdges.add(new DAGEdge(srcDAGNode, dagEDGE
									.getDst()));
						}
					}
				}
			}

			// update all dag Edges to new merged component
			for (ComponentVertex v : this.dag.values()) {
				for (DAGEdge dagEDGE : v.getEdges()) {
					if (!mergedComponets.contains(dagEDGE.getDst())) {
						newDagEdges.add(new DAGEdge(v, srcDAGNode));
					}
				}
			}
=======
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

			if (srcDAGNode.ed.containsKey(dstIndex)) {
				srcDAGNode.ed.get(dstIndex).add(e);
			} else {
				HashSet<DirectedEdge> temp = new HashSet<>();
				temp.add(e);
				srcDAGNode.ed.put(dstIndex, temp);
			}

			if (dstDagNode.ed.containsKey(srcDAGNode.getIndex())) {
				HashSet<ComponentVertex> mergedComponets = mergeComponent(
						dstDagNode, srcDAGNode);
				HashMap<Integer, Set<DirectedEdge>> newDagEdges = new HashMap<>();

				// update the component and the dag edges to other nodes
				for (ComponentVertex v : mergedComponets) {

					if (v.getIndex() != srcIndex) {
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
					System.out.println("bla");

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

>>>>>>> some stuff
		}

		return true;
	}

	private HashSet<ComponentVertex> mergeComponent(ComponentVertex v,
			ComponentVertex srcComp) {
<<<<<<< HEAD
		HashSet<ComponentVertex> comp = new HashSet<>();
		for (DAGEdge edge : v.getEdges()) {
			if (edge.getDst().getIndex() == srcComp.getIndex()) {
				comp.add(edge.getDst());
			} else {
				HashSet<ComponentVertex> temp = mergeComponent(edge.getDst(),
=======
		HashSet<ComponentVertex> comp = new HashSet<ComponentVertex>();
<<<<<<< HEAD
		for (int cV : v.ed.keySet()) {
			if (cV == srcComp.getIndex()) {
				comp.add(dag.get(cV));
			} else {
				HashSet<ComponentVertex> temp = mergeComponent(dag.get(cV),
>>>>>>> some stuff
						srcComp);
				if (temp.contains(srcComp.getIndex())) {
					comp.addAll(temp);
=======
		HashSet<ComponentVertex> seen = new HashSet<ComponentVertex>();

		Queue<ComponentVertex> q = new LinkedList<>();
		q.add(v);
		seen.add(v);
		while (!q.isEmpty()) {
			ComponentVertex n = q.poll();
			for (int cV : n.ed.keySet()) {
				if (!seen.contains(this.dag.get(cV))) {
					q.add(this.dag.get(cV));
					seen.add(this.dag.get(cV));
>>>>>>> some stuff
				}
				if (this.path.containsKey(cV)) {
					this.path.get(cV).add(n.getIndex());
				} else {
					LinkedList<Integer> temp = new LinkedList<>();
					temp.add(n.getIndex());
					this.path.put(cV, temp);
				}

			}
		}
		q.add(srcComp);
		seen.clear();
		seen.add(srcComp);
		while (!q.isEmpty()) {
			ComponentVertex n = q.poll();
			comp.add(n);
			for (int i : this.path.get(n.getIndex())) {

				if (!seen.contains(this.dag.get(i))) {
					q.add(this.dag.get(i));
					seen.add(this.dag.get(i));
				}
			}
		}

		return comp;
	}

	private boolean applyAfterNodeRemoval(Update u) {
		DirectedNode node = (DirectedNode) ((NodeRemoval) u).getNode();
		for (IElement ie : node.getEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
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
