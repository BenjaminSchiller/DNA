package dna.metrics.connectedComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import sun.misc.Queue;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;

public abstract class CCDirected extends Metric {

	protected int compCounter;
	private boolean[] visited;
	private boolean[] discoverd;
	protected int[] nodeComponentMembership;
	protected SpanningTreeNode[] nodesTreeElement;
	protected Map<Integer, List<Node>> reachableNodesFromComponet;

	// new algorithm
	protected List<List<ComponentVertex>> components;
	protected int[] nodeComponentMembership2;

	public CCDirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init() {
		this.nodeComponentMembership = new int[this.g.getNodes().size()];
		this.nodesTreeElement = new SpanningTreeNode[this.g.getNodes().size()];
		this.visited = new boolean[this.g.getNodes().size()];
		this.discoverd = new boolean[this.g.getNodes().size()];
		this.reachableNodesFromComponet = new HashMap<Integer, List<Node>>();
		this.compCounter = 0;
	}

	@Override
	public void reset_() {
		this.nodeComponentMembership = new int[this.g.getNodes().size()];
		this.nodesTreeElement = new SpanningTreeNode[this.g.getNodes().size()];
		this.visited = new boolean[this.g.getNodes().size()];
		this.discoverd = new boolean[this.g.getNodes().size()];
		this.reachableNodesFromComponet = new HashMap<Integer, List<Node>>();
		this.compCounter = 0;
	}

	@Override
	protected boolean compute_() {
		for (Node n : this.g.getNodes()) {
			if (!visited[n.getIndex()]) {
				this.discoverd = new boolean[this.g.getNodes().size()];
				bfs(n);
			}
		}

		return true;
	}

	private void bfs(Node node) {
		try {
			compCounter++;
			int comp = node.getIndex();
			Queue q = new Queue();
			q.enqueue(new SpanningTreeNode(node));
			this.discoverd[node.getIndex()] = true;

			List<Node> reachables = new ArrayList<Node>();
			while (!q.isEmpty()) {
				SpanningTreeNode temp = (SpanningTreeNode) q.dequeue();
				for (Node n : temp.getNode().getOut()) {
					if (!this.discoverd[n.getIndex()]) {
						this.discoverd[n.getIndex()] = true;
						SpanningTreeNode newChild = new SpanningTreeNode(n);
						newChild.setParent(temp);
						temp.addChild(newChild);
						reachables.add(n);
						q.enqueue(newChild);

					} else if (!this.visited[n.getIndex()]) {
						this.visited[n.getIndex()] = true;
						this.nodeComponentMembership[temp.getNode().getIndex()] = comp;
						this.nodesTreeElement[temp.getNode().getIndex()] = temp;
						reachables.remove(n);
					}
				}
			}

			reachableNodesFromComponet.put(comp, reachables);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof CCDirected)) {
			return false;
		}
		CCDirected cc = (CCDirected) m;

		if (!ArrayUtils.equals(this.nodeComponentMembership,
				cc.getNodeComponentMembership())) {
			return false;
		}

		if (this.compCounter != cc.getCompCounter()) {
			return false;
		}
		return true;
	}

	@Override
	protected Value[] getValues() {
		Value v1 = new Value("NumberofComponents", countComponents());
		Value v2 = new Value("AverageComponentSize",
				calculateAverageComponentSize());

		return new Value[] { v1, v2 };
	}

	private double countComponents() {
		HashSet<Integer> compSize = new HashSet<Integer>();
		for (int i = 0; i < nodeComponentMembership.length; i++) {
			if (!compSize.contains(this.nodeComponentMembership[i])) {
				compSize.add(this.nodeComponentMembership[i]);
			}
		}
		return compSize.size();
	}

	private double calculateAverageComponentSize() {
		HashSet<Integer> compSize = new HashSet<Integer>();
		for (int i = 0; i < nodeComponentMembership.length; i++) {
			if (!compSize.contains(this.nodeComponentMembership[i])) {
				compSize.add(this.nodeComponentMembership[i]);
			}
		}

		return this.g.getNodes().size() / compSize.size();
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("Components", calculateComponents());
		return new Distribution[] { d1 };
	}

	private double[] calculateComponents() {
		HashMap<Integer, Integer> sum = new HashMap<Integer, Integer>();
		for (int i = 0; i < nodeComponentMembership.length; i++) {
			if (sum.containsKey(nodeComponentMembership[i])) {
				int temp = sum.get(nodeComponentMembership[i]) + 1;
				sum.put(nodeComponentMembership[i], temp);
			} else {
				sum.put(nodeComponentMembership[i], 1);
			}
		}
		double[] components = new double[sum.size()];
		int count = 0;
		for (int s : sum.keySet()) {
			components[count] = sum.get(s);
			count++;
		}

		return components;
	}

	public int[] getNodeComponentMembership() {
		return nodeComponentMembership;
	}

	public int getCompCounter() {
		return compCounter;
	}
}
