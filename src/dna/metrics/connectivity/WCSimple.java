package dna.metrics.connectivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.Log;

public class WCSimple extends Metric {

	public ArrayList<WCComponent> components;

	public WCSimple(String name, MetricType metricType) {
		super(name, metricType);
	}

	@Override
	public Value[] getValues() {
		Value components = new Value("count", this.components.size());
		Value max = new Value("max", this.components.get(0).size());
		return new Value[] { components, max };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		BinnedIntDistr d = new BinnedIntDistr("components");
		for (WCComponent c : this.components) {
			d.incr(c.size());
		}
		return new Distr[] { d };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		NodeValueList ids = new NodeValueList("ids", 0);
		for (WCComponent c : this.components) {
			for (Node n : c.getNodes()) {
				ids.setValue(n.getIndex(), c.getIndex());
			}
		}
		return new NodeValueList[] { ids };
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof WCSimple;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof WCSimple)) {
			return false;
		}
		WCSimple m_ = (WCSimple) m;
		if (this.components.size() != m_.components.size()) {
			Log.warn("size differs: " + this.components.size() + " != "
					+ m_.components.size());
			return false;
		}
		for (int i = 0; i < this.components.size(); i++) {
			if (this.components.get(i).size() != m_.components.get(i).size()) {
				Log.warn("size of component #" + i + " differs: "
						+ this.components.get(i).size() + " != "
						+ m_.components.get(i).size());
				return false;
			}
			if (this.components.get(i).getIndex() != m_.components.get(i)
					.getIndex()) {
				Log.warn("index of component #" + i + " differs: "
						+ this.components.get(i).getIndex() + " != "
						+ m_.components.get(i).getIndex());
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return true;
	}

	@Override
	public boolean isApplicable(Batch b) {
		return true;
	}

	public void sortComponents() {
		Collections.sort(this.components, new Comparator<WCComponent>() {
			@Override
			public int compare(WCComponent c1, WCComponent c2) {
				if (c1.size() != c2.size()) {
					return c2.size() - c1.size();
				} else {
					return c2.getIndex() - c1.getIndex();
				}
			}
		});
	}

	public boolean compute() {
		this.components = getComponents(this.g.getNodes());

		this.sortComponents();

		return true;
	}

	protected static ArrayList<WCComponent> getComponents(
			Iterable<IElement> nodes) {
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
