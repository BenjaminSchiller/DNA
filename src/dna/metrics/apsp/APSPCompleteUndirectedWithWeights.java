package dna.metrics.apsp;

import java.util.HashMap;
import java.util.PriorityQueue;

import dna.graph.Graph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedEdgeWeighted;
import dna.graph.undirected.UndirectedGraphAlAl;
import dna.graph.undirected.UndirectedNode;
import dna.graph.undirected.UndirectedNodeAlWeighted;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class APSPCompleteUndirectedWithWeights extends Metric {

	protected HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>> parents;

	protected HashMap<UndirectedNode, HashMap<UndirectedNode, Double>> heights;

	public APSPCompleteUndirectedWithWeights(String name, ApplicationType type) {
		super(name, type);

	}

	@Override
	public boolean compute() {
		UndirectedGraphAlAl g = (UndirectedGraphAlAl) this.g;

		for (UndirectedNode s : g.getNodes()) {

			HashMap<UndirectedNode, UndirectedNode> parent = new HashMap<UndirectedNode, UndirectedNode>();
			HashMap<UndirectedNode, Double> height = new HashMap<UndirectedNode, Double>();

			for (UndirectedNode t : g.getNodes()) {
				if (t.equals(s)) {
					height.put(s, 0d);
				} else {
					height.put(t, Double.MAX_VALUE);
				}
			}

			PriorityQueue<UndirectedNodeAlWeighted> q = new PriorityQueue<>();
			q.add((UndirectedNodeAlWeighted) s);
			while (!q.isEmpty()) {
				UndirectedNodeAlWeighted current = q.poll();

				if (height.get(current) == Double.MAX_VALUE) {
					break;
				}

				for (UndirectedEdge e : current.getEdges()) {
					UndirectedEdgeWeighted d = (UndirectedEdgeWeighted) e;

					UndirectedNode neighbor = d.getDifferingNode(current);

					double alt = height.get(current) + d.getWeight();

					if (alt < height.get(neighbor)) {
						height.put(neighbor, alt);
						parent.put(neighbor, current);
						if (q.contains(neighbor)) {
							q.remove(neighbor);
						}
						q.add((UndirectedNodeAlWeighted) neighbor);
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
		this.parents = new HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>>();
		this.heights = new HashMap<UndirectedNode, HashMap<UndirectedNode, Double>>();
	}

	@Override
	public void reset_() {
		this.parents = new HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>>();
		this.heights = new HashMap<UndirectedNode, HashMap<UndirectedNode, Double>>();
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
		if (!(m instanceof APSPCompleteDirected)) {
			return false;
		}
		boolean success = true;
		APSPCompleteUndirectedWithWeights apsp = (APSPCompleteUndirectedWithWeights) m;

		for (UndirectedNode n1 : heights.keySet()) {
			for (UndirectedNode n2 : heights.get(n1).keySet()) {
				if (this.heights.get(n1).get(n2).intValue() != apsp.heights
						.get(n1).get(n2).intValue()) {
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
		return m != null && m instanceof APSPCompleteUndirectedWithWeights;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

}
