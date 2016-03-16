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
import dna.metrics.algorithms.IRecomputation;
import dna.series.data.Value;
import dna.series.data.distr.BinnedIntDistr;
import dna.series.data.distr.Distr;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;
import dna.util.Log;

public class WCSimpleR extends Metric implements IRecomputation {

	public ArrayList<WCComponent> components;

	public WCSimpleR() {
		super("WCSimpleR", MetricType.exact);
		this.components = new ArrayList<WCComponent>();
	}

	public WCSimpleR(String[] nodeTypes) {
		super("WCSimpleR", MetricType.exact, nodeTypes);
		this.components = new ArrayList<WCComponent>();
	}

	@Override
	public Value[] getValues() {
		Value components = new Value("count", this.components.size());
		Value max = new Value("max", this.components.get(0).nodes.size());
		return new Value[] { components, max };
	}

	@Override
	public Distr<?, ?>[] getDistributions() {
		BinnedIntDistr d = new BinnedIntDistr("components");
		for (WCComponent c : this.components) {
			d.incr(c.nodes.size());
		}
		return new Distr[] { d };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		NodeValueList ids = new NodeValueList("ids", 0);
		for (WCComponent c : this.components) {
			for (Node n : c.nodes) {
				ids.setValue(n.getIndex(), c.index);
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
		return m != null && m instanceof WCSimpleR;
	}

	@Override
	public boolean equals(IMetric m) {
		if (m == null || !(m instanceof WCSimpleR)) {
			return false;
		}
		WCSimpleR m_ = (WCSimpleR) m;
		if (this.components.size() != m_.components.size()) {
			Log.warn("size differs: " + this.components.size() + " != "
					+ m_.components.size());
			return false;
		}
		for (int i = 0; i < this.components.size(); i++) {
			if (this.components.get(i).nodes.size() != m_.components.get(i).nodes
					.size()) {
				Log.warn("size of component #" + i + " differs: "
						+ this.components.get(i).nodes.size() + " != "
						+ m_.components.get(i).nodes.size());
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

	@Override
	public boolean recompute() {
		this.components = new ArrayList<WCComponent>();

		HashSet<Node> seen = new HashSet<Node>();

		for (IElement n_ : this.g.getNodes()) {
			Node n = (Node) n_;
			if (seen.contains(n)) {
				continue;
			}
			WCComponent comp = new WCComponent(this.components.size() + 1);
			this.components.add(comp);

			Queue<Node> stack = new LinkedList<Node>();
			seen.add(n);
			stack.add(n);

			while (!stack.isEmpty()) {
				Node current = stack.poll();
				comp.nodes.add(current);

				for (IElement e_ : current.getEdges()) {
					Node neighbor = ((Edge) e_).getDifferingNode(current);
					if (seen.contains(neighbor)) {
						continue;
					}
					seen.add(neighbor);
					stack.add(neighbor);
				}
			}
		}

		this.sortComponents();

		return true;
	}

	public void sortComponents() {
		Collections.sort(this.components, new Comparator<WCComponent>() {
			@Override
			public int compare(WCComponent c1, WCComponent c2) {
				return c2.nodes.size() - c1.nodes.size();
			}
		});
	}

}
