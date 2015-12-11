package dna.metrics.streaM_k;

import java.io.IOException;
import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IDynamicAlgorithm;
import dna.metrics.streaM_k.grouping.GroupingV1;
import dna.metrics.streaM_k.groupingWithGroups.Group;
import dna.metrics.streaM_k.groupingWithGroups.Grouping2;
import dna.metrics.streaM_k.rules.motifs.UndirectedMotifsMapping;
import dna.series.data.distr.BinnedIntDistr;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;

public class StreaM_k2 extends StreaM_k implements IDynamicAlgorithm,
		IBeforeEA, IAfterER {

	// protected int nodes;

	protected Grouping2 grouping2;

	// protected UndirectedMotifsMapping umm;

	// protected BinnedIntDistr motifs;

	public StreaM_k2(int nodes, Grouping2 grouping) throws IOException {
		super(nodes, grouping);
		// super("StreaM_k2", MetricType.exact, new IntParameter("nodes",
		// nodes),
		// new StringParameter("grouping", grouping.getClass()
		// .getSimpleName()));
		// this.nodes = nodes;
		this.grouping2 = grouping;
		String dir = "config/motifs/";
		String filename = "um-" + nodes;
		this.umm = UndirectedMotifsMapping.read(dir, filename);
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		HashSet<Group> groups = this.grouping2.getGroups(this.nodes,
				(Edge) ea.getEdge());
		for (Group group : groups) {
			int[] keys = group.getKeys(ea.getEdge().getN1(), ea.getEdge()
					.getN2());

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
		HashSet<Group> groups = this.grouping2.getGroups(this.nodes,
				(Edge) er.getEdge());
		for (Group group : groups) {
			int[] keys = group.getKeys(er.getEdge().getN1(), er.getEdge()
					.getN2());

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
		// if (true) {
		// return true;
		// }

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
