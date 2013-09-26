package dna.metrics.connectedComponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import dna.graph.Graph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedEdge;
import dna.graph.undirected.UndirectedGraph;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class CCUndirected extends Metric {

	protected HashMap<Integer, Integer> nodeComponentMembership;
	protected boolean[] visited;
	protected HashMap<Integer, SpanningTreeNode> nodesTreeElement;
	protected List<SpanningTreeNode> componentList;

	public CCUndirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init_() {
		this.nodeComponentMembership = new HashMap<Integer, Integer>();
		this.nodesTreeElement = new HashMap<Integer, SpanningTreeNode>();
		this.visited = new boolean[this.g.getNodes().size()];
		this.componentList = new ArrayList<SpanningTreeNode>();
	}

	@Override
	public void reset_() {
		this.nodeComponentMembership = new HashMap<Integer, Integer>();
		this.visited = new boolean[this.g.getNodes().size()];
		this.nodesTreeElement = new HashMap<Integer, SpanningTreeNode>();
		this.componentList = new ArrayList<SpanningTreeNode>();
	}

	@Override
	public boolean compute() {
		UndirectedGraph g = (UndirectedGraph) this.g;
		for (UndirectedNode n : g.getNodes()) {
			if (!this.visited[n.getIndex()]) {
				bfs(n);
			}
		}
		for (SpanningTreeNode n : nodesTreeElement.values()) {
			if (n.getWeight() == 0) {
				calculateWeights(n);
			}
		}
		return true;
	}

	protected void calculateWeights(SpanningTreeNode n) {

		// if (nodesTreeElement.get(n.getNode().getIndex()).getChildren() ==
		// null) {
		// nodesTreeElement.get(n.getNode().getIndex()).setWeight(1);
		// } else {
		// int sumChildren = 0;
		// for (SpanningTreeNode child : n.getChildren()) {
		// if (child.getWeight() == 0) {
		// calculateWeights(child);
		// }
		// sumChildren += child.getWeight();
		// }
		// nodesTreeElement.get(n.getNode().getIndex()).setWeight(
		// sumChildren + 1);
		// }

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
			this.nodeComponentMembership.put(temp.getNode().getIndex(), comp);
			this.nodesTreeElement.put(temp.getNode().getIndex(), temp);
			for (UndirectedEdge n : temp.getNode().getEdges()) {
				UndirectedNode des = n.getDifferingNode(temp.getNode());
				if (!visited[des.getIndex()]) {
					visited[des.getIndex()] = true;
					SpanningTreeNode newChild = new SpanningTreeNode(des);
					newChild.setParent(temp);
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

		boolean success = true;

		success &= (this.componentList.size() == cc.componentList.size());
		success &= check(this.nodeComponentMembership,
				cc.nodeComponentMembership);
		return success;
	}

	private boolean check(HashMap<Integer, Integer> map1,
			HashMap<Integer, Integer> map2) {
		if (map1.size() != map2.size()) {
			System.out.println("diff @Number of Compents " + map1.size()
					+ " != " + map2.size());
			return false;
		}

		HashMap<Integer, Integer> checkComp = new HashMap<Integer, Integer>();
		for (int i : map1.keySet()) {
			if (!map2.containsKey(i)) {
				return false;
			}
			if (checkComp.containsKey(map1.get(i))) {
				if (checkComp.get(map1.get(i)) != map2.get(i)) {
					return false;
				}
			} else {
				checkComp.put(map1.get(i), map2.get(i));
			}
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

	public HashMap<Integer, Integer> getNodeComponentMembership() {
		return nodeComponentMembership;
	}

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof CCUndirected;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return DirectedNode.class.isAssignableFrom(g.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(g
						.getGraphDatastructures().getNodeType());
	}

	@Override
	public boolean isApplicable(Batch b) {
		return DirectedNode.class.isAssignableFrom(b.getGraphDatastructures()
				.getNodeType())
				|| UndirectedNode.class.isAssignableFrom(b
						.getGraphDatastructures().getNodeType());
	}

}
