package dna.metrics.connectivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.Edge;
import dna.graph.nodes.Node;
import dna.metrics.algorithms.IAfterBatch;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IAfterNR;
import dna.metrics.algorithms.IBeforeEA;
import dna.metrics.algorithms.IBeforeNA;
import dna.updates.batch.Batch;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class WCBasicU extends WCBasic implements IBeforeEA, IAfterER,
		IBeforeNA, IAfterNR, IAfterBatch {

	public WCBasicU() {
		super("WCBasicU");
	}

	private HashMap<Node, WCComponent> mapping;

	@Override
	public boolean init() {
		if (!this.compute()) {
			return false;
		}
		this.mapping = new HashMap<Node, WCComponent>();
		for (WCComponent c : this.components) {
			for (Node n : c.getNodes()) {
				this.mapping.put(n, c);
			}
		}

		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		Node n = (Node) nr.getNode();
		WCComponent c = this.mapping.get(n);
		this.mapping.remove(n);

		c.removeNode(n);
		this.components.remove(c);
		if (c.size() > 0) {
			ArrayList<WCComponent> components = this.getComponents((Iterable) c
					.getNodes());
			this.components.addAll(components);
			for (WCComponent c_ : components) {
				for (Node n_ : c_.getNodes()) {
					this.mapping.put(n_, c_);
				}
			}
		}

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(NodeAddition na) {
		Node n = (Node) na.getNode();
		WCComponent c = new WCComponent(n);
		this.components.add(c);
		this.mapping.put(n, c);
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		Edge e = (Edge) er.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();
		WCComponent c = this.mapping.get(n1);

		// if edge is not in spanning tree: do noting
		if (!c.containsEdge(e)) {
			return true;
		}

		// split partitions otherwise
		this.components.remove(c);
		ArrayList<WCComponent> components = getComponents((Iterable) c
				.getNodes());
		this.components.addAll(components);
		for (WCComponent c_ : components) {
			for (Node n_ : c_.getNodes()) {
				this.mapping.put(n_, c_);
			}
		}

		return true;
	}

	@Override
	public boolean applyBeforeUpdate(EdgeAddition ea) {
		Edge e = (Edge) ea.getEdge();
		Node n1 = e.getN1();
		Node n2 = e.getN2();

		WCComponent c1 = this.mapping.get(n1);
		WCComponent c2 = this.mapping.get(n2);

		// do noting in case nodes are already in same component
		if (c1 == c2) {
			return true;
		}

		// merge components otherwise
		if (c1.size() > c2.size()) {
			this.merge(c1, c2);
			c1.addEdge(e);
		} else {
			this.merge(c2, c1);
			c2.addEdge(e);
		}

		return true;
	}

	/**
	 * 
	 * merges the two components, i.e., adds all nodes from c2 to c1 and changes
	 * the mapping for all added nodes to c1. finally, c2 is removed from the
	 * list of components
	 * 
	 * @param c1
	 * @param c2
	 */
	protected void merge(WCComponent c1, WCComponent c2) {
		for (Node n : c2.getNodes()) {
			c1.addNode(n);
			this.mapping.put(n, c1);
		}
		c1.addEdges(c2.getEdges());
		this.components.remove(c2);
	}

	protected boolean areConnected(Node n1, Node n2) {
		if (n1.getDegree() == 0 || n2.getDegree() == 0) {
			return false;
		}
		HashSet<Node> seen = new HashSet<Node>();
		Queue<Node> queue = new LinkedList<Node>();
		queue.add(n1);
		seen.add(n1);
		while (!queue.isEmpty()) {
			Node current = queue.poll();
			for (IElement e_ : current.getEdges()) {
				Node neighbor = ((Edge) e_).getDifferingNode(current);
				if (neighbor.equals(n2)) {
					return true;
				}
				if (!seen.contains(neighbor)) {
					seen.add(neighbor);
					queue.add(neighbor);
				}
			}
		}
		return false;
	}

	@Override
	public boolean applyAfterBatch(Batch b) {
		this.setIds();
		return true;
	}

}
