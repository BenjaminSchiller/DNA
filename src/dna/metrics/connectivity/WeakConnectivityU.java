package dna.metrics.connectivity;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import dna.graph.IElement;
import dna.graph.edges.DirectedEdge;
import dna.graph.edges.Edge;
import dna.graph.edges.UndirectedEdge;
import dna.graph.nodes.DirectedNode;
import dna.graph.nodes.Node;
import dna.graph.nodes.UndirectedNode;
import dna.metrics.algorithms.IAfterEA;
import dna.metrics.algorithms.IAfterER;
import dna.metrics.algorithms.IAfterNA;
import dna.metrics.algorithms.IAfterNR;
import dna.updates.update.EdgeAddition;
import dna.updates.update.EdgeRemoval;
import dna.updates.update.NodeAddition;
import dna.updates.update.NodeRemoval;

public class WeakConnectivityU extends WeakConnectivity implements IAfterNA,
		IAfterNR, IAfterEA, IAfterER {

	public WeakConnectivityU() {
		super("WeakConnectivityU");
	}

	@Override
	public boolean init() {
		// TODO Auto-generated method stub
		return this.compute();
	}

	@Override
	public boolean applyAfterUpdate(EdgeRemoval er) {
		Edge e = (Edge) er.getEdge();
		Node n1;
		Node n2;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			n1 = ((DirectedEdge) e).getSrc();
			n2 = ((DirectedEdge) e).getDst();
		} else {
			n1 = ((UndirectedEdge) e).getNode1();
			n2 = ((UndirectedEdge) e).getNode2();
		}

		if (lookUp(n1) == lookUp(n2)) {
			if (this.parents.containsKey(n1) && parents.get(n1).equals(n2)) {
				checkEdgeRemoval(n2, n1);
				return true;
			}
			if (this.parents.containsKey(n2) && parents.get(n2).equals(n1)) {
				checkEdgeRemoval(n1, n2);
				return true;
			}
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(EdgeAddition ea) {
		Edge e = (Edge) ea.getEdge();
		Node n1;
		Node n2;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			n1 = ((DirectedEdge) e).getSrc();
			n2 = ((DirectedEdge) e).getDst();
		} else {
			n1 = ((UndirectedEdge) e).getNode1();
			n2 = ((UndirectedEdge) e).getNode2();
		}

		int c1 = lookUp(n1);
		int c2 = lookUp(n2);
		if (c1 != c2) {
			if (this.componentList.get(c1).getSize() < this.componentList.get(
					c2).getSize()) {
				if (DirectedNode.class.isAssignableFrom(this.g
						.getGraphDatastructures().getNodeType())) {
					n1 = ((DirectedEdge) e).getDst();
					n2 = ((DirectedEdge) e).getSrc();
				} else {
					n1 = ((UndirectedEdge) e).getNode2();
					n2 = ((UndirectedEdge) e).getNode1();
				}
				int temp = c1;
				c1 = c2;
				c2 = temp;
			}
			this.componentList.get(c1).increaseSize(
					this.componentList.get(c2).getSize());

			Node temp = n2;
			Node newParent = n1;
			Node newChild = parents.get(temp);

			while (this.parents.containsKey(temp)) {
				newChild = parents.get(temp);
				parents.put(temp, newParent);
				newParent = temp;
				temp = newChild;
			}
			parents.put(temp, newParent);
			this.componentList.remove(c2);
			componentConnection.put(c2, c1);
		}
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeRemoval nr) {
		Node n = (Node) nr.getNode();
		g.addNode(n);
		HashSet<Edge> bla = new HashSet<>();
		for (IElement ie : n.getEdges()) {
			Edge e = (Edge) ie;
			e.connectToNodes();
			bla.add(e);
		}
		for (Edge e : bla) {
			e.disconnectFromNodes();
			this.applyAfterUpdate(new EdgeRemoval(e));
		}
		this.componentList.remove(this.lookUp(n));
		this.nodeComponentMembership.remove(n);
		g.removeNode(n);
		return true;
	}

	@Override
	public boolean applyAfterUpdate(NodeAddition na) {
		Node n = (Node) na.getNode();
		counter++;
		WeakComponent stn = new WeakComponent(counter);
		stn.setSize(1);
		this.componentList.put(counter, stn);
		this.nodeComponentMembership.put(n, counter);
		return true;
	}

	private void checkEdgeRemoval(Node n1, Node n2) {
		boolean neighbourFound = false;
		int degreeN1;
		int degreeN2;
		if (DirectedNode.class.isAssignableFrom(this.g.getGraphDatastructures()
				.getNodeType())) {
			degreeN1 = ((DirectedNode) n1).getDegree();
			degreeN2 = ((DirectedNode) n2).getDegree();
		} else {
			degreeN1 = ((UndirectedNode) n1).getDegree();
			degreeN2 = ((UndirectedNode) n2).getDegree();
		}

		if (degreeN1 == 0) {
			this.componentList.get(lookUp(n1)).decreaseSize(1);
			counter++;
			WeakComponent c = new WeakComponent(counter);
			c.setSize(1);
			parents.remove(n2);
			this.nodeComponentMembership.put(n1, counter);
			this.componentList.put(counter, c);
			return;
		}

		if (degreeN2 == 0) {
			parents.remove(n2);
			counter++;
			this.componentList.get(lookUp(n2)).decreaseSize(1);
			WeakComponent c = new WeakComponent(counter);
			c.setSize(1);
			this.nodeComponentMembership.put(n2, counter);
			this.componentList.put(counter, c);
			return;
		}

		// check for direct neighbour
		HashSet<Node> reachableNodes = new HashSet<Node>();
		for (IElement ie : n1.getEdges()) {
			Edge ed = (Edge) ie;
			Node node = (Node) ed.getDifferingNode(n1);
			reachableNodes.add(node);
		}
		for (IElement ie : n2.getEdges()) {
			Edge ed = (Edge) ie;
			Node node = (Node) ed.getDifferingNode(n2);
			if (reachableNodes.contains(node)) {
				if (parents.containsKey(n1) && parents.get(n1).equals(node)) {
					parents.put(n2, node);
					neighbourFound = true;
					break;
				}
				if (parents.containsKey(node) && parents.get(node).equals(n2)) {
					parents.put(node, n1);
					parents.put(n2, node);
					neighbourFound = true;
					break;
				}

			}
		}

		if (!neighbourFound) {
			neighbourFound = saveRemove(n2, reachableNodes, n1);
		}

	}

	private boolean saveRemove(Node n, HashSet<Node> reachables, Node target) {

		Queue<Node> q = new LinkedList<Node>();
		q.add(n);

		HashSet<Node> seenNodes = new HashSet<Node>();
		HashSet<Node> reachableNodes = new HashSet<Node>();
		seenNodes.add(n);
		boolean found = false;
		Node temp = n;
		while (!q.isEmpty() && !found) {
			temp = q.poll();
			for (IElement ie : temp.getEdges()) {
				Edge ed = (Edge) ie;
				Node x = (Node) ed.getDifferingNode(temp);

				if (parents.containsKey(x) && parents.get(x).equals(temp)) {
					q.add(x);
					reachableNodes.remove(x);
					seenNodes.add(x);
					continue;
				}
				if (!seenNodes.contains(x)) {
					reachableNodes.add(x);
				}
			}
		}

		if (reachableNodes.isEmpty()) {
			counter++;
			WeakComponent c = new WeakComponent(counter);
			this.componentList.put(counter, c);
			c.setSize(seenNodes.size());
			this.parents.remove(n);
			this.componentList.get(lookUp(n)).decreaseSize(seenNodes.size());
			for (Node uN : seenNodes) {
				this.nodeComponentMembership.put(uN, counter);
			}
			return false;
		}

		Node connection = reachableNodes.iterator().next();
		for (IElement ie : connection.getEdges()) {
			Edge ed = (Edge) ie;
			Node node = (Node) ed.getDifferingNode(connection);
			if (seenNodes.contains(node)) {
				restructureTree(connection, node, n);
				break;
			}
		}

		return true;

	}

	private void restructureTree(Node connection, Node node, Node start) {
		Node temp = node;
		Node newParent = connection;
		Node newChild = (Node) parents.get(temp);

		while (!temp.equals(start)) {
			newChild = (Node) parents.get(temp);
			parents.put(temp, newParent);

			newParent = temp;
			temp = newChild;
		}
		parents.put(temp, newParent);
	}

}
