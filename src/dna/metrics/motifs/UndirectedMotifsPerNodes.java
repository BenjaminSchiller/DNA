package dna.metrics.motifs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import dna.graph.IGraph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.parameters.IntParameter;
import dna.util.parameters.Parameter;

public abstract class UndirectedMotifsPerNodes extends Metric {

	public static enum UndirectedMotifType {
		UM1, UM2, UM3, UM4, UM5, UM6
	};

	protected BinnedIntDistr motifs;

	protected int[] nodeIndexes;
	protected UndirectedNode[] nodes;
	protected HashSet<UndirectedNode> nodesSet;
	protected HashSet<String> motifsFound;

	public UndirectedMotifsPerNodes(String name, int... nodeIndexes) {
		super(name, MetricType.exact, getParameters(nodeIndexes));
		this.nodeIndexes = nodeIndexes;
		this.nodes = null;
	}

	public static Parameter[] getParameters(int[] nodeIndexes) {
		Parameter[] p = new Parameter[nodeIndexes.length];
		for (int i = 0; i < nodeIndexes.length; i++) {
			p[i] = new IntParameter("node" + i, nodeIndexes[i]);
		}
		return p;
	}

	@Override
	public Value[] getValues() {
		Value m0 = new Value("TOTAL", this.motifs.getDenominator());
		Value m1 = new Value("UM1", (double) this.motifs.getValues()[1]);
		Value m2 = new Value("UM2", (double) this.motifs.getValues()[2]);
		Value m3 = new Value("UM3", (double) this.motifs.getValues()[3]);
		Value m4 = new Value("UM4", (double) this.motifs.getValues()[4]);
		Value m5 = new Value("UM5", (double) this.motifs.getValues()[5]);
		Value m6 = new Value("UM6", (double) this.motifs.getValues()[6]);
		return new Value[] { m0, m1, m2, m3, m4, m5, m6 };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr<?, ?>[] { this.motifs };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		if (m == null || !(m instanceof UndirectedMotifsPerNodes)) {
			return false;
		}
		UndirectedMotifsPerNodes m_ = (UndirectedMotifsPerNodes) m;
		if (m_.nodeIndexes.length != this.nodeIndexes.length) {
			return false;
		}
		for (int i = 0; i < this.nodeIndexes.length; i++) {
			if (this.nodeIndexes[i] != m_.nodeIndexes[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof UndirectedMotifsPerNodes)) {
			return false;
		}
		UndirectedMotifsPerNodes um = (UndirectedMotifsPerNodes) m;
		boolean success = true;
		success &= this.motifs.equalsVerbose(um.motifs);
		return success;
	}

	@Override
	public boolean isApplicable(IGraph g) {
		return g.getGraphDatastructures().isNodeType(UndirectedNode.class);
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(UndirectedNode.class);
	}

	protected UndirectedNode[] getNeighborsSorted(UndirectedNode n) {
		ArrayList<UndirectedNode> unsorted = new ArrayList<UndirectedNode>(
				n.getDegree());
		for (IElement e : n.getEdges()) {
			UndirectedNode neighbor = (UndirectedNode) ((UndirectedEdge) e)
					.getDifferingNode(n);
			// if (neighbor.getIndex() > n.getIndex()) {
			unsorted.add(neighbor);
			// }
		}
		UndirectedNode[] sorted = unsorted.toArray(new UndirectedNode[unsorted
				.size()]);
		Arrays.sort(sorted);
		return sorted;
	}

	protected UndirectedMotifType getType(boolean ab, boolean ac, boolean ad,
			boolean bc, boolean bd, boolean cd) {

		return null;
	}

	protected UndirectedMotifType getType(boolean bc, boolean bd, boolean cd) {
		int sum = (bc ? 1 : 0) + (bd ? 1 : 0) + (cd ? 1 : 0);

		if (sum == 0) {
			return UndirectedMotifType.UM2;
		} else if (sum == 1) {
			return UndirectedMotifType.UM4;
		} else if (sum == 2) {
			return UndirectedMotifType.UM5;
		} else if (sum == 3) {
			return UndirectedMotifType.UM6;
		}

		return null;
	}

	public HashSet<NodeGroup> groups;

	protected void addGroup(UndirectedNode... nodes) {
		NodeGroup g = new NodeGroup(nodes);
		if (!g.containsDuplicate()) {
			groups.add(g);
		}
	}

	protected boolean compute() {
		this.nodes = new UndirectedNode[this.nodeIndexes.length];
		this.nodesSet = new HashSet<UndirectedNode>();
		for (int i = 0; i < this.nodeIndexes.length; i++) {
			this.nodes[i] = (UndirectedNode) this.g
					.getNode(this.nodeIndexes[i]);
			this.nodesSet.add(this.nodes[i]);
		}
		this.motifs = new BinnedIntDistr("UndirectedMotifs", 1, new long[7], 0);
		this.motifsFound = new HashSet<String>();

		UndirectedNode a = this.nodes[0];
		UndirectedNode[] a_ = this.getNeighborsSorted(a);

		groups = new HashSet<NodeGroup>();

		// 3 nodes
		for (int i = 0; i < a_.length; i++) {
			for (int j = i + 1; j < a_.length; j++) {
				for (int k = j + 1; k < a_.length; k++) {
					this.addGroup(a, a_[i], a_[j], a_[k]);
				}
			}
		}

		// 2 nodes
		for (int i = 0; i < a_.length; i++) {
			for (int j = i + 1; j < a_.length; j++) {
				for (UndirectedNode d : this.getNeighborsSorted(a_[i])) {
					this.addGroup(a, a_[i], a_[j], d);
				}
				for (UndirectedNode d : this.getNeighborsSorted(a_[j])) {
					this.addGroup(a, a_[i], a_[j], d);
				}
			}
		}

		// 1 node (I)
		for (UndirectedNode b : a_) {
			UndirectedNode[] b_ = this.getNeighborsSorted(b);
			for (UndirectedNode c : b_) {
				UndirectedNode[] c_ = this.getNeighborsSorted(c);
				for (UndirectedNode d : c_) {
					this.addGroup(a, b, c, d);
				}
			}
		}

		// 1 node (II)
		for (UndirectedNode b : a_) {
			UndirectedNode[] b_ = this.getNeighborsSorted(b);
			for (int i = 0; i < b_.length; i++) {
				for (int j = i + 1; j < b_.length; j++) {
					this.addGroup(a, b, b_[i], b_[j]);
				}
			}
		}

		for (NodeGroup g : groups) {
			this.count(g);
		}

		return true;
	}

	protected void count(NodeGroup g) {
		int counter = 0;
		for (UndirectedNode n : g.nodes) {
			counter += this.nodesSet.contains(n) ? 1 : 0;
		}
		if (counter != this.nodes.length) {
			return;
		}

		int edges = 0;
		int[] degree = new int[4];
		boolean[][] am = new boolean[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = i + 1; j < 4; j++) {
				am[i][j] = g.nodes[i].hasEdge(g.nodes[i], g.nodes[j]);
				am[j][i] = am[i][j];
				if (am[i][j]) {
					edges++;
					degree[i]++;
					degree[j]++;
				}
			}
		}

		if (edges == 3) {
			if (degree[0] == 3 || degree[1] == 3 || degree[2] == 3
					|| degree[3] == 3) {
				this.motifs.incr(2);
			} else {
				this.motifs.incr(1);
			}
		} else if (edges == 4) {
			if (degree[0] == 3 || degree[1] == 3 || degree[2] == 3
					|| degree[3] == 3) {
				this.motifs.incr(4);
			} else {
				this.motifs.incr(3);
			}
		} else if (edges == 5) {
			this.motifs.incr(5);
		} else if (edges == 6) {
			this.motifs.incr(6);
		}
	}

