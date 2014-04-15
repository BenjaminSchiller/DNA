package dna.metrics.apsp;

import java.util.Comparator;
import java.util.PriorityQueue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.graph.weights.IWeighted;
import dna.graph.weights.IntWeight;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

public class IntWeightedAllPairsShortestPathsR extends
		IntWeightedAllPairsShortestPaths {

	public IntWeightedAllPairsShortestPathsR() {
		super("IntWeightedAllPairsShortestPathsR",
				ApplicationType.Recomputation);
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

	@Override
	public boolean compute() {

		for (IElement source_ : this.g.getNodes()) {
			Node source = (Node) source_;

			int[] dist = this.getInitialDist(this.g.getMaxNodeIndex() + 1);
			Node[] previous = new Node[this.g.getMaxNodeIndex() + 1];
			boolean[] visited = new boolean[g.getMaxNodeIndex() + 1];
			PriorityQueue<Node> Q = new PriorityQueue<Node>(g.getNodeCount(),
					new DistComparator(dist));

			dist[source.getIndex()] = 0;
			Q.add(source);

			while (!Q.isEmpty()) {
				Node current = (Node) Q.remove();

				if (visited[current.getIndex()]) {
					continue;
				}
				visited[current.getIndex()] = true;

				if (current instanceof DirectedNode) {
					for (IElement e : ((DirectedNode) current)
							.getOutgoingEdges()) {
						Node n = ((DirectedEdge) e).getDst();
						IntWeight w = (IntWeight) ((IWeighted) e).getWeight();
						this.process(source, current, n, (int) w.getWeight(), dist,
								previous, visited, Q);
					}
				} else if (current instanceof UndirectedNode) {
					for (IElement e : ((UndirectedNode) current).getEdges()) {
						Node n = ((UndirectedEdge) e).getDifferingNode(current);
						IntWeight w = (IntWeight) ((IWeighted) e).getWeight();
						this.process(source, current, n, (int) w.getWeight(), dist,
								previous, visited, Q);
					}
				}
			}

			for (int d : dist) {
				if (d > 0 && d != Integer.MAX_VALUE) {
					this.apsp.incr(d);
				}
			}
		}

		return true;
	}

	protected class DistComparator implements Comparator<Node> {

		private int[] dist;

		public DistComparator(int[] dist) {
			this.dist = dist;
		}

		@Override
		public int compare(Node o1, Node o2) {
			return this.dist[o1.getIndex()] - this.dist[o2.getIndex()];
		}
	}

	protected void process(Node source, Node current, Node n, int weight,
			int[] dist, Node[] previous, boolean[] visited,
			PriorityQueue<Node> Q) {
		if (n.getIndex() == source.getIndex()) {
			return;
		}
		int newDist = dist[current.getIndex()] + weight;
		if (previous[n.getIndex()] == null || newDist < dist[n.getIndex()]) {
			dist[n.getIndex()] = newDist;
			previous[n.getIndex()] = current;
			Q.add(n);
		}
	}

	protected int[] getInitialDist(int size) {
		int[] dist = new int[size];
		for (int i = 0; i < dist.length; i++) {
			dist[i] = Integer.MAX_VALUE;
		}
		return dist;
	}

	// protected HashMap<Node, HashMap<Node, Node>> parents;
	//
	// protected HashMap<Node, HashMap<Node, Integer>> heights;
	//
	// @Override
	// public void init_() {
	// super.init_();
	// this.parents = new HashMap<Node, HashMap<Node, Node>>();
	// this.heights = new HashMap<Node, HashMap<Node, Integer>>();
	// }
	//
	// @Override
	// public void reset_() {
	// super.reset_();
	// this.parents = new HashMap<Node, HashMap<Node, Node>>();
	// this.heights = new HashMap<Node, HashMap<Node, Integer>>();
	// }
	//
	// @Override
	// public boolean compute() {
	//
	// for (IElement ie : g.getNodes()) {
	// Node s = (Node) ie;
	//
	// HashMap<Node, Node> parent = new HashMap<Node, Node>();
	// HashMap<Node, Integer> height = new HashMap<Node, Integer>();
	//
	// for (IElement iNode : g.getNodes()) {
	// Node t = (Node) iNode;
	// if (t.equals(s)) {
	// height.put(s, 0);
	// } else {
	// height.put(t, Integer.MAX_VALUE);
	// }
	// }
	// if (DirectedNode.class.isAssignableFrom(this.g
	// .getGraphDatastructures().getNodeType())) {
	// PriorityQueue<QueueElement<DirectedNode>> q = new
	// PriorityQueue<QueueElement<DirectedNode>>();
	// q.add(new QueueElement((DirectedNode) s, height.get(s)));
	// while (!q.isEmpty()) {
	// QueueElement<DirectedNode> c = q.poll();
	// DirectedNode current = c.e;
	// if (height.get(current) == Integer.MAX_VALUE) {
	// break;
	// }
	//
	// for (IElement iEdge : current.getOutgoingEdges()) {
	// DirectedIntWeightedEdge d = (DirectedIntWeightedEdge) iEdge;
	//
	// DirectedNode neighbor = d.getDst();
	//
	// int alt = height.get(current) + d.getWeight();
	// if (alt < 0) {
	// continue;
	// }
	// if (alt < height.get(neighbor)) {
	// height.put(neighbor, alt);
	// parent.put(neighbor, current);
	// QueueElement<DirectedNode> temp = new QueueElement<DirectedNode>(
	// neighbor, height.get(neighbor));
	// if (q.contains(temp)) {
	// q.remove(temp);
	// }
	// q.add(temp);
	// }
	// }
	// }
	// } else {
	// PriorityQueue<QueueElement<UndirectedNode>> q = new
	// PriorityQueue<QueueElement<UndirectedNode>>();
	// q.add(new QueueElement((UndirectedNode) s, height.get(s)));
	// while (!q.isEmpty()) {
	// QueueElement<UndirectedNode> c = q.poll();
	// UndirectedNode current = c.e;
	//
	// if (height.get(current) == Integer.MAX_VALUE) {
	// break;
	// }
	//
	// for (IElement iEdge : current.getEdges()) {
	// UndirectedIntWeightedEdge d = (UndirectedIntWeightedEdge) iEdge;
	//
	// UndirectedNode neighbor = d.getDifferingNode(current);
	//
	// int alt = height.get(current) + d.getWeight();
	// if (alt < 0) {
	// continue;
	// }
	// if (alt < height.get(neighbor)) {
	// height.put(neighbor, alt);
	// parent.put(neighbor, current);
	// QueueElement<UndirectedNode> temp = new QueueElement<UndirectedNode>(
	// neighbor, height.get(neighbor));
	// if (q.contains(temp)) {
	// q.remove(temp);
	// }
	// q.add(temp);
	// }
	// }
	// }
	// }
	// for (int i : height.values()) {
	// if (i != Integer.MAX_VALUE && i != 0) {
	// apsp.incr(i);
	// }
	// }
	// apsp.truncate();
	// parents.put(s, parent);
	// heights.put(s, height);
	// }
	//
	// return true;
	// }

}
