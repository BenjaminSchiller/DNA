package dna.metrics.apsp.allPairShortestPathWeights;

import java.util.HashMap;
import java.util.PriorityQueue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedDoubleWeightedEdge;
import dna.graph.edges.UndirectedDoubleWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metrics.apsp.QueueElement;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class AllPairShortestPathWeights extends Metric {

	protected HashMap<Node, HashMap<Node, Node>> parents;
	protected HashMap<Node, HashMap<Node, Double>> heights;

	public AllPairShortestPathWeights(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

	}

	@Override
	public boolean compute() {

		for (IElement ie : g.getNodes()) {
			Node s = (Node) ie;

			HashMap<Node, Node> parent = new HashMap<Node, Node>();
			HashMap<Node, Double> height = new HashMap<Node, Double>();

			for (IElement iNode : g.getNodes()) {
				Node t = (Node) iNode;
				if (t.equals(s)) {
					height.put(s, 0d);
				} else {
					height.put(t, Double.MAX_VALUE);
				}
			}
			if (DirectedNode.class.isAssignableFrom(this.g
					.getGraphDatastructures().getNodeType())) {
				PriorityQueue<QueueElement<DirectedNode>> q = new PriorityQueue<QueueElement<DirectedNode>>();
				q.add(new QueueElement((DirectedNode) s, height.get(s)));
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
			} else {
				PriorityQueue<QueueElement<UndirectedNode>> q = new PriorityQueue<QueueElement<UndirectedNode>>();
				q.add(new QueueElement((UndirectedNode) s, height.get(s)));
				while (!q.isEmpty()) {
					QueueElement<UndirectedNode> c = q.poll();
					UndirectedNode current = c.e;

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
			parents.put(s, parent);
			heights.put(s, height);
		}

		return true;
	}

	@Override
	public void init_() {
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Double>>();
		newResults = true;

	}

	@Override
	public void reset_() {
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Double>>();
		newResults = true;

	}

	@Override
	public Value[] getValues() {
		getValueAndDist();
		Value v1 = new Value("diameter", diameter);
		Value v2 = new Value("avg_path_length", avg_path_length
				/ (g.getNodeCount() * (g.getNodeCount() - 1)));
		return new Value[] { v1, v2 };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	double diameter;
	double avg_path_length;
	Distribution[] result;
	boolean newResults;

	@Override
	public Distribution[] getDistributions() {
		getValueAndDist();
		return result;
	}

	private void getValueAndDist() {
		if (!newResults) {
			newResults = true;
			return;
		}
		int i = 0;
		result = new Distribution[this.heights.size()];
		for (Node n : heights.keySet()) {
			result[i] = new Distribution("distsForNode_" + n.getIndex(),
					getDistribution(this.heights.get(n)));
			i++;
		}
		newResults = false;
	}

	private double[] getDistribution(HashMap<Node, Double> hashMap) {
		double[] result = new double[this.g.getMaxNodeIndex() + 1];
		for (Node d : hashMap.keySet()) {
			result[d.getIndex()] = hashMap.get(d);
			diameter = Math.max(diameter, hashMap.get(d));
			avg_path_length += hashMap.get(d);
		}

		return result;
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof AllPairShortestPathWeights)) {
			return false;
		}
		boolean success = true;
		AllPairShortestPathWeights apsp = (AllPairShortestPathWeights) m;

		for (Node n1 : heights.keySet()) {
			for (Node n2 : heights.get(n1).keySet()) {
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
		return m != null && m instanceof AllPairShortestPathWeights;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return Node.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return Node.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
