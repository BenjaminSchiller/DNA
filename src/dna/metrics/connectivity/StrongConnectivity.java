package dna.metrics.connectivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import dna.graph.Graph;
import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.metrics.IMetric;
import dna.metrics.Metric;
import dna.series.data.Distribution;
import dna.series.data.DistributionDouble;
import dna.series.data.NodeNodeValueList;
import dna.series.data.NodeValueList;
import dna.series.data.Value;
import dna.updates.batch.Batch;

public abstract class StrongConnectivity extends Metric {

	private boolean[] visited;

	// DAGGER
	protected Stack<DirectedNode> s;
	private int[] lowLink;
	private int[] index;
	private int ind = 0;
	protected int componentCounter = 0;
	protected Map<DirectedNode, Integer> containmentEdges;
	protected Map<Integer, Integer> containmentEdgesForComponents;
	protected Map<Integer, StrongComponent> dag;
	protected Map<Integer, StrongComponent> dagExpired;

	public StrongConnectivity(String name) {
		super(name);
	}

	protected boolean compute() {
		this.s = new Stack<DirectedNode>();
		this.lowLink = new int[this.g.getMaxNodeIndex() + 1];
		this.index = new int[this.g.getMaxNodeIndex() + 1];
		this.visited = new boolean[this.g.getMaxNodeIndex() + 1];
		this.containmentEdges = new HashMap<DirectedNode, Integer>();
		this.dag = new HashMap<Integer, StrongComponent>();
		this.containmentEdgesForComponents = new HashMap<>();

		s.clear();
		ind = 0;
		for (IElement ie : g.getNodes()) {
			DirectedNode n = (DirectedNode) ie;
			if (!visited[n.getIndex()]) {
				tarjan(n);
			}
		}

		for (IElement i : g.getNodes()) {
			DirectedNode n = (DirectedNode) i;
			int iN = this.containmentEdges.get(n);
			StrongComponent cV = this.dag.get(iN);
			for (IElement ie : n.getOutgoingEdges()) {
				DirectedEdge ed = (DirectedEdge) ie;

				int iDst = this.containmentEdges.get(ed.getDst());
				if (iDst != iN) {
					if (cV.ed.containsKey(iDst)) {
						cV.ed.put(iDst, cV.ed.get(iDst) + 1);
					} else {
						cV.ed.put(iDst, 1);
					}
				}
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

		for (IElement ie : node.getOutgoingEdges()) {
			DirectedEdge e = (DirectedEdge) ie;
			if (!visited[e.getDst().getIndex()]) {
				tarjan(e.getDst());
				lowLink[node.getIndex()] = Math.min(lowLink[node.getIndex()],
						lowLink[e.getDst().getIndex()]);
			} else if (s.contains(e.getDst())) {
				lowLink[node.getIndex()] = Math.min(lowLink[node.getIndex()],
						index[e.getDst().getIndex()]);
			}
		}

		if (index[node.getIndex()] == lowLink[node.getIndex()]) {
			DirectedNode n;
			StrongComponent newComponent = new StrongComponent(
					this.componentCounter);
			int size = 0;
			do {
				n = s.pop();
				size++;
				containmentEdges.put(n, componentCounter);
			} while (!n.equals(node));
			newComponent.setSize(size);
			this.dag.put(this.componentCounter, newComponent);
			componentCounter++;
		}
	}

	protected int lookup(DirectedNode n) {
		int nIndex = this.containmentEdges.get(n);
		while (this.containmentEdgesForComponents.containsKey(nIndex)) {
			nIndex = this.containmentEdgesForComponents.get(nIndex);
		}
		this.containmentEdges.put(n, nIndex);
		return nIndex;
	}

	@Override
	public Value[] getValues() {
		Value v1 = new Value("NumberofComponents", countComponents());
		Value v2 = new Value("average size", getaverageSize());
		return new Value[] { v1, v2 };
	}

	private double getaverageSize() {
		if (this.dag.isEmpty()) {
			return 0d;
		} else {
			return (double) this.g.getNodeCount() / (double) this.dag.size();
		}
	}

	private double countComponents() {
		return this.dag.size();
	}

	@Override
	public Distribution[] getDistributions() {
		Distribution d1 = new DistributionDouble("ComponentsSize",
				calculateComponents());
		return new Distribution[] { d1 };
	}

	private double[] calculateComponents() {
		double[] components = new double[dag.size()];
		int count = 0;
		for (int s : dag.keySet()) {
			components[count] = this.dag.get(s).getSize();
			count++;
		}
		return components;
	}

	@Override
	public NodeValueList[] getNodeValueLists() {
		return new NodeValueList[] {};
	}

	@Override
	public NodeNodeValueList[] getNodeNodeValueLists() {
		return new NodeNodeValueList[] {};
	}

	@Override
	public boolean isComparableTo(IMetric m) {
		return m != null && m instanceof StrongConnectivity;
	}

	@Override
	public boolean equals(IMetric m) {
		if (!(m instanceof StrongConnectivity)) {
			return false;
		}
		StrongConnectivity cc = (StrongConnectivity) m;

		boolean success = true;
		if (this.dag.size() != cc.dag.size()) {
			System.out.println("diff @ number of componets expected number "
					+ this.dag.size() + " is " + cc.dag.size());
		}
		HashMap<Integer, Integer> sizes = new HashMap<Integer, Integer>();
		for (StrongComponent cV : this.dag.values()) {
			if (sizes.containsKey(cV.getSize())) {
				sizes.put(cV.getSize(), sizes.get(cV.getSize()) + 1);
			} else {
				sizes.put(cV.getSize(), +1);
			}
		}
		for (StrongComponent cV : cc.dag.values()) {
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

		for (IElement ie : g.getNodes()) {
			DirectedNode n = (DirectedNode) ie;

			if (this.dag.get(this.containmentEdges.get(n)).getSize() != cc.dag
					.get(cc.lookup(n)).getSize()
					&& cc.dag.get(cc.lookup(n)).getSize() == 1) {
				System.out.println("ns " + cc.lookup(n));
				for (IElement i : n.getIncomingEdges()) {
					System.out.println("In"
							+ cc.lookup(((DirectedEdge) i).getSrc()));
				}
				for (IElement i : n.getOutgoingEdges()) {
					System.out.println("out "
							+ cc.lookup(((DirectedEdge) i).getDst()));
				}
			}

		}
		for (IElement ie : g.getEdges()) {
			DirectedEdge n = (DirectedEdge) ie;

			if (this.containmentEdges.get(n.getSrc()).equals(
					this.containmentEdges.get(n.getDst()))) {

				if (cc.lookup(n.getSrc()) != cc.lookup(n.getDst())) {
					System.out.println(n);
				}
			} else {
				if (cc.lookup(n.getSrc()) == cc.lookup(n.getDst())) {
					System.out.println(n);
				}
			}

		}

		return success;
	}

	@Override
	public boolean isApplicable(Graph g) {
		return g.isDirected();
	}

	@Override
	public boolean isApplicable(Batch b) {
		return b.getGraphDatastructures().isNodeType(DirectedNode.class);
	}

}
