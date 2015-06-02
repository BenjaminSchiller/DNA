package dna.metrics.connectivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Value;
import dna.series.data.distributions.Distribution;
import dna.series.data.distributions.DistributionDouble;
import dna.series.data.nodevaluelists.NodeNodeValueList;
import dna.series.data.nodevaluelists.NodeValueList;
import dna.updates.batch.Batch;

public abstract class WeakConnectivity extends Metric {

	protected HashMap<Node, Integer> nodeComponentMembership;
	protected boolean[] visited;
	protected HashMap<Node, Node> parents;
	protected HashMap<Integer, WeakComponent> componentList;
	protected HashMap<Integer, Integer> componentConnection;
	protected int counter;

	public WeakConnectivity(String name) {
		super(name);
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("NumberofComponents", countComponents());
		Value v2 = new Value("AverageComponentSize",
				calculateAverageComponentSize());

		return new Value[] { v1, v2 };
	}

	private double countComponents() {
		return componentList.size();
	}

	private double calculateAverageComponentSize() {
		if (this.componentList.isEmpty()) {
			return 0;
		} else {
			return (double) this.g.getNodeCount()
					/ (double) componentList.size();
		}
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution d1 = new DistributionDouble("Components",
				calculateComponents());
		return new Distribution[] { d1 };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	private double[] calculateComponents() {
		double[] sumComp = new double[componentList.size()];
		int counter = 0;
		for (WeakComponent n : componentList.values()) {
			sumComp[counter] = n.getSize();
			counter++;
		}
		return sumComp;
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof WeakConnectivity;
	}

	@Override
	public boolean equals(IMetric m) {
		if (!(m instanceof WeakConnectivity)) {
			return false;
		}
		WeakConnectivity cc = (WeakConnectivity) m;

		boolean success = true;
		if (this.componentList.size() != cc.componentList.size()) {
			System.out.println("diff @ number of components expected "
					+ this.componentList.size() + " is "
					+ cc.componentList.size());

			success = false;
		}

		HashMap<Integer, Integer> sizes = new HashMap<Integer, Integer>();
		for (WeakComponent cV : this.componentList.values()) {
			if (sizes.containsKey(cV.getSize())) {
				sizes.put(cV.getSize(), sizes.get(cV.getSize()) + 1);
			} else {
				sizes.put(cV.getSize(), +1);
			}
		}
		for (WeakComponent cV : cc.componentList.values()) {
			if (!sizes.containsKey(cV.getSize())) {
				System.out.println("no existing size " + cV.getSize()
						+ " Index " + cV.getIndex());
				success = false;
			} else if (sizes.get(cV.getSize()) == 0) {
				System.out.println("to much of this size " + cV.getSize()
						+ " Index " + cV.getIndex());
				success = false;
			} else {
				sizes.put(cV.getSize(), sizes.get(cV.getSize()) - 1);
			}

		}

		HashMap<Integer, Integer> check = new HashMap<>();
		for (IElement node : g.getNodes()) {
			Node n = (Node) node;
			int id1 = this.nodeComponentMembership.get(n);
			int id2 = cc.lookUp(n);
			int size1 = this.componentList.get(id1).getSize();
			int size2 = cc.componentList.get(id2).getSize();

			if (size1 != size2) {
				System.out.println("component with wrong size for node " + n
						+ " expected " + size1 + " is " + size2);
				success = false;
			}
			if (check.containsKey(id1)) {
				if (!check.get(id1).equals(id2)) {
					System.out.println("component with wrong index for node "
							+ n + " expected " + check.get(id1) + " is " + id2);
					success = false;
				}
			} else {
				check.put(id1, id2);
			}
		}

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

	protected boolean compute() {
		this.nodeComponentMembership = new HashMap<Node, Integer>();
		this.parents = new HashMap<Node, Node>();
		this.visited = new boolean[this.g.getMaxNodeIndex() + 1];
		this.componentList = new HashMap<Integer, WeakComponent>();
		this.componentConnection = new HashMap<>();
		this.counter = 0;

		for (IElement ie : g.getNodes()) {
			Node n = (Node) ie;
			if (!this.visited[n.getIndex()]) {
				bfs(n);
			}
		}
		return true;
	}

	protected void bfs(Node node) {
		int comp = counter++;
		Queue<Node> q = new LinkedList<Node>();
		WeakComponent root = new WeakComponent(comp);
		int size = 0;
		this.componentList.put(comp, root);
		q.add(node);
		visited[node.getIndex()] = true;
		while (!q.isEmpty()) {
			size++;
			Node temp = q.poll();
			this.nodeComponentMembership.put(temp, comp);
			for (IElement ie : temp.getEdges()) {
				Edge n = (Edge) ie;
				Node dst = n.getDifferingNode(temp);
				if (!visited[dst.getIndex()]) {
					visited[dst.getIndex()] = true;
					parents.put(dst, temp);
					q.add(dst);
				}
			}
		}
		root.setSize(size);
	}

	protected int lookUp(Node n) {
		int result = this.nodeComponentMembership.get(n);
		while (componentConnection.containsKey(result)) {
			result = componentConnection.get(result);
		}
		return result;
	}

}
