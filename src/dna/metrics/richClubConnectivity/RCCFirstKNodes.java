package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;

public abstract class RCCFirstKNodes extends Metric {
	private SortedSet<Integer> degrees;
	protected LinkedList<Node> richClub;
	protected LinkedList<Node> rest;
	private Map<Integer, LinkedList<Node>> nodesSortedByDegree;
	protected int richClubSize;
	protected int edgesBetweenRichClub;
	protected double rCC;

	public RCCFirstKNodes(String name, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		super(name, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
	}

	@Override
	protected boolean compute_() {
		for (Node n : this.g.getNodes()) {
			int degree = n.getIn().size();
			this.degrees.add(degree);
			if (nodesSortedByDegree.containsKey(degree)) {
				this.nodesSortedByDegree.get(degree).add(n);
			} else {
				LinkedList<Node> temp = new LinkedList<>();
				temp.add(n);
				this.nodesSortedByDegree.put(degree, temp);
			}

		}

		LinkedList<Node> temp = new LinkedList<Node>();
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

		for (Node n : richClub) {
			for (Node des : n.getOut()) {
				if (richClub.contains(des)) {
					edgesBetweenRichClub++;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean cleanupApplication() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void reset_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 500;
		this.rCC = 0d;
		this.richClub = new LinkedList<Node>();
		this.rest = new LinkedList<Node>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<Node>>();
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
	protected void init(Graph g) {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 500;
		this.rCC = 0d;
		this.richClub = new LinkedList<Node>();
		this.rest = new LinkedList<Node>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<Node>>();
		this.degrees = new TreeSet<Integer>();
	}

}
