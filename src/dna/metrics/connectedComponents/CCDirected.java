package dna.metrics.connectedComponents;

import java.util.HashMap;
<<<<<<< HEAD
=======
import java.util.HashSet;
>>>>>>> some stuff
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
<<<<<<< HEAD
	private Stack<DirectedNode> s = new Stack<DirectedNode>();
	private int[] lowLink = new int[this.g.getNodeCount()];
	private int[] index = new int[this.g.getNodeCount()];
	private int ind = 0;
	protected int componentCounter = 0;
	protected HashMap<Integer, Integer> containmentEdges = new HashMap<>();
	protected Map<Integer, ComponentVertex> dag = new HashMap<Integer, ComponentVertex>();
	protected Map<Integer, ComponentVertex> dagExpired = new HashMap<Integer, ComponentVertex>();
=======
	private Stack<DirectedNode> s;
	private int[] lowLink;
	private int[] index;
	private int ind = 0;
	protected int componentCounter = 0;
	protected Map<Integer, Integer> containmentEdges;
	protected Map<Integer, ComponentVertex> dag;
	protected Map<Integer, ComponentVertex> dagExpired;
>>>>>>> some stuff

	public CCDirected(String name, ApplicationType type) {
		super(name, type);
	}

	@Override
	public void init_() {
<<<<<<< HEAD

=======
		this.s = new Stack<DirectedNode>();
		this.lowLink = new int[this.g.getNodeCount()];
		this.index = new int[this.g.getNodeCount()];
		this.visited = new boolean[this.g.getNodeCount()];
		this.dagExpired = new HashMap<>();
		this.containmentEdges = new HashMap<>();
		this.dag = new HashMap<>();
>>>>>>> some stuff
	}

	@Override
	public void reset_() {
<<<<<<< HEAD

=======
		this.s = new Stack<DirectedNode>();
		this.lowLink = new int[this.g.getNodeCount()];
		this.index = new int[this.g.getNodeCount()];
		this.dagExpired = new HashMap<>();
		this.containmentEdges = new HashMap<>();
		this.dag = new HashMap<>();
		this.visited = new boolean[this.g.getNodeCount()];
>>>>>>> some stuff
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
<<<<<<< HEAD
			// TODO: nur erzeugen wenn component size groößer gleich 1
=======
			ComponentVertex newComponent = new ComponentVertex(
					this.componentCounter);
>>>>>>> some stuff
			do {
				n = s.pop();
				containmentEdges.put(n.getIndex(), componentCounter);
				for (DirectedEdge ed : n.getOutgoingEdges()) {
					if (containmentEdges.containsKey(ed.getDst().getIndex())) {
						if (containmentEdges.get(ed.getDst().getIndex()) != componentCounter) {
<<<<<<< HEAD
							// TODO:dAGEdges.add(new DAGEdge(, dst))
						}
					}
				}
				for (DirectedEdge ed : n.getOutgoingEdges()) {
					if (containmentEdges.containsKey(ed.getSrc().getIndex())) {
						if (containmentEdges.get(ed.getSrc().getIndex()) != componentCounter) {
							// TODO:dAGEdges.add(new DAGEdge(, dst))
=======
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
>>>>>>> some stuff
						}
					}
				}

			} while (n == node);
<<<<<<< HEAD
			this.dag.put(this.componentCounter, new ComponentVertex(
					this.componentCounter));
=======
			this.dag.put(this.componentCounter, newComponent);
>>>>>>> some stuff
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
<<<<<<< HEAD
		success &= this.dag.equals(cc.dag);
=======
		success &= (this.dag.size() == cc.dag.size());
>>>>>>> some stuff

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
<<<<<<< HEAD
		HashMap<Integer, Integer> sum = new HashMap<Integer, Integer>();
		// for (int i = 0; i < nodeComponentMembership.length; i++) {
		// if (sum.containsKey(nodeComponentMembership[i])) {
		// int temp = sum.get(nodeComponentMembership[i]) + 1;
		// sum.put(nodeComponentMembership[i], temp);
		// } else {
		// sum.put(nodeComponentMembership[i], 1);
		// }
		// }
		double[] components = new double[sum.size()];
=======

		double[] components = new double[dag.size()];
>>>>>>> some stuff
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
<<<<<<< HEAD

=======
>>>>>>> some stuff
	}

}
