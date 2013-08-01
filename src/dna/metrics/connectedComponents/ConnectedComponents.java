//package dna.metrics.connectedComponents;
//
//import java.util.HashSet;
//import java.util.Set;
//import java.util.Stack;
//
//import dna.diff.Diff;
//import dna.diff.DiffNotApplicableException;
//import dna.graph.Edge;
//import dna.graph.Graph;
//import dna.graph.Node;
//import dna.metrics.Metric;
//import dna.series.data.Distribution;
//import dna.series.data.Value;
//
//public class ConnectedComponents extends Metric {
//
//	public ConnectedComponents() {
//		super("connectedComponents", true, false, false);
//	}
//
//	int[] index;
//	int[] lowLink;
//	Stack<Node> s;
//	int componentIndex;
//	Set<Integer>[] components;
//	Set<Integer>[] reachableComponents;
//	Set<Integer> reachalbeComponet;
//	private Set<Integer> component;
//
//	@Override
//	protected void init(Graph g) {
//		this.index = new int[this.g.getNodes().length];
//		this.lowLink = new int[this.g.getNodes().length];
//
//		this.s = new Stack<Node>();
//		this.componentIndex = 0;
//
//		this.components = new Set[this.g.getNodes().length];
//		this.reachalbeComponet = new HashSet<Integer>();
//		this.component = new HashSet<Integer>();
//		this.reachableComponents = new Set[this.g.getNodes().length];
//	}
//
//	@Override
//	public boolean equals(Metric m) {
//		if (!(m instanceof ConnectedComponents)) {
//			return false;
//		}
//
//		ConnectedComponents cc = (ConnectedComponents) m;
//
//		return true;
//	}
//
//	@Override
//	protected boolean compute_() {
//		this.componentIndex++;
//		int arrayIndex = 0;
//		for (Node n : this.g.getNodes()) {
//			if (index[n.getIndex()] == 0) {
//				this.reachalbeComponet = new HashSet<Integer>();
//				strongConnect(n);
//				this.components[arrayIndex] = this.component;
//				this.reachableComponents[arrayIndex] = this.reachalbeComponet;
//				arrayIndex++;
//			}
//
//		}
//
//		return true;
//	}
//
//	private void strongConnect(Node n) {
//		this.index[n.getIndex()] = this.componentIndex;
//		this.lowLink[n.getIndex()] = this.componentIndex;
//		this.componentIndex++;
//		this.s.push(n);
//
//		for (Node w : n.getOut()) {
//			this.reachalbeComponet.add(w.getIndex());
//			if (this.index[w.getIndex()] == 0) {
//				strongConnect(w);
//				this.lowLink[n.getIndex()] = Math.min(
//						this.lowLink[n.getIndex()], this.lowLink[w.getIndex()]);
//			} else {
//				if (this.s.contains(w)) {
//					this.lowLink[n.getIndex()] = Math.min(
//							this.lowLink[n.getIndex()],
//							this.index[w.getIndex()]);
//				}
//			}
//		}
//
//		this.component = new HashSet<Integer>();
//		if (index[n.getIndex()] == lowLink[n.getIndex()]) {
//			Node w = s.pop();
//			while (w != n) {
//				w = s.pop();
//				this.component.add(w.getIndex());
//			}
//		}
//
//	}
//
//	@Override
//	protected boolean applyBeforeDiff_(Diff d) {
//
//		addEdges(d);
//
//		deleteEdges(d);
//
//		return true;
//	}
//
//	private void deleteEdges(Diff d) {
//		if (!d.getRemovedEdges().isEmpty()) {
//			compute();
//		}
//	}
//
//	private void addEdges(Diff d) {
//		for (Edge e : d.getAddedEdges()) {
//			if (this.g.containsEdge(e)) {
//				continue;
//			}
//			Node source = e.getSrc();
//			Node dest = e.getDst();
//
//			for (int i = 0; i < this.components.length
//					&& this.components[i] != null; i++) {
//				if (this.components[i].contains(source)) {
//					if (!this.components[i].contains(dest)) {
//						for (int j = 0; j < this.reachableComponents.length
//								&& reachableComponents[j] != null; j++) {
//							if (this.reachableComponents[j].contains(dest)) {
//								if (this.reachableComponents[j]
//										.contains(source)) {
//									if (this.components[j].contains(dest)) {
//
//										this.components[i]
//												.addAll(components[j]);
//										this.components[j] = null;
//									}
//								} else {
//									if (this.reachableComponents[i]
//											.contains(source)) {
//										this.reachableComponents[i].add(dest
//												.getIndex());
//									}
//								}
//
//							}
//
//						}
//					}
//				}
//			}
//
//		}
//	}
//
//	@Override
//	protected boolean applyAfterEdgeAddition_(Diff d, Edge e)
//			throws DiffNotApplicableException {
//		throw new DiffNotApplicableException("edge addition");
//	}
//
//	@Override
//	protected boolean applyAfterEdgeRemoval_(Diff d, Edge e)
//			throws DiffNotApplicableException {
//		throw new DiffNotApplicableException("edge removal");
//	}
//
//	@Override
//	protected boolean applyAfterDiff_(Diff d) throws DiffNotApplicableException {
//		throw new DiffNotApplicableException("after diff");
//	}
//
//	@Override
//	public boolean cleanupApplication() {
//		// TODO Auto-generated method stub
//		return false;
//	}
//
//	@Override
//	public void reset_() {
//		this.index = null;
//		this.lowLink = null;
//		this.s = null;
//		this.componentIndex = -1;
//		this.components = null;
//	}
//
//	@Override
//	protected Value[] getValues() {
//		Value v1 = new Value("Connected Components",
//				computeAverageComponentSize());
//		return new Value[] { v1 };
//	}
//
//	private double computeAverageComponentSize() {
//		int size = 0;
//		int countComponents = 0;
//		for (int i = 0; i < components.length && components[i] != null; i++) {
//			size += components[i].size();
//			countComponents = i;
//		}
//		return (double) size / (double) countComponents;
//	}
//
//	@Override
//	protected Distribution[] getDistributions() {
//
//		return new Distribution[] {};
//
//	}
//
// }
