package dna.metrics.apsp;

import java.util.HashMap;
import java.util.PriorityQueue;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedEdgeWeighted;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class APSPCompleteDirectedWithWeights extends Metric {

	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parents;

	protected HashMap<DirectedNode, HashMap<DirectedNode, Double>> heights;

	public APSPCompleteDirectedWithWeights(String name, ApplicationType type) {
		super(name, type);

	}

	@Override
	public boolean compute() {
		DirectedGraph g = (DirectedGraph) this.g;
		System.out.println(g.getEdges());
		for (DirectedNode s : g.getNodes()) {

			HashMap<DirectedNode, DirectedNode> parent = new HashMap<DirectedNode, DirectedNode>();
			HashMap<DirectedNode, Double> height = new HashMap<DirectedNode, Double>();

			for (DirectedNode t : g.getNodes()) {
				if (t.equals(s)) {
					height.put(s, 0d);
				} else {
					height.put(t, Double.MAX_VALUE);
				}
			}

			PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<>();
			q.add(new QueueElement(s, height.get(s)));
			while (!q.isEmpty()) {
				QueueElement<DirectedNode> c = q.poll();
				DirectedNode current = c.e;
				if (height.get(current) == Double.MAX_VALUE) {
					break;
				}

				for (DirectedEdge e : current.getOutgoingEdges()) {
					DirectedEdgeWeighted d = (DirectedEdgeWeighted) e;

					DirectedNode neighbor = d.getDst();

					double alt = height.get(current) + 1d;
					// d.getWeight();

					if (alt < height.get(neighbor)) {
						height.put(neighbor, alt);
						parent.put(neighbor, current);
						if (q.contains(neighbor)) {
							q.remove(neighbor);
						}
						QueueElement<DirectedNode> temp = new QueueElement<DirectedNode>(
								neighbor, height.get(neighbor));
						if (q.contains(temp)) {
							q.remove(temp);
						}
						q.add(temp);
					}
				}
			}
			parents.put(s, parent);
			heights.put(s, height);
		}

		return true;
	}

	@Override
	protected void init_() {
		this.parents = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heights = new HashMap<DirectedNode, HashMap<DirectedNode, Double>>();
	}

	@Override
	public void reset_() {
		this.parents = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heights = new HashMap<DirectedNode, HashMap<DirectedNode, Double>>();
	}

	@Override
	protected Value[] getValues() {
		return new Value[] {};
	}

	@Override
	protected Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof APSPCompleteDirectedWithWeights)) {
			return false;
		}
		boolean success = true;
		APSPCompleteDirectedWithWeights apsp = (APSPCompleteDirectedWithWeights) m;

		for (DirectedNode n1 : heights.keySet()) {
			for (DirectedNode n2 : heights.get(n1).keySet()) {
				if (this.heights.get(n1).get(n2).doubleValue() != apsp.heights
						.get(n1).get(n2).doubleValue()) {
					success = false;
					System.out.println("Diff @ Height for Node " + n2
							+ " in Tree " + n1 + " expected "
							+ this.heights.get(n1).get(n2) + " is "
							+ apsp.heights.get(n1).get(n2));
				}
			}

		}

		return success;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof APSPCompleteDirectedWithWeights;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| DirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| DirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

}
