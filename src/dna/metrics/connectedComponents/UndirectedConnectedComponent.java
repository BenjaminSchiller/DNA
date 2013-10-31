package dna.metrics.connectedComponents;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class UndirectedConnectedComponent extends Metric {

	protected HashMap<UndirectedNode, Integer> nodeComponentMembership;
	protected boolean[] visited;
	protected HashMap<UndirectedNode, UndirectedNode> parents;
	protected HashMap<Integer, UndirectedComponent> componentList;
	protected HashMap<Integer, Integer> componentConnection;
	protected int counter;

	public UndirectedConnectedComponent(String name, ApplicationType type) {
		super(name, type, MetricType.exact);
	}

	@Override
	public void init_() {
		this.nodeComponentMembership = new HashMap<UndirectedNode, Integer>();
		this.parents = new HashMap<UndirectedNode, UndirectedNode>();
		this.visited = new boolean[this.g.getMaxNodeIndex() + 1];
		this.componentList = new HashMap<Integer, UndirectedComponent>();
		this.componentConnection = new HashMap<>();
		this.counter = 0;
	}

	@Override
	public void reset_() {
		this.nodeComponentMembership = new HashMap<UndirectedNode, Integer>();
		this.visited = new boolean[this.g.getMaxNodeIndex() + 1];
		this.parents = new HashMap<UndirectedNode, UndirectedNode>();
		this.componentList = new HashMap<Integer, UndirectedComponent>();
		this.componentConnection = new HashMap<>();
		this.counter = 0;
	}

	@Override
	public boolean compute() {
		for (IElement ie : g.getNodes()) {
			UndirectedNode n = (UndirectedNode) ie;
			if (!this.visited[n.getIndex()]) {
				bfs(n);
			}
		}

		return true;
	}

	protected void bfs(UndirectedNode node) {
		int comp = counter++;
		Queue<UndirectedNode> q = new LinkedList<UndirectedNode>();
		UndirectedComponent root = new UndirectedComponent(comp);
		int size = 0;
		this.componentList.put(comp, root);
		q.add(node);
		visited[node.getIndex()] = true;
		while (!q.isEmpty()) {
			size++;
			UndirectedNode temp = q.poll();
			this.nodeComponentMembership.put(temp, comp);
			for (IElement ie : temp.getEdges()) {
				UndirectedEdge n = (UndirectedEdge) ie;
				UndirectedNode dst = n.getDifferingNode(temp);
				if (!visited[dst.getIndex()]) {
					visited[dst.getIndex()] = true;
					parents.put(dst, temp);
					q.add(dst);
				}
			}
		}
		root.setSize(size);
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof UndirectedConnectedComponent)) {
			return false;
		}
		UndirectedConnectedComponent cc = (UndirectedConnectedComponent) m;

		boolean success = true;
		if (this.componentList.size() != cc.componentList.size()) {
			System.out.println("diff @ number of components expected "
					+ this.componentList.size() + " is "
					+ cc.componentList.size());
			success = false;
		}

		HashMap<Integer, Integer> sizes = new HashMap<Integer, Integer>();
		for (UndirectedComponent cV : this.componentList.values()) {
			if (sizes.containsKey(cV.getSize())) {
				sizes.put(cV.getSize(), sizes.get(cV.getSize()) + 1);
			} else {
				sizes.put(cV.getSize(), +1);
			}
		}
		for (UndirectedComponent cV : cc.componentList.values()) {
			if (!sizes.containsKey(cV.getSize())) {
				System.out.println("no existing size " + cV.getSize()
						+ " Index " + cV.getSize());
				success = false;
			} else if (sizes.get(cV.getSize()) == 0) {
				System.out.println("to much of this size " + cV.getSize()
						+ " Index " + cV.getSize());
				success = false;
			} else {
				sizes.put(cV.getSize(), sizes.get(cV.getSize()) - 1);
			}

		}

		boolean success1 = check(this, cc);
		return success && success1;
	}

	private boolean check(UndirectedConnectedComponent c1,
			UndirectedConnectedComponent c2) {
		if (c1.nodeComponentMembership.size() != c2.nodeComponentMembership
				.size()) {
			System.out.println("diff @Number of  pointers to components "
					+ c1.nodeComponentMembership.size() + " != "
					+ c1.nodeComponentMembership.size());
			return false;
		}

		HashMap<Integer, Integer> checkComp = new HashMap<Integer, Integer>();
		boolean success = true;

		for (UndirectedNode i : c1.nodeComponentMembership.keySet()) {
			if (!c2.nodeComponentMembership.containsKey(i)) {
				System.out.println("missing pointer for " + i);
				return false;
			}

			if (checkComp.containsKey(c1.nodeComponentMembership.get(i))) {
				if (!checkComp.get(c1.nodeComponentMembership.get(i)).equals(
						c2.lookUp(i))) {
					System.out.println("diff @ node " + i
							+ " wrong component: expected "
							+ checkComp.get(c1.nodeComponentMembership.get(i))
							+ " is " + c2.lookUp(i));
					success = false;
				}
			} else {
				checkComp.put(c1.nodeComponentMembership.get(i), c2.lookUp(i));
			}
		}

		return success;
	}

	public int lookUp(UndirectedNode n) {
		int result = this.nodeComponentMembership.get(n);
		while (componentConnection.containsKey(result)) {
			result = componentConnection.get(result);
		}
		return result;
	}

	// /compcounter
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
		return this.g.getNodes().size() / componentList.size();
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution d1 = new Distribution("Components", calculateComponents());
		return new Distribution[] { d1 };
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	private double[] calculateComponents() {
		double[] sumComp = new double[componentList.size()];
		int counter = 0;
		for (UndirectedComponent n : componentList.values()) {
			sumComp[counter] = n.getSize();
			counter++;
		}
		return sumComp;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof UndirectedConnectedComponent;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return UndirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return UndirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType());
	}

}
