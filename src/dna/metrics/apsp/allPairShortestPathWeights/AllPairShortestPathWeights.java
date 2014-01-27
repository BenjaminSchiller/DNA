package dna.metrics.apsp.allPairShortestPathWeights;

import java.util.HashMap;
import java.util.PriorityQueue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedIntWeightedEdge;
import dna.graph.edges.UndirectedIntWeightedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.metrics.apsp.QueueElement;
import dna.series.data.Distribution;
import dna.series.data.DistributionInt;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class AllPairShortestPathWeights extends Metric {

	protected HashMap<Node, HashMap<Node, Node>> parents;
	protected HashMap<Node, HashMap<Node, Integer>> heights;
	protected DistributionInt dists;

	public AllPairShortestPathWeights(String name, ApplicationType type) {
		super(name, type, MetricType.exact);

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
				if (i != Integer.MAX_VALUE) {
					dists.incr(i);
				}
			}
			dists.truncate();
			parents.put(s, parent);
			heights.put(s, height);
		}

		return true;
	}

	@Override
	public void init_() {
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Integer>>();
		this.dists = new DistributionInt("ShortestPathDist");
	}

	@Override
	public void reset_() {
		this.parents = new HashMap<Node, HashMap<Node, Node>>();
		this.heights = new HashMap<Node, HashMap<Node, Integer>>();
		this.dists = new DistributionInt("ShortestPathDist");
	}

	@Override
	public Value[] getValues() {
		double avg1;
		double avg2;
		double dia;
		if (g.getNodeCount() != 0) {
			dists.truncate();
			double sum1 = getSum();
			avg1 = sum1 / (double) dists.getDenominator();
			avg2 = sum1 / (double) (g.getNodeCount() * (g.getNodeCount() - 1));
			dia = this.dists.getMax();
		} else {
			avg1 = 0d;
			avg2 = 0d;
			dia = 0d;
		}

		Value v1 = new Value("avg_shortest_path_Number_Existing_Paths", avg1);
		Value v2 = new Value("diameter", dia);
		Value v3 = new Value("avg_shortest_path_Number_Possible_Paths", avg2);
		return new Value[] { v1, v2, v3 };
	}

	private double getSum() {
		double s = 0d;
		int[] v = dists.getIntValues();
		for (int i = 0; i < v.length; i++) {
			s += v[i];
		}
		return s;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution[] result;
		if (g.getNodeCount() != 0) {
			this.dists.truncate();
			result = new Distribution[] { dists };
		} else {
			result = new Distribution[0];
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
				if (!this.heights.get(n1).get(n2)
						.equals(apsp.heights.get(n1).get(n2))) {
					success = false;
					System.out.println("Diff @ Height for Node " + n2
							+ " in Tree " + n1 + " expected "
							+ this.heights.get(n1).get(n2) + " is "
							+ apsp.heights.get(n1).get(n2));
					if (n1.getIndex() == 93 && n2.getIndex() == 90) {
						System.out.println("parent from "
								+ n1
								+ " is "
								+ parents.get(n1).get(n2)
								+ " and "
								+ apsp.parents.get(n1).get(n2)
								+ " heights "
								+ this.heights.get(n1).get(
										parents.get(n1).get(n2))
								+ " "
								+ apsp.heights.get(n1).get(
										parents.get(n1).get(n2)));
					}
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
