package dna.metrics.connectedComponents;

import java.util.ArrayList;
import java.util.List;

import sun.misc.Queue;
import dna.graph.Graph;
import dna.graph.Node;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;

public abstract class CCUndirected extends Metric {

	protected int[] nodeComponentMembership;
	private boolean[] visited;
	protected SpanningTreeNode[] nodesTreeElement;
	protected List<SpanningTreeNode> componentList;

	public CCUndirected(String name, boolean appliedBeforeDiff,
			boolean appliedAfterEdge, boolean appliedAfterDiff) {
		super(name, appliedBeforeDiff, appliedAfterEdge, appliedAfterDiff);
	}

	@Override
	protected void init(Graph g) {
		this.nodeComponentMembership = new int[this.g.getNodes().length];
		this.nodesTreeElement = new SpanningTreeNode[this.g.getNodes().length];
		this.visited = new boolean[this.g.getNodes().length];
		this.componentList = new ArrayList<SpanningTreeNode>();
	}

	@Override
	public void reset_() {
		this.nodeComponentMembership = new int[this.g.getNodes().length];
		this.visited = new boolean[this.g.getNodes().length];
		this.nodesTreeElement = new SpanningTreeNode[this.g.getNodes().length];
		this.componentList = new ArrayList<SpanningTreeNode>();
	}

	@Override
	protected boolean compute_() {
		for (Node n : this.g.getNodes()) {
			if (!this.visited[n.getIndex()]) {
				bfs(n);
			}
		}
		for (SpanningTreeNode n : nodesTreeElement) {
			if (n.getWeight() == 0) {
				calculateWeights(n);
			}
		}
		return true;
	}

	private void calculateWeights(SpanningTreeNode n) {

		if (nodesTreeElement[n.getNode().getIndex()].getChildren() == null) {
			nodesTreeElement[n.getNode().getIndex()].setWeight(1);
		} else {
			int sumChildren = 0;
			for (SpanningTreeNode child : n.getChildren()) {
				if (child.getWeight() == 0) {
					calculateWeights(child);
				}
				sumChildren += child.getWeight();
			}
			nodesTreeElement[n.getNode().getIndex()].setWeight(sumChildren + 1);
		}

	}

	private void bfs(Node node) {
		try {
			int comp = node.getIndex();
			Queue q = new Queue();
			SpanningTreeNode root = new SpanningTreeNode(node);
			root.setRoot(true);

			this.componentList.add(root);
			q.enqueue(root);
			visited[node.getIndex()] = true;
			while (!q.isEmpty()) {
				SpanningTreeNode temp = (SpanningTreeNode) q.dequeue();
				this.nodeComponentMembership[temp.getNode().getIndex()] = comp;
				this.nodesTreeElement[temp.getNode().getIndex()] = temp;
				for (Node n : temp.getNode().getNeighbors()) {
					if (!visited[n.getIndex()]) {
						visited[n.getIndex()] = true;
						SpanningTreeNode newChild = new SpanningTreeNode(n);
						newChild.setParent(temp);
						temp.addChild(newChild);
						q.enqueue(newChild);

					}
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof CCUndirected)) {
			return false;
		}
		CCUndirected cc = (CCUndirected) m;

		if (!ArrayUtils.equals(this.nodeComponentMembership,
				cc.getNodeComponentMembership())) {
			return false;
		}

		return true;
	}

	@Override
	public boolean cleanupApplication() {
		// TODO Auto-generated method stub
		return false;
	}

	// /compcounter
	@Override
	protected Value[] getValues() {
		Value v1 = new Value("NumberofComponents", countComponents());
		Value v2 = new Value("AverageComponentSize",
				calculateAverageComponentSize());

		return new Value[] { v1, v2 };
	}

	private double countComponents() {
		return componentList.size();
	}

	private double calculateAverageComponentSize() {
		return this.g.getNodes().length / componentList.size();
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("Components", calculateComponents());
		return new Distribution[] { d1 };
	}

	private double[] calculateComponents() {
		double[] sumComp = new double[componentList.size()];
		int counter = 0;
		for (SpanningTreeNode n : componentList) {
			sumComp[counter] = n.getWeight();
			counter++;
		}
		return sumComp;
	}

	public int[] getNodeComponentMembership() {
		return nodeComponentMembership;
	}

}
