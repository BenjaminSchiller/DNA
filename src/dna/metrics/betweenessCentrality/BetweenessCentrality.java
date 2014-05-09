package dna.metrics.betweenessCentrality;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
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
import dna.series.data.BinnedDistributionDouble;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class BetweenessCentrality extends Metric {

	// protected HashMap<Node, Double> bC;

	protected NodeValueList bCC;
	protected double bCSum;
	
	protected BinnedDistributionDouble binnedBC;
	protected int sumShortestPaths;

	public BetweenessCentrality(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		this.bCC = new NodeValueList("BC_Score",
		new double[this.g.getMaxNodeIndex() + 1]);
		this.binnedBC = new BinnedDistributionDouble("Normalized-BC", 0.01d);
		this.bCSum = 0d;
		this.sumShortestPaths = 0;
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
		
		if(sumShortestPaths != bc.getSumShortestPaths()){
			success = false;
			System.out.println("diff at sum of shortest paths: " + sumShortestPaths + " is expected. Result is: " + bc.getSumShortestPaths());
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
		computeBinnedBC();
		return new Distribution[] {binnedBC};

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

	protected void computeBinnedBC() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
	
		for(Double d : bCC.getValues()){
			max = (d > max) ? d : max;
			min = (d < min) ? d : min;
		}

		
		for(Double d : bCC.getValues()){
			double norm = 0;
			if(sumShortestPaths!=0)
				norm = d/sumShortestPaths;
			else
				norm = 0.0;
			
			binnedBC.incr(norm);
		}
	}

	protected int sumSPFromHM(HashMap<Node, Integer> spc, Node n) {
		int sum = 0;
		for(Entry<Node, Integer> e : spc.entrySet()){
			if(!e.getKey().equals(n)){
				sum += e.getValue();
			}
		}
		return sum;
	}

	public int getSumShortestPaths() {
		return sumShortestPaths;
	}

}
