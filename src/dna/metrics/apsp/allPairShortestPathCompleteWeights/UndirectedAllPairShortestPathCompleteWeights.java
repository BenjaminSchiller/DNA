package dna.metrics.apsp.allPairShortestPathCompleteWeights;

import java.util.HashMap;
import java.util.PriorityQueue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedDoubleWeightedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class UndirectedAllPairShortestPathCompleteWeights extends Metric {

	protected HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>> parents;

	protected HashMap<UndirectedNode, HashMap<UndirectedNode, Double>> heights;

	public UndirectedAllPairShortestPathCompleteWeights(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			UndirectedNode s = (UndirectedNode) ie;

			HashMap<UndirectedNode, UndirectedNode> parent = new HashMap<UndirectedNode, UndirectedNode>();
			HashMap<UndirectedNode, Double> height = new HashMap<UndirectedNode, Double>();

			for (IElement iNode : g.getNodes()) {
				UndirectedNode t = (UndirectedNode) iNode;
				if (t.equals(s)) {
					height.put(s, 0d);
				} else {
					height.put(t, Double.MAX_VALUE);
				}
			}

			PriorityQueue<UndirectedNode> q = new PriorityQueue<>();
			q.add(s);
			while (!q.isEmpty()) {
				UndirectedNode current = q.poll();

				if (height.get(current) == Double.MAX_VALUE) {
					break;
				}

				for (IElement iEdge : current.getEdges()) {
					UndirectedDoubleWeightedEdge d = (UndirectedDoubleWeightedEdge) iEdge;

					UndirectedNode neighbor = d.getDifferingNode(current);

					double alt = height.get(current) + d.getWeight();

					if (alt < height.get(neighbor)) {
						height.put(neighbor, alt);
						parent.put(neighbor, current);
						if (q.contains(neighbor)) {
							q.remove(neighbor);
						}
						q.add(neighbor);
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
		this.parents = new HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>>();
		this.heights = new HashMap<UndirectedNode, HashMap<UndirectedNode, Double>>();
	}

	@Override
	public void reset_() {
		this.parents = new HashMap<UndirectedNode, HashMap<UndirectedNode, UndirectedNode>>();
		this.heights = new HashMap<UndirectedNode, HashMap<UndirectedNode, Double>>();
	}

	@Override
	public Value[] getValues() {
		return new Value[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof UndirectedAllPairShortestPathCompleteWeights)) {
			return false;
		}
		boolean success = true;
		UndirectedAllPairShortestPathCompleteWeights apsp = (UndirectedAllPairShortestPathCompleteWeights) m;

		for (UndirectedNode n1 : heights.keySet()) {
			for (UndirectedNode n2 : heights.get(n1).keySet()) {
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
		return m != null && m instanceof UndirectedAllPairShortestPathCompleteWeights;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
