package dna.metrics.apsp;

import java.util.HashMap;
import java.util.PriorityQueue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

@SuppressWarnings("rawtypes")
public abstract class APSPCompleteDirectedWithWeights extends Metric {

	protected HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>> parents;

	protected HashMap<DirectedNode, HashMap<DirectedNode, Double>> heights;

	public APSPCompleteDirectedWithWeights(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			DirectedNode s = (DirectedNode) ie;

			HashMap<DirectedNode, DirectedNode> parent = new HashMap<DirectedNode, DirectedNode>();
			HashMap<DirectedNode, Double> height = new HashMap<DirectedNode, Double>();

			for (IElement iNode : g.getNodes()) {
				DirectedNode t = (DirectedNode) iNode;
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

				for (IElement iEdge : current.getOutgoingEdges()) {
					DirectedDoubleWeightedEdge d = (DirectedDoubleWeightedEdge) iEdge;

					DirectedNode neighbor = d.getDst();

					double alt = height.get(current) + d.getWeight();
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
	public void init_() {
		this.parents = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heights = new HashMap<DirectedNode, HashMap<DirectedNode, Double>>();
	}

	@Override
	public void reset_() {
		this.parents = new HashMap<DirectedNode, HashMap<DirectedNode, DirectedNode>>();
		this.heights = new HashMap<DirectedNode, HashMap<DirectedNode, Double>>();
	}

	@Override
	public Value[] getValues() {
		return new Value[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Distribution[] getDistributions() {
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
