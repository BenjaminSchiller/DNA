package dna.metrics.apsp.allPairShortestPathWeights;

import java.util.HashMap;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.DirectedIntWeightedEdge;
import dna.graph.edges.UndirectedIntWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.apsp.QueueElement;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class AllPairShortestPathWeightsR extends AllPairShortestPathWeights {

	public AllPairShortestPathWeightsR() {
		super("AllPairShortestPathWeightsR", ApplicationType.Recomputation);
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
		return false;
	}

	// @Override
	// public boolean compute() {
	// // TODO Auto-generated method stub
	// return false;
	// }

	protected HashMap<Node, HashMap<Node, Node>> parents;

	protected HashMap<Node, HashMap<Node, Integer>> heights;

	@Override
	public void init_() {
		super.init_();
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Integer>>();
	}

	@Override
	public void reset_() {
		super.reset_();
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Integer>>();
	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			Node s = (Node) ie;

			HashMap<Node, Node> parent = new HashMap<Node, Node>();
			HashMap<Node, Integer> height = new HashMap<Node, Integer>();

			for (IElement iNode : g.getNodes()) {
				Node t = (Node) iNode;
				if (t.equals(s)) {
					height.put(s, 0);
				} else {
					height.put(t, Integer.MAX_VALUE);
				}
			}
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<QueueElement<DirectedNode>>();
				q.add(new QueueElement((DirectedNode) s, height.get(s)));
				while (!q.isEmpty()) {
					QueueElement<DirectedNode> c = q.poll();
					DirectedNode current = c.e;
					if (height.get(current) == Integer.MAX_VALUE) {
						break;
					}

					for (IElement iEdge : current.getOutgoingEdges()) {
						DirectedIntWeightedEdge d = (DirectedIntWeightedEdge) iEdge;

						DirectedNode neighbor = d.getDst();

						int alt = height.get(current) + d.getWeight();
						if (alt < 0) {
							continue;
						}
						if (alt < height.get(neighbor)) {
							height.put(neighbor, alt);
							parent.put(neighbor, current);
							QueueElement<DirectedNode> temp = new QueueElement<DirectedNode>(
									neighbor, height.get(neighbor));
							if (q.contains(temp)) {
								q.remove(temp);
							}
							q.add(temp);
						}
					}
				}
			} else {
				PriorityQueue<QueueElement<UndirectedNode>> q = new PriorityQueue<QueueElement<UndirectedNode>>();
				q.add(new QueueElement((UndirectedNode) s, height.get(s)));
				while (!q.isEmpty()) {
					QueueElement<UndirectedNode> c = q.poll();
					UndirectedNode current = c.e;

					if (height.get(current) == Integer.MAX_VALUE) {
						break;
					}

					for (IElement iEdge : current.getEdges()) {
						UndirectedIntWeightedEdge d = (UndirectedIntWeightedEdge) iEdge;

						UndirectedNode neighbor = d.getDifferingNode(current);

						int alt = height.get(current) + d.getWeight();
						if (alt < 0) {
							continue;
						}
						if (alt < height.get(neighbor)) {
							height.put(neighbor, alt);
							parent.put(neighbor, current);
							QueueElement<UndirectedNode> temp = new QueueElement<UndirectedNode>(
									neighbor, height.get(neighbor));
							if (q.contains(temp)) {
								q.remove(temp);
							}
							q.add(temp);
						}
					}
				}
			}
			for (int i : height.values()) {
				if (i != Integer.MAX_VALUE && i != 0) {
					apsp.incr(i);
				}
			}
			apsp.truncate();
			parents.put(s, parent);
			heights.put(s, height);
		}

		return true;
	}

}
