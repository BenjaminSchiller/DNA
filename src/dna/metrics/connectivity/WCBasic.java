package dna.metrics.connectivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.ArrayUtils;

public class WCBasic extends Metric {

	public NodeValueList ids;

	public WCBasic(String name) {
		super(name, MetricType.exact);
	}

	@Override
	public Value[] getValues() {
		return new Value[] {};
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		return new Distr[] {};
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] { ids };
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof WCBasic;
	}

	@Override
	public boolean equals(IMetric m) {
		WCBasic m_ = (WCBasic) m;
		boolean success = true;
		success &= ArrayUtils.equals(this.ids.getValues(), m_.ids.getValues(),
				"IDS");
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

	public ArrayList<WCComponent> components;

	protected boolean compute() {
		this.components = getComponents(this.g.getNodes());
		this.setIds();
		return true;
	}

	protected void setIds() {
		this.ids = new NodeValueList("ids", this.g.getMaxNodeIndex());
		for (WCComponent c : this.components) {
			for (Node n : c.getNodes()) {
				this.ids.setValue(n.getIndex(), c.getIndex());
			}
		}
	}

	public static ArrayList<WCComponent> getComponents(Iterable<IElement> nodes) {
		ArrayList<WCComponent> components = new ArrayList<WCComponent>();
		HashSet<Node> seen = new HashSet<Node>();

		for (IElement n_ : nodes) {
			Node n = (Node) n_;
			if (seen.contains(n)) {
				continue;
			}
			WCComponent comp = new WCComponent();
			components.add(comp);

			Queue<Node> stack = new LinkedList<Node>();
			seen.add(n);
			stack.add(n);
			comp.addNode(n);

			while (!stack.isEmpty()) {
				Node current = stack.poll();
				for (IElement e_ : current.getEdges()) {
					Edge e = (Edge) e_;
					Node neighbor = e.getDifferingNode(current);
					if (seen.contains(neighbor)) {
						continue;
					}
					seen.add(neighbor);
					stack.add(neighbor);
					comp.addNode(neighbor);
					comp.addEdge(e);
				}
			}
		}
		return components;
	}

}
