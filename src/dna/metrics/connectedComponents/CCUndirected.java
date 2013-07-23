package dna.metrics.connectedComponents;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.util.ArrayUtils;

public abstract class CCUndirected extends Metric {

	protected int[] nodeComponentMembership;
	protected boolean[] visited;
	protected SpanningTreeNode[] nodesTreeElement;
	protected List<SpanningTreeNode> componentList;

	public CCUndirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init() {
		this.nodeComponentMembership = new int[this.g.getNodes().size()];
		this.nodesTreeElement = new SpanningTreeNode[this.g.getNodes().size()];
		this.visited = new boolean[this.g.getNodes().size()];
		this.componentList = new ArrayList<SpanningTreeNode>();
	}

	@Override
	public void reset_() {
		this.nodeComponentMembership = new int[this.g.getNodes().size()];
		this.visited = new boolean[this.g.getNodes().size()];
		this.nodesTreeElement = new SpanningTreeNode[this.g.getNodes().size()];
		this.componentList = new ArrayList<SpanningTreeNode>();
	}

	protected void calculateWeights(SpanningTreeNode n) {

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

	protected void bfs(UndirectedNode node) {
		int comp = node.getIndex();
		Queue<SpanningTreeNode> q = new LinkedList<SpanningTreeNode>();
		SpanningTreeNode root = new SpanningTreeNode(node);
		root.setRoot(true);

		this.componentList.add(root);
		q.add(root);
		visited[node.getIndex()] = true;
		while (!q.isEmpty()) {
			SpanningTreeNode temp = (SpanningTreeNode) q.poll();
			this.nodeComponentMembership[temp.getNode().getIndex()] = comp;
			this.nodesTreeElement[temp.getNode().getIndex()] = temp;
			for (UndirectedEdge n : temp.getNode().getEdges()) {
				UndirectedNode des = n.getNode1();
				if (des != temp.getNode()) {
					des = n.getNode2();
				}
				if (!visited[des.getIndex()]) {
					visited[des.getIndex()] = true;
					SpanningTreeNode newChild = new SpanningTreeNode(des);
					newChild.setParent(temp);
					temp.addChild(newChild);
					q.add(newChild);

				}
			}
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
		return this.g.getNodes().size() / componentList.size();
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
