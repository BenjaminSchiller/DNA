package dna.metrics.betweenessCentrality;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class BetweenessCentrality extends Metric {

	// protected HashMap<Node, Double> bC;

	protected NodeValueList bCC;
	protected double bCSum;

	public BetweenessCentrality(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		
		this.bCC = new NodeValueList("BC_Score",
		new double[this.g.getMaxNodeIndex() + 1]);
		this.bCSum = 0d;
	}

	@Override
	public void reset_() {
		// this.bC = new HashMap<Node, Double>();
		this.bCC = new NodeValueList("BC_Score",
				new double[this.g.getMaxNodeIndex() + 1]);
		this.bCSum = 0d;
	}

	

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof BetweenessCentrality)) {
			return false;
		}
		boolean success = true;
		BetweenessCentrality bc = (BetweenessCentrality) m;

		/*
		 * detailed comparison is no longer possible -> only saved in update variant
		 */
		
//		for (IElement ie1 : g.getNodes()) {
//			Node n1 = (Node) ie1;
//			for (IElement ie2 : g.getNodes()) {
//				Node n2 = (Node) ie2;
//
//				if (!this.spcs.get(n1).get(n2).equals(bc.spcs.get(n1).get(n2))) {
//					System.out.println("diff at Tree " + n1 + "in Node n " + n2
//							+ " expected SPC "
//							+ this.spcs.get(n1).get(n2).intValue() + " is "
//							+ bc.spcs.get(n1).get(n2).intValue());
//					success = false;
//				}
//
//				if (!this.parents.get(n1).get(n2)
//						.containsAll(bc.parents.get(n1).get(n2))
//						|| this.parents.get(n1).get(n2).size() != bc.parents
//								.get(n1).get(n2).size()) {
//					System.out.println("diff at Tree " + n1 + "in Node n " + n2
//							+ " expected parents "
//							+ this.parents.get(n1).get(n2) + " is "
//							+ bc.parents.get(n1).get(n2));
//					success = false;
//				}
//
//				if (Math.abs(this.accSums.get(n1).get(n2).doubleValue()
//						- bc.accSums.get(n1).get(n2).doubleValue()) > 0.000001) {
//					System.out.println("diff at Tree " + n1 + "in Node n " + n2
//							+ " expected Sum " + this.accSums.get(n1).get(n2)
//							+ " is " + bc.accSums.get(n1).get(n2)
//							+ " height == " + bc.distances.get(n1).get(n2));
//					success = false;
//				}
//
//				if (!this.distances.get(n1).get(n2)
//						.equals(bc.distances.get(n1).get(n2))) {
//					System.out.println("diff at Tree " + n1 + "in Node n " + n2
//							+ " expected dist "
//							+ this.distances.get(n1).get(n2) + " is "
//							+ bc.distances.get(n1).get(n2));
//					success = false;
//				}
//
//			}
//		}

		for (IElement ie : g.getNodes()) {
			Node n = (Node) ie;
			if (Math.abs(this.bCC.getValue(n.getIndex())
					- bc.bCC.getValue(n.getIndex())) > 0.0001) {
				System.out.println("diff at Node n " + n + " expected Score "
						+ this.bCC.getValue(n.getIndex()) + " is "
						+ bc.bCC.getValue(n.getIndex()));
				success = false;
			}

		}

		return success;
	}

	@Override
	public Value[] getValues() {
		// Value v1 = new Value("median", getMedian());
		Value v2 = new Value("avg_bc", bCSum / (double) g.getNodeCount());
		return new Value[] { v2 };
	}

	private double getMedian() {
		double[] sortedArray = bCC.getValues();
		Arrays.sort(sortedArray);
		double median;
		if (sortedArray.length % 2 == 0) {
			median = ((double) sortedArray[sortedArray.length / 2] + (double) sortedArray[sortedArray.length / 2 + 1]) / 2;
		} else {
			median = (double) sortedArray[sortedArray.length / 2];
		}
		return median;

	}

	@Override
	public Distribution[] getDistributions() {
		// Distribution d1 = new Distribution("BetweenessCentrality",
		// getDistribution(this.bC));
		return new Distribution[] {};

	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		this.bCC.toString();
		return new NodeValueList[] { this.bCC };
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	private double[] getDistribution(
			HashMap<Node, Double> betweeneesCentralityScore2) {
		double[] temp = new double[betweeneesCentralityScore2.size()];
		int counter = 0;
		for (Node i : betweeneesCentralityScore2.keySet()) {
			temp[counter] = betweeneesCentralityScore2.get(i);
			counter++;
		}
		return temp;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof BetweenessCentrality;
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
