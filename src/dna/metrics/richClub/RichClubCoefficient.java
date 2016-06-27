package dna.metrics.richClub;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.DataUtils;
import dna.util.parameters.IntParameter;

public class RichClubCoefficient extends Metric {

	protected int k;

	protected long nodes;
	protected long edges;

	public RichClubCoefficient(String name, int k) {
		super(name, MetricType.exact, new IntParameter("k", k));
		this.k = k;
	}

	@Override
	public Value[] getValues() {
		if (this.g.isDirected()) {
			return new Value[] { new Value("RCC", 1.0 * edges
					/ (nodes * (nodes - 1))) };
		} else {
			return new Value[] { new Value("RCC", 2.0 * edges
					/ (nodes * (nodes - 1))) };
		}
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr[0];
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
		return m != null && m instanceof RichClubCoefficient
				&& ((RichClubCoefficient) m).k == this.k;
	}

	@Override
	public boolean equals(IMetric m) {
		if (!this.isComparableTo(m)) {
			return false;
		}
		RichClubCoefficient rcc = (RichClubCoefficient) m;
		boolean success = true;
		success &= DataUtils.equals(this.nodes, rcc.nodes, "nodes");
		success &= DataUtils.equals(this.edges, rcc.edges, "edges");
		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	public boolean compute() {
		nodes = 0;
		edges = 0;
		if (this.g.isDirected()) {
			for (IElement v_ : this.g.getNodes()) {
				DirectedNode v = (DirectedNode) v_;
				if (v.getDegree() > this.k) {
					nodes++;
					for (IElement e_ : v.getOutgoingEdges()) {
						DirectedNode w = ((DirectedEdge) e_).getDst();
						if (w.getDegree() > this.k) {
							edges++;
						}
					}
				}
			}

		} else {
			for (IElement v_ : this.g.getNodes()) {
				UndirectedNode v = (UndirectedNode) v_;
				if (v.getDegree() > k) {
					nodes++;
					for (IElement e_ : v.getEdges()) {
						Node w = ((UndirectedEdge) e_).getDifferingNode(v);
						if (w.getDegree() > k && v.getIndex() < w.getIndex()) {
							edges++;
						}
					}
				}
			}
		}
		return true;
	}

}
