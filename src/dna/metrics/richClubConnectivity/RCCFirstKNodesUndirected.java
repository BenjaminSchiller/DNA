package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class RCCFirstKNodesUndirected extends Metric {
	protected SortedSet<Integer> degrees;
	protected LinkedList<UndirectedNode> richClub;
	protected Map<Integer, LinkedList<UndirectedNode>> nodesSortedByDegree;
	protected int richClubSize;

	protected int edgesBetweenRichClub;
	protected double rCC;

	public RCCFirstKNodesUndirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public boolean compute() {
		UndirectedGraph g = (UndirectedGraph) this.g;

		for (UndirectedNode n : g.getNodes()) {

			int degree = n.getDegree();
			this.degrees.add(degree);
			if (nodesSortedByDegree.containsKey(degree)) {
				this.nodesSortedByDegree.get(degree).add(n);
			} else {
				LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
				temp.add(n);
				this.nodesSortedByDegree.put(degree, temp);
			}

		}

		LinkedList<UndirectedNode> temp = new LinkedList<UndirectedNode>();
		int size = this.degrees.size();
		for (int i = 0; i < size; i++) {
			int currentDegree = this.degrees.last();
			this.degrees.remove(currentDegree);
			temp.addAll(this.nodesSortedByDegree.get(currentDegree));
			this.nodesSortedByDegree.remove(currentDegree);
			if (temp.size() >= this.richClubSize) {
				break;
			}
		}

		// First k biggest Nodes to richClub
		richClub.addAll(temp.subList(0, richClubSize));
		// the rest are maintained in nodesortedbydegree

		LinkedList<UndirectedNode> rest = new LinkedList<UndirectedNode>(
				temp.subList(richClubSize, temp.size()));
		if (!rest.isEmpty()) {
			this.nodesSortedByDegree.put(temp.get(richClubSize).getDegree(),
					rest);
		}

		for (UndirectedNode n : richClub) {
			for (UndirectedEdge e : n.getEdges()) {
				UndirectedNode node = e.getNode1();
				if (node == n) {
					node = e.getNode2();
				}
				if (richClub.contains(node)) {
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
		if (m == null || !(m instanceof RCCFirstKNodesUndirected)) {
			return false;
		}
		RCCFirstKNodesUndirected rcc = (RCCFirstKNodesUndirected) m;
		boolean success = true;
		if (this.richClub.size() != rcc.richClub.size()) {
			System.out.println("diff @ richClubSize");

			success = false;
		}

		for (int i = 0; i < this.richClub.size(); i++) {

			if (this.richClub.get(i).getDegree() != rcc.richClub.get(i)
					.getDegree()) {
				success = false;
				System.out.println(this.richClub.get(i).getDegree() + " "
						+ rcc.richClub.get(i).getDegree());
			}
		}

		return success;
	}

	@Override
	public void reset_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 200;
		this.rCC = 0d;
		this.richClub = new LinkedList<UndirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<UndirectedNode>>();
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
		this.richClubSize = 200;
		this.rCC = 0d;
		this.richClub = new LinkedList<UndirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<UndirectedNode>>();
		this.degrees = new TreeSet<Integer>();
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RCCFirstKNodesUndirected;
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

	public void setRichClubSize(int richClubSize) {
		this.richClubSize = richClubSize;
	}

}
