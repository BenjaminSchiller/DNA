package dna.metrics.richClubConnectivity.directedRichClubConnectivitySizeN;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class DirectedRichClubConnectivitySizeN extends Metric {
	protected Map<Integer, LinkedList<DirectedNode>> richClub;
	protected Map<Integer, LinkedList<DirectedNode>> nodesSortedByDegree;
	protected int richClubSize;
	protected int edgesBetweenRichClub;

	public DirectedRichClubConnectivitySizeN(String name, ApplicationType type,
			int richClubSize) {
		super(name, type, MetricType.exact);
		this.richClubSize = richClubSize;
	}

	@Override
	public boolean compute() {

		SortedSet<Integer> degrees = new TreeSet<Integer>();
		for (IElement iE : g.getNodes()) {
			DirectedNode n = (DirectedNode) iE;
			int degree = n.getOutDegree();
			degrees.add(degree);
			if (nodesSortedByDegree.containsKey(degree)) {
				this.nodesSortedByDegree.get(degree).add(n);
			} else {
				LinkedList<DirectedNode> temp = new LinkedList<>();
				temp.add(n);
				this.nodesSortedByDegree.put(degree, temp);
			}

		}

		HashSet<DirectedNode> currentRichClub = new HashSet<>();
		int currentRichClubSize = 0;
		int size = degrees.size();
		for (int i = 0; i < size; i++) {
			int currentDegree = degrees.last();
			degrees.remove(currentDegree);
			LinkedList<DirectedNode> current = this.nodesSortedByDegree
					.get(currentDegree);
			currentRichClubSize += current.size();
			this.nodesSortedByDegree.remove(currentDegree);

			if (currentRichClubSize >= this.richClubSize) {
				int seperateAT = current.size()
						- (currentRichClubSize - this.richClubSize);
				LinkedList<DirectedNode> temp = new LinkedList<>();
				temp.addAll(current.subList(0, seperateAT));
				this.richClub.put(currentDegree, temp);
				currentRichClub.addAll(temp);
				LinkedList<DirectedNode> temp2 = new LinkedList<>();
				temp2.addAll(current.subList(seperateAT, current.size()));
				if (!temp.isEmpty())
					this.nodesSortedByDegree.put(currentDegree,
							(LinkedList<DirectedNode>) temp2);
				break;
			} else {
				richClub.put(currentDegree, current);
				currentRichClub.addAll(current);
			}
		}

		for (DirectedNode n : currentRichClub) {
			for (IElement iE : n.getOutgoingEdges()) {
				DirectedEdge e = (DirectedEdge) iE;
				if (currentRichClub.contains(e.getDst())) {
					edgesBetweenRichClub++;
				}
			}
		}
		for (IElement ie : g.getNodes()) {
			DirectedNode node = (DirectedNode) ie;
			if (!((richClub.containsKey(node.getOutDegree()) && richClub.get(
					node.getOutDegree()).contains(node)) || (nodesSortedByDegree
					.containsKey(node.getOutDegree()) && nodesSortedByDegree
					.get(node.getOutDegree()).contains(node)))) {
				System.out.println("fuck");
			}
		}
		return true;
	}

	private double caculateRCC() {
		return (double) this.edgesBetweenRichClub
				/ (double) (this.richClubSize * (this.richClubSize - 1));
	}

	@Override
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof DirectedRichClubConnectivitySizeN)) {
			return false;
		}
		DirectedRichClubConnectivitySizeN rcc = (DirectedRichClubConnectivitySizeN) m;
		boolean success = true;
		if (this.richClub.size() != rcc.richClub.size()) {
			System.out.println("diff @ richClubSize expected "
					+ this.richClub.size() + " is " + rcc.richClub.size());
			success = false;
		}

		if (!this.richClub.keySet().containsAll(rcc.richClub.keySet())) {
			System.out.println("diff @ contained Degrees");
			System.out.println("expected " + this.richClub.keySet());
			System.out.println("is " + rcc.richClub.keySet());
			System.out.println("expected rest"
					+ this.nodesSortedByDegree.keySet());
			System.out.println("is rest" + rcc.nodesSortedByDegree.keySet());
			success = false;
		}

		if (success) {
			int thisSize = 0;
			int rccSize = 0;
			for (int i : this.richClub.keySet()) {
				thisSize += this.richClub.get(i).size();
				rccSize += rcc.richClub.get(i).size();
				if (this.richClub.get(i).size() != rcc.richClub.get(i).size()) {
					success = false;
					System.out.println("Degree i " + i + " expected size : "
							+ this.richClub.get(i).size() + " is "
							+ rcc.richClub.get(i).size());
				}
			}
			if (thisSize != rccSize || thisSize != this.richClubSize) {
				System.out.println("Size " + this.richClubSize
						+ " expected size : " + thisSize + " is " + rccSize);
			}
		}
		return success;
	}

	@Override
	public void reset_() {
		this.edgesBetweenRichClub = 0;
		this.richClub = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("RichClubCoeffizient", this.caculateRCC());
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
		this.richClub = new HashMap<Integer, LinkedList<DirectedNode>>();
		this.nodesSortedByDegree = new HashMap<Integer, LinkedList<DirectedNode>>();
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof DirectedRichClubConnectivitySizeN;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
