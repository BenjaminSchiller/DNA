package dna.metrics.motifs;

import java.util.HashSet;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;
import dna.updates.update.Update;

/**
 * 
 * this metric gives a computation of the total number of directed 3-node motifs
 * in a network. also, it computed an upper bound for the number of motifs based
 * on the degree distribution and an approximation of the number of motifs based
 * on the assumption that all nodes have roughly the same degree.
 * 
 * @author benni
 * 
 */
public class CountingDirectedMotifsR extends Metric {

	protected int computation;

	protected int upperBound;

	protected int approximation;

	public CountingDirectedMotifsR() {
		super("CountingDirectedMotifsR", ApplicationType.Recomputation,
				MetricType.exact);
	}

	@Override
	public boolean applyBeforeBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		return false;
	}

	@Override
	public boolean applyBeforeUpdate(Update u) {
		return false;
	}

	@Override
	public boolean applyAfterUpdate(Update u) {
		return false;
	}

	@Override
	public boolean compute() {
		int sum1 = 0;
		int sum2 = 0;
		int upper1 = 0;
		int upper2 = 0;
		for (IElement element : this.g.getNodes()) {
			DirectedNode node = (DirectedNode) element;
			HashSet<DirectedNode> neighbors = this.getConnectedNodes(node);
			int n = neighbors.size();
			sum1 += n * (n - 1) / 2;
			upper1 += n * (n - 1) / 2;
			for (DirectedNode neighbor : neighbors) {
				sum2 += this.getConnectedNodesHigherId(neighbor,
						node.getIndex(), neighbors).size();
				// upper2 += this.getConnectedNodes(neighbor).size() - 1;
			}
			upper2 += n * (n - 1);
		}
		this.computation = (2 * sum2 - sum1) / 3;
		this.upperBound = (2 * upper2 - upper1) / 3;
		double e = this.g.getEdgeCount();
		double v = this.g.getNodeCount();
		double d = e / v;
		// this.totalApproximation = (int) (5.0 / 6.0 * (e * e - 1) / v);
		this.approximation = (int) (5.0 / 6.0 * v * d * (d - 1));

		return true;
	}

	private HashSet<DirectedNode> getConnectedNodes(DirectedNode node) {
		HashSet<DirectedNode> n = new HashSet<DirectedNode>();
		for (IElement in : node.getIncomingEdges()) {
			n.add(((DirectedEdge) in).getSrc());
		}
		for (IElement out : node.getOutgoingEdges()) {
			n.add(((DirectedEdge) out).getDst());
		}
		return n;
	}

	private HashSet<DirectedNode> getConnectedNodesHigherId(DirectedNode node,
			int exclude, HashSet<DirectedNode> neighbors) {
		HashSet<DirectedNode> n = new HashSet<DirectedNode>();
		for (IElement in : node.getIncomingEdges()) {
			DirectedNode neighbor = ((DirectedEdge) in).getSrc();
			if (neighbor.getIndex() < node.getIndex()
					&& neighbors.contains(neighbor)) {
				continue;
			}
			if (neighbor.getIndex() == exclude) {
				continue;
			}
			n.add(neighbor);
		}
		for (IElement out : node.getOutgoingEdges()) {
			DirectedNode neighbor = ((DirectedEdge) out).getDst();
			if (neighbor.getIndex() < node.getIndex()
					&& neighbors.contains(neighbor)) {
				continue;
			}
			if (neighbor.getIndex() == exclude) {
				continue;
			}
			n.add(neighbor);
		}
		return n;
	}

	@Override
	public void init_() {
		this.approximation = 0;
		this.computation = 0;
		this.upperBound = 0;
	}

	@Override
	public void reset_() {
		this.approximation = 0;
		this.computation = 0;
		this.upperBound = 0;
	}

	@Override
	public Value[] getValues() {
		Value comp = new Value("COMPUTATION", this.computation);
		Value upper = new Value("UPPER_BOUND", this.upperBound);
		Value approx = new Value("APPROXIMATION", this.approximation);
		return new Value[] { comp, upper, approx };
	}

	@Override
	public Distribution[] getDistributions() {
		return new Distribution[] {};
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
	public boolean equals(Metric m) {
		if (m == null || !(m instanceof CountingDirectedMotifsR)) {
			return false;
		}
		CountingDirectedMotifsR c = (CountingDirectedMotifsR) m;
		return this.approximation == c.approximation
				&& this.computation == c.computation
				&& this.upperBound == c.upperBound;
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

	@Override
	public boolean isComparableTo(Metric m) {
		return m instanceof CountingDirectedMotifsR;
	}

}
