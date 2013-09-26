package dna.metrics.connectedComponents;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.directed.DirectedEdge;
import dna.graph.directed.DirectedGraph;
import dna.graph.directed.DirectedNode;
import dna.graph.undirected.UndirectedNode;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.Value;
import dna.updates.Batch;

@SuppressWarnings("rawtypes")
public abstract class CCDirected extends Metric {

	private boolean[] visited;

	// DAGGER
	private Stack<DirectedNode> s;
	private int[] lowLink;
	private int[] index;
	private int ind = 0;
	protected int componentCounter = 0;
	protected Map<Integer, Integer> containmentEdges;
	protected Map<Integer, ComponentVertex> dag;
	protected Map<Integer, ComponentVertex> dagExpired;

	public CCDirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init_() {
		this.s = new Stack<DirectedNode>();
		this.lowLink = new int[this.g.getNodeCount()];
		this.index = new int[this.g.getNodeCount()];
		this.visited = new boolean[this.g.getNodeCount()];
		this.dagExpired = new HashMap<Integer, ComponentVertex>();
		this.containmentEdges = new HashMap<Integer, Integer>();
		this.dag = new HashMap<Integer, ComponentVertex>();
	}

	@Override
	public void reset_() {
		this.s = new Stack<DirectedNode>();
		this.lowLink = new int[this.g.getNodeCount()];
		this.index = new int[this.g.getNodeCount()];
		this.visited = new boolean[this.g.getNodeCount()];
		this.dagExpired = new HashMap<Integer, ComponentVertex>();
		this.containmentEdges = new HashMap<Integer, Integer>();
		this.dag = new HashMap<Integer, ComponentVertex>();
	}

	@Override
	public boolean compute() {
		DirectedGraph g = (DirectedGraph) this.g;
		s.clear();
		ind = 0;
		for (DirectedNode n : g.getNodes()) {
			if (!visited[n.getIndex()]) {
				tarjan(n);
			}
		}
		return true;
	}

	private void tarjan(DirectedNode node) {
		index[node.getIndex()] = ind;
		lowLink[node.getIndex()] = ind;
		visited[node.getIndex()] = true;
		ind += 1;
		s.push(node);

		for (DirectedEdge e : node.getOutgoingEdges()) {
			if (!visited[e.getDst().getIndex()]) {
				tarjan(e.getDst());
				lowLink[node.getIndex()] = Math.min(lowLink[node.getIndex()],
						lowLink[e.getDst().getIndex()]);
			} else if (s.contains(e.getDst())) {
				lowLink[node.getIndex()] = Math.min(lowLink[node.getIndex()],
						lowLink[e.getDst().getIndex()]);
			}
		}

		if (index[node.getIndex()] == lowLink[node.getIndex()]) {
			DirectedNode n;
			ComponentVertex newComponent = new ComponentVertex(
					this.componentCounter);
			do {
				n = s.pop();
				containmentEdges.put(n.getIndex(), componentCounter);
				for (DirectedEdge ed : n.getOutgoingEdges()) {
					if (containmentEdges.containsKey(ed.getDst().getIndex())) {
						if (containmentEdges.get(ed.getDst().getIndex()) != componentCounter) {
							if (newComponent.ed.containsKey(containmentEdges
									.get(ed.getDst().getIndex()))) {
								newComponent.ed.get(
										containmentEdges.get(ed.getDst()
												.getIndex())).add(ed);
							} else {
								HashSet<DirectedEdge> temp = new HashSet<>();
								temp.add(ed);
								newComponent.ed.put(containmentEdges.get(ed
										.getDst().getIndex()), temp);
							}
						}
					}
				}
				for (DirectedEdge ed : n.getIncomingEdges()) {
					if (containmentEdges.containsKey(ed.getSrc().getIndex())) {
						if (containmentEdges.get(ed.getSrc().getIndex()) != componentCounter) {
							ComponentVertex srcVertex = dag
									.get(containmentEdges.get(ed.getSrc()
											.getIndex()));
							if (srcVertex.ed.containsKey(containmentEdges
									.get(ed.getDst().getIndex()))) {
								srcVertex.ed.get(
										containmentEdges.get(ed.getDst()
												.getIndex())).add(ed);
							} else {
								HashSet<DirectedEdge> temp = new HashSet<>();
								temp.add(ed);
								srcVertex.ed.put(containmentEdges.get(ed
										.getDst().getIndex()), temp);
							}
						}
					}
				}

			} while (n != node);
			this.dag.put(this.componentCounter, newComponent);
			componentCounter++;
		}

	}

	@Override
	public boolean equals(Metric m) {
		if (!(m instanceof CCDirected)) {
			return false;
		}
		CCDirected cc = (CCDirected) m;

		boolean success = true;
		success &= (this.dag.size() == cc.dag.size());

		return success;
	}

	@Override
	protected Value[] getValues() {
		Value v1 = new Value("NumberofComponents", countComponents());

		return new Value[] { v1 };
	}

	private double countComponents() {

		return this.dag.size();
	}

	@Override
	protected Distribution[] getDistributions() {
		Distribution d1 = new Distribution("Components", calculateComponents());
		return new Distribution[] {};
	}

	private double[] calculateComponents() {

		double[] components = new double[dag.size()];
		int count = 0;
		for (int s : dag.keySet()) {

			count++;
		}

		return components;
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

	@Override
	public boolean isComparableTo(Metric m) {
		return m != null && m instanceof CCDirected;
	}

}