	public static class NodeGroup {
		public static Comparator<UndirectedNode> comp = new NodeComparator();
		public UndirectedNode[] nodes;

		public NodeGroup(UndirectedNode... nodes) {
			this.nodes = nodes;
			Arrays.sort(this.nodes, comp);
		}

		public boolean containsDuplicate() {
			return nodes[0].getIndex() == nodes[1].getIndex()
					|| nodes[1].getIndex() == nodes[2].getIndex()
					|| nodes[2].getIndex() == nodes[3].getIndex();
		}

		public String toString() {
			return nodes[0].getIndex() + " " + nodes[1].getIndex() + " "
					+ nodes[2].getIndex() + " " + nodes[3].getIndex();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof NodeGroup)) {
				return false;
			}
			NodeGroup g = (NodeGroup) obj;
			return nodes[0].equals(g.nodes[0]) && nodes[1].equals(g.nodes[1])
					&& nodes[2].equals(g.nodes[2])
					&& nodes[3].equals(g.nodes[3]);
		}

		@Override
		public int hashCode() {
			return this.toString().hashCode();
		}
	}

	public static class NodeComparator implements Comparator<UndirectedNode> {
		@Override
		public int compare(UndirectedNode o1, UndirectedNode o2) {
			return o1.getIndex() - o2.getIndex();
		}
	}

}
