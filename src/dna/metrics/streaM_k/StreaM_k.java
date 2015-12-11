package dna.metrics.streaM_k;

import java.io.IOException;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.metrics.streaM_k.grouping.Grouping;
import dna.metrics.streaM_k.groupingWithGroups.Grouping2;
import dna.metrics.streaM_k.rules.motifs.UndirectedMotifsMapping;
import dna.series.data.Value;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.util.parameters.IntParameter;
import dna.util.parameters.StringParameter;

public class StreaM_k extends Metric implements IDynamicAlgorithm, IBeforeEA,
		IAfterER {

	protected int nodes;

	protected Grouping grouping;
	protected Grouping2 grouping2;

	protected UndirectedMotifsMapping umm;

	protected BinnedIntDistr motifs;

	public StreaM_k(int nodes, Grouping grouping) throws IOException {
		super("StreaM_k", MetricType.exact, new IntParameter("nodes", nodes),
				new StringParameter("grouping", grouping.getClass()
						.getSimpleName()));
		this.nodes = nodes;
		this.grouping = grouping;
		this.grouping2 = null;
		String dir = "config/motifs/";
		String filename = "um-" + nodes;
		this.umm = UndirectedMotifsMapping.read(dir, filename);
	}

	protected StreaM_k(int nodes, Grouping2 grouping2) throws IOException {
		super("StreaM_k2", MetricType.exact, new IntParameter("nodes", nodes),
				new StringParameter("grouping2", grouping2.getClass()
						.getSimpleName()));
		this.nodes = nodes;
		this.grouping = null;
		this.grouping2 = grouping2;
		String dir = "config/motifs/";
		String filename = "um-" + nodes;
		this.umm = UndirectedMotifsMapping.read(dir, filename);
	}

	@Override
	public Value[] getValues() {
		Value[] values = new Value[this.motifs.getValues().length];
		for (int i = 0; i < values.length - 1; i++) {
			values[i] = new Value("UM" + (i + 1),
					this.motifs.getValues()[i + 1]);
		}
		values[values.length - 1] = new Value("TOTAL",
				this.motifs.getDenominator());
		return values;
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr[] { this.motifs };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[0];
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[0];
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof StreaM_k
				&& ((StreaM_k) m).nodes == this.nodes;
	}

	@Override
	public boolean equals(IMetric m) {
		StreaM_k m_ = (StreaM_k) m;
		return this.motifs.equalsVerbose(m_.motifs);
	}

	@Override
	public boolean isApplicable(Graph g) {
		return !g.isDirected();
	}

	@Override
	public boolean isApplicable(Batch b) {
		return !b.getGraphDatastructures().createsDirected();
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		HashSet<String> groups = this.grouping.getGroups(this.g, this.nodes,
				(Edge) ea.getEdge());
		for (String group : groups) {
			int[] keys = this.grouping.getKeys(g, ea.getEdge().getN1(), ea
					.getEdge().getN2(), group);

			int m0 = this.umm.getMotif(keys[0]);
			int m1 = this.umm.getMotif(keys[1]);
			if (m0 > 0) {
				this.motifs.decr(m0);
			}
			if (m1 > 0) {
				this.motifs.incr(m1);
			}
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		HashSet<String> groups = this.grouping.getGroups(this.g, this.nodes,
				(Edge) er.getEdge());
		for (String group : groups) {
			int[] keys = this.grouping.getKeys(g, er.getEdge().getN1(), er
					.getEdge().getN2(), group);

			int m0 = this.umm.getMotif(keys[0]);
			int m1 = this.umm.getMotif(keys[1]);
			if (m0 > 0) {
				this.motifs.incr(m0);
			}
			if (m1 > 0) {
				this.motifs.decr(m1);
			}
		}
		return true;
	}

	@Override
	public boolean init() {
		this.motifs = new BinnedIntDistr("motifs", 1,
				new long[this.umm.getMotifsCount() + 1], 0);

		Graph original = this.g;

		this.g = this.g.getGraphDatastructures().newGraphInstance("temp", 0,
				this.g.getNodeCount(), this.g.getEdgeCount());
		for (IElement n_ : original.getNodes()) {
			Node n = (Node) n_;
			Node newNode = this.g.getGraphDatastructures().newNodeInstance(
					n.getIndex());
			this.g.addNode(newNode);
		}

		for (IElement e_ : original.getEdges()) {
			Edge e = (Edge) e_;
			Edge newEdge = this.g.getGraphDatastructures().newEdgeInstance(
					this.g.getNode(e.getN1Index()),
					this.g.getNode(e.getN2Index()));
			EdgeAddition ea = new EdgeAddition(newEdge);
			this.applyBeforeUpdate(ea);
			ea.apply(this.g);
		}

		this.g = original;
		return true;
	}

}
