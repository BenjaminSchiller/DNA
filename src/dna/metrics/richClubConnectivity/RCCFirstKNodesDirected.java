package dna.metrics.richClubConnectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class RCCFirstKNodesDirected extends Metric {
	protected SortedSet<Integer> degrees;
	protected LinkedList<DirectedNode> richClub;
	protected Map<Integer, LinkedList<DirectedNode>> nodesSortedByDegree;
	protected int richClubSize;
	protected Map<DirectedNode, Integer> positonInRcc;

	protected int edgesBetweenRichClub;
	protected double rCC;

	public RCCFirstKNodesDirected(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public boolean compute() {
		for (IElement iE : g.getNodes()) {
			DirectedNode n = (DirectedNode) iE;
			positonInRcc.put(n, Integer.MAX_VALUE);
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
			temp.addAll(this.nodesSortedByDegree.get(currentDegree));
			this.nodesSortedByDegree.remove(currentDegree);
			if (temp.size() >= this.richClubSize) {
				break;
			}
		}

		// First k biggest Nodes to richClub
		richClub.addAll(temp.subList(0, richClubSize));
		// the rest are maintained in nodesortedbydegree

		LinkedList<DirectedNode> rest = new LinkedList<DirectedNode>(
				temp.subList(richClubSize, temp.size()));
		if (!rest.isEmpty()) {
			this.nodesSortedByDegree.put(temp.get(richClubSize).getOutDegree(),
					rest);
		}

		int counter = 0;
		for (DirectedNode n : richClub) {
			positonInRcc.put(n, counter);
			counter++;
			for (IElement iE : n.getOutgoingEdges()) {
				DirectedEdge e = (DirectedEdge) iE;
				if (richClub.contains(e.getDst())) {
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
		if (m == null || !(m instanceof RCCFirstKNodesDirected)) {
			return false;
		}
		RCCFirstKNodesDirected rcc = (RCCFirstKNodesDirected) m;
		boolean success = true;
		if (this.richClub.size() != rcc.richClub.size()) {
			System.out.println("diff @ richClubSize");

			success = false;
		}

		for (int i = 0; i < this.richClub.size(); i++) {

			if (this.richClub.get(i).getOutDegree() != rcc.richClub.get(i)
					.getOutDegree()) {
				success = false;
				System.out.println("Position i " + i + " : "
						+ this.richClub.get(i).getOutDegree() + " "
						+ rcc.richClub.get(i).getOutDegree());
			}
		}

		return success;
	}

	@Override
	public void reset_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 200;
		this.rCC = 0d;
		this.richClub = new LinkedList<DirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.degrees = new TreeSet<Integer>();
		this.positonInRcc = new HashMap<>();

	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("RichClubCoeffizient", this.rCC);
		Value v2 = new Value("RichClubSize", this.richClub.size());
		Value v3 = new Value("EdgesBetweenRichClub", this.edgesBetweenRichClub);
		return new Value[] { v1, v2, v3 };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] {};
	}

	@Override
	public void init_() {
		this.edgesBetweenRichClub = 0;
		this.richClubSize = 200;
		this.rCC = 0d;
		this.richClub = new LinkedList<DirectedNode>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.degrees = new TreeSet<Integer>();
		this.positonInRcc = new HashMap<>();
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof RCCFirstKNodesDirected;
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
