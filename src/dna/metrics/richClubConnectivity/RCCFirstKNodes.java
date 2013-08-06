package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class RCCFirstKNodes extends Metric {
	protected SortedSet<Integer> degrees;
	protected LinkedList<DirectedNode> richClub;
	protected LinkedList<DirectedNode> rest;
	protected Map<Integer, LinkedList<DirectedNode>> nodesSortedByDegree;
	protected int richClubSize;
	protected int edgesBetweenRichClub;
	protected double rCC;

	public RCCFirstKNodes(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public boolean compute() {
		DirectedGraph g = (DirectedGraph) this.g;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			for (DirectedNode n : g.getNodes()) {

				int degree = n.getOutDegree();
				this.degrees.add(degree);
				if (nodesSortedByDegree.containsKey(degree)) {
					this.nodesSortedByDegree.get(degree).add(n);
				} else {
					LinkedList<DirectedNode> temp = new LinkedList<>();
					temp.add(n);
					this.nodesSortedByDegree.put(degree, temp);
				}

			}

			LinkedList<DirectedNode> temp = new LinkedList<DirectedNode>();
			int size = this.degrees.size();
			for (int i = 0; i < size; i++) {
				int currentDegree = this.degrees.last();
				this.degrees.remove(currentDegree);
				temp.addAll(nodesSortedByDegree.get(currentDegree));
			}

			// First k biggest Nodes to richClub
			richClub.addAll(temp.subList(0, richClubSize));
			// the rest are maintained in Rest
			rest.addAll(temp.subList(richClubSize, temp.size()));

			for (DirectedNode n : richClub) {
				for (DirectedEdge e : n.getOutgoingEdges()) {
					if (richClub.contains(e.getDst())) {
						edgesBetweenRichClub++;
					}
				}
			}
		}
		caculateRCC();
		return true;
	}

	protected void caculateRCC() {
		this.rCC = (double) this.edgesBetweenRichClub
				/ (double) (this.richClubSize * (this.richClubSize - 1));
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof RCCFirstKNodes)) {
			return false;
		}
		RCCFirstKNodes rcc = (RCCFirstKNodes) m;

		boolean success = true;
		success &= this.richClub.equals(rcc.richClub);
		success &= (this.rCC == rcc.rCC);

		return success;
	}

	@Override
	public void reset_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 500;
		this.rCC = 0d;
		this.richClub = new LinkedList<DirectedNode>();
		this.rest = new LinkedList<DirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.degrees = new TreeSet<Integer>();

	}

	@Override
	protected Value[] getValues() {
		Value v1 = new Value("RichClubCoeffizient", this.rCC);
		Value v2 = new Value("RichClubSize", this.richClub.size());
		Value v3 = new Value("EdgesBetweenRichClub", this.edgesBetweenRichClub);
		return new Value[] { v1, v2, v3 };
	}

	@Override
	protected Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	protected void init_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 500;
		this.rCC = 0d;
		this.richClub = new LinkedList<DirectedNode>();
		this.rest = new LinkedList<DirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.degrees = new TreeSet<Integer>();
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RCCFirstKNodes;
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
